package com.telegram_lite.websocket;

import com.telegram_lite.entity.Message; // Import Message entity
import com.telegram_lite.entity.MessageType; // Import MessageType enum
import com.telegram_lite.entity.User; // Import User entity
import com.telegram_lite.service.MessageService; // Import MessageService
import com.telegram_lite.service.UserService; // Import UserService (nếu cần lấy User object từ username)

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

// Thư viện JSON, ví dụ Jackson (cần thêm dependency vào pom.xml nếu chưa có)
// Hoặc bạn có thể parse/tạo JSON thủ công cho đơn giản ban đầu
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat/{username}")
public class ChatServerEndpoint {

    private static Map<String, Session> activeSessions = new ConcurrentHashMap<>();
    private static Set<String> usernames = Collections.synchronizedSet(new HashSet<>());

    // Khởi tạo MessageService và UserService
    // Trong ứng dụng thực tế, bạn có thể muốn inject chúng (ví dụ qua CDI nếu dùng Java EE đầy đủ)
    private static MessageService messageService = new MessageService();
    // private static UserService userService = new UserService(); // Nếu cần lấy đối tượng User

    // ObjectMapper để xử lý JSON (nên là static và khởi tạo một lần)
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Đăng ký module để ObjectMapper có thể serialize/deserialize Java 8 Time (LocalDateTime)
        objectMapper.registerModule(new JavaTimeModule());
        // Cấu hình định dạng ngày giờ nếu cần, ví dụ:
        // objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }


    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        // ... (giữ nguyên logic onOpen, nhưng có thể không cần broadcast join message nếu client tự xử lý)
        // Thay vào đó, khi client kết nối, nó có thể yêu cầu lịch sử chat với người dùng cụ thể
        if (username == null || username.trim().isEmpty()) {
            // ... (xử lý lỗi như cũ)
            return;
        }
        if (activeSessions.containsKey(username)) {
            // ... (xử lý lỗi như cũ)
            return;
        }

        session.getUserProperties().put("username", username);
        activeSessions.put(username, session);
        usernames.add(username);

        System.out.println("User: " + username + " connected. Session ID: " + session.getId());
        // Thông báo cho client này biết kết nối thành công
        try {
            Map<String, Object> connectionSuccessMsg = new HashMap<>();
            connectionSuccessMsg.put("type", "connection_ack");
            connectionSuccessMsg.put("message", "Successfully connected as " + username);
            session.getBasicRemote().sendText(objectMapper.writeValueAsString(connectionSuccessMsg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        broadcastUserList(); // Vẫn gửi danh sách người dùng online
    }

    /**
     * Client sẽ gửi tin nhắn dưới dạng JSON.
     * Ví dụ:
     * Cho TEXT: {"to": "receiverUsername", "type": "TEXT", "content": "Hello!"}
     * Cho IMAGE: {"to": "receiverUsername", "type": "IMAGE", "url": "/uploads/image.jpg", "caption": "My photo"}
     */
    @OnMessage
    public void onMessage(String jsonMessage, Session session) {
        String senderUsername = (String) session.getUserProperties().get("username");
        if (senderUsername == null) return;

        try {
            // Parse JSON message từ client
            // Tạo một class DTO (Data Transfer Object) cho ClientMessage sẽ tốt hơn
            // Ví dụ: ClientMessageDto clientMsg = objectMapper.readValue(jsonMessage, ClientMessageDto.class);
            // Tạm thời parse thủ công hoặc dùng Map
            Map<String, String> clientMsg = objectMapper.readValue(jsonMessage, Map.class);

            String receiverUsername = clientMsg.get("to");
            String messageTypeStr = clientMsg.get("messageType"); // TEXT, IMAGE, VIDEO
            String content = clientMsg.get("content");       // Nội dung text hoặc caption
            String mediaUrl = clientMsg.get("mediaUrl");       // URL của media nếu có

            if (receiverUsername == null || messageTypeStr == null) {
                System.err.println("Invalid message format from " + senderUsername + ": " + jsonMessage);
                sendErrorMessage(session, "Invalid message format. 'to' and 'messageType' are required.");
                return;
            }

            MessageType messageType = MessageType.valueOf(messageTypeStr.toUpperCase());

            // 1. Lưu tin nhắn vào database
            Optional<Message> savedMessageOpt = messageService.createAndSaveMessage(
                    senderUsername, receiverUsername, messageType, content, mediaUrl
            );

            if (savedMessageOpt.isEmpty()) {
                System.err.println("Failed to save message from " + senderUsername + " to " + receiverUsername);
                sendErrorMessage(session, "Failed to send message. Please try again.");
                return;
            }

            Message savedMessage = savedMessageOpt.get();

            // 2. Tạo đối tượng JSON để gửi cho client (sender và receiver)
            // Đối tượng này nên chứa đầy đủ thông tin để client hiển thị
            Map<String, Object> outgoingMessage = new HashMap<>();
            outgoingMessage.put("type", "newMessage"); // Để client biết đây là tin nhắn mới
            outgoingMessage.put("id", savedMessage.getId());
            outgoingMessage.put("from", senderUsername);
            outgoingMessage.put("to", receiverUsername);
            outgoingMessage.put("messageType", savedMessage.getMessageType().toString());
            outgoingMessage.put("content", savedMessage.getContent());
            outgoingMessage.put("mediaUrl", savedMessage.getMediaUrl());
            outgoingMessage.put("timestamp", savedMessage.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); // Định dạng timestamp
            // outgoingMessage.put("isRead", savedMessage.isRead()); // Có thể gửi sau

            String outgoingJsonMessage = objectMapper.writeValueAsString(outgoingMessage);

            // 3. Gửi tin nhắn đến người nhận (nếu online)
            Session receiverSession = activeSessions.get(receiverUsername);
            if (receiverSession != null && receiverSession.isOpen()) {
                try {
                    receiverSession.getBasicRemote().sendText(outgoingJsonMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("User " + receiverUsername + " is offline. Message saved.");
                // Logic xử lý thông báo offline hoặc unread count có thể thêm ở đây
            }

            // 4. Gửi lại tin nhắn (hoặc một thông báo xác nhận) cho người gửi
            // Điều này giúp client của người gửi biết tin nhắn đã được xử lý và có ID, timestamp
            try {
                Map<String, Object> ackMessage = new HashMap<>(outgoingMessage); // Copy nội dung
                ackMessage.put("type", "messageSentAck"); // Loại tin nhắn khác để client tự xử lý
                session.getBasicRemote().sendText(objectMapper.writeValueAsString(ackMessage));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing JSON message from " + senderUsername + ": " + jsonMessage);
            e.printStackTrace();
            sendErrorMessage(session, "Invalid JSON format.");
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid message type from " + senderUsername + ": " + jsonMessage);
            e.printStackTrace();
            sendErrorMessage(session, "Invalid message type.");
        }
    }

    private void sendErrorMessage(Session session, String errorMessageContent) {
        try {
            Map<String, Object> errorMsgMap = new HashMap<>();
            errorMsgMap.put("type", "error");
            errorMsgMap.put("message", errorMessageContent);
            session.getBasicRemote().sendText(objectMapper.writeValueAsString(errorMsgMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        // ... (logic onClose có thể cần cập nhật để không broadcast join/leave message
        // cho tất cả mọi người nếu chúng ta chuyển sang UI tập trung vào 1-1)
        // Hoặc vẫn giữ để biết ai online/offline.
        String username = (String) session.getUserProperties().get("username");
        if (username != null) {
            activeSessions.remove(username);
            usernames.remove(username);
            System.out.println("User: " + username + " disconnected. Reason: " + closeReason.getReasonPhrase());
            // Có thể không cần broadcast join/leave dạng text nữa nếu client xử lý userlist
            broadcastUserList();
        } else {
            // ... (xử lý cũ)
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // ... (giữ nguyên logic onError)
        String username = (String) session.getUserProperties().get("username");
        System.err.println("Error for user: " + (username != null ? username : "Unknown") + ", Session ID: " + session.getId());
        throwable.printStackTrace();
    }

    // broadcastUserList vẫn hữu ích để client biết ai đang online
    private static void broadcastUserList() {
        Map<String, Object> userListUpdate = new HashMap<>();
        userListUpdate.put("type", "userlist"); // Để client phân biệt
        userListUpdate.put("users", new ArrayList<>(usernames)); // Gửi danh sách username

        try {
            String userListMessage = objectMapper.writeValueAsString(userListUpdate);
            activeSessions.values().forEach(s -> {
                synchronized (s) {
                    if (s.isOpen()) {
                        try {
                            s.getBasicRemote().sendText(userListMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Không cần hàm broadcast(String message) cũ nữa nếu mọi tin nhắn đều là JSON và có định tuyến
}
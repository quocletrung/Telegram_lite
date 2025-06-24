package com.telegram_lite.websocket;

import com.google.gson.Gson;
import com.telegram_lite.entity.ChatGroup;
import com.telegram_lite.entity.Message;
import com.telegram_lite.entity.MessageType;
import com.telegram_lite.entity.User;
import com.telegram_lite.service.GroupChatService;
import com.telegram_lite.service.MessageService;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat")
public class ChatServerEndpoint {

    private static final Map<String, Session> activeSessions = new ConcurrentHashMap<>();
    private final MessageService messageService = new MessageService();
    // <<< BƯỚC 4: THÊM GROUP CHAT SERVICE >>>
    private final GroupChatService groupChatService = new GroupChatService();
    private final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        String username = session.getQueryString().split("=")[1];
        activeSessions.put(username, session);
        System.out.println("User " + username + " connected");
    }

    @OnMessage
    public void onMessage(String jsonMessage, Session session) {
        // Lấy username của người gửi từ session hiện tại
        String senderUsername = null;
        for (Map.Entry<String, Session> entry : activeSessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                senderUsername = entry.getKey();
                break;
            }
        }

        if (senderUsername == null) {
            System.err.println("Không tìm thấy username cho session: " + session.getId());
            return;
        }

        // Decode tin nhắn JSON
        Map<String, String> messageMap = gson.fromJson(jsonMessage, Map.class);
        String chatType = messageMap.getOrDefault("chatType", "private"); // Mặc định là private nếu không có
        String target = messageMap.get("to");
        String content = messageMap.get("content");
        MessageType messageType = MessageType.valueOf(messageMap.getOrDefault("messageType", "TEXT").toUpperCase());
        String mediaUrl = messageMap.get("mediaUrl");

        // <<< BƯỚC 4: LOGIC ĐIỀU PHỐI TIN NHẮN >>>
        if ("group".equalsIgnoreCase(chatType)) {
            handleGroupMessage(senderUsername, target, content, messageType, mediaUrl);
        } else {
            handlePrivateMessage(senderUsername, target, content, messageType, mediaUrl);
        }
    }

    // <<< BƯỚC 4: PHƯƠNG THỨC MỚI ĐỂ XỬ LÝ TIN NHẮN NHÓM >>>
    private void handleGroupMessage(String senderUsername, String groupIdStr, String content, MessageType messageType, String mediaUrl) {
        Long groupId;
        try {
            groupId = Long.parseLong(groupIdStr);
        } catch (NumberFormatException e) {
            System.err.println("Invalid Group ID format: " + groupIdStr);
            return;
        }

        // 1. Lưu tin nhắn vào database
        Optional<Message> savedMessageOpt = groupChatService.saveGroupMessage(senderUsername, groupId, content, messageType, mediaUrl);

        if (savedMessageOpt.isEmpty()) {
            System.err.println("Không thể lưu tin nhắn cho nhóm: " + groupId);
            return;
        }

        // 2. Lấy thông tin nhóm để biết ai là thành viên
        Optional<ChatGroup> groupOpt = groupChatService.findGroupById(groupId);
        if (groupOpt.isEmpty()) {
            return;
        }

        // 3. Gửi tin nhắn đến tất cả thành viên đang online trong nhóm
        ChatGroup group = groupOpt.get();
        String outgoingJson = buildJsonResponse(savedMessageOpt.get());

        for (User member : group.getMembers()) {
            Session memberSession = activeSessions.get(member.getUsername());
            // Kiểm tra xem thành viên có đang online không
            if (memberSession != null && memberSession.isOpen()) {
                try {
                    memberSession.getBasicRemote().sendText(outgoingJson);
                } catch (IOException e) {
                    System.err.println("Lỗi khi gửi tin nhắn đến thành viên " + member.getUsername() + ": " + e.getMessage());
                }
            }
        }
    }

    // <<< BƯỚC 4: TÁCH LOGIC XỬ LÝ TIN NHẮN RIÊNG TƯ RA PHƯƠNG THỨC RIÊNG >>>
    private void handlePrivateMessage(String senderUsername, String receiverUsername, String content, MessageType messageType, String mediaUrl) {
        // 1. Lưu tin nhắn vào database bằng cách gọi đúng phương thức từ MessageService
        // --- ĐÂY LÀ DÒNG ĐÃ SỬA ---
        Optional<Message> savedMessageOpt = messageService.createAndSaveMessage(senderUsername, receiverUsername, messageType, content, mediaUrl);
        // -------------------------

        if (savedMessageOpt.isEmpty()) {
            System.err.println("Không thể lưu tin nhắn từ " + senderUsername + " đến " + receiverUsername);
            return;
        }

        // 2. Lấy session của người nhận
        Session receiverSession = activeSessions.get(receiverUsername);
        String outgoingJson = buildJsonResponse(savedMessageOpt.get());

        // 3. Gửi tin nhắn đến người nhận nếu họ online
        if (receiverSession != null && receiverSession.isOpen()) {
            try {
                receiverSession.getBasicRemote().sendText(outgoingJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 4. Gửi lại tin nhắn cho chính người gửi để xác nhận (và cập nhật UI)
        Session senderSession = activeSessions.get(senderUsername);
        if(senderSession != null && senderSession.isOpen()){
            try {
                senderSession.getBasicRemote().sendText(outgoingJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        activeSessions.values().remove(session);
        // Có thể thêm logic để thông báo user offline
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Xử lý lỗi
        throwable.printStackTrace();
    }

    // <<< BƯỚC 4: PHƯƠNG THỨC TIỆN ÍCH ĐỂ TẠO JSON RESPONSE >>>
    private String buildJsonResponse(Message message) {
        // Định dạng thời gian
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Tạo một Map để build JSON
        Map<String, Object> responseMap = new ConcurrentHashMap<>();
        responseMap.put("id", message.getId());
        responseMap.put("content", message.getContent());
        responseMap.put("sender", message.getSender().getUsername());
        responseMap.put("timestamp", message.getTimestamp().format(formatter));
        responseMap.put("messageType", message.getMessageType().toString());
        responseMap.put("mediaUrl", message.getMediaUrl());

        if (message.getReceiverGroup() != null) {
            // Đây là tin nhắn nhóm
            responseMap.put("chatType", "group");
            responseMap.put("groupId", message.getReceiverGroup().getId());
            responseMap.put("groupName", message.getReceiverGroup().getGroupName());
        } else {
            // Đây là tin nhắn riêng tư
            responseMap.put("chatType", "private");
            responseMap.put("receiver", message.getReceiver().getUsername());
        }

        return gson.toJson(responseMap);
    }
}
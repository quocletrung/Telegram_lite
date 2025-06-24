<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>

        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <title>Chat Dashboard</title>
            <style>
                /* Giữ nguyên các style đã có, hoặc chỉnh sửa nếu cần */
                body {
                    font-family: Arial, sans-serif;
                    margin: 0;
                    padding: 0;
                    background-color: #f4f4f4;
                    display: flex;
                    flex-direction: column;
                    height: 100vh;
                }

                .top-bar {
                    background-color: #333;
                    color: white;
                    padding: 10px 20px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }

                .top-bar .welcome-user {
                    font-size: 1.1em;
                }

                .top-bar .logout-link {
                    color: white;
                    text-decoration: none;
                    padding: 5px 10px;
                    background-color: #d9534f;
                    border-radius: 3px;
                }

                .top-bar .logout-link:hover {
                    background-color: #c9302c;
                }

                .chat-container {
                    display: flex;
                    flex-grow: 1;
                    overflow: hidden;
                }

                #user-list-container {
                    width: 250px;
                    background-color: #f0f0f0;
                    padding: 15px;
                    border-right: 1px solid #ddd;
                    overflow-y: auto;
                    display: flex;
                    flex-direction: column;
                }

                #user-list-container h3 {
                    margin-top: 0;
                    padding-bottom: 10px;
                    border-bottom: 1px solid #ccc;
                }

                #user-list {
                    list-style: none;
                    padding: 0;
                    margin: 0;
                }

                #user-list li {
                    padding: 8px 10px;
                    cursor: pointer;
                    border-bottom: 1px solid #e0e0e0;
                }

                #user-list li:hover {
                    background-color: #e0e0e0;
                }

                #user-list li.active-chat {
                    background-color: #007bff;
                    color: white;
                }

                #chat-area {
                    flex-grow: 1;
                    display: flex;
                    flex-direction: column;
                    background-color: #fff;
                }

                #chat-header {
                    padding: 10px 15px;
                    border-bottom: 1px solid #ddd;
                    background-color: #f9f9f9;
                    font-weight: bold;
                }

                #messages {
                    flex-grow: 1;
                    padding: 15px;
                    overflow-y: auto;
                    border-bottom: 1px solid #ddd;
                    display: flex;
                    flex-direction: column;
                }

                .message-wrapper {
                    display: flex;
                    margin-bottom: 10px;
                }

                .message {
                    padding: 8px 12px;
                    border-radius: 18px;
                    max-width: 70%;
                    word-wrap: break-word;
                    font-size: 0.9em;
                    line-height: 1.4;
                }

                .message .timestamp {
                    font-size: 0.7em;
                    color: black;
                    display: block;
                    margin-top: 4px;
                    text-align: right;
                }

                .message-wrapper.sent {
                    justify-content: flex-end;
                }

                .message-wrapper.sent .message {
                    background-color: #007bff;
                    color: white;
                    border-bottom-right-radius: 5px;
                }

                .message-wrapper.received {
                    justify-content: flex-start;
                }

                .message-wrapper.received .message {
                    background-color: #e9e9eb;
                    color: #333;
                    border-bottom-left-radius: 5px;
                }

                .message.system {
                    background-color: #fffacd;
                    font-style: italic;
                    text-align: center;
                    max-width: 100%;
                    margin-left: auto;
                    margin-right: auto;
                }

                .message.system .timestamp {
                    text-align: center;
                }


                .input-area {
                    display: flex;
                    padding: 10px;
                    border-top: 1px solid #ddd;
                    background-color: #f9f9f9;
                }

                #messageInput {
                    flex-grow: 1;
                    padding: 10px;
                    border: 1px solid #ccc;
                    border-radius: 20px;
                    margin-right: 10px;
                }

                /* Thêm input cho file upload, tạm thời ẩn đi */
                #fileInput {
                    display: none;
                }

                #uploadButton {
                    /* Kiểu dáng cho nút upload, ví dụ: paperclip icon */
                    padding: 10px;
                    background-color: #6c757d;
                    color: white;
                    border: none;
                    border-radius: 50%;
                    cursor: pointer;
                    margin-right: 10px;
                    width: 40px;
                    height: 40px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                #uploadButton:hover {
                    background-color: #5a6268;
                }

                #sendButton {
                    padding: 10px 15px;
                    background-color: #007bff;
                    color: white;
                    border: none;
                    border-radius: 20px;
                    cursor: pointer;
                    min-width: 70px;
                }

                #sendButton:hover {
                    background-color: #0056b3;
                }

                #sendButton:disabled {
                    background-color: #ccc;
                    cursor: not-allowed;
                }
            </style>
        </head>

        <body>
            <div class="top-bar">
                <c:set var="loggedInUsername" value="${sessionScope.loggedInUser}" />
                <span class="welcome-user">Welcome,
                    <c:out value="${sessionScope.displayName}" />!
                </span>
                <a class="logout-link" href="${pageContext.request.contextPath}/logout">Logout</a>
            </div>

            <div class="chat-container">
                <div id="user-list-container">

                    <h3>Online Users</h3>
                    <ul id="user-list">
                        <%-- User list populated by JavaScript --%>
                    </ul>
                </div>
                <div id="chat-area">
                    <div id="chat-header">
                        Chatting with: <span id="current-chat-partner">No one selected</span>
                    </div>
                    <div id="messages">
                        <%-- Messages appended here --%>
                    </div>
                    <div class="input-area">
                        <input type="file" id="fileInput" accept="image/*,video/*"> <%-- Thêm accept filter --%>
                            <button id="uploadButton" title="Send file">&#128206;</button> <input type="text"
                                id="messageInput" placeholder="Type a message..." autocomplete="off" disabled>
                            <button id="sendButton" disabled>Send</button>
                    </div>
                </div>
            </div>

            <script type="text/javascript">
                const loggedInUsername = "<c:out value='${loggedInUsername}'/>";
                const CONTEXT_PATH = "<c:out value='${pageContext.request.contextPath}'/>";
                if (!loggedInUsername) {
                    console.error("Logged in username not found.");
                    // Redirect or show error
                }

                const messagesDiv = document.getElementById('messages');
                const messageInput = document.getElementById('messageInput');
                const sendButton = document.getElementById('sendButton');
                const userListUl = document.getElementById('user-list');
                const currentChatPartnerSpan = document.getElementById('current-chat-partner');
                const chatHeaderDiv = document.getElementById('chat-header');
                const fileInput = document.getElementById('fileInput');
                const uploadButton = document.getElementById('uploadButton');

                const wsProtocol = document.location.protocol === "https:" ? "wss:" : "ws:";

                const loggedInUsernameFromSession = "${sessionScope.loggedInUser}"; // Lấy trực tiếp từ sessionScope
                const loggedInUsernameForJS = "<c:out value='${sessionScope.loggedInUser}'/>"; // Dùng c:out để an toàn

                const hostFromServer = document.location.host; // Lấy từ trình duyệt
                const contextPathFromJSP = "${pageContext.request.contextPath}"; // Lấy từ JSP EL

                console.log("--- DEBUG GHÉP CHUỖI ---");
                console.log("Phần 1 (protocol):", wsProtocol + "//");
                console.log("Phần 2 (host):", hostFromServer);
                console.log("Phần 3 (context):", contextPathFromJSP);
                console.log("Phần 4 (endpoint base):", "/chat/");
                console.log("Phần 5 (username):", loggedInUsernameForJS);

                const part1 = wsProtocol + "//";
                const part2 = hostFromServer;
                const part3 = contextPathFromJSP;
                const part4 = "/chat/";
                const part5 = loggedInUsernameForJS;

                const wsUrl = part1 + part2 + part3 + part4 + part5;
                console.log("DEBUG: testWsUrl ghép thủ công:", wsUrl);
                // So sánh testWsUrl với wsUrl được tạo bằng template string.
                let socket;
                let currentChatTargetUsername = null; // Lưu username của người đang chat cùng

                function connect() {
                    // ... (giữ nguyên hàm connect từ Bước 15, chỉ cần đảm bảo nó gọi các hàm xử lý message mới)
                    if (!loggedInUsername) {
                        console.error("Cannot connect WebSocket without a username.");
                        return;
                    }
                    socket = new WebSocket(wsUrl);

                    socket.onopen = function (event) {
                        console.log("WebSocket connection established to: " + wsUrl);
                        appendSystemMessage("Connected to chat server.");
                    };

                    socket.onmessage = function (event) {
                        const rawMessageData = event.data;
                        console.log("Raw message from server: ", rawMessageData);
                        try {
                            const messageData = JSON.parse(rawMessageData);

                            if (messageData.type === "userlist") {
                                updateUserList(messageData.users);
                            } else if (messageData.type === "newMessage" || messageData.type === "messageSentAck") {
                                // Chỉ hiển thị nếu tin nhắn liên quan đến cuộc chat hiện tại
                                if ((messageData.from === loggedInUsername && messageData.to === currentChatTargetUsername) ||
                                    (messageData.from === currentChatTargetUsername && messageData.to === loggedInUsername)) {
                                    appendChatMessage(messageData);
                                }
                                // Nếu là messageSentAck và from là mình, có thể cập nhật trạng thái tin nhắn (ví dụ: đã gửi)
                                if (messageData.type === "messageSentAck" && messageData.from === loggedInUsername) {
                                    console.log("Message sent and acknowledged by server:", messageData.id);
                                }

                            } else if (messageData.type === "connection_ack") {
                                console.log("Server Acknowledgment:", messageData.message);
                            } else if (messageData.type === "error") {
                                console.error("Server error message:", messageData.message);
                                appendSystemMessage("Server error: " + messageData.message);
                            } else {
                                console.warn("Received unhandled message type or format:", messageData);
                                // appendSystemMessage(rawMessageData); // Hiển thị nguyên văn nếu không xử lý được
                            }
                        } catch (e) {
                            console.error("Error parsing message from server or unhandled raw message: ", e, rawMessageData);
                            // appendSystemMessage(rawMessageData); // Hiển thị tin nhắn không phải JSON (nếu có)
                        }
                    };

                    socket.onclose = function (event) {
                        console.log("WebSocket connection closed. Code: " + event.code + ", Reason: " + event.reason);
                        appendSystemMessage("Disconnected. Attempting to reconnect...");
                        // setTimeout(connect, 5000); // Tùy chọn tự động kết nối lại
                    };

                    socket.onerror = function (error) {
                        console.error("WebSocket Error: ", error);
                        appendSystemMessage("Error connecting to chat server.");
                    };
                }

                connect(); // Khởi tạo kết nối

                function updateUserList(users) {
                    userListUl.innerHTML = ''; // Xóa danh sách cũ
                    users.forEach(function (user) {
                        if (user === loggedInUsername) return; // Không hiển thị chính mình trong danh sách chat

                        const listItem = document.createElement('li');
                        listItem.textContent = user;
                        listItem.dataset.username = user; // Lưu username vào data attribute

                        if (user === currentChatTargetUsername) {
                            listItem.classList.add('active-chat');
                        }

                        listItem.onclick = function () {
                            selectChatPartner(user);
                        };
                        userListUl.appendChild(listItem);
                    });
                }

                function selectChatPartner(username) {
                    if (currentChatTargetUsername === username) return; // Đã chọn rồi

                    currentChatTargetUsername = username;
                    currentChatPartnerSpan.textContent = username;
                    messagesDiv.innerHTML = ''; // Xóa tin nhắn của cuộc chat cũ
                    messageInput.disabled = false; // Bật ô nhập liệu
                    sendButton.disabled = false;   // Bật nút gửi
                    uploadButton.disabled = false; // Bật nút upload (sẽ thêm sau)


                    // Highlight người dùng đang được chọn trong danh sách
                    document.querySelectorAll('#user-list li').forEach(li => {
                        if (li.dataset.username === username) {
                            li.classList.add('active-chat');
                        } else {
                            li.classList.remove('active-chat');
                        }
                    });

                    // TODO SAU: Gọi hàm tải lịch sử chat cho (loggedInUsername, currentChatTargetUsername)
                    // loadChatHistory(loggedInUsername, currentChatTargetUsername);
                    appendSystemMessage(`Chatting with ${username}`);
                }

                sendButton.onclick = function () {
                    sendTextMessage();
                };

                messageInput.onkeypress = function (event) {
                    if (event.key === 'Enter' && !event.shiftKey) { // Gửi khi nhấn Enter (không phải Shift+Enter)
                        event.preventDefault();
                        sendTextMessage();
                    }
                };

                uploadButton.onclick = function () {
                    if (!currentChatTargetUsername) {
                        appendSystemMessage("Please select a user to chat with before sending a file.");
                        return;
                    }
                    fileInput.click(); // Mở hộp thoại chọn file
                };

                fileInput.onchange = function (event) {
                    const file = event.target.files[0];
                    if (file && currentChatTargetUsername) {
                        const caption = messageInput.value.trim(); // Lấy caption từ ô message input
                        uploadAndSendFile(file, caption, currentChatTargetUsername);
                        fileInput.value = null; // Reset file input để có thể chọn lại cùng file
                        // messageInput.value = ''; // Xóa caption sau khi đã lấy (tùy chọn)
                    } else if (!currentChatTargetUsername && file) {
                        appendSystemMessage("Please select a user to chat with before sending a file.");
                        fileInput.value = null;
                    }
                    // Nếu không có file được chọn (người dùng nhấn cancel), không làm gì cả.
                };

                async function uploadAndSendFile(file, caption, recipientUsername) {
                    if (!socket || socket.readyState !== WebSocket.OPEN) {
                        appendSystemMessage("Not connected to server. Cannot send file.");
                        return;
                    }

                    appendSystemMessage(`Uploading ${file.name}...`); // Thông báo đang upload

                    const formData = new FormData();
                    formData.append("fileToUpload", file); // "fileToUpload" phải khớp với request.getPart("fileToUpload") trong Servlet

                    try {
                        const response = await fetch("${pageContext.request.contextPath}/uploadFile", {
                            method: 'POST',
                            body: formData
                            // Không cần set Content-Type header, trình duyệt sẽ tự động làm với FormData
                        });

                        const result = await response.json(); // Đọc response dưới dạng JSON

                        if (response.ok && result.success) {
                            const mediaUrl = result.fileUrl;
                            let messageType = "FILE"; // Mặc định

                            if (file.type.startsWith("image/")) {
                                messageType = "IMAGE";
                            } else if (file.type.startsWith("video/")) {
                                messageType = "VIDEO";
                            }
                            // Bạn có thể thêm các loại file khác ở đây

                            const messageObject = {
                                to: recipientUsername,
                                messageType: messageType,
                                mediaUrl: mediaUrl,
                                content: caption // Caption có thể rỗng
                            };
                            socket.send(JSON.stringify(messageObject));
                            appendSystemMessage(`${file.name} sent successfully.`);
                            if (caption) messageInput.value = ''; // Xóa caption nếu nó đã được gửi

                        } else {
                            console.error("File upload failed on server:", result.message);
                            appendSystemMessage(`Failed to upload ${file.name}: ${result.message || 'Server error'}`);
                        }
                    } catch (error) {
                        console.error("Error during file upload:", error);
                        appendSystemMessage(`Error uploading ${file.name}: ${error.message}`);
                    }
                }


                function sendTextMessage() {
                    if (!socket || socket.readyState !== WebSocket.OPEN) {
                        appendSystemMessage("Not connected to server.");
                        return;
                    }
                    if (!currentChatTargetUsername) {
                        // Đã có disable nút send, nhưng vẫn check lại cho chắc
                        appendSystemMessage("Please select a user to chat with.");
                        return;
                    }

                    const textContent = messageInput.value.trim();
                    if (textContent) {
                        const messageObject = {
                            to: currentChatTargetUsername,
                            messageType: "TEXT",
                            content: textContent
                        };
                        socket.send(JSON.stringify(messageObject));
                        messageInput.value = '';
                    }
                }

                function appendSystemMessage(message) {
                    const msgWrapper = document.createElement('div');
                    msgWrapper.classList.add('message-wrapper');

                    const messageElement = document.createElement('div');
                    messageElement.classList.add('message', 'system');
                    messageElement.textContent = message;

                    msgWrapper.appendChild(messageElement);
                    messagesDiv.appendChild(msgWrapper);
                    messagesDiv.scrollTop = messagesDiv.scrollHeight;
                }

                function appendChatMessage(msgData) { // msgData là object JSON đã parse từ server
                    const msgWrapper = document.createElement('div');
                    msgWrapper.classList.add('message-wrapper');

                    const messageElement = document.createElement('div');
                    messageElement.classList.add('message');

                    // Tạo nội dung tin nhắn (text, image, video)
                    if (msgData.messageType === "TEXT") {
                        // Xử lý xuống dòng cho text content
                        if (msgData.content) {
                            msgData.content.split('\\n').forEach((line, index, arr) => {
                                messageElement.appendChild(document.createTextNode(line));
                                if (index < arr.length - 1) {
                                    messageElement.appendChild(document.createElement('br'));
                                }
                            });
                        }
                    } else if (msgData.messageType === "IMAGE" && msgData.mediaUrl) {
                        const img = document.createElement('img');
                        img.src = msgData.mediaUrl;
                        img.style.maxWidth = '100%'; // Hoặc kích thước cụ thể
                        img.style.borderRadius = '10px';
                        img.alt = msgData.content || "Image"; // Caption hoặc text thay thế
                        messageElement.appendChild(img);
                        if (msgData.content) { // Hiển thị caption nếu có
                            const caption = document.createElement('div');
                            caption.style.marginTop = '5px';
                            caption.style.fontSize = '0.85em';
                            caption.textContent = msgData.content;
                            messageElement.appendChild(caption);
                        }
                    } else if (msgData.messageType === "VIDEO" && msgData.mediaUrl) {
                        const video = document.createElement('video');
                        video.src = msgData.mediaUrl;
                        video.controls = true;
                        video.style.maxWidth = '100%';
                        video.style.borderRadius = '10px';
                        messageElement.appendChild(video);
                        if (msgData.content) { // Hiển thị caption nếu có
                            const caption = document.createElement('div');
                            caption.style.marginTop = '5px';
                            caption.style.fontSize = '0.85em';
                            caption.textContent = msgData.content;
                            messageElement.appendChild(caption);
                        }
                    } else if (msgData.messageType === "FILE" && msgData.mediaUrl) {
                        const link = document.createElement('a');
                        link.href = msgData.mediaUrl;
                        link.textContent = msgData.content || "Download file";
                        link.target = "_blank";
                        messageElement.appendChild(link);
                    } else {
                        messageElement.textContent = msgData.content || "[Unsupported message type]";
                    }


                    // Thêm timestamp
                    if (msgData.timestamp) {
                        const timestampSpan = document.createElement('span');
                        timestampSpan.classList.add('timestamp');
                        try {
                            // Định dạng lại thời gian cho dễ đọc hơn
                            timestampSpan.textContent = new Date(msgData.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
                        } catch (e) {
                            timestampSpan.textContent = msgData.timestamp; // Để nguyên nếu không parse được
                        }
                        messageElement.appendChild(timestampSpan);
                    }

                    // Xác định tin nhắn gửi đi hay nhận được
                    if (msgData.from === loggedInUsername) {
                        msgWrapper.classList.add('sent');
                    } else {
                        msgWrapper.classList.add('received');
                    }

                    msgWrapper.appendChild(messageElement);
                    messagesDiv.appendChild(msgWrapper);
                    messagesDiv.scrollTop = messagesDiv.scrollHeight; // Tự động cuộn xuống cuối
                }

                function selectChatPartner(username) {
                    if (currentChatTargetUsername === username) return;

                    currentChatTargetUsername = username;
                    currentChatPartnerSpan.textContent = username;
                    messagesDiv.innerHTML = ''; // Xóa tin nhắn của cuộc chat cũ
                    messageInput.disabled = false;
                    sendButton.disabled = false;
                    uploadButton.disabled = false;


                    document.querySelectorAll('#user-list li').forEach(li => {
                        if (li.dataset.username === username) {
                            li.classList.add('active-chat');
                        } else {
                            li.classList.remove('active-chat');
                        }
                    });

                    appendSystemMessage(`Opening chat with ${username}...`);
                    // Gọi hàm tải lịch sử chat
                    loadChatHistory(username);
                }




                async function loadChatHistory(partnerUsername) {
                    if (!loggedInUsername || !partnerUsername) {
                        console.log("loadChatHistory: Missing loggedInUsername or partnerUsername");
                        return;
                    }
                    if (CONTEXT_PATH === null || typeof CONTEXT_PATH === 'undefined') { // Kiểm tra lại
                        console.error("loadChatHistory: CONTEXT_PATH is not available!");
                        appendSystemMessage("Error: Application context path not found for history request.");
                        return;
                    }


                    const page = 0;
                    const pageSize = 20;
                    appendSystemMessage(`Loading history with ${partnerUsername}...`);

                    try {
                        console.log("ConTEXT_PATH:", CONTEXT_PATH); // Log CONTEXT_PATH để kiểm tra
                        console.log("Logged in username:", loggedInUsername);
                        console.log("Partner username:", partnerUsername);
                        console.log("Page:", page, "Page size:", pageSize);
                        const params = new URLSearchParams({
                            partnerUsername: partnerUsername,
                            page: page,
                            pageSize: pageSize
                        });
                        console.log("typeof CONTEXT_PATH:", typeof CONTEXT_PATH);
                        console.log("CONTEXT_PATH actual value:", CONTEXT_PATH);

                        console.log("Query parameters for history request:", params.toString());
                        const historyUrl = CONTEXT_PATH + "/chatHistory?" + params.toString();
                        console.log("Requesting chat history from:", historyUrl); // Log URL sẽ gọi
                        const response = await fetch(historyUrl);
                        // -----------------------------------------------

                        if (!response.ok) {
                            const errorData = await response.json().catch(() => ({ message: "Failed to load chat history. Status: " + response.status }));
                            console.error("Error loading chat history:", errorData);
                            appendSystemMessage(`Error loading history: ${errorData.message || errorData.error || 'Unknown error'}`);
                            return;
                        }

                        const historyMessages = await response.json();
                        console.log("Chat history received:", historyMessages);

                        if (historyMessages && historyMessages.length > 0) {
                            historyMessages.reverse().forEach(msgData => {
                                const clientMsgData = {
                                    id: msgData.id,
                                    from: msgData.sender.username,
                                    to: msgData.receiver.username,
                                    messageType: msgData.messageType.toString(),
                                    content: msgData.content,
                                    mediaUrl: msgData.mediaUrl,
                                    timestamp: msgData.timestamp
                                };
                                appendChatMessage(clientMsgData);
                            });
                            messagesDiv.scrollTop = messagesDiv.scrollHeight;
                        } else {
                            appendSystemMessage(`No chat history with ${partnerUsername}.`);
                        }

                    } catch (error) {
                        console.error("Failed to fetch chat history:", error);
                        appendSystemMessage("Could not load chat history. Network error or server issue.");
                    }
                }

            </script>
        </body>

        </html>
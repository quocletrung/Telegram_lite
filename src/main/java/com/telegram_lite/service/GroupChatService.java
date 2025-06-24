package com.telegram_lite.service;

import com.telegram_lite.dao.*;
import com.telegram_lite.entity.ChatGroup;
import com.telegram_lite.entity.Message;
import com.telegram_lite.entity.MessageType;
import com.telegram_lite.entity.User;

import java.util.List;
import java.util.Optional;

public class GroupChatService {

    private final UserDao userDao = new UserDaoImpl();
    private final ChatGroupDao chatGroupDao = new ChatGroupDaoImpl();
    private final MessageDao messageDao = new MessageDaoImpl();

    /**
     * Tạo một nhóm chat mới.
     *
     * @param groupName Tên của nhóm.
     * @param creatorUsername Username của người tạo nhóm.
     * @param memberUsernames Danh sách username của các thành viên được thêm vào.
     * @return Nhóm chat đã được tạo hoặc null nếu có lỗi.
     */
    public ChatGroup createGroup(String groupName, String creatorUsername, List<String> memberUsernames) {
        // 1. Tìm người dùng tạo nhóm
        Optional<User> creatorOpt = userDao.findUserByUsername(creatorUsername);
        if (creatorOpt.isEmpty()) {
            System.err.println("Người tạo nhóm không tồn tại: " + creatorUsername);
            return null; // Không tìm thấy người tạo
        }
        User creator = creatorOpt.get();

        // 2. Tạo đối tượng ChatGroup mới
        ChatGroup newGroup = new ChatGroup();
        newGroup.setGroupName(groupName);
        newGroup.setCreator(creator);

        // 3. Thêm người tạo vào danh sách thành viên
        newGroup.addMember(creator);

        // 4. Tìm và thêm các thành viên khác
        for (String memberUsername : memberUsernames) {
            Optional<User> memberOpt = userDao.findUserByUsername(memberUsername);
            memberOpt.ifPresent(newGroup::addMember); // Chỉ thêm nếu user tồn tại
        }

        // 5. Lưu nhóm vào database
        return chatGroupDao.save(newGroup);
    }

    /**
     * Lưu một tin nhắn được gửi đến nhóm.
     *
     * @param senderUsername Username người gửi.
     * @param groupId ID của nhóm nhận.
     * @param content Nội dung tin nhắn.
     * @param messageType Loại tin nhắn (TEXT, IMAGE, VIDEO).
     * @param mediaUrl URL của file media (nếu có).
     * @return Tin nhắn đã được lưu.
     */
    public Optional<Message> saveGroupMessage(String senderUsername, Long groupId, String content, MessageType messageType, String mediaUrl) {
        Optional<User> senderOpt = userDao.findUserByUsername(senderUsername);
        Optional<ChatGroup> groupOpt = chatGroupDao.findById(groupId);

        // Chỉ lưu tin nhắn nếu cả người gửi và nhóm đều tồn tại
        if (senderOpt.isPresent() && groupOpt.isPresent()) {
            User sender = senderOpt.get();
            ChatGroup group = groupOpt.get();

            Message message = new Message(sender, group, messageType, content, mediaUrl);
            messageDao.saveMessage(message);
            return Optional.of(message);
        }
        return Optional.empty();
    }

    /**
     * Lấy lịch sử tin nhắn của một nhóm.
     * @param groupId ID của nhóm.
     * @return Danh sách tin nhắn.
     */
    public List<Message> getGroupHistory(Long groupId) {
        return messageDao.getMessagesForGroup(groupId);
    }

    /**
     * Lấy danh sách các nhóm mà một người dùng tham gia.
     * @param username Username của người dùng.
     * @return Danh sách các ChatGroup.
     */
    public List<ChatGroup> findGroupsForUser(String username) {
        Optional<User> userOpt = userDao.findUserByUsername(username);
        if (userOpt.isPresent()) {
            return chatGroupDao.findGroupsByUserId(userOpt.get().getId());
        }
        return List.of(); // Trả về danh sách rỗng nếu không tìm thấy user
    }

    /**
     * Tìm một nhóm bằng ID.
     * @param groupId ID của nhóm.
     * @return Optional chứa ChatGroup nếu tìm thấy.
     */
    public Optional<ChatGroup> findGroupById(Long groupId) {
        return chatGroupDao.findById(groupId);
    }
}
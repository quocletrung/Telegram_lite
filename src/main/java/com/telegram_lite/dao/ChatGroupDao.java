package com.telegram_lite.dao;

import com.telegram_lite.entity.ChatGroup;
import java.util.List;
import java.util.Optional;

public interface ChatGroupDao {

    /**
     * Lưu hoặc cập nhật một nhóm chat.
     * @param group Nhóm chat cần lưu.
     * @return Nhóm chat đã được lưu.
     */
    ChatGroup save(ChatGroup group);

    /**
     * Tìm một nhóm chat bằng ID.
     * @param groupId ID của nhóm.
     * @return Optional chứa nhóm nếu tìm thấy, ngược lại là Optional rỗng.
     */
    Optional<ChatGroup> findById(Long groupId);

    /**
     * Tìm tất cả các nhóm mà một người dùng là thành viên.
     * @param userId ID của người dùng.
     * @return Danh sách các nhóm chat.
     */
    List<ChatGroup> findGroupsByUserId(Long userId);

}
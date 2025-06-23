package com.telegram_lite.dto;

public class UserDto {
    private String username;
    private String displayName;
    // Bạn có thể thêm avatarUrl nếu muốn hiển thị ở danh sách
    // private String avatarUrl;

    public UserDto(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }

    // Getters (và Setters nếu cần)
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    // public String getAvatarUrl() { return avatarUrl; }
}
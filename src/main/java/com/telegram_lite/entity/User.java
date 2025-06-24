package com.telegram_lite.entity; // Thay com.yourproject bằng package của bạn

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet; // Sẽ dùng sau cho mối quan hệ
import java.util.Set;    // Sẽ dùng sau cho mối quan hệ

@Entity
@Table(name = "users", uniqueConstraints = { // Đặt tên bảng là "users"
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID tự tăng, phù hợp với MySQL
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false) // Mật khẩu đã được hash
    private String password;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(nullable = true, length = 100) // Email có thể không bắt buộc ban đầu
    private String email;

    @Column(name = "avatar_url", nullable = true)
    private String avatarUrl; // Đường dẫn tới ảnh đại diện

    @Column(name = "status", length = 20)
    private String status; // Ví dụ: ONLINE, OFFLINE, BUSY

    @CreationTimestamp // Tự động gán thời gian khi tạo mới
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Tự động gán thời gian khi cập nhật
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @ManyToMany(mappedBy = "members")
    private Set<ChatGroup> groups = new HashSet<>();

    // Thêm getter và setter cho `groups`
    public Set<ChatGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<ChatGroup> groups) {
        this.groups = groups;
    }

    // Constructors
    public User() {
    }

    public User(String username, String password, String displayName, String email) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // (Tùy chọn) toString, equals, hashCode
    // Bạn có thể tự động generate các phương thức này bằng IDE

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
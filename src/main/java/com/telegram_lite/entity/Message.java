package com.telegram_lite.entity;

import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = true) // << THAY ĐỔI: nullable = true
    private User receiver;

    @Enumerated(EnumType.STRING) // Lưu trữ Enum dưới dạng String trong DB
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(columnDefinition = "TEXT") // Vẫn dùng TEXT cho content, có thể là caption cho media
    private String content; // Đối với media, đây có thể là caption hoặc null

    @Column(name = "media_url", length = 1024) // Đường dẫn tới file media
    private String mediaUrl; // Sẽ là NULL nếu messageType là TEXT

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = true)
    private ChatGroup receiverGroup;
    // Constructors
    public Message() {
    }

    public Message(User sender, User receiver, MessageType messageType, String mediaUrl, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageType = messageType;
        this.mediaUrl = mediaUrl;
        this.content = content;
    }

    // >>> THÊM CONSTRUCTOR MỚI CHO TIN NHẮN NHÓM <<<
    public Message(User sender, ChatGroup receiverGroup, MessageType messageType, String content, String mediaUrl) {
        this.sender = sender;
        this.receiverGroup = receiverGroup;
        this.messageType = messageType;
        this.content = content;
        this.mediaUrl = mediaUrl;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ChatGroup getReceiverGroup() {
        return receiverGroup;
    }

    public void setReceiverGroup(ChatGroup receiverGroup) {
        this.receiverGroup = receiverGroup;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender=" + (sender != null ? sender.getUsername() : "null") +
                ", receiver=" + (receiver != null ? receiver.getUsername() : "null") +
                ", messageType=" + messageType +
                ", content='" + content + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                '}';
    }
}
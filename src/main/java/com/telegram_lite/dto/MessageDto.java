package com.telegram_lite.dto;

import java.time.LocalDateTime;

public class MessageDto {
    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private String messageType;
    private String mediaUrl;
    private LocalDateTime timestamp;

    public MessageDto(Long id, String sender, String receiver, String content, String messageType, String mediaUrl, LocalDateTime timestamp) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.messageType = messageType;
        this.mediaUrl = mediaUrl;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getContent() { return content; }
    public String getMessageType() { return messageType; }
    public String getMediaUrl() { return mediaUrl; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setId(Long id) { this.id = id; }
    public void setSender(String sender) { this.sender = sender; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public void setContent(String content) { this.content = content; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
} 
package com.telegram_lite.dto;

import java.util.List;

public class GroupDto {
    private Long id;
    private String groupName;
    private List<String> members; // Chỉ cần username của các thành viên

    public GroupDto(Long id, String groupName, List<String> members) {
        this.id = id;
        this.groupName = groupName;
        this.members = members;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
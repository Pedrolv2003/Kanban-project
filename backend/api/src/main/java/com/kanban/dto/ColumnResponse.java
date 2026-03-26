package com.kanban.dto;

public class ColumnResponse {
    private String status;
    private String title;
    private String color;
    private String icon;
    private Integer orderIndex;
    
    public ColumnResponse(String status, String title, String color, String icon, Integer orderIndex) {
        this.status = status;
        this.title = title;
        this.color = color;
        this.icon = icon;
        this.orderIndex = orderIndex;
    }
    
    // Getters e Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}
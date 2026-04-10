package com.example.energygymapp.domain.model;

public class ScheduleItem {
    private String id;
    private String scheduleItemName;
    private String startTime;
    private String endTime;
    private String location;
    private String day;

    public ScheduleItem() {
    }

    public ScheduleItem(String id, String scheduleItemName, String startTime,
                        String endTime, String location, String day) {
        this.id = id;
        this.scheduleItemName = scheduleItemName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.day = day;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScheduleItemName() {
        return scheduleItemName;
    }

    public void setScheduleItemName(String scheduleItemName) {
        this.scheduleItemName = scheduleItemName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}

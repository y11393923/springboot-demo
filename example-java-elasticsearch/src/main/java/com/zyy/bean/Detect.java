
package com.zyy.bean;

import java.io.Serializable;

public class Detect implements Serializable {
    private String serial;
    private Long capturedTime;
    private Long receivedTime;
    private String multiSuSource;
    private String taskId;
    private int isAlarm;
    private String eventId;
    private String taskName;
    private long eventTime;
    private long status;
    private String scene;
    private long occurTime;

    public Detect() {
    }

    public long getOccurTime() {
        return this.occurTime;
    }

    public void setOccurTime(long occurTime) {
        this.occurTime = occurTime;
    }

    public String getScene() {
        return this.scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public long getStatus() {
        return this.status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public long getEventTime() {
        return this.eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getIsAlarm() {
        return this.isAlarm;
    }

    public void setIsAlarm(int isAlarm) {
        this.isAlarm = isAlarm;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    public String getMultiSuSource() {
        return this.multiSuSource;
    }

    public void setMultiSuSource(String multiSuSource) {
        this.multiSuSource = multiSuSource;
    }

    public Long getCapturedTime() {
        return this.capturedTime;
    }

    public void setCapturedTime(Long capturedTime) {
        this.capturedTime = capturedTime;
    }

    public Long getReceivedTime() {
        return this.receivedTime;
    }

    public void setReceivedTime(Long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getSerial() {
        return this.serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    @Override
    public String toString() {
        return "Detect{" +
                "serial='" + serial + '\'' +
                ", capturedTime=" + capturedTime +
                ", receivedTime=" + receivedTime +
                ", multiSuSource='" + multiSuSource + '\'' +
                ", taskId='" + taskId + '\'' +
                ", isAlarm=" + isAlarm +
                ", eventId='" + eventId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", eventTime=" + eventTime +
                ", status=" + status +
                ", scene='" + scene + '\'' +
                ", occurTime=" + occurTime +
                '}';
    }
}

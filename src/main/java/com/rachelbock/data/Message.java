package com.rachelbock.data;

import java.util.Date;

/**
 * Class to hold Message data
 */
public class Message {

    private int messageId;
    private String recepName;
    private String notes;
    private Date messageDate;
    private String claimedBy;
    private String claimedDate;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getRecepName() {
        return recepName;
    }

    public void setRecepName(String recepName) {
        this.recepName = recepName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(String claimedBy) {
        this.claimedBy = claimedBy;
    }

    public String getClaimedDate() {
        return claimedDate;
    }

    public void setClaimedDate(String claimedDate) {
        this.claimedDate = claimedDate;
    }
}

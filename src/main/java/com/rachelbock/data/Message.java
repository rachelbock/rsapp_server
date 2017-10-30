package com.rachelbock.data;

/**
 * Class to hold Message data
 */
public class Message {

    protected int climbId;
    protected int gymRating;
    protected int userRating;
    protected String tapeColor;
    protected int wallId;
    protected boolean isProject;
    protected boolean isCompleted;
    protected String type;
    protected boolean removed;

    public int getClimbId() {
        return climbId;
    }

    public void setClimbId(int id) {
        this.climbId = id;
    }

    public int getGymRating() {
        return gymRating;
    }

    public void setGymRating(int gymRating) {
        this.gymRating = gymRating;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }

    public String getTapeColor() {
        return tapeColor;
    }

    public void setTapeColor(String tapeColor) {
        this.tapeColor = tapeColor;
    }

    public int getWallId() {
        return wallId;
    }

    public void setWallId(int wallId) {
        this.wallId = wallId;
    }
    public boolean isProject() {
        return isProject;
    }

    public void setProject(boolean project) {
        isProject = project;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}

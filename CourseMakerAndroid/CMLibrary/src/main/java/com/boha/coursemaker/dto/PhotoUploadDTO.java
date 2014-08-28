package com.boha.coursemaker.dto;

public class PhotoUploadDTO {
	private Integer  traineeID, companyID, instructorID, authorID, administratorID;
    private int numberOfImages;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getTraineeID() {
        return traineeID;
    }

    public void setTraineeID(Integer traineeID) {
        this.traineeID = traineeID;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(Integer instructorID) {
        this.instructorID = instructorID;
    }

    public Integer getAuthorID() {
        return authorID;
    }

    public void setAuthorID(Integer authorID) {
        this.authorID = authorID;
    }

    public Integer getAdministratorID() {
        return administratorID;
    }

    public void setAdministratorID(Integer administratorID) {
        this.administratorID = administratorID;
    }

   

    public int getNumberOfImages() {
        return numberOfImages;
    }

    public void setNumberOfImages(int numberOfImages) {
        this.numberOfImages = numberOfImages;
    }

   public static final int INSTRUCTOR = 1, TRAINEE = 2, ADMINISTRATOR = 3, AUTHOR = 4;}
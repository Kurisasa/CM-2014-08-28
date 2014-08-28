package com.boha.coursemaker.dto;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/08/02.
 */
public class TraineeSkillDTO implements Serializable {
    private int traineeSkillID, traineeID, instructorID, skillID, skillLevelID, level;
    private long dateAssessed;
    private String skillName, skillLevelName;

    public int getTraineeSkillID() {
        return traineeSkillID;
    }

    public void setTraineeSkillID(int traineeSkillID) {
        this.traineeSkillID = traineeSkillID;
    }

    public int getTraineeID() {
        return traineeID;
    }

    public void setTraineeID(int traineeID) {
        this.traineeID = traineeID;
    }

    public int getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(int instructorID) {
        this.instructorID = instructorID;
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public int getSkillLevelID() {
        return skillLevelID;
    }

    public void setSkillLevelID(int skillLevelID) {
        this.skillLevelID = skillLevelID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getDateAssessed() {
        return dateAssessed;
    }

    public void setDateAssessed(long dateAssessed) {
        this.dateAssessed = dateAssessed;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillLevelName() {
        return skillLevelName;
    }

    public void setSkillLevelName(String skillLevelName) {
        this.skillLevelName = skillLevelName;
    }

}

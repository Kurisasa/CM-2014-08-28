package com.boha.coursemaker.dto;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/08/02.
 */
public class SkillLevelDTO implements Serializable{
    private int skillLevelID, level, companyID;
    private String skillLevelName;

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
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

    public String getSkillLevelName() {
        return skillLevelName;
    }

    public void setSkillLevelName(String skillLevelName) {
        this.skillLevelName = skillLevelName;
    }

}

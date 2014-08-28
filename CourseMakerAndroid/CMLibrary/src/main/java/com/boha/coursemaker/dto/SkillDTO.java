package com.boha.coursemaker.dto;

import java.io.Serializable;

/**
 * Created by aubreyM on 2014/08/02.
 */
public class SkillDTO implements Serializable{
    private int skillID, companyID;
    private String skillName;
    boolean selected;
    private SkillLevelDTO skillLevel;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public SkillLevelDTO getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(SkillLevelDTO skillLevel) {
        this.skillLevel = skillLevel;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}

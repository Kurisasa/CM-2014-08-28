package com.boha.coursemaker.dto;

import java.io.Serializable;


/**
 *
 * @author Aubrey Malabie
 */
public class ReminderDTO  implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ReminderDTO () {
		
	}
public ReminderDTO (int minutes, String method) {
		this.minutes = minutes;
		this.method = method;
	}
	private int minutes, days, hours;
    private String method;
    private long reminderKeyID, eventKeyID;

    public long getEventKeyID() {
		return eventKeyID;
	}
	public void setEventKeyID(long eventKeyID) {
		this.eventKeyID = eventKeyID;
	}
	public long getReminderKeyID() {
		return reminderKeyID;
	}
	public void setReminderKeyID(long reminderKeyID) {
		this.reminderKeyID = reminderKeyID;
	}
	
	public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        if (minutes > 59) {
            if (minutes >= DAY) {
                days = minutes / DAY;
            } else {
                hours = minutes / HOUR;
            }
            return;
        }
        this.minutes = minutes;
    }
    private static final int HOUR = 60;
    private static final int DAY = 60 * 24;
    public static final String METHOD_EMAIL = "EMAIL";
    public static final String METHOD_ALERT = "ALERT";
}
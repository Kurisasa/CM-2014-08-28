package com.boha.coursemaker.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Aubrey Malabie
 */
public class RecurrenceDTO  implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String frequency;
    ArrayList<String> byDayList = new ArrayList<String>();
    ArrayList<Integer> byDayListInt = new ArrayList<Integer>();
    int count, index, month, byMonthDay, interval, frequencyInt, startHour, startMinute, endHour, endMinute;
    long untilDate;

    public ArrayList<String> getByDayList() {
        return byDayList;
    }

    public void setByDayListIntegers() {
      
        for (Iterator<String> it = byDayList.iterator(); it.hasNext();) {
            String day = it.next();
            if (day.equals("MO")) {
                byDayListInt.add(1);
                break;
            }
            if (day.equals("TU")) {
                byDayListInt.add(2);
                break;
            }
            if (day.equals("WE")) {
                byDayListInt.add(3);
                break;
            }
            if (day.equals("TH")) {
                byDayListInt.add(4);
                break;
            }
            if (day.equals("FR")) {
                byDayListInt.add(5);
                break;
            }
            if (day.equals("SA")) {
                byDayListInt.add(6);
                break;
            }
            if (day.equals("SU")) {
                byDayListInt.add(7);
                break;
            }


        }

    }
public static final String MONDAY = "MO";
public static final String TUESDAY = "TU";
public static final String WEDNESDAY = "WE";
public static final String THURSDAY = "TH";
public static final String FRIDAY = "FR";
public static final String SATURDAY = "SA";
public static final String SUNDAY = "SU";


    public int getIndex() {
	return index;
}

public void setIndex(int index) {
	this.index = index;
}

	public void setByDayList(ArrayList<String> byDayList) {
		this.byDayList = byDayList;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getByMonthDay() {
        return byMonthDay;
    }

    public void setByMonthDay(int byMonthDay) {
        this.byMonthDay = byMonthDay;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int getFrequencyInt() {
        return frequencyInt;
    }

    public void setFrequencyInt(int frequencyInt) {
        this.frequencyInt = frequencyInt;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
        if (frequency.equalsIgnoreCase(FREQ_YEARLY)) {
            frequencyInt = YEARLY;
            return;
        }
        if (frequency.equalsIgnoreCase(FREQ_MONTHLY)) {
            frequencyInt = MONTHLY;
            return;
        }
        if (frequency.equalsIgnoreCase(FREQ_WEEKLY)) {
            frequencyInt = WEEKLY;
            return;
        }
        if (frequency.equalsIgnoreCase(FREQ_DAILY)) {
            frequencyInt = DAILY;
            return;
        }
        
       
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public long getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(long untilDate) {
        this.untilDate = untilDate;
    }
    public static final String FREQ_DAILY = "DAILY";
    public static final String FREQ_WEEKLY = "WEEKLY";
    public static final String FREQ_MONTHLY = "MONTHLY";
    public static final String FREQ_YEARLY = "YEARLY";
    public static final int YEARLY = 1, MONTHLY = 2, WEEKLY = 3, DAILY = 4, HOURLY = 5;
}


package com.boha.coursemaker.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CalendarUtil {

	// Projection array. Creating indices for this array instead of doing
	// dynamic lookups improves performance.
	public static final String[] EVENT_PROJECTION = new String[] {
	    Calendars._ID,                           // 0
	    Calendars.ACCOUNT_NAME,                  // 1
	    Calendars.CALENDAR_DISPLAY_NAME,         // 2
	    Calendars.OWNER_ACCOUNT                  // 3
	};
	  
	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	
	public static List<CalendarRequest> queryCalendars(Context ctx, String email) {
		List<CalendarRequest> list = new ArrayList<CalendarRequest>();
		Cursor cur = null;
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Calendars.CONTENT_URI;   
		String selection = Calendars.ACCOUNT_NAME + " = '" + email + "'";
		//String[] selectionArgs = new String[] {email}; 
		// Submit the query and get a Cursor object back. 
		cur = cr.query(uri, EVENT_PROJECTION, selection, null, null);
		// Use the cursor to step through the returned records
		while (cur.moveToNext()) {
		    long calID = 0;
		    String displayName = null;
		    String accountName = null;
		    String ownerName = null;
		      
		    // Get the field values
		    calID = cur.getLong(PROJECTION_ID_INDEX);
		    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
		    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
		    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
		    Log.i(LOG, "Calendar found, displayName: " + displayName + " acctName: " + accountName + " owner: " + ownerName + " id: " + calID);
		    CalendarRequest crr = new CalendarRequest();
		    crr.setCalendarID(calID);
		    crr.setCalendarName(displayName);
		    list.add(crr);

		}
		return list;
	}
	/** The main/basic URI for the android calendars table */
	private static final Uri CAL_URI = CalendarContract.Calendars.CONTENT_URI;
	/** The main/basic URI for the android events table */
	private static final Uri EVENT_URI = CalendarContract.Events.CONTENT_URI;

	/** Builds the Uri for events (as a Sync Adapter) */
	private static Uri buildEventUri(String accountName) {
		return EVENT_URI
				.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,
						"true")
				.appendQueryParameter(Calendars.ACCOUNT_NAME, accountName)
				.appendQueryParameter(Calendars.ACCOUNT_TYPE,
						CalendarContract.ACCOUNT_TYPE_LOCAL).build();
	}
	public static final long MINUTE = 1;
	public static final long HOUR = MINUTE * 60;
	public static final long DAY = HOUR * 24;
	public static final long WEEK = DAY * 7;
	public static final long MONTH = DAY * 30;
	
	
	public static void addReminder(Context ctx, 
			long eventID, long minutes) throws Exception{
		ContentResolver cr = ctx.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Reminders.MINUTES, minutes);
		values.put(Reminders.EVENT_ID, eventID);
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		cr.insert(Reminders.CONTENT_URI, values);
	}
	public static void addAttendees(Context ctx, List<AttendeeRequest> list) throws Exception {
		for (AttendeeRequest ar : list) {
			addAttendee(ctx, ar.getEventID(), ar.getEmail(),ar.getAttendeeName());
		}
	}
	private static void addAttendee(Context ctx, long eventID, String email, String attendeeName) 
		throws Exception {
		ContentResolver cr = ctx.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Attendees.ATTENDEE_NAME, attendeeName);
		values.put(Attendees.ATTENDEE_EMAIL, email);
		values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ATTENDEE);
		values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_REQUIRED);
		values.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_INVITED);
		values.put(Attendees.EVENT_ID, eventID);
		Uri u = cr.insert(Attendees.CONTENT_URI, values);
		Log.i(LOG, "attendee added ... " + attendeeName + "\n" + u.toString());
	}
	/**
	 * Create and insert new calendar into android database
	 * 
	 * @param ctx
	 *            The context (e.g. activity)
	 */
	public static String createCalendar(CalendarRequest crx) throws Exception {
		ContentResolver cr = crx.getContext().getContentResolver();
		final ContentValues cv = getCalendarContentValues(
				crx.getCalendarName(), crx.getAccountName());
		Uri calUri = buildCalUri(crx.getAccountName());
		// insert the calendar into the database
		
		Uri u = cr.insert(calUri, cv);
		String s = u.toString();
		int index = s.indexOf("calendars/");
		int index2 = s.indexOf("?");
		if (index > -1) {
			index += 10;
			String id = s.substring(index, index2);
			Log.i(LOG, "----------- Substring gave this calendar id: " + id);
		}
				
		Log.i(LOG, "Calendar created: " + u.toString());

		return u.toString();
	}
	static final String LOG = "CalendarUtil";
	/**
	 * Update event
	 */
	public static void updateEvent(CalendarRequest crx) throws Exception {
		ContentResolver cr = crx.getContext().getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.TITLE, crx.getTitle());
		values.put(Events.DESCRIPTION, crx.getDescription());
		values.put(Events.EVENT_LOCATION, crx.getLocation());
		String selection = "(" + Events._ID + " = ?)";
		String[] selectionArgs = new String[] { String
				.valueOf(crx.getEventID()) };
		cr.update(buildEventUri(crx.getAccountName()), values, selection,
				selectionArgs);
	}

	/**
	 * Create event - Add an event to our calendar
	 * 
	 * @param dtstart
	 *            Event start time (in millis)
	 * @param dtend
	 *            Event end time (in millis)
	 */
	public static long addEvent(CalendarRequest crx) throws Exception {
		ContentResolver cr = crx.getContext().getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(Events.CALENDAR_ID, crx.getCalendarID());
		cv.put(Events.TITLE, crx.getTitle());
		cv.put(Events.DTSTART, crx.getDateStart());
		cv.put(Events.DTEND, crx.getDateEnd());
		cv.put(Events.EVENT_LOCATION, SharedUtil.getCompany(crx.getContext()).getCompanyName());
		cv.put(Events.DESCRIPTION, crx.getDescription());
		// TODO - get country, see if we can tease the time zone out of it, no?
		cv.put(Events.EVENT_TIMEZONE, "Africa/Johannesburg");
		Uri uri = buildEventUri(crx.getAccountName());
		Uri u = cr.insert(uri, cv);
		Log.w(LOG, "-- uri returned: " + u.toString());
		String id = null;
		String s = u.toString();
		int index = s.indexOf("events/");
		int index2 = s.indexOf("?");
		if (index > -1) {
			index += 7;
			id = s.substring(index, index2);
			Log.i(LOG, "----------- Substring gave this event id: " + id);
		}
				
		Log.i(LOG, "Event added: " + u.toString());
		long eventID = Long.parseLong(id);
		//check if attendees
		if (crx.getAttendeeRequestList() != null) {
			for (AttendeeRequest ar : crx.getAttendeeRequestList()) {
				ar.setEventID(eventID);
			}
			addAttendees(crx.getContext(), crx.getAttendeeRequestList());
		}
		return eventID;
	}

	/** Permanently deletes our calendar from database (along with all events) */
	public static int deleteCalendar(CalendarRequest crx) throws Exception {
		ContentResolver cr = crx.getContext().getContentResolver();
		Uri calUri = ContentUris.withAppendedId(
				buildCalUri(crx.getAccountName()), crx.getCalendarID());
		Log.w(LOG, "about to delete this calendar: " + calUri.toString());
		int x = cr.delete(calUri, null, null);
		return x;
	}

	/**
	 * Delete event
	 */
	public static int deleteEvent(CalendarRequest crx) throws Exception {
		ContentResolver cr = crx.getContext().getContentResolver();
		Uri deleteUri = ContentUris.withAppendedId(
				CalendarContract.Events.CONTENT_URI, crx.getEventID());
		int y = cr.delete(deleteUri, null, null);
		return y;
	}

	/** Builds the Uri for your Calendar in android database (as a Sync Adapter) */

	private static Uri buildCalUri(String accountName) {
		return CAL_URI
				.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,
						"true")
				.appendQueryParameter(Calendars.ACCOUNT_NAME, accountName)
				.appendQueryParameter(Calendars.ACCOUNT_TYPE,
						CalendarContract.ACCOUNT_TYPE_LOCAL).build();
	}

	/**
	 * Finds an event based on the ID
	 * 
	 * @param ctx
	 *            The context (e.g. activity)
	 * @param id
	 *            The id of the event to be found
	 */
	public static CalendarRequest getEventByID(CalendarRequest crx) {
		ContentResolver cr = crx.getContext().getContentResolver();
		// Projection array for query (the values you want)
		final String[] PROJECTION = new String[] { Events._ID, Events.TITLE,
				Events.DESCRIPTION, Events.EVENT_LOCATION, Events.DTSTART,
				Events.DTEND, };
		final int TITLE_INDEX = 1, DESC_INDEX = 2, LOCATION_INDEX = 3, START_INDEX = 4, END_INDEX = 5;
		long start_millis = 0, end_millis = 0;
		String title = null, description = null, location = null;
		final String selection = "(" + Events.OWNER_ACCOUNT + " = ? AND "
				+ Events._ID + " = ?)";
		final String[] selectionArgs = new String[] { crx.getAccountName(),
				crx.getEventID() + "" };
		Cursor cursor = cr.query(buildEventUri(crx.getAccountName()),
				PROJECTION, selection, selectionArgs, null);
		// at most one event will be returned because event ids are unique in
		// the table
		if (cursor.moveToFirst()) {
			title = cursor.getString(TITLE_INDEX);
			description = cursor.getString(DESC_INDEX);
			location = cursor.getString(LOCATION_INDEX);
			start_millis = cursor.getLong(START_INDEX);
			end_millis = cursor.getLong(END_INDEX);
			// do something with the values...
			crx.setTitle(title);
			crx.setDescription(description);
			crx.setDateStart(start_millis);
			crx.setDateEnd(end_millis);
			crx.setLocation(location);
		}
		cursor.close();
		return crx;
	}

	private static ContentValues getCalendarContentValues(String calendarName,
			String accountName) {

		final ContentValues cv = new ContentValues();
		cv.put(Calendars.ACCOUNT_NAME, accountName);
		cv.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		cv.put(Calendars.NAME, calendarName);
		cv.put(Calendars.CALENDAR_DISPLAY_NAME, calendarName);
		cv.put(Calendars.CALENDAR_COLOR, 0xEA8561);
		// user can only read the calendar
		cv.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		cv.put(Calendars.OWNER_ACCOUNT, accountName);
		cv.put(Calendars.VISIBLE, 1);
		cv.put(Calendars.SYNC_EVENTS, 1);
		return cv;
	}
}

package com.boha.coursemaker.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.boha.coursemaker.listeners.CalendarListener;

import java.util.List;

public class CalendarAsyncTask {

	static CalendarListener calendarListener;
	static Context ctx;
	 
	public static void performTask(CalendarRequest crx, CalendarListener listener) {
		calendarListener = listener;
		CalendarRequest calendarRequest = new CalendarRequest();
		calendarRequest.setAccountName(crx.getAccountName());
		calendarRequest.setAttendeeRequestList(crx.getAttendeeRequestList());
		calendarRequest.setCalendarID(crx.getCalendarID());
		calendarRequest.setCalendarName(crx.getCalendarName());
		calendarRequest.setContext(crx.getContext());
		calendarRequest.setRequestType(crx.getRequestType());
		calendarRequest.setDateEnd(crx.getDateEnd());
		calendarRequest.setDateStart(crx.getDateStart());
		calendarRequest.setDescription(crx.getDescription());
		calendarRequest.setLocation(crx.getLocation());
		calendarRequest.setMinutes(crx.getMinutes());
		calendarRequest.setTrainingClassID(crx.getTrainingClassID());
		new CTask().execute(calendarRequest);
	}
	public static void queryCalendars(Context c, String email, CalendarListener listener) {
		calendarListener = listener;
		ctx = c;
		new QTask().execute(email);
	}
	static class QTask extends AsyncTask<String, Void, List<CalendarRequest>> {

		@Override
		protected List<CalendarRequest> doInBackground(String... params) {
			String email = params[0];
			List<CalendarRequest> list = CalendarUtil.queryCalendars(ctx, email);
			Log.w("CalTask", "calendars found: " + list.size());
			return list;
		}
		@Override
		protected void onPostExecute(List<CalendarRequest> list) {
			calendarListener.onCalendarQueryComplete(list);
		}
		
	}
	static class CTask extends AsyncTask<CalendarRequest, Void, CalendarRequest> {

		@Override
		protected CalendarRequest doInBackground(CalendarRequest... params) {
			long id = 0;
			CalendarRequest calendarRequest = params[0];
			try {
				switch (calendarRequest.getRequestType()) {
				case CalendarRequest.ADD_CALENDAR:
					String x = CalendarUtil.createCalendar(calendarRequest);
					calendarRequest.setCalendarID(id); 
					Log.i(LOG, "**** Calendar added, id: " + x);
					break;
				case CalendarRequest.ADD_EVENT:
					id = CalendarUtil.addEvent(calendarRequest);
					calendarRequest.setEventID(id);
					Log.i(LOG, "Event added, id: " + id);
					break;
				case CalendarRequest.ADD_ATTENDEES:
					CalendarUtil.addAttendees(calendarRequest.getContext(), calendarRequest.getAttendeeRequestList());
					Log.i(LOG, "Attendees added to calendar event: " + calendarRequest.getEventID());
					break;
				case CalendarRequest.DELETE_CALENDAR:
					int x1 = CalendarUtil.deleteCalendar(calendarRequest);
					if (x1 > 0) {
						calendarRequest.setRemoved(true);
						Log.i(LOG, "Calendar deleted, id: " + calendarRequest.getCalendarName() + " return count: " + x1);
					} else {
						Log.d(LOG, "Calendar NOT deleted, id: " + calendarRequest.getCalendarName() + " return count: " + x1);
					}
					
					break;
				case CalendarRequest.DELETE_EVENT:
					int y = CalendarUtil.deleteEvent(calendarRequest);
					if (y > 0) {
						calendarRequest.setRemoved(true);
						Log.i(LOG, "Event deleted, id: " + calendarRequest.getEventID() + " return count: " + y);
					} else {
						Log.d(LOG, "Event NOT deleted, id: " + calendarRequest.getEventID() + " return count: " + y);
					}
					
					break;
				case CalendarRequest.UPDATE_CALENDAR:
					
					break;
				case CalendarRequest.UPDATE_EVENT:
					CalendarUtil.updateEvent(calendarRequest);
					Log.i(LOG, "Event updated, id: " + calendarRequest.getEventID());
					break;
				case CalendarRequest.GET_EVENT_BY_ID:
					CalendarUtil.getEventByID(calendarRequest);
					break;
				case CalendarRequest.ADD_REMINDER:
					CalendarUtil.addReminder(calendarRequest.getContext(), calendarRequest.getEventID(), calendarRequest.getMinutes());
					Log.i(LOG, "Reminder added, event id: " + calendarRequest.getEventID());
					break;
				default:
					break;
				}
				
			} catch (Exception e) {
				Log.e(LOG, "Calendar request failed", e);
				return null;
			}
			return calendarRequest;
		}
		@Override
		protected void onPostExecute(CalendarRequest res) {
			if (res == null) {
				calendarListener.onError();
			} else {
				calendarListener.onCalendarTaskDone(res);
			}
		}
		
		static final String LOG = "CalendarAsyncTask";
	}
}

package com.boha.coursemaker.email;

import android.os.AsyncTask;
import android.util.Log;
import com.boha.coursemaker.listeners.MailSenderListener;

public class AsyncMailSender {

	public static void sendMail(String senderEmail, String senderPassword,  
			String subject, String body, 
			String recipients, MailSenderListener listener) {
		mSubject = subject;
		mBody = body;
		mRecipients = recipients;
		mListener = listener;
		mSenderEmail = senderEmail;
		mSenderPassword = senderPassword;
		
		Log.w(LOG, "Starting to send email to " + recipients);
		new SendTask().execute();
		
	}
	
	static class SendTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			
			try {   
                GmailSender sender = new GmailSender(mSenderEmail, mSenderPassword);
                sender.sendMail(mSubject,   
                        mBody, mSenderEmail,  
                        mRecipients);   
            } catch (Exception e) {   
                Log.e(LOG, e.getMessage(), e);
                return 1;
            } 
			return 0;
		}
		@Override
		protected void onPostExecute(Integer res) {
			if (res == 0) {
				Log.i(LOG, "email has been sent to " + mRecipients);
				mListener.onMailSent();
			} else {
				mListener.onMailError();
			}
		}
	}
	static final String LOG = "AsyncMailSender";
	static String mSubject,  mBody,  mRecipients; 
	static MailSenderListener mListener;
	static String mSenderEmail, mSenderPassword;
}

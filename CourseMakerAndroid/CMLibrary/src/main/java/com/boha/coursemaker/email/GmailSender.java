package com.boha.coursemaker.email;

import android.util.Log;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

public class GmailSender extends javax.mail.Authenticator {
	private static final String MAIL_HOST = "smtp.gmail.com";
	private static final String LOG = "GmailSender";
	private String user;
	private String password;
	private Session session;

	static {
		Security.addProvider(new JSSEProvider());
	}

	public GmailSender(String user, String password) {
		this.user = user;
		this.password = password;

		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", MAIL_HOST);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");

		session = Session.getDefaultInstance(props, this);
		Log.d(LOG, "user: " + user + " pswd: " + password);
		Log.i(LOG, "Mail Session - " + session.toString());

	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, password);
	}

	public synchronized void sendMail(String subject, String body,
			String sender, String recipients) throws EmailException {
		
		try {
			MimeMessage message = new MimeMessage(session);
			DataHandler handler = new DataHandler(new ByteArrayDataSource(
					body.getBytes(), "text/plain"));
			message.setSender(new InternetAddress(sender));
			message.setSubject(subject);
			message.setDataHandler(handler);
			if (recipients.indexOf(',') > 0)
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(recipients));
			else
				message.setRecipient(Message.RecipientType.TO,
						new InternetAddress(recipients));
			Transport.send(message);
			Log.i(LOG, "Yebo! - email transport did not crash!");
		} catch (Exception e) {
			Log.e(LOG, "Failed to send mail", e);
			throw new EmailException();
		}
	}

	public class ByteArrayDataSource implements DataSource {
		private byte[] data;
		private String type;

		public ByteArrayDataSource(byte[] data, String type) {
			super();
			this.data = data;
			this.type = type;
		}

		public ByteArrayDataSource(byte[] data) {
			super();
			this.data = data;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContentType() {
			if (type == null)
				return "application/octet-stream";
			else
				return type;
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}

		public String getName() {
			return "ByteArrayDataSource";
		}

		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not Supported");
		}
	}
}

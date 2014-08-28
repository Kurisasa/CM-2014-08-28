package com.boha.coursemaker.util;

import android.content.Context;
import android.util.Log;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.util.exception.CommsException;
import com.boha.coursemaker.util.exception.NetworkUnavailableException;
import com.google.gson.Gson;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class CommsUtil {

	public static ResponseDTO getZippedData(Context ctx, String request)
			throws CommsException, NetworkUnavailableException {
		String x;
		try {
			x = URLDecoder.decode(request, "UTF-8");
			Log.d(COMMS, "getZippedData: sending request: .......\n" + x);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		ResponseDTO sr = new ResponseDTO();
		HttpURLConnection con = null;
		URL url;
		String response = null;
		InputStream is = null;
		Gson gson = new Gson();
		// check network
		WebCheckResult res = WebCheck.checkNetworkAvailability(ctx);
		if (res.isNetworkUnavailable()) {
			throw new NetworkUnavailableException();
		}
		try {
			url = new URL(request);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			// Start the query
			con.connect();
			is = con.getInputStream();
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
			@SuppressWarnings("unused")
			ZipEntry entry = null;
			ByteArrayBuffer bab = new ByteArrayBuffer(2048);
			while ((entry = zis.getNextEntry()) != null) {
				int size = 0;
				byte[] buffer = new byte[2048];
				while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
					bab.append(buffer, 0, size);
				}

			}
			response = new String(bab.toByteArray());
			sr = gson.fromJson(response, ResponseDTO.class);
			if (sr != null) {
				Log.i(COMMS, "*** Back-end status code: " + sr.getStatusCode()
						+ " msg: " + sr.getMessage());
			} else {
				Log.e(COMMS,
						"&&&&&&& ++ It's a Houston kind of problem: json deserializer returned null");
				sr = new ResponseDTO();
				sr.setStatusCode(9999);
				sr.setMessage("Funny problem, Haha! response is NULL!");
			}
			int code = con.getResponseCode();
			Log.d(COMMS, "### (getZipped) HTTP response code: " + code
					+ " msg: " + con.getResponseMessage());
		} catch (IOException e) {
			Log.e(COMMS, "Houston, we have a problem - IOException issues..");
			Log.d(COMMS, "Request in error: \n" + request);
			throw new CommsException(CommsException.CONNECTION_ERROR);
		} catch (Exception e) {
			throw new CommsException(CommsException.CONNECTION_ERROR);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// Log.e(COMMS, "Unable to close input stream, but no prob!");
			}
		}
		return sr;
	}

	public static ResponseDTO getData(Context ctx, String request)
			throws CommsException, NetworkUnavailableException {
		String x;
		try {
			x = URLDecoder.decode(request, "UTF-8");
			Log.d(COMMS, "getData: sending request: .......\n" + x);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		HttpURLConnection con = null;
		URL url;
		String response = null;
		InputStream is = null;
		ResponseDTO webResp = null;
		// check network
		WebCheckResult res = WebCheck.checkNetworkAvailability(ctx);
		if (res.isNetworkUnavailable()) {
			throw new NetworkUnavailableException();
		}
		try {
			url = new URL(request);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.connect();
			is = con.getInputStream();
			int httpCode = con.getResponseCode();
			String msg = con.getResponseMessage();
			Log.d(COMMS, "### (getData) HTTP response code: " + httpCode
					+ " msg: " + msg);
			response = readStream(is);
			// check for html
			int idx = response.indexOf("DOCTYPE html");
			if (idx > -1) {
				Log.e(COMMS, "@@@ ERROR RESPONSE, some html received:\n"
						+ response);
				throw new NetworkUnavailableException();
			}
			Gson gson = new Gson();
			webResp = gson.fromJson(response, ResponseDTO.class);
			if (webResp != null) {
				Log.i(COMMS, "Back-end status code: " + webResp.getStatusCode()
						+ " msg: " + webResp.getMessage());
			} else {
				Log.e(COMMS,
						"&&&&&&& ++ It's a Houston kind of problem: json deserializer returned null");
				webResp = new ResponseDTO();
				webResp.setStatusCode(9999);
				webResp.setMessage("Funny problem, Haha! response is NULL!");
			}

		} catch (IOException e) {
			Log.e(COMMS, "Houston, we have an IOException. F%$%K!", e);
			if (res.isMobileConnected()) {
				throw new NetworkUnavailableException();
			} else {
				throw new CommsException(CommsException.CONNECTION_ERROR);
			}
		} catch (Exception e) {
			throw new CommsException(CommsException.CONNECTION_ERROR);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// Log.e(COMMS,
				// "Unable to close input stream - should be no problem.");
			}
		}

		return webResp;
	}


	public static String getSimpleData(Context ctx, String request)
			throws CommsException, NetworkUnavailableException {
		Log.d(COMMS, "getData: sending request: .......\n" + request);
		HttpURLConnection con = null;		

		URL url;
		String response = null;
		InputStream is = null;
		// check network
		WebCheckResult res = WebCheck.checkNetworkAvailability(ctx);
		if (res.isNetworkUnavailable()) {
			throw new NetworkUnavailableException();
		}
		try {
			url = new URL(request);
			
				con = (HttpURLConnection) url.openConnection(); 				
			con.setRequestMethod("GET");
			con.connect();
			is = con.getInputStream();
			int httpCode = con.getResponseCode();
			String msg = con.getResponseMessage();
			Log.d(COMMS, "### HTTP response code: " + httpCode + " msg: " + msg);
			response = readStream(is);
			Log.d(COMMS, "### RESPONSE: \n" + response);
			// check for html
			int idx = response.indexOf("DOCTYPE html");
			if (idx > -1) {
				Log.e(COMMS, "@@@ ERROR RESPONSE, some html received:\n"
						+ response);
				throw new NetworkUnavailableException();
			}

		} catch (IOException e) {
			Log.e(COMMS, "Houston, we have an IOException. F%$%K!", e);
			if (res.isMobileConnected()) {
				throw new NetworkUnavailableException();
			} else {
				throw new CommsException(CommsException.CONNECTION_ERROR);
			}
		} catch (Exception e) {
			throw new CommsException(CommsException.CONNECTION_ERROR);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
		Log.i(COMMS, "Response from request:\n" + response);
		return response;
	}

	/*
	 * public static String postData(String urlReq, String request) throws
	 * CommsException { HttpURLConnection con = null; URL url; String response =
	 * null; InputStream is = null; try { url = new URL(urlReq); con =
	 * (HttpURLConnection) url.openConnection(); con.setRequestMethod("POST");
	 * con.setDoOutput(true);
	 * con.setFixedLengthStreamingMode(request.getBytes().length);
	 * OutputStreamWriter wr = new OutputStreamWriter( con.getOutputStream());
	 * wr.write(request); wr.flush(); // Start the query con.connect(); is =
	 * con.getInputStream(); response = readStream(is); int code =
	 * con.getResponseCode(); Log.i(COMMS, "Comms HTTP POST response code: " +
	 * code); } catch (IOException e) { throw new
	 * CommsException(CommsException.CONNECTION_ERROR); } finally { try {
	 * is.close(); } catch (Exception e) { Log.e(COMMS,
	 * "Unable to close input stream"); } } return response; }
	 */

	private static String readStream(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1024);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	private static final String COMMS = "CommsUtil";
}

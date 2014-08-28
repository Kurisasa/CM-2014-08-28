package com.boha.coursemaker.base;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.boha.cmlibrary.R;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.AuthorDTO;
import com.boha.coursemaker.dto.CityDTO;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.GcmDeviceDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.ProvinceDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.listeners.GCMUtilListener;
import com.boha.coursemaker.util.GCMUtil;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;
import com.boha.coursemaker.util.actor.AuthorUtil;
import com.boha.coursemaker.util.actor.InstructorUtil;
import com.boha.coursemaker.util.actor.TraineeUtil;
import com.boha.volley.toolbox.BohaRequest;
import com.boha.volley.toolbox.BohaVolley;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class BaseRegistration extends FragmentActivity implements
		com.google.android.gms.location.LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	public abstract void processSignIn();

	public abstract void setFields();

	public abstract void processRemoteResponse();

	public abstract void startMain();

	public abstract void setBusy();

	public abstract void setNotBusy();

	public void setVolley() {

		requestQueue = BohaVolley.getRequestQueue(ctx);
	}

	/**
	 * Check that device has been registered. Register new device to GCM if
	 * necessary
	 * 
	 * @param checkType
	 */
	public void checkVirginity(int checkType) {

		switch (checkType) {
		case ADMINISTRATOR:
			CompanyDTO co = SharedUtil.getCompany(ctx);
			if (co != null) {
				Log.d(LOG, log);
				startMain();
				return;
			}
			break;
		case INSTRUCTOR:
			InstructorDTO ws = SharedUtil.getInstructor(ctx);
			if (ws != null) {
				Log.d(LOG, log);
				startMain();
				return;
			}
			break;
		case TRAINEE:
			TraineeDTO cc = SharedUtil.getTrainee(ctx);
			if (cc != null) {
				Log.d(LOG, log);
				startMain();
				return;
			}
			break;
		case AUTHOR:
			AuthorDTO d = SharedUtil.getAuthor(ctx);
			if (d != null) {
				Log.d(LOG, log);
				startMain();
				return;
			}
			break;

		default:
			break;
		}

		mLocationClient = new LocationClient(getApplicationContext(), this,
				this);
		getEmail();
		registerDevice();

		if (checkType == ADMINISTRATOR) {
			getCountryList();
		}

	}

	protected void getCountryList() {
		type = RequestDTO.GET_COUNTRY_LIST;
		setBusy();
		try {
			getRemoteData(type, Statics.SERVLET_ADMIN);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Register device to accept Google Cloud notifications
	 */
	private void registerDevice() {
		GCMUtil.startGCMRegistration(ctx, new GCMUtilListener() {
			@Override
			public void onGCMError() {
				Log.e(LOG, "Error registering device for gcm");
			}

			@Override
			public void onDeviceRegistered(String regID) {
				gcmRegistrationID = regID;
				Log.w(LOG, "onDeviceRegistered - stored in variable: " + regID);
				gcmDevice = new GcmDeviceDTO();
				gcmDevice.setRegistrationID(gcmRegistrationID);

			}
		});
	}

	private int mType;

	/**
	 * Call via Https to server to get registration related data
	 * 
	 * @param type
	 *            - identifies the server call
	 * @param suffix
	 *            - identifies the servlet destination
	 * @throws UnsupportedEncodingException
	 */
	public void getRemoteData(int type, String suffix)
			throws UnsupportedEncodingException {
		Log.d(LOG, "....getRemoteData ...type: " + type + " -- " + LOG);
		this.mType = type;
		RequestDTO request = new RequestDTO();
		request.setRequestType(type);
		//
		
		switch (type) {
		case RequestDTO.GET_COUNTRY_LIST:
			String countryCode = Locale.getDefault().getCountry();
			Log.i(LOG, "Country code is " + countryCode);
			request.setCountryCode(countryCode);
			
			break;
		case RequestDTO.REGISTER_ADMINISTRATOR:
			request.setAdministrator(administrator);
			break;
		case RequestDTO.LOGIN_ADMINISTRATOR:
			request.setEmail(email);
			request.setPassword(password);
			request.setGcmDevice(gcmDevice);
			break;
		case RequestDTO.REGISTER_TRAINING_COMPANY:
			request.setCompany(company);
			request.setAdministrator(administrator);
			break;
		case RequestDTO.REGISTER_AUTHOR:
			request = AuthorUtil.registerAuthor(ctx, author,
					author.getCompanyID());
			break;
		case RequestDTO.LOGIN_AUTHOR:
			request = AuthorUtil.loginAuthor(ctx, email, password);
			request.setGcmDevice(gcmDevice);
			break;
		case RequestDTO.REGISTER_TRAINEE:
			request = TraineeUtil.registerTrainee(ctx, trainee,
					trainingClassID, city.getCityID());
			break;
		case RequestDTO.LOGIN_TRAINEE:
			request = TraineeUtil.loginTrainee(ctx, email, password);
			request.setGcmDevice(gcmDevice);
			break;
		case RequestDTO.REGISTER_INSTRUCTOR:
			request = InstructorUtil.registerInstructor(ctx, instructor);
			break;
		case RequestDTO.LOGIN_INSTRUCTOR:
			request = InstructorUtil.loginInstructor(ctx, email, password);
			request.setGcmDevice(gcmDevice);
			break;
		default:
			break;
		}
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;

		setBusy();
        WebSocketUtil.sendRequest(ctx, suffix, request, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        response = r;
                        setNotBusy();
                        Log.e(LOG,
                                "....Yup! got a response from the server, type = "
                                        + mType);
                        if (r.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            btnSignIn.setEnabled(true);
                            if (btnSignUp != null) {
                                btnSignUp.setEnabled(true);
                            }
                            return;
                        }
                        if (mType == RequestDTO.GET_COUNTRY_LIST) {
                            Log.d(LOG, "Response back with country data");
                            setProvinceSpinner();
                            return;
                        }
                        savePrefs();
                        new LocalTask().execute();
                        processRemoteResponse();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(suffix, request, ctx,
//				new BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						setNotBusy();
//						Log.e(LOG, "Problem: " + error.getMessage());
//						btnSignIn.setEnabled(true);
//						if (btnSignUp != null)
//							btnSignUp.setEnabled(true);
//						if (error instanceof NetworkError) {
//							NetworkError ne = (NetworkError) error;
//							if (ne.networkResponse != null) {
//								Log.w(LOG, "volley http status code: "
//										+ ne.networkResponse.statusCode);
//							}
//							ToastUtil.errorToast(
//									ctx,
//									ctx.getResources().getString(
//											R.string.error_server_unavailable));
//						} else {
//							ToastUtil.errorToast(ctx, ctx.getResources()
//									.getString(R.string.error_server_comms));
//						}
//
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						response = r;
//						setNotBusy();
//						Log.e(LOG,
//								"....Yup! got a response from the server, type = "
//										+ mType);
//						if (r.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//							btnSignIn.setEnabled(true);
//							if (btnSignUp != null) {
//								btnSignUp.setEnabled(true);
//							}
//							return;
//						}
//						if (mType == RequestDTO.GET_COUNTRY_LIST) {
//							Log.d(LOG, "Response back with country data");
//							setProvinceSpinner();
//							return;
//						}
//						savePrefs();
//						new LocalTask().execute();
//						processRemoteResponse();
//					}
//				});

	}
	class LocalTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
		
	}

	private void savePrefs() {
		if (response.getCompany() != null) {
			SharedUtil.saveCompany(ctx, response.getCompany());
			Log.i(LOG, "Company saved by SharedUtil");
		}
		if (response.getInstructor() != null) {
			SharedUtil.saveInstructor(ctx, response.getInstructor());
			Log.i(LOG, "Instructor saved by SharedUtil");
		}
		if (response.getTrainee() != null) {
			SharedUtil.saveTrainee(ctx, response.getTrainee());
			Log.i(LOG, "Trainee saved by SharedUtil");
		}
		if (response.getTrainingClass() != null) {
			SharedUtil.saveTrainingClass(ctx, response.getTrainingClass());
			Log.i(LOG, "Training Class saved by SharedUtil");
		}
		if (response.getAdministrator() != null) {
			SharedUtil.saveAdmin(ctx, response.getAdministrator());
			Log.i(LOG, "Admin saved by SharedUtil");
		}
		if (response.getAuthor() != null) {
			SharedUtil.saveAuthor(ctx, response.getAuthor());
			Log.i(LOG, "Author saved by SharedUtil");
		}
		Log.w(LOG, "###### APP actor Preferences saved. Cool!");
		startMain();
	}

	public void getEmail() {
		AccountManager am = AccountManager.get(getApplicationContext());
		Account[] accts = am
				.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		final ArrayList<String> tarList = new ArrayList<String>();
		if (accts != null) {
			for (int i = 0; i < accts.length; i++) {
				tarList.add(accts[i].name);
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					R.layout.xsimple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(R.layout.xsimple_spinner_dropdown_item);
			emailSpinner.setAdapter(dataAdapter);
			emailSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							email = tarList.get(arg2);
							Log.d("Reg", "###### Email account selected is "
									+ email);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
		}
	}

	// //////////// spinners for province and city ....
	private void setProvinceSpinner() {
		if (provinceSpinner == null) {
			return;
		}
		provinceList = response.getProvinceList();
		final ArrayList<String> tarList = new ArrayList<String>();
		if (provinceList != null) {
			tarList.add("Please select Province");
			for (ProvinceDTO p : provinceList) {
				tarList.add(p.getProvinceName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			provinceSpinner.setAdapter(dataAdapter);
			provinceSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							if (arg2 == 0) {
								province = null;
								return;
							}
							province = provinceList.get(arg2 - 1);
							Log.d("Reg", "###### province selected is "
									+ province.getProvinceName());
							setCitySpinner(province.getCityList());
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
		}

	}

	private void setCitySpinner(final List<CityDTO> list) {
		final ArrayList<String> tarList = new ArrayList<String>();
		if (list != null) {
			tarList.add("Please select nearest City");
			for (CityDTO p : list) {
				tarList.add(p.getCityName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			citySpinner.setAdapter(dataAdapter);
			citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (arg2 == 0) {
						city = null;
						return;
					}
					city = list.get(arg2 - 1);
					Log.d("Reg",
							"###### city selected is " + city.getCityName());

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}
	}

	// ////////////// location stuff
	@Override
	public void onStart() {
		super.onStart();
		if (mLocationClient != null) {
			mLocationClient.connect();
			Log.i(LOG,
					"#################### onStart - locationClient connecting ... ");
		}

	}

	@Override
	public void onStop() {
		if (mLocationClient == null) {
			super.onStop();
			return;
		}
		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();
		Log.w(LOG, "############ onStop - locationClient disconnected: "
				+ mLocationClient.isConnected());
		super.onStop();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.e(LOG, "onConnection failed: " + arg0.toString());

	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i(LOG, "### ---> PlayServices onConnected() - gotta go! >>");
		if (mLocationClient == null) {
			Log.i(LOG,
					"onConnected - mLocationClient is NULL. How da fuck is dat?");
			return;
		}
		try {
			mCurrentLocation = mLocationClient.getLastLocation();
			if (mCurrentLocation != null) {
				latitude = mCurrentLocation.getLatitude();
				longitude = mCurrentLocation.getLongitude();
			} else {
				Log.e(LOG, "$$$$ mCurrentLocation is NULL");
			}
		} catch (IllegalStateException e) {
			Log.w(LOG, "Something's properly fucked up!", e);
		}

	}

	@Override
	public void onDisconnected() {
		Log.w(LOG, "### ---> PlayServices onDisconnected() ");

	}

	@Override
	public void onLocationChanged(Location loc) {
		Log.e(LOG, "### Location changed, lat: " + loc.getLatitude() + " lng: "
				+ loc.getLongitude());
		latitude = loc.getLatitude();
		longitude = loc.getLongitude();
		mCurrentLocation = loc;

	}

	public static final int COMPANY = 1, INSTRUCTOR = 2, ADMINISTRATOR = 3,
			TRAINEE = 4, AUTHOR = 6;
	private static final String log = "-------> Returning user, not a virgin. OK!";
	private Location mCurrentLocation;
	protected ProvinceDTO province;
	protected List<ProvinceDTO> provinceList;
	protected Button btnSignIn, btnSignUp;
	protected EditText editCompanyName, editFirstName, editLastName,
			editPassword, editCompanyNumber, editCellphone, editClassNumber;
	protected ProgressBar progress;
	protected TextView txtTitle;
	protected BohaRequest bohaRequest;
	protected RequestQueue requestQueue;
	protected ImageLoader imageLoader;
	protected ProgressBar progressBar;
	protected int type;
	static final String LOG = "BaseRegistration";
	protected Context ctx;
	protected CityDTO city;
	protected ResponseDTO response;
	protected CompanyDTO company;
	protected String email, password;
	protected double latitude, longitude;
	protected Spinner emailSpinner, provinceSpinner, citySpinner;
	protected LocationClient mLocationClient;
	protected TraineeDTO trainee;
	protected AdministratorDTO administrator;
	protected AuthorDTO author;
	protected InstructorDTO instructor;
	protected TrainingClassDTO trainingClass;
	protected String gcmRegistrationID;
	protected int trainingClassID, cityID;
	protected GcmDeviceDTO gcmDevice;
}

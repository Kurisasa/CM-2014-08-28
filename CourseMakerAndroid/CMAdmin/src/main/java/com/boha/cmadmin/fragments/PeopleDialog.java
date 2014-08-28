package com.boha.cmadmin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.cmadmin.R;
import com.boha.cmadmin.listeners.PeopleDialogListener;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.AdministratorDTO;
import com.boha.coursemaker.dto.AuthorDTO;
import com.boha.coursemaker.dto.CityDTO;
import com.boha.coursemaker.dto.InstructorDTO;
import com.boha.coursemaker.dto.ProvinceDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.dto.TraineeDTO;
import com.boha.coursemaker.dto.TrainingClassDTO;
import com.boha.coursemaker.email.AsyncMailSender;
import com.boha.coursemaker.listeners.MailSenderListener;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.ArrayList;
import java.util.List;

public class PeopleDialog extends DialogFragment {

	public interface ContentListener {
		public void onSaveButtonClicked(String name, String desc);
	}

	public PeopleDialog() {
	}

	ContentListener contentListener;
	boolean isFirstTime;
	PeopleDialog dialog;
	ProgressBar progressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(LOG, "onCreateView of Dialog");
		dialog = this;
		final View view = inflater.inflate(R.layout.edit_person, container);
		editFirstName = (EditText) view.findViewById(R.id.REG_firstName);
		editLastName = (EditText) view.findViewById(R.id.REG_lastName);
		editEmail = (EditText) view.findViewById(R.id.REG_email);
		editCell = (EditText) view.findViewById(R.id.REG_cellphone);
		
		progressBar = (ProgressBar) view.findViewById(R.id.REG_progress);
		progressBar.setVisibility(View.GONE);

		classSpinner = (Spinner) view.findViewById(R.id.REG_spinnerClass);
		provinceSpinner = (Spinner) view.findViewById(R.id.REG_spinnerProvince);
		citySpinner = (Spinner) view.findViewById(R.id.REG_spinnerCity);
		radioFemale = (RadioButton) view.findViewById(R.id.REG_radioFemale);
		radioMale = (RadioButton) view.findViewById(R.id.REG_radioMale);

		txtHeader = (TextView) view.findViewById(R.id.REG_header);
		btnCancel = (Button) view.findViewById(R.id.EC_btnCancel);
		btnSave = (Button) view.findViewById(R.id.EC_btnSave);
		getDialog().setTitle(
				ctx.getResources().getString(R.string.people_editor));
		if (type != TRAINEE) {
			classSpinner.setVisibility(View.GONE);
			provinceSpinner.setVisibility(View.GONE);
			citySpinner.setVisibility(View.GONE);
			radioFemale.setVisibility(View.GONE);
			radioMale.setVisibility(View.GONE);
		}

		switch (type) {
		case ADMIN:
			txtHeader.setText(ctx.getResources().getString(
					R.string.administrator));
			break;
		case AUTHOR:
			txtHeader.setText(ctx.getResources().getString(R.string.author));
			break;
		case INSTRUCTOR:
			txtHeader
					.setText(ctx.getResources().getString(R.string.instructor));
			break;
		case TRAINEE:
			txtHeader.setText(ctx.getResources().getString(R.string.trainee));
			setProvinceSpinner();
			setClassSpinner();
			break;

		default:
			break;
		}

		if (action == UPDATE) {
			setFormFields();
		}
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.w(LOG, "btnCancel pressed - should dismiss now!");
				dialog.dismiss();
				Log.i(LOG, ".......about to dismiss dialog, one more time");
				dialog.dismissAllowingStateLoss();
			}
		});
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.w(LOG, "btnSave pressed...should validate");
				validate();
			}
		});
		animate(view);
		return view;
	}


	private void validate() {
		Log.w(LOG, "validating .... ");
		if (editFirstName.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getActivity(),
					ctx.getResources().getString(R.string.enter_firstname));
			return;
		}
		if (editLastName.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getActivity(),
					ctx.getResources().getString(R.string.enter_lastname));
			return;
		}
		if (editEmail.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getActivity(),
					ctx.getResources().getString(R.string.enter_email));
			return;
		}
		if (editCell.getText().toString().isEmpty()) {
			ToastUtil.errorToast(getActivity(),
					ctx.getResources().getString(R.string.enter_cell));
			return;
		}
		if (type == TRAINEE) {
			if (!radioFemale.isChecked() && !radioMale.isChecked()) {
				ToastUtil.errorToast(getActivity(), ctx.getResources()
						.getString(R.string.select_gender));
				return;
			}
			if (trainingClass == null) {
				ToastUtil.errorToast(getActivity(), ctx.getResources()
						.getString(R.string.select_class));
				return;
			}
			if (city == null) {
				ToastUtil.errorToast(getActivity(), ctx.getResources()
						.getString(R.string.select_city));
				return;
			}
		}
		// ready to send
		progressBar.setVisibility(View.VISIBLE);
		request = new RequestDTO();
		//
		String suffix = Statics.SERVLET_ADMIN;
		;
		switch (type) {
		case ADMIN:
			if (action == ADD_NEW) {
				administrator = new AdministratorDTO();
				request.setRequestType(RequestDTO.REGISTER_ADMINISTRATOR);
				administrator.setCompanyID(SharedUtil.getCompany(ctx)
						.getCompanyID());
			} else {
				request.setRequestType(RequestDTO.UPDATE_ADMIN);
			}
			administrator.setFirstName(editFirstName.getText().toString());
			administrator.setLastName(editLastName.getText().toString());
			administrator.setCellphone(editCell.getText().toString());
			administrator.setEmail(editEmail.getText().toString());
			request.setAdministrator(administrator);

			break;
		case AUTHOR:
			if (action == ADD_NEW) {
				author = new AuthorDTO();
				request.setRequestType(RequestDTO.REGISTER_AUTHOR);
				author.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
			} else {
				request.setRequestType(RequestDTO.UPDATE_AUTHOR);
			}
			author.setFirstName(editFirstName.getText().toString());
			author.setLastName(editLastName.getText().toString());
			author.setCellphone(editCell.getText().toString());
			author.setEmail(editEmail.getText().toString());
			request.setAuthor(author);
			request.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
			break;
		case INSTRUCTOR:
			if (action == ADD_NEW) {
				instructor = new InstructorDTO();
				request.setRequestType(RequestDTO.REGISTER_INSTRUCTOR);
				instructor.setCompanyID(SharedUtil.getCompany(ctx)
						.getCompanyID());
			} else {
				request.setRequestType(RequestDTO.UPDATE_INSTRUCTOR);
			}
			instructor.setFirstName(editFirstName.getText().toString());
			instructor.setLastName(editLastName.getText().toString());
			instructor.setCellphone(editCell.getText().toString());
			instructor.setEmail(editEmail.getText().toString());
			instructor.setCityID(SharedUtil.getCompany(ctx).getCity().getCityID());
			request.setInstructor(instructor);
			break;
		case TRAINEE:
			if (action == ADD_NEW) {
				trainee = new TraineeDTO();
				request.setRequestType(RequestDTO.REGISTER_TRAINEE);
				trainee.setCompanyID(SharedUtil.getCompany(ctx).getCompanyID());
			} else {
				request.setRequestType(RequestDTO.UPDATE_TRAINEE);
			}
			trainee.setFirstName(editFirstName.getText().toString());
			trainee.setLastName(editLastName.getText().toString());
			trainee.setCellphone(editCell.getText().toString());
			trainee.setEmail(editEmail.getText().toString());
			trainee.setCityID(city.getCityID());
			trainee.setTrainingClassID(trainingClass.getTrainingClassID());
			if (radioMale.isChecked())
				trainee.setGender(1);
			if (radioFemale.isChecked())
				trainee.setGender(2);
			request.setTrainee(trainee);
			request.setTrainingClassID(trainingClass.getTrainingClassID());
			request.setCityID(city.getCityID());
		default:
			break;
		}
		if (!BaseVolley.checkNetworkOnDevice(ctx))
			return;
        WebSocketUtil.sendRequest(ctx,Statics.ADMIN_ENDPOINT,request,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO response) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         int index = 0;
                        progressBar.setVisibility(View.GONE);
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, response.getMessage());
                            return;
                        }
                        switch (type) {
                            case ADMIN:
                                if (action == ADD_NEW) {
                                    for (AdministratorDTO adm : response
                                            .getAdministratorList()) {
                                        if (adm.getEmail().equalsIgnoreCase(
                                                administrator.getEmail())) {
                                            break;
                                        }
                                        index++;
                                    }
                                    sendMail(response.getAdministratorList()
                                            .get(index).getPassword());
                                } else {
                                    index = -1;
                                }
                                break;
                            case AUTHOR:
                                if (action == ADD_NEW) {
                                    for (AuthorDTO adm : response.getAuthorList()) {
                                        if (adm.getEmail().equalsIgnoreCase(
                                                author.getEmail())) {
                                            break;
                                        }
                                        index++;
                                    }
                                    sendMail(response.getAuthorList().get(index)
                                            .getPassword());
                                } else {
                                    index = -1;
                                }
                                break;
                            case INSTRUCTOR:
                                if (action == ADD_NEW) {
                                    for (InstructorDTO adm : response
                                            .getInstructorList()) {
                                        if (adm.getEmail().equalsIgnoreCase(
                                                instructor.getEmail())) {
                                            break;
                                        }
                                        index++;
                                    }
                                    sendMail(response.getInstructorList()
                                            .get(index).getPassword());
                                } else {
                                    index = -1;
                                }
                                break;
                            case TRAINEE:
                                if (action == ADD_NEW) {
                                    index = 0;
                                    if (response.getCourseTraineeList() != null) {
                                        for (TraineeDTO adm : response.getTraineeList()) {
                                            if (adm.getEmail().equalsIgnoreCase(
                                                    trainee.getEmail())) {
                                                break;
                                            }
                                            index++;
                                        }
                                        sendMail(response.getTraineeList().get(index)
                                                .getPassword());
                                    } else {
                                        Log.e(LOG, "ERROR - ############### - traineeList expected is NULL...cannot send password email");
                                    }
                                } else {
                                    index = -1;
                                }
                                break;

                            default:
                                break;
                        }

                        peopleDialogListener.onRequestFinished(response, index);
                        Log.i(LOG, ".......about to dismiss dialog");
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.errorToast(ctx,message);
                    }
                });
            }
        });
//		BaseVolley.getRemoteData(suffix, request, ctx,
//				new BaseVolley.BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						progressBar.setVisibility(View.GONE);
//						ToastUtil.errorToast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//						peopleDialogListener.onError();
//					}
//
//					int index = 0;
//
//					@Override
//					public void onResponseReceived(ResponseDTO response) {
//						progressBar.setVisibility(View.GONE);
//						if (response.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, response.getMessage());
//							return;
//						}
//						switch (type) {
//						case ADMIN:
//							if (action == ADD_NEW) {
//								for (AdministratorDTO adm : response
//										.getAdministratorList()) {
//									if (adm.getEmail().equalsIgnoreCase(
//											administrator.getEmail())) {
//										break;
//									}
//									index++;
//								}
//								sendMail(response.getAdministratorList()
//										.get(index).getPassword());
//							} else {
//								index = -1;
//							}
//							break;
//						case AUTHOR:
//							if (action == ADD_NEW) {
//								for (AuthorDTO adm : response.getAuthorList()) {
//									if (adm.getEmail().equalsIgnoreCase(
//											author.getEmail())) {
//										break;
//									}
//									index++;
//								}
//								sendMail(response.getAuthorList().get(index)
//										.getPassword());
//							} else {
//								index = -1;
//							}
//							break;
//						case INSTRUCTOR:
//							if (action == ADD_NEW) {
//								for (InstructorDTO adm : response
//										.getInstructorList()) {
//									if (adm.getEmail().equalsIgnoreCase(
//											instructor.getEmail())) {
//										break;
//									}
//									index++;
//								}
//								sendMail(response.getInstructorList()
//										.get(index).getPassword());
//							} else {
//								index = -1;
//							}
//							break;
//						case TRAINEE:
//							if (action == ADD_NEW) {
//                                index = 0;
//                                if (response.getCourseTraineeList() != null) {
//                                    for (TraineeDTO adm : response.getTraineeList()) {
//                                        if (adm.getEmail().equalsIgnoreCase(
//                                                trainee.getEmail())) {
//                                            break;
//                                        }
//                                        index++;
//                                    }
//                                    sendMail(response.getTraineeList().get(index)
//                                            .getPassword());
//                                } else {
//                                    Log.e(LOG, "ERROR - ############### - traineeList expected is NULL...cannot send password email");
//                                }
//							} else {
//								index = -1;
//							}
//							break;
//
//						default:
//							break;
//						}
//
//						peopleDialogListener.onRequestFinished(response, index);
//						Log.i(LOG, ".......about to dismiss dialog");
//						dialog.dismiss();
//
//					}
//				});
	}

	private void animate(View v) {
		Animation a = AnimationUtils.loadAnimation(getActivity(),
				R.anim.grow_fade_in_center);
		a.setDuration(1000);
		v.startAnimation(a);
	}

	private void setFormFields() {
		Log.w(LOG, "setFormFields");
		switch (type) {
		case ADMIN:
			editFirstName.setText(administrator.getFirstName());
			editLastName.setText(administrator.getLastName());
			editEmail.setText(administrator.getEmail());
			editCell.setText(administrator.getCellphone());
			break;
		case AUTHOR:
			editFirstName.setText(author.getFirstName());
			editLastName.setText(author.getLastName());
			editEmail.setText(author.getEmail());
			editCell.setText(author.getCellphone());
			break;
		case INSTRUCTOR:
			editFirstName.setText(instructor.getFirstName());
			editLastName.setText(instructor.getLastName());
			editEmail.setText(instructor.getEmail());
			editCell.setText(instructor.getCellphone());
			break;
		case TRAINEE:
			editFirstName.setText(trainee.getFirstName());
			editLastName.setText(trainee.getLastName());
			editEmail.setText(trainee.getEmail());
			editCell.setText(trainee.getCellphone());
			if (trainee.getGender() == 1) {
				radioMale.setChecked(true);
			}
			if (trainee.getGender() == 2) {
				radioFemale.setChecked(true);
			}

			// set spinners ----
			setProvinceSpinner();
			setClassSpinner();
			if (action == UPDATE) {
				isFirstTime = true;
				if (trainee.getGender() == 1) {
					radioMale.setChecked(true);
				}
				if (trainee.getGender() == 2) {
					radioFemale.setChecked(true);
				}
				if (trainee.getProvinceName() != null) {
					int x = findProvinceIndex(trainee.getProvinceID());
					if (x > -1) {
						provinceSpinner.setSelection(x + 1);
					}
				}

				if (trainee.getTrainingClassName() != null) {
					int x = findClassIndex(trainee.getTrainingClassName());
					classSpinner.setSelection(x + 1);
				}
			}
			break;

		default:
			break;
		}

	}

	private void setProvinceSpinner() {
		Log.w(LOG, "setting provinceSpinner ...");

		final ArrayList<String> tarList = new ArrayList<String>();
		if (provinceList != null) {
			tarList.add(ctx.getResources().getString(R.string.select_prov));
			for (ProvinceDTO p : provinceList) {
				tarList.add(p.getProvinceName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_spinner_item,
					tarList);
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
							cityList = province.getCityList();
							Log.d(LOG, "...province selected: " + province.getProvinceName());
							setCitySpinner();
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
		}

	}

	private void setCitySpinner() {
		final ArrayList<String> tarList = new ArrayList<String>();
		Log.d(LOG, "setting citySpinner ...");
		if (cityList != null) {
			tarList.add(ctx.getResources().getString(R.string.select_city));
			for (CityDTO p : cityList) {
				tarList.add(p.getCityName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					android.R.layout.simple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			citySpinner.setAdapter(dataAdapter);
			Log.w(LOG, "... adapter for citySpinner set, isFirstTime boolean: " + isFirstTime);
			if (isFirstTime) {
				isFirstTime = false;
				
				if (action == UPDATE) {
					int i = findCityIndex(trainee.getCityName());
					if (i > -1) {
						citySpinner.setSelection(i + 1);
					}
				}
			}
			citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (arg2 == 0) {
						city = null;
						return;
					}
					city = cityList.get(arg2 - 1);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}
	}

	private void setClassSpinner() {
		Log.d(LOG, "setting classSpinner ...");

		final ArrayList<String> tarList = new ArrayList<String>();
		if (trainingClassList != null) {
			tarList.add(getActivity().getResources().getString(
					R.string.select_class));
			for (TrainingClassDTO p : trainingClassList) {
				tarList.add(p.getTrainingClassName());
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
					android.R.layout.simple_spinner_item, tarList);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			classSpinner.setAdapter(dataAdapter);
			classSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							if (arg2 == 0) {
								trainingClass = null;
								return;
							}
							trainingClass = trainingClassList.get(arg2 - 1);

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
		}
	}

	private int findProvinceIndex(int id) {
		int found = -1;
		if (provinceList == null) {
			Log.e(LOG, "provinceList is null");
			return found;
		}
		int index = 0;
		for (ProvinceDTO c : provinceList) {
			if (c.getProvinceID() == id) {
				return index;
			}
			index++;
		}
		return found;
	}

	private int findClassIndex(String name) {
		int found = -1;
		if (trainingClassList == null) {
			Log.e(LOG, "classList is null");
			return found;
		}
		int index = 0;
		for (TrainingClassDTO c : trainingClassList) {
			if (c.getTrainingClassName().equalsIgnoreCase(name)) {
				return index;
			}
			index++;
		}
		return found;
	}

	private int findCityIndex(String name) {
		Log.w(LOG, "finding city index for: " + name);
		int found = -1;
		int index = 0;
		for (CityDTO c : cityList) {
			if (c.getCityName().equalsIgnoreCase(name)) {
				Log.e(LOG, "..found city index: " + index);
				return index;
			}
			index++;
		}
		return found;
	}

	private void sendMail(String password) {
		AdministratorDTO admin = SharedUtil.getAdministrator(ctx);
		String name = null;
		String actorEmail = null, app = null;
		switch (type) {
		case ADMIN:
			name = administrator.getFirstName() + " "
					+ administrator.getLastName();
			actorEmail = administrator.getEmail();
			app = ctx.getResources().getString(R.string.administrator);
			break;
		case AUTHOR:
			name = author.getFirstName() + " " + author.getLastName();
			actorEmail = author.getEmail();
			app = ctx.getResources().getString(R.string.author);
			break;
		case INSTRUCTOR:
			name = instructor.getFirstName() + " " + instructor.getLastName();
			actorEmail = instructor.getEmail();
			app = ctx.getResources().getString(R.string.instructor);
			break;
		case TRAINEE:
			name = trainee.getFirstName() + " " + trainee.getLastName();
			actorEmail = trainee.getEmail();
			app = ctx.getResources().getString(R.string.trainee);
			break;

		default:
			break;
		}

		String subject = ctx.getResources().getString(
				R.string.password_email_subject);
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("\n\n");
		sb.append(ctx.getResources().getString(R.string.body)).append("\n\n");
		sb.append(ctx.getResources().getString(R.string.welcome_aboard))
				.append("\n\n\n");
		sb.append("\t").append(password).append("\t").append(app)
				.append("\n\n\n");
		sb.append(ctx.getResources().getString(R.string.regards)).append("\n");
		sb.append(ctx.getResources().getString(R.string.coursemaker_community))
				.append("\n");
		String body = sb.toString();

		Log.i(LOG, body);

		AsyncMailSender.sendMail(admin.getEmail(), admin.getPassword(),
				subject, body, actorEmail, new MailSenderListener() {

					@Override
					public void onMailSent() {
						ToastUtil.toast(
								ctx,
								ctx.getResources().getString(
										R.string.password_email_sent));
					}

					@Override
					public void onMailError() {
						ToastUtil.errorToast(
								ctx,
								ctx.getResources().getString(
										R.string.error_email));
					}
				});
	}

	public static final int ADMIN = 1, AUTHOR = 2, INSTRUCTOR = 3, TRAINEE = 4,
			ADD_NEW = 5, UPDATE = 6;
	private EditText editFirstName, editLastName, editEmail, editCell;
	private RadioButton radioMale, radioFemale;
	private Spinner classSpinner, provinceSpinner, citySpinner;
	private TextView txtHeader;
	Button btnCancel, btnSave;
	private List<ProvinceDTO> provinceList;
	private List<TrainingClassDTO> trainingClassList;
	private List<CityDTO> cityList;
	private CityDTO city;
	private ProvinceDTO province;
	private TraineeDTO trainee;
	private InstructorDTO instructor;
	private AuthorDTO author;
	private AdministratorDTO administrator;
	private int type, action;
	private Context ctx;
	static final String LOG = "PeopleDialog";
	private RequestDTO request;
	private TrainingClassDTO trainingClass;
	PeopleDialogListener peopleDialogListener;

	//
	public void setPeopleDialogListener(PeopleDialogListener listener) {
		peopleDialogListener = listener;
	}

	public void setProvinceList(List<ProvinceDTO> provinceList) {
		this.provinceList = provinceList;
	}

	public void setTrainingClassList(List<TrainingClassDTO> trainingClassList) {
		this.trainingClassList = trainingClassList;
	}

	public void setCityList(List<CityDTO> cityList) {
		this.cityList = cityList;
	}

	public void setTrainee(TraineeDTO trainee) {
		this.trainee = trainee;
	}

	public void setAuthor(AuthorDTO author) {
		this.author = author;
	}

	public void setAdministrator(AdministratorDTO administrator) {
		this.administrator = administrator;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}

	public void setInstructor(InstructorDTO i) {
		this.instructor = i;
	}

	void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editFirstName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editFirstName.getWindowToken(), 0);
	}
}

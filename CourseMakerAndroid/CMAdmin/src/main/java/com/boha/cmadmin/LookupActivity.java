package com.boha.cmadmin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.cmadmin.adapter.HelpTypeAdapter;
import com.boha.cmadmin.adapter.RatingAdapter;
import com.boha.coursemaker.base.BaseVolley;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.HelpTypeDTO;
import com.boha.coursemaker.dto.LookupInterface;
import com.boha.coursemaker.dto.RatingDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.util.SharedUtil;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebSocketUtil;

import java.util.List;

public class LookupActivity extends Activity {

	CompanyDTO company;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_lookup);
		ctx = getApplicationContext();
		processingType = getIntent().getIntExtra("pType", RATING);
		company = SharedUtil.getCompany(ctx);
		setFields();
		setTitle(ctx.getResources().getString(R.string.lookup));
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void getRemoteData() {
		RequestDTO req = new RequestDTO();
		req.setRequestType(RequestDTO.GET_RATING_LIST);
		req.setCompanyID(company.getCompanyID());

		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        response = r;
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        ratingList = response.getRatingList();
                        helpTypeList = response.getHelpTypeList();
                        setList();
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
//		BaseVolley.getRemoteData(Statics.SERVLET_TRAINEE, req, ctx,
//				new BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						setRefreshActionButtonState(false);
//						ToastUtil.errorToast(
//								ctx,
//								ctx.getResources().getString(
//										R.string.error_server_comms));
//
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO r) {
//						setRefreshActionButtonState(false);
//						response = r;
//						if (response.getStatusCode() > 0) {
//							ToastUtil.errorToast(ctx, r.getMessage());
//							return;
//						}
//						ratingList = response.getRatingList();
//						helpTypeList = response.getHelpTypeList();
//						setList();
//					}
//				});
	}

	private void addNewLookup() {
		currentOperation = ADD;
		editLayout.setVisibility(View.VISIBLE);
		switch (processingType) {
		case RATING:
			editNumber.setVisibility(View.VISIBLE);
			break;
		case HELPTYPE:
			editNumber.setVisibility(View.GONE);
			break;

		default:
			break;
		}

		// animate
	}

	private void updateLookup() {
		currentOperation = UPDATE;
		editLayout.setVisibility(View.VISIBLE);
		switch (processingType) {
		case RATING:
			editNumber.setVisibility(View.VISIBLE);
			editName.setText(rating.getRatingName());
			editNumber.setText("" + rating.getRatingNumber());
			break;
		case HELPTYPE:
			editNumber.setVisibility(View.GONE);
			editName.setText(helpType.getHelpTypeName());
			break;

		default:
			break;
		}

		// animate
	}

	@SuppressWarnings("unused")
	private void deleteLookup() {
		currentOperation = DELETE;
		editLayout.setVisibility(View.VISIBLE);
		switch (processingType) {
		case RATING:
			editNumber.setVisibility(View.VISIBLE);
			editName.setText(rating.getRatingName());
			editNumber.setText("" + rating.getRatingNumber());
			break;
		case HELPTYPE:
			editNumber.setVisibility(View.GONE);
			editName.setText(helpType.getHelpTypeName());
			break;

		default:
			break;
		}

		// animate
	}

	ResponseDTO response;

	private void setList() {
		switch (processingType) {
		case RATING:
			if (response.getRatingList().size() == 0) {
				addNewLookup();
				return;
			}
			ratingAdapter = new RatingAdapter(ctx, R.layout.lookup_item,
					ratingList);
			listView.setAdapter(ratingAdapter);
			break;
		case HELPTYPE:
			if (response.getHelpTypeList().size() == 0) {
				addNewLookup();
				return;
			}
			helpTypeAdapter = new HelpTypeAdapter(ctx, R.layout.lookup_item,
					helpTypeList);
			listView.setAdapter(helpTypeAdapter);

			break;

		default:
			break;
		}
		listView.setDividerHeight(5);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (processingType) {
				case RATING:
					rating = ratingList.get(arg2);
					break;
				case HELPTYPE:
					helpType = helpTypeList.get(arg2);
					break;

				default:
					break;
				}
			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (processingType) {
				case RATING:
					rating = ratingList.get(arg2);
					break;
				case HELPTYPE:
					helpType = helpTypeList.get(arg2);
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lookup, menu);
		mMenu = menu;
		controlMenu();
		getRemoteData();
		return true;
	}

	private void controlMenu() {
		switch (processingType) {
		case RATING:
			//remove rating from menu 
			mMenu.getItem(1).setVisible(false);
			mMenu.getItem(2).setVisible(true);
			break;
		case HELPTYPE:
			//remove helptype from menu 
			mMenu.getItem(1).setVisible(true);
			mMenu.getItem(2).setVisible(false);
			break;

		default:
			break;
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_rating:
			processingType = RATING;
			setForm();
			setList();
			controlMenu();
			return true;
		case R.id.menu_helptype:
			processingType = HELPTYPE;
			setForm();
			setList();
			controlMenu();
			return true;
		case R.id.menu_info:
			underConstruction();
			return true;
		case R.id.menu_back:
			finish();
			return true;
            case android.R.id.home:
                finish();
                return true;
		}
		return false;
	}
	@Override
	public void onPause() {
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		super.onPause();
	}
	Menu mMenu;
	ContextMenu contextMenu;
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (mMenu != null) {
			final MenuItem refreshItem = mMenu.findItem(R.id.menu_back);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.action_bar_progess);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.lookup_context, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		switch (processingType) {
		case RATING:
			rating = ratingList.get(info.position);
			break;
		case HELPTYPE:
			helpType = helpTypeList.get(info.position);
			break;

		default:
			break;
		}

		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.w(LOG,
				"onContextItemSelected - select option ..." + item.getTitle());
		switch (item.getItemId()) {
		case R.id.menu_update:
			currentOperation = UPDATE;
			updateLookup();
			return true;
		case R.id.menu_delete:
			currentOperation = DELETE;
			underConstruction();
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	private void submit() {
		RequestDTO req = new RequestDTO();
		switch (processingType) {
		case RATING:

			if (editName.getText().toString().isEmpty()) {
				ToastUtil.toast(ctx,
						ctx.getResources()
								.getString(R.string.enter_rating_name));
				return;
			}
			if (editNumber.getText().toString().isEmpty()) {
				ToastUtil.toast(
						ctx,
						ctx.getResources().getString(
								R.string.enter_rating_number));
				return;
			}
			RatingDTO r = new RatingDTO();
			r.setCompanyID(company.getCompanyID());
			r.setRatingName(editName.getText().toString());
			r.setRatingNumber(Integer.parseInt(editNumber.getText().toString()));
			req.setRating(r);
			switch (currentOperation) {
			case ADD:
				req.setRequestType(RequestDTO.ADD_RATING);
				break;
			case UPDATE:
				req.setRequestType(RequestDTO.UPDATE_RATING);
				r.setRatingID(rating.getRatingID());
				break;
			case DELETE:
				req.setRequestType(RequestDTO.DELETE_RATING);
				r.setRatingID(rating.getRatingID());
				break;
			}
			break;
		case HELPTYPE:
			if (editName.getText().toString().isEmpty()) {
				ToastUtil.toast(
						ctx,
						ctx.getResources().getString(
								R.string.enter_helptype_name));
				return;
			}
			HelpTypeDTO h = new HelpTypeDTO();
			h.setCompanyID(company.getCompanyID());
			h.setHelpTypeName(editName.getText().toString());
			req.setHelpType(h);
			switch (currentOperation) {
			case ADD:
				req.setRequestType(RequestDTO.ADD_HELPTYPE);
				break;
			case UPDATE:
				req.setRequestType(RequestDTO.UPDATE_HELPTYPE);			
				h.setHelpTypeID(helpType.getHelpTypeID());
				break;
			case DELETE:
				req.setRequestType(RequestDTO.DELETE_HELPTYPE);
				h.setHelpTypeID(helpType.getHelpTypeID());
				break;
			}
			break;
		}
		if (!BaseVolley.checkNetworkOnDevice(ctx)) return;
		
		setRefreshActionButtonState(true);
        WebSocketUtil.sendRequest(ctx, Statics.ADMIN_ENDPOINT, req, new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        response = r;
                        if (response.getStatusCode() > 0) {
                            ToastUtil.errorToast(ctx, r.getMessage());
                            return;
                        }
                        clearForm();
                        ToastUtil.toast(ctx, ctx.getResources().getString(R.string.data_saved));
                        switch(processingType) {
                            case RATING:
                                switch (currentOperation) {
                                    case ADD:
                                        ratingList.add(0, r.getRatingList().get(0));
                                        ratingAdapter.notifyDataSetChanged();
                                        break;
                                    case UPDATE:
                                        int index = getRatingIndex(rating);
                                        if (index > -1) {
                                            ratingList.get(index).setRatingName(
                                                    r.getRatingList().get(0).getRatingName());
                                            ratingList.get(index).setRatingNumber(
                                                    r.getRatingList().get(0).getRatingNumber());
                                            ratingAdapter.notifyDataSetChanged();
                                        }
                                        break;
                                    case DELETE:

                                        break;
                                }
                                break;
                            case HELPTYPE:
                                switch (currentOperation) {
                                    case ADD:
                                        helpTypeList.add(0, r.getHelpTypeList().get(0));
                                        helpTypeAdapter.notifyDataSetChanged();
                                        break;
                                    case UPDATE:
                                        int index = getHelpTypeIndex(helpType);
                                        if (index > -1) {
                                            helpTypeList.get(index).setHelpTypeName(
                                                    r.getHelpTypeList().get(0).getHelpTypeName());

                                            helpTypeAdapter.notifyDataSetChanged();
                                        }
                                        break;
                                    case DELETE:

                                        break;
                                }
                                break;
                        }
                        setList();
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
//		BaseVolley.getRemoteData(Statics.SERVLET_ADMIN, req, ctx, new BohaVolleyListener() {
//
//			@Override
//			public void onVolleyError(VolleyError error) {
//				setRefreshActionButtonState(false);
//				ToastUtil.errorToast(
//						ctx,
//						ctx.getResources().getString(
//								R.string.error_server_comms));
//
//			}
//
//			@Override
//			public void onResponseReceived(ResponseDTO r) {
//				setRefreshActionButtonState(false);
//				response = r;
//				if (response.getStatusCode() > 0) {
//					ToastUtil.errorToast(ctx, r.getMessage());
//					return;
//				}
//				clearForm();
//				ToastUtil.toast(ctx, ctx.getResources().getString(R.string.data_saved));
//				switch(processingType) {
//				case RATING:
//					switch (currentOperation) {
//					case ADD:
//						ratingList.add(0, r.getRatingList().get(0));
//						ratingAdapter.notifyDataSetChanged();
//						break;
//					case UPDATE:
//						int index = getRatingIndex(rating);
//						if (index > -1) {
//							ratingList.get(index).setRatingName(
//									r.getRatingList().get(0).getRatingName());
//							ratingList.get(index).setRatingNumber(
//									r.getRatingList().get(0).getRatingNumber());
//							ratingAdapter.notifyDataSetChanged();
//						}
//						break;
//					case DELETE:
//
//						break;
//					}
//					break;
//				case HELPTYPE:
//					switch (currentOperation) {
//					case ADD:
//						helpTypeList.add(0, r.getHelpTypeList().get(0));
//						helpTypeAdapter.notifyDataSetChanged();
//						break;
//					case UPDATE:
//						int index = getHelpTypeIndex(helpType);
//						if (index > -1) {
//							helpTypeList.get(index).setHelpTypeName(
//									r.getHelpTypeList().get(0).getHelpTypeName());
//
//							helpTypeAdapter.notifyDataSetChanged();
//						}
//						break;
//					case DELETE:
//
//						break;
//					}
//					break;
//				}
//				setList();
//
//			}
//		});
		
	}
	
	private void clearForm() {
		editName.setText("");
		editNumber.setText("");
	}

	private int getRatingIndex(RatingDTO r) {
		int index = 0;
		for (RatingDTO rr : response.getRatingList()) {
			if (rr.getRatingID() == r.getRatingID()) {
				return index;
			}
			index++;
		}
		
		return -1;
	}
	private int getHelpTypeIndex(HelpTypeDTO r) {
		int index = 0;
		for (HelpTypeDTO rr : response.getHelpTypeList()) {
			if (rr.getHelpTypeID() == r.getHelpTypeID()) {
				return index;
			}
			index++;
		}
		
		return -1;
	}
	private void setForm() {
		editName.setText("");
		switch (processingType) {
		case RATING:
			txtHeader.setText(ctx.getResources().getString(R.string.rating_types));
			editNumber.setVisibility(View.VISIBLE);		
			editNumber.setText("");
			break;
		case HELPTYPE:
			txtHeader.setText(ctx.getResources().getString(R.string.help_types));
			editNumber.setVisibility(View.GONE);
			break;

		default:
			break;
		}
		Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
		a.setDuration(1000);
		editLayout.startAnimation(a);
	}
	private void setFields() {
		editName = (EditText) findViewById(R.id.LKP_name);
		editNumber = (EditText) findViewById(R.id.LKP_number);
		editLayout = findViewById(R.id.LKP_mainLayout);
		btnSave = (Button) findViewById(R.id.LKP_btnSave);
		btnCancel = (Button) findViewById(R.id.LKP_btnCancel);
		listView = (ListView) findViewById(R.id.LKP_listView);
		txtHeader = (TextView) findViewById(R.id.LKP_header);

		editLayout.setVisibility(View.VISIBLE);
		setForm();
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentOperation == 0) {
					currentOperation = ADD;
				}
				hideKeyboard();
				submit();
			}
		});
	}

	private void underConstruction() {
		ToastUtil.toast(ctx, "Feature under construction. Watch the space!");
	}
	void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
	}

	EditText editName, editNumber;
	Button btnSave, btnCancel;
	View editLayout;
	TextView txtHeader;
	Context ctx;
	static final String LOG = "LookupActivity";
	int currentOperation = ADD, processingType;
	static final int ADD = 1, UPDATE = 2, DELETE = 3;
	LookupInterface lookupInterface;
	List<RatingDTO> ratingList;
	List<HelpTypeDTO> helpTypeList;
	HelpTypeDTO helpType;
	RatingDTO rating;
	ListView listView;
	RatingAdapter ratingAdapter;
	HelpTypeAdapter helpTypeAdapter;
	public static final int HELPTYPE = 11, RATING = 33;

}

package com.boha.cmauthor.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.cmauthor.R;
import com.boha.coursemaker.dto.ActivityDTO;
import com.boha.coursemaker.dto.AuthorDTO;
import com.boha.coursemaker.dto.CategoryDTO;
import com.boha.coursemaker.dto.CompanyDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.dto.LessonResourceDTO;
import com.boha.coursemaker.dto.ObjectiveDTO;
import com.boha.coursemaker.dto.RequestDTO;
import com.boha.coursemaker.dto.ResponseDTO;
import com.boha.coursemaker.listeners.PageInterface;
import com.boha.coursemaker.util.Statics;
import com.boha.coursemaker.util.ToastUtil;
import com.boha.coursemaker.util.WebCheck;
import com.boha.coursemaker.util.WebCheckResult;
import com.boha.coursemaker.util.WebSocketUtil;
import com.boha.coursemaker.util.actor.AuthorUtil;

import java.util.List;

public abstract class AbstractBuilder extends Fragment implements PageInterface {

	Context ctx;
	View view;
	AuthorDTO author;
	CategoryDTO category;
	CourseDTO course;
	ResponseDTO response;
	CompanyDTO company;
	ObjectiveDTO lessonObjective;
	LessonResourceDTO lessonResource;
	ActivityDTO activity;
	List<CourseDTO> courseList;
	List<ObjectiveDTO> lessonObjectiveList;
	List<ActivityDTO> activityList;
	List<LessonResourceDTO> lessonResourceList;
	List<CategoryDTO> categoryList;
	boolean isUpdate;
	EditText editName, editDescription, editSequence;
	Button btnSave, btnCancel;
	ListView listView;
	View addLayout;
	TextView label;
	int priorityFlag;

	public abstract void setFields();

	public abstract void setList();

	public abstract void networkCallDone(boolean isOK);

	public void openAddLayout() {
		Animation an = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		an.setDuration(1000);
		if (addLayout != null) {
			editName.setText("");
			editDescription.setText("");
			addLayout.setVisibility(View.VISIBLE);
			addLayout.startAnimation(an);
		}

	}

	public void openAddLayout(String name, String desc) {
		Animation an = AnimationUtils.loadAnimation(ctx,
				R.anim.grow_fade_in_center);
		an.setDuration(1000);
		if (addLayout != null) {
            if (name != null) {
                editName.setText(name);
            } else {
                editName.setText("");
            }
			if (desc != null) {
				editDescription.setText(desc);
			} else {
                editDescription.setText("");
            }
			addLayout.setVisibility(View.VISIBLE);
			label.setText(desc);
			addLayout.startAnimation(an);
		}

	}

	public void closeAddLayout() {
		if (addLayout == null || addLayout.getVisibility() == View.GONE) {
			return;
		}
		Animation an = AnimationUtils.loadAnimation(ctx, R.anim.push_up_out);
		an.setDuration(1000);
		an.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				addLayout.setVisibility(View.GONE);
			}
		});
		if (addLayout != null) {
			addLayout.startAnimation(an);
		}
	}

	static final String LOG = "AbstractBuilder";
	int mType;

	public void sendRequest(int type, String suffix) {
		Log.d(LOG, "....sendRequest ...type: " + type);
		this.mType = type;
		RequestDTO request = new RequestDTO();
		WebCheckResult r = WebCheck.checkNetworkAvailability(ctx);
		if (r.isNetworkUnavailable()) {
			ToastUtil.errorToast(ctx,
					getResources()
							.getString(R.string.error_network_unavailable));
			return;
		}
		request.setRequestType(type);
        request.setCompanyID(company.getCompanyID());
		switch (type) {

		case RequestDTO.ADD_CATEGORY:
            request.setCategory(category);
			break;
		case RequestDTO.REGISTER_COURSE:
            request.setCourse(course);
            request.setAuthorID(author.getAuthorID());
			break;
		case RequestDTO.ADD_OBJECTIVES:
            request.setObjective(lessonObjective);
            request.setCourseID(course.getCourseID());

			break;
		case RequestDTO.ADD_ACTIVITIES:
            request.setActivity(activity);
            request.setCourseID(course.getCourseID());
			break;

		case RequestDTO.ADD_RESOURCES:
            request.setActivityID(activity.getActivityID());
            request.setLessonResource(lessonResource);
			break;
		case RequestDTO.GET_COURSE_LIST_BY_CATEGORY: // by category
			if (category != null) {
                request.setCategoryID(category.getCategoryID());
			}
			break;
		case RequestDTO.GET_LESSON_LIST_BY_COURSE: // by course
			request = AuthorUtil.getLessonsByCourse(ctx, course.getCourseID());
			break;
		case RequestDTO.GET_OBJECTIVE_LIST_BY_COURSE: // by lesson
			if (course != null) {
				request = AuthorUtil.getObjectivesByCourse(ctx,
						course.getCourseID());
			}
			break;
		case RequestDTO.GET_ACTIVITY_LIST_BY_LESSON: // by lesson

			break;
		case RequestDTO.GET_RESOURCE_LIST_BY_LESSON: // by lesson

			break;
		case RequestDTO.GET_CATEGORY_LIST_BY_COMPANY:
			request = AuthorUtil.getCategoryList(ctx, company.getCompanyID());
			break;
		case RequestDTO.UPDATE_CATEGORY:
			request = AuthorUtil.updateCategory(ctx, category);
			break;
		case RequestDTO.UPDATE_COURSE:
			request = AuthorUtil
					.updateCourse(ctx, course, author.getAuthorID());
			break;
		case RequestDTO.UPDATE_LESSON:
			break;

		case RequestDTO.UPDATE_ACTIVITIES:
			request = AuthorUtil.updateActivities(ctx, shuffledActivityList);
			break;
		case RequestDTO.UPDATE_OBJECTIVES:
			request = AuthorUtil.updateObjectives(ctx, shuffledObjectiveList);
			break;
		case RequestDTO.DELETE_LESSON:

			break;
		case RequestDTO.DELETE_ACTIVITIES:

			break;
		case RequestDTO.DELETE_OBJECTIVES:

			break;
		case RequestDTO.DELETE_COURSE:
			request = AuthorUtil.deleteCourse(ctx, course.getCourseID(),
					author.getAuthorID());
			break;
		case RequestDTO.DELETE_CATEGORY:
			request = AuthorUtil.deleteCategory(ctx, category);
			break;

		default:
			break;
		}
        WebSocketUtil.sendRequest(ctx,Statics.AUTHOR_ENDPOINT,request,new WebSocketUtil.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO r) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        networkCallDone(true);
                        response = r;
                        if (response != null) {
                            if (response.getStatusCode() > 0) {
                                ToastUtil.errorToast(ctx, response.getMessage());
                                return;
                            }
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        networkCallDone(true);
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }


        });

//		BaseVolley.sendRequest(Statics.SERVLET_AUTHOR, request, ctx,
//				new BaseVolley.BohaVolleyListener() {
//
//					@Override
//					public void onVolleyError(VolleyError error) {
//						Log.d("AbstractBuilder", "We have a Volley error ....");
//						networkCallDone(false);
//					}
//
//					@Override
//					public void onResponseReceived(ResponseDTO resp) {
//						networkCallDone(true);
//						response = resp;
//						if (response != null) {
//							if (response.getStatusCode() > 0) {
//								ToastUtil.errorToast(ctx, response.getMessage());
//								return;
//							}
//						}
//
//						setList();
//
//					}
//				});

	}

	public void log(String message) {
		Log.d("AbstractBuilder", message);
	}

	public abstract void onLocalDataTaskDone();

	int type;

	public CategoryDTO getCategory() {
		return category;
	}

	public void setCategory(CategoryDTO category) {
		this.category = category;
	}



	public ResponseDTO getResponse() {
		return response;
	}

	public void setResponse(ResponseDTO response) {
		this.response = response;
	}

	public ObjectiveDTO getObjective() {
		return lessonObjective;
	}

	public void setObjective(ObjectiveDTO lessonObjective) {
		this.lessonObjective = lessonObjective;
	}

	public ActivityDTO getActivityDTO() {
		return activity;
	}

	public void setActivityDTO(ActivityDTO activity) {
		this.activity = activity;
	}

	public List<ObjectiveDTO> getObjectiveList() {
		return lessonObjectiveList;
	}

	public void setObjectiveList(
			List<ObjectiveDTO> lessonObjectiveList) {
		this.lessonObjectiveList = lessonObjectiveList;
	}

	public List<ActivityDTO> getActivityList() {
		return activityList;
	}

	public void setActivityList(List<ActivityDTO> activityList) {
		this.activityList = activityList;
	}


	public List<LessonResourceDTO> getLessonResourceList() {
		return lessonResourceList;
	}

	public void setLessonResourceList(List<LessonResourceDTO> lessonResourceList) {
		this.lessonResourceList = lessonResourceList;
	}

	public AuthorDTO getAuthor() {
		return author;
	}

	public void setAuthor(AuthorDTO author) {
		this.author = author;
	}

	public CourseDTO getCourse() {
		return course;
	}

	public CompanyDTO getCompany() {
		return company;
	}

	public void setCompany(CompanyDTO company) {
		this.company = company;
	}

	public LessonResourceDTO getLessonResource() {
		return lessonResource;
	}

	public void setLessonResource(LessonResourceDTO lessonResource) {
		this.lessonResource = lessonResource;
	}

	List<ActivityDTO> shuffledActivityList;
	List<ObjectiveDTO> shuffledObjectiveList;

	public List<CourseDTO> getCourseList() {
		return courseList;
	}

	public void setCourseList(List<CourseDTO> courseList) {
		this.courseList = courseList;
	}

	public List<ActivityDTO> getShuffledActivityList() {
		return shuffledActivityList;
	}

	public void setShuffledActivityList(List<ActivityDTO> shuffledActivityList) {
		this.shuffledActivityList = shuffledActivityList;
	}

	public List<ObjectiveDTO> getShuffledObjectiveList() {
		return shuffledObjectiveList;
	}

	public void setShuffledObjectiveList(
			List<ObjectiveDTO> shuffledObjectiveList) {
		this.shuffledObjectiveList = shuffledObjectiveList;
	}
}

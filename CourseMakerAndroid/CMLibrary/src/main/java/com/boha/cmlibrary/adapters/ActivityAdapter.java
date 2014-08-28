package com.boha.cmlibrary.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boha.cmlibrary.R;
import com.boha.coursemaker.dto.CourseTraineeActivityDTO;
import com.boha.coursemaker.dto.InstructorRatingDTO;
import com.boha.coursemaker.dto.TraineeRatingDTO;
import com.boha.coursemaker.util.Statics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityAdapter extends ArrayAdapter<CourseTraineeActivityDTO> {

    public interface ActivityAdapterListener {
        public void onRatingRequested(CourseTraineeActivityDTO cta);
        public void onHelpRequested(CourseTraineeActivityDTO cta);
    }
	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<CourseTraineeActivityDTO> mList;
	private Context ctx;
	private boolean hideDescription, hideHelp;
    private ActivityAdapterListener activityAdapterListener;

	public ActivityAdapter(Context context, int textViewResourceId,
			List<CourseTraineeActivityDTO> list,
            boolean hideDescription, boolean hideHelp, ActivityAdapterListener activityAdapterListener) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
        this.hideHelp = hideHelp;
        this.activityAdapterListener = activityAdapterListener;
		ctx = context;
		this.hideDescription = hideDescription;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem v;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            v = new ViewHolderItem();
            v.txtName = (TextView) convertView
                    .findViewById(R.id.AR_txtActName);
            v.txtDesc = (TextView) convertView
                    .findViewById(R.id.AR_txtActDesc);
            v.txtPriority = (TextView) convertView
                    .findViewById(R.id.AR_txtPriority);
            v.txtDate = (TextView) convertView
                    .findViewById(R.id.AR_completionDate);
            v.txtTraineeRating = (TextView) convertView
                    .findViewById(R.id.AR_tRating);
            v.txtInsRating = (TextView) convertView
                    .findViewById(R.id.AR_instructorRating);
            v.txtMessage = (TextView) convertView
                    .findViewById(R.id.AR_completeMsg);
            v.txtTraineeRatingCount = (TextView) convertView
                    .findViewById(R.id.AR_tRatingCount);
            v.txtInsRatingCount = (TextView) convertView
                    .findViewById(R.id.AR_instructorRatingCount);
            v.imgHelp = (ImageView) convertView
                    .findViewById(R.id.AR_imageHelp);
            v.imgRating = (ImageView) convertView
                    .findViewById(R.id.AR_imageRating);
            v.btnLayout = convertView.findViewById(R.id.AR_btnLayout);
            v.trLayout1 = convertView.findViewById(R.id.AR_traineeLayout);
            v.trLayout2 = convertView.findViewById(R.id.AR_traineeLayout2);
            v.insLayout1 = convertView.findViewById(R.id.AR_insLayout);
            v.insLayout2 = convertView.findViewById(R.id.AR_insLayout2);
            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }


		final CourseTraineeActivityDTO p = mList.get(position);

		v.txtName.setText(p.getActivity().getActivityName());
		if (hideDescription) {
			v.txtDesc.setVisibility(View.GONE);
		} else {
            v.txtDesc.setVisibility(View.VISIBLE);
		}
		if (p.getActivity().getDescription() == null || p.getActivity().getDescription().isEmpty()) {
            v.txtDesc.setText(ctx.getResources().getString(R.string.no_description));
		} else {
            v.txtDesc.setText(p.getActivity().getDescription());
        }
		if (p.getCompletedFlag() > 0) {
            v.txtMessage.setText(ctx.getResources().getString(R.string.task_complete));
			v.txtMessage.setTextColor(ctx.getResources().getColor(R.color.green));
			if (p.getCompletionDate() > 0) {
				Date d = new Date(p.getCompletionDate());
				v.txtDate.setText(sdf.format(d));
                v.txtDate.setVisibility(View.VISIBLE);
			} else {
                v.txtDate.setVisibility(View.GONE);
			}
			Statics.setRobotoFontBold(ctx, v.txtMessage);
		} else {
            v.txtMessage.setText(ctx.getResources().getString(R.string.task_incomplete));
            v.txtMessage.setTextColor(ctx.getResources().getColor(R.color.black));
			v.txtDate.setVisibility(View.GONE);
			Statics.setRobotoItalic(ctx, v.txtMessage);
		}
		//v.txtTraineeRating.setText(df2.format(getTraineeRatingAverage(p)) + "%");
        if (p.getTraineeRatingList() != null && !p.getTraineeRatingList().isEmpty()) {
            v.txtTraineeRating.setText(df2.format(p.getTraineeRatingList().get(0).getRating().getRatingNumber()));
        } else {
            v.trLayout1.setVisibility(View.GONE);
            v.trLayout2.setVisibility(View.GONE);
        }
        if (p.getInstructorRatingList() != null && !p.getInstructorRatingList().isEmpty()) {
            v.txtInsRating.setText(df2.format(p.getInstructorRatingList().get(0).getRating().getRatingNumber()));
        } else {
            v.insLayout1.setVisibility(View.GONE);
            v.insLayout2.setVisibility(View.GONE);
        }

		Statics.setRobotoFontBold(ctx, v.txtName);
		Statics.setRobotoFontBold(ctx, v.txtPriority);
		Statics.setRobotoFontLight(ctx, v.txtDesc);
        if (p.getCompletedFlag() > 0) {
            v.btnLayout.setVisibility(View.GONE);
        } else {
            v.btnLayout.setVisibility(View.VISIBLE);
        }
		if (position > 9) {
			v.txtPriority.setText("" + (position + 1));
		} else {
			v.txtPriority.setText("0" + (position + 1));
		}
        if (hideHelp) {
            v.imgHelp.setVisibility(View.GONE);
        }
        v.imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityAdapterListener.onHelpRequested(p);
            }
        });
        v.imgRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityAdapterListener.onRatingRequested(p);
            }
        });
        if (p.getTraineeRatingList() == null
                || p.getTraineeRatingList().isEmpty()) {
            v.txtTraineeRatingCount.setText("0");
        } else {
            v.txtTraineeRatingCount.setText("" + p.getTraineeRatingList().size());
        }
        if (p.getInstructorRatingList() == null || p.getInstructorRatingList().isEmpty()) {
            v.txtInsRatingCount.setText("0");
        } else {
            v.txtInsRatingCount.setText("" + p.getInstructorRatingList().size());
        }
		animatePriority(v.txtPriority);
		return (convertView);
	}

	private double getInstructorRatingAverage(CourseTraineeActivityDTO p) {
		int totalInstructorTotal = 0;
		if (p.getInstructorRatingList() == null || p.getInstructorRatingList().isEmpty()) {
			return 0;
		}
		for (InstructorRatingDTO tr : p.getInstructorRatingList()) {
			totalInstructorTotal += tr.getRating().getRatingNumber();
		}
		
		Double d = Double.valueOf(totalInstructorTotal);
		Double t = Double.valueOf(p.getInstructorRatingList().size());
		Double avg = (d/t);
		
		return avg.doubleValue();
	}
	private double getTraineeRatingAverage(CourseTraineeActivityDTO p) {
		int totalTraineeTotal = 0;
		if (p.getTraineeRatingList() == null || p.getTraineeRatingList().isEmpty()) {
			return 0;
		}
        for (TraineeRatingDTO tr : p.getTraineeRatingList()) {
			totalTraineeTotal += tr.getRating().getRatingNumber();
		}
		Double d = Double.valueOf(totalTraineeTotal);
		Double t = Double.valueOf(p.getTraineeRatingList().size());
		Double avg = (d/t);
		
		return avg.doubleValue();
	}
	public void animateView(final View view) {
		Animation a  = AnimationUtils.loadAnimation(
				ctx, R.anim.grow_fade_in);
		a.setDuration(500);		
		view.startAnimation(a);
	}

	static final DecimalFormat df1 = new DecimalFormat("###,###,###,###");
	static final DecimalFormat df2 = new DecimalFormat("###,###,###.#");
	private static final Locale locale = Locale.getDefault();
	private static final SimpleDateFormat sdf = new  SimpleDateFormat("dd MMM yyyy HH:mm", locale);

    static class ViewHolderItem {
        TextView txtName, txtDesc, txtMessage, txtDate,
        txtTraineeRatingCount, txtInsRatingCount;
        TextView txtPriority, txtTraineeRating, txtInsRating;
        ImageView imgHelp, imgRating;
        View btnLayout, trLayout1, trLayout2, insLayout1, insLayout2;

    }
    private void animatePriority(TextView txt) {
        final ObjectAnimator an = ObjectAnimator.ofFloat(txt, View.SCALE_X, 0);
        an.setRepeatCount(1);
        an.setDuration(100);
        an.setRepeatMode(ValueAnimator.REVERSE);
        an.start();
    }
}

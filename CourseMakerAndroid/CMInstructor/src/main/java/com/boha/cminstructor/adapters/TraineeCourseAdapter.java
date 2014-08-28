package com.boha.cminstructor.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.cminstructor.R;
import com.boha.coursemaker.dto.TrainingClassCourseDTO;
import com.boha.coursemaker.util.Statics;

import java.util.List;

public class TraineeCourseAdapter extends ArrayAdapter<TrainingClassCourseDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<TrainingClassCourseDTO> mList;
    private Context ctx;

    public TraineeCourseAdapter(Context context, int textViewResourceId,
                                List<TrainingClassCourseDTO> list) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View view;
    //TODO - refactor to ViewHolder pattern

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem v;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            v = new ViewHolderItem();
            v.txtName = (TextView) convertView
                    .findViewById(R.id.CRSITEM_name);
            v.txtDesc = (TextView) convertView
                    .findViewById(R.id.CRSITEM_desc);
            v.txtPriority = (TextView) convertView
                    .findViewById(R.id.CRSITEM_priority);
            v.txtCategory = (TextView) convertView
                    .findViewById(R.id.CRSITEM_cat);
            v.txtCount = (TextView) convertView
                    .findViewById(R.id.CRSITEM_count);
            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }


        TrainingClassCourseDTO classCourse = mList.get(position);

        v.txtName.setText(classCourse.getCourse().getCourseName());
        v.txtDesc.setText(classCourse.getCourse().getDescription());
        v.txtCount.setText("" + classCourse.getNumberOfActivities());
        v.txtCategory.setText(classCourse.getCourse().getCategory().getCategoryName());

        Statics.setRobotoFontBold(ctx, v.txtName);
        Statics.setRobotoFontLight(ctx, v.txtDesc);


        if (classCourse.getCourse().getPriorityFlag() > 9) {
            v.txtPriority.setText("" + (classCourse.getCourse().getPriorityFlag()));
        } else {
            v.txtPriority.setText("0" + (classCourse.getCourse().getPriorityFlag()));
        }


        //RowColor.setColor(view, position);
        animateText(v.txtCount);

        return (convertView);
    }

    private void animateText(TextView txt) {
        final ObjectAnimator an = ObjectAnimator.ofFloat(txt, View.SCALE_X, 0);
        an.setRepeatCount(1);
        an.setDuration(100);
        an.setRepeatMode(ValueAnimator.REVERSE);
        an.start();
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx,
                R.anim.grow_fade_in);
        a.setDuration(500);
        view.startAnimation(a);
    }

    static class ViewHolderItem {
        TextView txtName, txtDesc;
        TextView txtPriority, txtCount, txtCategory;

    }
}

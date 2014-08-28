package com.boha.cmauthor.adapter;

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

import com.boha.cmauthor.R;
import com.boha.coursemaker.dto.CategoryDTO;
import com.boha.coursemaker.dto.CourseDTO;
import com.boha.coursemaker.util.Statics;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<CategoryDTO> {

    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<CategoryDTO> mList;
    private Context ctx;

    public CategoryAdapter(Context context, int textViewResourceId,
                           List<CategoryDTO> list) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;

        mList = list;
        ctx = context;
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
                    .findViewById(R.id.CATITEM_categoryName);
            v.txtCount1 = (TextView) convertView
                    .findViewById(R.id.CATITEM_count);
            v.txtCount2 = (TextView) convertView
                    .findViewById(R.id.CATITEM_countx);
            v.txtPriority = (TextView) convertView
                    .findViewById(R.id.CATITEM_priority);
            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }

        final CategoryDTO p = mList.get(position);
        v.txtName.setText(p.getCategoryName());
        int count = 0;
        if (p.getCourseList() != null) {
            if (p.getCourseList().size() < 10) {
                v.txtCount1.setText("0" + p.getCourseList().size());
            } else {
                v.txtCount1.setText("" + p.getCourseList().size());
            }
            for (CourseDTO d : p.getCourseList()) {
                count += d.getActivityList().size();
            }
            v.txtCount2.setText("" + count);
        } else {
            v.txtCount1.setText("00");
            v.txtCount2.setText("00");
        }
        v.txtPriority.setText("" + p.getPriorityFlag());
        Statics.setRobotoFontBold(ctx, v.txtName);

        animatePriority(v.txtPriority);


        return (convertView);
    }

    private void animatePriority(TextView txt) {
        final ObjectAnimator an = ObjectAnimator.ofFloat(txt, View.SCALE_X, 0);
        an.setRepeatCount(1);
        an.setDuration(100);
        an.setRepeatMode(ValueAnimator.REVERSE);
        an.start();
    }
    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(
                ctx, R.anim.grow_fade_in_center);
        a.setDuration(500);
        view.startAnimation(a);
    }

    static class ViewHolderItem {
        TextView txtName;
        TextView txtPriority, txtCount1, txtCount2;

    }
}

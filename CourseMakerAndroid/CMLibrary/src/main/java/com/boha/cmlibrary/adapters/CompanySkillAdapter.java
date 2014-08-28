package com.boha.cmlibrary.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.boha.cmlibrary.R;
import com.boha.coursemaker.dto.SkillDTO;
import com.boha.coursemaker.util.Statics;

import java.util.List;

public class CompanySkillAdapter extends ArrayAdapter<SkillDTO> {


	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<SkillDTO> mList;
	private Context ctx;

	public CompanySkillAdapter(Context context, int textViewResourceId,
                               List<SkillDTO> list) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	View view;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem v;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            v = new ViewHolderItem();
            v.txtName = (TextView) convertView
                    .findViewById(R.id.CSK_txtSkillName);
            v.txtNumber = (TextView) convertView
                    .findViewById(R.id.CSK_txtNumber);
            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }
		final SkillDTO skill = mList.get(position);
		v.txtNumber.setText("" + (position + 1));
        v.txtName.setText(skill.getSkillName());
		
		Statics.setRobotoFontBold(ctx, v.txtName);
		animateText(v.txtNumber);

		return (convertView);
	}

    private void animateText(TextView txt) {
        final ObjectAnimator an = ObjectAnimator.ofFloat(txt, View.SCALE_X, 0);
        an.setRepeatCount(1);
        an.setDuration(40);
        an.setRepeatMode(ValueAnimator.REVERSE);
        an.start();
    }


    static class ViewHolderItem {
        TextView txtName, txtNumber;
        Spinner spinner;
        CheckBox chkBox;

    }
}

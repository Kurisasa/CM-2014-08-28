package com.boha.cmlibrary.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.cmlibrary.R;
import com.boha.coursemaker.dto.TraineeSkillDTO;
import com.boha.coursemaker.util.Statics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TraineeSkillAdapter extends ArrayAdapter<TraineeSkillDTO> {


	private final LayoutInflater mInflater;
	private final int mLayoutRes;
	private List<TraineeSkillDTO> mList;
	private Context ctx;

	public TraineeSkillAdapter(Context context, int textViewResourceId,
                               List<TraineeSkillDTO> list) {
		super(context, textViewResourceId, list);
		this.mLayoutRes = textViewResourceId;
		mList = list;
		ctx = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	View view;
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem v;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            v = new ViewHolderItem();
            v.txtName = (TextView) convertView
                    .findViewById(R.id.TSK_ITEM_txtSkillname);
            v.txtNumber = (TextView) convertView
                    .findViewById(R.id.TSK_ITEM_txtNumber);
            v.txtLevel = (TextView) convertView
                    .findViewById(R.id.TSK_ITEM_txtLevel);
            v.txtLevelName = (TextView) convertView
                    .findViewById(R.id.TSK_ITEM_txtLevelName);
            convertView.setTag(v);
        } else {
            v = (ViewHolderItem) convertView.getTag();
        }
		final TraineeSkillDTO ic = mList.get(position);

		v.txtNumber.setText("" + (position + 1));
        v.txtName.setText(ic.getSkillName());
        v.txtLevel.setText("" + ic.getLevel());
        v.txtLevelName.setText(ic.getSkillLevelName());
        if (ic.getDateAssessed() > 0) {
            v.txtLevelName.setText("" + ic.getSkillLevelName() + " - " + sdf.format(new Date(ic.getDateAssessed())));
        }



        switch (ic.getLevel()) {
            case 1:
                v.txtLevel.setBackground(ctx.getResources().getDrawable(R.drawable.xgrey_box));
                break;
            case 2:
                v.txtLevel.setBackground(ctx.getResources().getDrawable(R.drawable.xblue_oval));
                break;
            case 3:
                v.txtLevel.setBackground(ctx.getResources().getDrawable(R.drawable.xgreen_box));
                break;
            case 4:
                v.txtLevel.setBackground(ctx.getResources().getDrawable(R.drawable.xblack_box));
                break;
            case 5:
                v.txtLevel.setBackground(ctx.getResources().getDrawable(R.drawable.xred_box));
                break;
            case 6:
                v.txtLevel.setBackground(ctx.getResources().getDrawable(R.drawable.xred_box));
                break;
        }
		
		Statics.setRobotoFontLight(ctx, v.txtName);
        Statics.setRobotoFontLight(ctx,v.txtLevelName);
		animateText(v.txtLevel);

		return (convertView);
	}

    private void animateText(TextView txt) {
        final ObjectAnimator an = ObjectAnimator.ofFloat(txt, View.SCALE_X, 0);
        an.setRepeatCount(1);
        an.setDuration(30);
        an.setRepeatMode(ValueAnimator.REVERSE);
        an.start();
    }

    static class ViewHolderItem {
        TextView txtName, txtNumber, txtLevel, txtLevelName;

    }
    static final Locale loc = Locale.getDefault();
    static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", loc);
}

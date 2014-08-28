package com.boha.cmauthor.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.boha.cmauthor.R;
import com.boha.coursemaker.util.ToastUtil;

public class ContentDialog extends DialogFragment {

	public interface ContentListener {
		public void onSaveButtonClicked(String name, String desc);
	}
	public ContentDialog() {
	}

	ContentListener contentListener;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.course_editor, container);
        editName = (EditText) view.findViewById(R.id.CRS_name);
        editDesc = (EditText) view.findViewById(R.id.CRS_desc);
        txtHeaderLabel = (TextView)view.findViewById(R.id.CRS_categoryLabel);
        txtHeader = (TextView)view.findViewById(R.id.CRS_category);
        txtAddLabel = (TextView)view.findViewById(R.id.CRS_addLabel);
        btnCancel = (Button)view.findViewById(R.id.CRS_btnCancel);
        btnSave = (Button)view.findViewById(R.id.CRS_btnSave);
        getDialog().setTitle(title);
        
        if (name != null) {
        	editName.setText(name);
        }
        if (desc != null) {
        	editDesc.setText(desc);
        }
        if (header != null) {
        	txtHeader.setText(header);
        }
        if (label != null) {
        	//txtHeaderLabel.setText(label);
        }
        if (addLabel != null) {
        	txtAddLabel.setText(addLabel);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dismiss();
				
			}
		});
        btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (editName.getText().toString().isEmpty()) {
					ToastUtil.errorToast(getActivity(), "Please enter name");
					return;
				}
				if (editDesc.getText().toString().isEmpty()) {
					ToastUtil.errorToast(getActivity(), "Please enter description");
					return;
				}
				contentListener.onSaveButtonClicked(
				editName.getText().toString(), editDesc.getText().toString());
				dismiss();
			}
		});
        animate(view);
        return view;
    }
	
	private void animate(View v) {
		Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.grow_fade_in_center);
		a.setDuration(1000);
		v.startAnimation(a);
	}
	EditText editName, editDesc;
	TextView txtHeaderLabel, txtHeader, txtAddLabel;
	Button btnCancel,btnSave;
	private String name, desc, label, header, title, addLabel;

	
	public void setAddLabel(String addLabel) {
		this.addLabel = addLabel;
	}
	public void setListener(ContentListener listener) {
		contentListener = listener;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public void setName(String name) {
		this.name = name;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public void setHeader(String header) {
		this.header = header;
	}
}

package com.boha.cmserverprobe.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.boha.cmserverprobe.adapters.CompanyStatsAdapter;
import com.boha.cmserverprobe.listener.CompanyListener;
import com.boha.coursemaker.cmserverprobe.R;
import com.boha.coursemaker.dto.CompanyStatsDTO;
import com.boha.coursemaker.dto.StatsResponseDTO;
import com.boha.coursemaker.listeners.BusyListener;
import com.boha.coursemaker.listeners.PageInterface;

public class CompanyListFragment extends Fragment implements PageInterface{

	Context ctx;
	View view, space;
	BusyListener busyListener;
	CompanyListener companyListener;

	public CompanyListFragment() {

	}

	@Override
	public void onAttach(Activity a) {
		if (a instanceof BusyListener) {
			busyListener = (BusyListener) a;
		} else {
			throw new UnsupportedOperationException();
		}
		if (a instanceof CompanyListener) {
			companyListener = (CompanyListener) a;
		} else {
			throw new UnsupportedOperationException();
		}
		super.onAttach(a);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		ctx = getActivity();
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.fragment_company_stats_list, container, false);
		setFields();
		setList();
		return view;
	}



	public void setData(StatsResponseDTO response) {
		companyStatsList = response.getStatsList();
		setList();

	}

	private void setList() {
		if (companyStatsList == null) return;
		if (listView == null) return;
		adapter = new CompanyStatsAdapter(ctx, 
				R.layout.company_item, companyStatsList);
		txtCount.setText("" + companyStatsList.size());
		listView.setAdapter(adapter);
		listView.setDividerHeight(5);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				companyStats = companyStatsList.get(arg2);
				Log.i(LOG, "Company selected: " + companyStats.getCompanyName());
				companyListener.onCompanySelected(companyStats);

			}
		});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				companyStats = companyStatsList.get(arg2);
				companyListener.onCompanySelected(companyStats);
				return false;
			}
		});
	}


	static final String LOG = "CompanyListFragment";

	private void setFields() {
		
		txtCount = (TextView) view.findViewById(R.id.COMLST_count);
		listView = (ListView) view.findViewById(R.id.COMLST_list);
		
	}

	
	private List<CompanyStatsDTO> companyStatsList;
	private CompanyStatsDTO companyStats;
	private CompanyStatsAdapter adapter;
	
	private TextView txtCount;
	private ListView listView;

	
}

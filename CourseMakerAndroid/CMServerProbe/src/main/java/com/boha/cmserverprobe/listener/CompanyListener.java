package com.boha.cmserverprobe.listener;

import com.boha.coursemaker.dto.CompanyStatsDTO;

public interface CompanyListener {

	public void onCompanySelected(CompanyStatsDTO companyStats);
}

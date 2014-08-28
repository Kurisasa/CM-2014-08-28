package com.boha.cmtrainee.interfaces;

import com.boha.coursemaker.dto.TeamMemberDTO;

import java.util.List;

public interface TeamMemberAddedListener {

	public void onTeamMemberAdded(List<TeamMemberDTO> list);
}

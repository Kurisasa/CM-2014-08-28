package com.boha.cmauthor.interfaces;

import com.boha.coursemaker.dto.CategoryDTO;

public interface CategoryListener {

	public void onCategoryPicked(CategoryDTO category);
    public void onImportRequested();
	public void setBusy();
	public void setNotBusy();
}

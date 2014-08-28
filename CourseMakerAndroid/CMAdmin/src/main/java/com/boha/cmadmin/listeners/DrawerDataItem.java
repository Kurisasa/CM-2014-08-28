package com.boha.cmadmin.listeners;

public class DrawerDataItem {

	private int imageID, count;
	private String text;
	public int getImageID() {
		return imageID;
	}
	public DrawerDataItem(int imageID, String text, int count) {
		this.count = count;
		this.imageID = imageID;
		this.text = text;
	}
	public void setImageID(int imageID) {
		this.imageID = imageID;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}

package com.android.project.transmart;

import android.graphics.drawable.Drawable;

public class StateIteamDTO {
	private int idx;         // listview index
	private String fromTile;    // ���۾�� ����
	private String fromMemo;    // ���۾�� ����
	private String fromCode;    // ���۾�� �ڵ�
	private int fromImg;   // �����̹���
	private String toTitle;     // ������� ����
	private String toMemo;      // ������� ����
	private String toCode;      // ������� �ڵ�
	private int toImg;     // �����̹���
	private int listBg;    // listview ���
	
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public String getFromCode() {
		return fromCode;
	}
	public void setFromCode(String fromCode) {
		this.fromCode = fromCode;
	}
	public String getToCode() {
		return toCode;
	}
	public void setToCode(String toCode) {
		this.toCode = toCode;
	}
	public String getFromTile() {
		return fromTile;
	}
	public void setFromTile(String fromTile) {
		this.fromTile = fromTile;
	}
	public String getFromMemo() {
		return fromMemo;
	}
	public void setFromMemo(String fromMemo) {
		this.fromMemo = fromMemo;
	}
	public int getFromImg() {
		return fromImg;
	}
	public void setFromImg(int fromImg) {
		this.fromImg = fromImg;
	}
	public String getToTitle() {
		return toTitle;
	}
	public void setToTitle(String toTitle) {
		this.toTitle = toTitle;
	}
	public String getToMemo() {
		return toMemo;
	}
	public void setToMemo(String toMemo) {
		this.toMemo = toMemo;
	}
	public int getToImg() {
		return toImg;
	}
	public void setToImg(int toImg) {
		this.toImg = toImg;
	}
	public int getListBg() {
		return listBg;
	}
	public void setListBg(int listBg) {
		this.listBg = listBg;
	}
	
	
}

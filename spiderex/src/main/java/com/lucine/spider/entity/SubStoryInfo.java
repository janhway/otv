package com.lucine.spider.entity;

import net.sf.json.JSONObject;

public class SubStoryInfo {
	//子集名称   title
	private String title;
	//播放URL    playURL
	private String playUrl;
	//海报URL    picURL
	private String picUrl;
	//简介      description
	private String description;
	//时长       duration
	private String duration;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPlayUrl() {
		return playUrl;
	}
	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getDescrip() {
		return description;
	}
	public void setDescrip(String descrip) {
		this.description = descrip;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public String toString() {
		JSONObject jo = JSONObject.fromObject(this);
		return jo.toString();
//		JSONObject jo = new JSONObject();
//		jo.put("title", title);
//		jo.put("playUrl", playUrl);
//		jo.put("picUrl", picUrl);
//		jo.put("description", description);
//		jo.put("duration", duration);		
//		return jo.toString();
	}
}

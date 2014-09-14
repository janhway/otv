package com.lucine.spider.entity;

import java.util.List;

import net.sf.json.JSONObject;

public class OttMedia {
	// cp名字
	private String cpName;
	
	// 媒体类型 mediaType 电影、电视剧、动漫、综艺
	private MediaType mediaType;
	
	// 名称 title
	private String title;
	
	// 演员 actors
	private String actors;
	
	// 导演 directors
	private String directors;
	
	// 地区 originCountry
	private String originCountry;
	
	// 上映时间 releaseYear
	private String releaseYear;
	
	// 类型 genre
	private String genre;

	// 简介 description
	private String description;
	
	// 海报URL picUrl
	private String picUrl;
	
	// 播放Url
	private String playUrl;
	
	// 评分 score
	private String score;

	// 播放次数
	private String playNum;
	
	// 总集数 episodeTotalNum
	private String episodeTotalNum;

	// 更新集数 episodeUpdatedNum
	private String episodeUpdNum;
	
	private List<SubStoryInfo> episodeList;

	public String getCpName() {
		return cpName;
	}

	public void setCpName(String cpName) {
		this.cpName = cpName;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getActors() {
		return actors;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	public String getDirectors() {
		return directors;
	}

	public void setDirectors(String directors) {
		this.directors = directors;
	}

	public String getOriginCountry() {
		return originCountry;
	}

	public void setOriginCountry(String originCountry) {
		this.originCountry = originCountry;
	}

	public String getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(String releaseYear) {
		this.releaseYear = releaseYear;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String descrip) {
		this.description = descrip;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getEpisodeTotalNum() {
		return episodeTotalNum;
	}

	public void setEpisodeTotalNum(String episodeTotalNum) {
		this.episodeTotalNum = episodeTotalNum;
	}

	public String getEpisodeUpdNum() {
		return episodeUpdNum;
	}

	public void setEpisodeUpdNum(String episodeUpdNum) {
		this.episodeUpdNum = episodeUpdNum;
	}

	public List<SubStoryInfo> getEpisodeList() {
		return episodeList;
	}

	public void setEpisodeList(List<SubStoryInfo> episodeList) {
		this.episodeList = episodeList;
	}
	
	public String toString() {		
		
		JSONObject jo = JSONObject.fromObject(this);
		return jo.toString();
//		jo.put("cpName", cpName);
//		jo.put("mediaType", mediaType);
//		jo.put("title", title);	
//		jo.put("directors", directors);
//		jo.put("actors", actors);
//		jo.put("releaseYear", releaseYear);	
//		jo.put("genre", genre);
//		jo.put("originCountry", originCountry);
//		jo.put("picUrl", picUrl);
//		jo.put("playUrl", playUrl);
//		jo.put("score", score);
//		jo.put("playNum", playNum);
//		jo.put("episodeTotalNum", episodeTotalNum);
//		jo.put("episodeUpdNum", episodeUpdNum);
//		jo.put("description", description);
//		jo.put("episodeList", episodeList);
//		
//		return jo.toString();
	}

	public String getPlayNum() {
		return playNum;
	}

	public void setPlayNum(String playNum) {
		this.playNum = playNum;
	}

	public String getPlayUrl() {
		return playUrl;
	}

	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}

}

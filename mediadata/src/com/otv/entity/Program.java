package com.otv.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity  
@Table(name="PROGRAM")  
public class Program {
	
	private long id;
	
	// 媒体类型 mediaType 电影、电视剧、动漫、综艺
	//private MediaType mediaType;
	
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
	//private String score;

	// 播放次数
	//private String playNum;
	
	// 总集数 episodeTotalNum
	//private String episodeTotalNum;

	// 更新集数 episodeUpdatedNum
	//private String episodeUpdNum;
	
	private List<Episode> episodeList;
		
	@Id  
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)  
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Column(name = "TITLE", unique=true)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "ACTORS")
	public String getActors() {
		return actors;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	@Column(name = "DIRECTORS")
	public String getDirectors() {
		return directors;
	}

	public void setDirectors(String directors) {
		this.directors = directors;
	}

	@Column(name = "COUNTRY")
	public String getOriginCountry() {
		return originCountry;
	}

	public void setOriginCountry(String originCountry) {
		this.originCountry = originCountry;
	}

	@Column(name = "RELEASE_YEAR")
	public String getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(String releaseYear) {
		this.releaseYear = releaseYear;
	}

	@Column(name = "GENRE")
	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
	

	@Column(name = "DESCRIPTION", length=1024)
	public String getDescription() {
		return description;
	}

	public void setDescription(String descrip) {
		this.description = descrip;
	}

	@Column(name = "PIC_URL")
	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	@Column(name = "PLAY_URL")
	public String getPlayUrl() {
		return playUrl;
	}

	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}

	@OneToMany(mappedBy = "program", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public List<Episode> getEpisodeList() {
		return episodeList;
	}

	public void setEpisodeList(List<Episode> episodeList) {
		this.episodeList = episodeList;
	}	

}

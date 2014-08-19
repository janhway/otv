package com.otv.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity  
@Table(name="MOVIE")
public class Movie {

	
	private long id;
	
	private String playUrl;
	
	private Episode episode;

	@Id  
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO) 
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "PLAY_URL")
	public String getPlayUrl() {
		return playUrl;
	}

	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}	
	
	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name="EPISODE_ID", referencedColumnName="ID")//外键为sut_id，与student中的id关联 
	public Episode getEpisode() {
		return episode;
	}

	public void setEpisode(Episode episode) {
		this.episode = episode;
	}
}

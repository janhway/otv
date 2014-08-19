package com.otv.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity  
@Table(name="EPISODE")
public class Episode {
	
	private long id;

	// 子集名称 title
	private String title;
	// 海报URL picURL
	private String picUrl;
	// 简介 description
	private String description;
	// 时长 duration
	private int duration;

	private Program program;
	
	private Set<Movie> movieList;
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "TITLE")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "PIC_URL")
	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "DURATION")
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name="PROGRAM_ID", referencedColumnName="ID") 
	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}
	@OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Movie> getMovieList() {
		return movieList;
	}

	public void setMovieList(Set<Movie> movieList) {
		this.movieList = movieList;
	}
}

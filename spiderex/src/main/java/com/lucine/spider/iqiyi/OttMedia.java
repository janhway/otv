package com.lucine.spider.iqiyi;

public class OttMedia {
	private String playUrl="";
	private String title="";
	private String director="";
	private String actor="";
	private String descrip="";
	private String shangYing="";
	private String genre ="";

	public OttMedia(){
		
	}
	
	public String getPlayUrl() {
		return playUrl;
	}

	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getDescript() {
		return descrip;
	}

	public void setDescript(String descript) {
		this.descrip = descript;
	}
	
	private String xmlString(String labelName, String labelText){
		return "<" + labelName + ">"  + labelText + "</" + labelName + ">";
	}
	
	public String toString(){
		return xmlString("title",title) + xmlString("playUrl",playUrl) + 
				xmlString("shangYing",shangYing) + xmlString("director",director) + 
				xmlString("actor",actor) + xmlString("genre",genre) + 
				xmlString("descrip",descrip);
				//"title=" + title + " playUrl=" + playUrl + " shangYing=" + shangYing + 
				//" director=" + director + " actor=" + actor + " genre=" + genre  + " descrip=" + descrip;
	}

	public String getShangYing() {
		return shangYing;
	}

	public void setShangYing(String shangYing) {
		this.shangYing = shangYing;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

}

package com.otv.controller;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.otv.dao.ProgramDao;
import com.otv.dao.UserDao;
import com.otv.entity.Episode;
import com.otv.entity.Movie;
import com.otv.entity.Program;
import com.otv.msg.OttMedia;
import com.otv.msg.SubStoryInfo;
import com.otv.utils.OtvUtils;

@Controller
@RequestMapping("/spider")
public class Spider {

	private final Logger log = LoggerFactory.getLogger(Spider.class);

	@Autowired
	private ProgramDao programDao;
	
	@RequestMapping(value = "/ottmedia", method = RequestMethod.POST, consumes = "application/json")
	public void createOttMedia(HttpServletRequest req, HttpServletResponse rsp) {
		
		String httpContent = OtvUtils.readHttpContent(req);
		
		log.info("httpContent="+httpContent);
		JSONObject jo = JSONObject.fromObject(httpContent);
		
		OttMedia om = new OttMedia();
		om.setTitle(jo.getString("title"));
		om.setActors(jo.getString("actors"));
		om.setDirectors(jo.getString("directors"));
		om.setOriginCountry(jo.getString("originCountry"));
		om.setReleaseYear(jo.getString("releaseYear"));
		om.setGenre(jo.getString("genre"));
		om.setDescription(jo.getString("description"));
		om.setPicUrl(jo.getString("picUrl"));
		om.setScore(jo.getString("score"));
		om.setEpisodeTotalNum(jo.getString("episodeTotalNum"));
		om.setEpisodeUpdNum(jo.getString("episodeUpdNum"));
		List<SubStoryInfo> ssiList = new ArrayList<SubStoryInfo>();
		om.setEpisodeList(ssiList);
		
		JSONArray ja = jo.getJSONArray("episodeList");
		for(int i=0; i<ja.size(); ++i)
		{
			JSONObject subJO = (JSONObject)ja.get(i);
		    SubStoryInfo ssi = new SubStoryInfo();
		    ssi.setTitle(subJO.getString("title"));
		    ssi.setDescrip(subJO.getString("descrip"));
		    ssi.setDuration(subJO.getString("duration"));
		    ssi.setPicUrl(subJO.getString("picUrl"));
		    ssi.setPlayUrl(subJO.getString("playUrl"));
		    ssiList.add(ssi);
		}
		
		Program p = programDao.getProgram(om.getTitle());
		if (p == null) {
			p = new Program();
			p.setTitle(om.getTitle());
			p.setActors(om.getActors());
			p.setDirectors(om.getDirectors());
			p.setOriginCountry(om.getOriginCountry());
			p.setReleaseYear(om.getReleaseYear());
			p.setGenre(om.getGenre());
			p.setDescription(om.getDescription());
			p.setPicUrl(om.getPicUrl());
			List<Episode> epList = new ArrayList<Episode>();
			for(SubStoryInfo ssi: om.getEpisodeList()) {
				Episode ep = new Episode();
				Movie mv = new Movie();
				mv.setEpisode(ep);
				mv.setPlayUrl(ssi.getPlayUrl());
				Set<Movie> mvList = new HashSet<Movie>();
				mvList.add(mv);
				ep.setMovieList(mvList);
				ep.setProgram(p);
				ep.setTitle(ssi.getTitle());
				ep.setDuration(Integer.valueOf(ssi.getDuration()));
				ep.setPicUrl(ssi.getPicUrl());
				ep.setDescription(ssi.getDescrip());
				epList.add(ep);
			}
			p.setEpisodeList(epList);
			programDao.saveProgram(p);
		} else {
			log.info("add it later.");
		}			

		
		OtvUtils.writeHttpResponse(rsp, "ok");

		return;
	}
}

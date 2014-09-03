package com.otv.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.otv.msg.OttMedia;
import com.otv.utils.OtvUtils;

@Controller
@RequestMapping("/spider")
public class Spider {

	private final Logger log = LoggerFactory.getLogger(Spider.class);
	
	@RequestMapping(value = "/ottmedia", method = RequestMethod.POST, consumes = "application/json")
	public void createOttMedia(HttpServletRequest req, HttpServletResponse rsp) {
		
		String httpContent = OtvUtils.readHttpContent(req);
		
		log.info("httpContent="+httpContent);
		Object o = JSON.parse(httpContent);
		//JSONObject jo = JSONObject.parseObject(httpContent);
		
		//OttMedia om = JSON.parseObject(httpContent,OttMedia.class);
		//OttMedia om = OttMedia.JsonString2Program(httpContent);
		
		OtvUtils.writeHttpResponse(rsp, "ok");

		return;
	}
}

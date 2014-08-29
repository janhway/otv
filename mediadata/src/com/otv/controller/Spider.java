package com.otv.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.otv.utils.OtvUtils;

@Controller
@RequestMapping("/spider")
public class Spider {

	@RequestMapping(value = "/ottmedia", method = RequestMethod.POST, consumes = "application/json")
	public void createOttMedia(HttpServletRequest req, HttpServletResponse rsp) {
		
		String httpContent = OtvUtils.readHttpContent(req);
		
		System.out.print(httpContent);
		
		OtvUtils.writeHttpResponse(rsp, "ok");

		return;
	}
}

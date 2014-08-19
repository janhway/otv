package com.otv.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.otv.entity.ReturnMsg;
import com.otv.entity.User;

@Controller
@RequestMapping("/spider")
public class Spider {

	@RequestMapping(value = "/ottmedia", method = RequestMethod.POST, consumes = "application/json")
	public void createOttMedia(HttpServletRequest req, HttpServletResponse rsp) {

		
		
		rsp.setContentType("text/plain");
		PrintWriter out;
		try {
			out = rsp.getWriter();
			out.println("ok");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//rsp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "abcdefg");
		}

		return;
	}
}

package com.otv.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.otv.dao.UserDao;
import com.otv.entity.ReturnMsg;
import com.otv.entity.User;

@Controller
@RequestMapping("/user")
public class UserManager {
	
	@Autowired
	private UserDao userDao;
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public void welcomeName(HttpServletRequest req, HttpServletResponse rsp) {
		try {
			String name = (String) req.getParameter("name");
			
			rsp.setContentType("text/plain");
			PrintWriter out = rsp.getWriter();
			out.println("name=" + name);

		} catch (Exception ex) {

		}
		return;

	}
	
	@RequestMapping(value = "/hello2", method = RequestMethod.GET)
	public void welcomeName(@RequestParam/*(value="cc", required=false, defaultValue="World")*/ String name, HttpServletResponse rsp) {
		try {			
			rsp.setContentType("text/plain");
			PrintWriter out = rsp.getWriter();
			out.println("name=" + name);

		} catch (Exception ex) {

		}
		return;

	}

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json")  
	public @ResponseBody
	ReturnMsg createUser(@RequestBody User user) {
		
		userDao.addUser(user);
		
		String userInfo = "FirstName:" + user.getFirstName() + " LastName:"
				+ user.getLastName() + " UserName:" + user.getUserName()
				+ " Password:" + user.getPassword();
		return new ReturnMsg("0", userInfo);
	}	
	
	@RequestMapping(value = "/{userName}", method = RequestMethod.GET)  
	public @ResponseBody
	List<User> getUsers(@PathVariable String userName) {
		
		List<User> userList = userDao.getUsers(userName);		
		
		return userList;
	}	

}

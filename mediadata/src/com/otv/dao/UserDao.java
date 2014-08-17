package com.otv.dao;

import java.util.List;

import com.otv.entity.User;

public interface UserDao {

	public abstract void addUser(User user);

	public abstract List<User> getUsers(String userName);

}
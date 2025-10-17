package com.mountblue.blogApplication.service;

import com.mountblue.blogApplication.entity.User;

public interface UserService {
    User registerUser(String name, String  email, String password);
    User getCurrentUser();
    User findById(Long authorId);
}

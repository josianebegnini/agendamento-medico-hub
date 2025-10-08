package com.ms_auth.domain.service;

import com.msauth.domain.entity.User;

public interface UserService {
    User createUser(User user);
    User validateUser(String username, String password);
    Optional<User> findByUsername(String username);
}

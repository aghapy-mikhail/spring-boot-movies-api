package com.example.movies.sys.service;


import com.example.movies.sys.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getUsers();
    Optional<User> getUserById(Integer id);
    User saveUser(User user);
    void updateUser(User user, Integer id );
    void deleteUser(Integer id);

}

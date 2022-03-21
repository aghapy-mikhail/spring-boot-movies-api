package com.example.movies.sys.controller;

import com.example.movies.sys.entity.User;
import com.example.movies.sys.exception.InternalServerException;
import com.example.movies.sys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class UserController {

    @Autowired
    UserService service;

    @GetMapping("/users")
    public ResponseEntity<Object> getUsers(){

        return ResponseEntity.ok().body(service.getUsers());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Object> getUserById(
            @PathVariable("userId") Integer userId) {

        return ResponseEntity.ok().body(service.getUserById(userId));
    }
    @PostMapping("/users")
    public ResponseEntity<Object> saveUser(
            @Valid @RequestBody User user) {

        try {
            Integer userId=service.saveUser(user).getUserId();

            return ResponseEntity
                    .created(new URI(String.format("/users/%d", userId)))
                    .build();
        }catch (URISyntaxException e){
            throw new InternalServerException("The URI in the location header in POST /users has an error");
        }
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<Object> updateUser(
            @RequestBody User user,
            @PathVariable("userId") Integer userId) {
        service.updateUser(user,userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("users/{userId}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable("userId") Integer userId) {
        service.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}

package com.example.movies.sys.repository;

import com.example.movies.sys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByEmail(String email);
    List<User> findByEmailAndUserFirstNameAndUserLastName(String email, String firstName, String lastName);

}

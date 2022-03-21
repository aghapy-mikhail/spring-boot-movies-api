package com.example.movies.sys.service;

import com.example.movies.sys.entity.Favorite;
import com.example.movies.sys.entity.User;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.FavoriteRepository;
import com.example.movies.sys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private FavoriteRepository favoriteRepository;


    public UserServiceImpl(){}

    @Override
    public List<User> getUsers(){
        return repo.findAll();
    }

    @Override
    public Optional<User> getUserById(Integer id){
        if (!repo.existsById(id))
            throw new NotFoundException(id);
        return repo.findById(id);
    }
    @Override
    public User saveUser(User user){
        if (repo.findByEmailAndUserFirstNameAndUserLastName(user.getEmail(),user.getUserFirstName(),user.getUserLastName()).size()!=0)
            throw new ExistingRecordException(user.getEmail()+" user already has an account");

        repo.save(user);
        Favorite favorite=new Favorite();
        favorite.setUser(user);
        favoriteRepository.save(favorite);
        return user;

    }
    @Override
    public void updateUser(User user, Integer id){
        if (!repo.existsById(id))
            throw new NotFoundException(id);

        User current=repo.getById(id);
        current.updateTo(user);
        if (repo.findByEmailAndUserFirstNameAndUserLastName(user.getEmail(),user.getUserFirstName(),user.getUserLastName()).size()!=0)
            throw new ExistingRecordException(user.getEmail()+" user has an account already");
        repo.save(current);
    }

    @Override
    public void deleteUser(Integer id){
        if (!repo.existsById(id))
            throw new NotFoundException(id);
        repo.deleteById(id);
    }
}

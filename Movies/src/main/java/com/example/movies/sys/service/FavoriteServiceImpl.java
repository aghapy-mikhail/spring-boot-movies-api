package com.example.movies.sys.service;

import com.example.movies.sys.entity.Favorite;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.FavoriteRepository;
import com.example.movies.sys.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository repo;

    @Autowired
    private MovieRepository movieRepository;

    public FavoriteServiceImpl(){}



    @Override
    public Page<Favorite> getFavorites(Pageable paging){
        return repo.findAll( paging);
    }

    @Override
   public Optional<Favorite> getFavoriteById(Integer id){
        if (!repo.existsById(id))
            throw new NotFoundException(id);
        return repo.findById(id);
    }

    @Override
   public Favorite saveFavorite(Favorite favorite){

        for (Integer i: favorite.getMovieIds()) {
            if (movieRepository.existsById(i))
                favorite.getMovies().add(movieRepository.getById(i));
        }
        return repo.save(favorite);
    }

    @Override
    public void updateFavorite(Favorite favorite, Integer id){

        //check if the list exists
        if (!repo.existsById(id))
            throw new NotFoundException(id);

      Favorite current=repo.getById(id);
        List<Integer> a=favorite.getMovieIds();
      current.updateTo(favorite);
      current.getMovies().clear();

        for (Integer i: favorite.getMovieIds()) {
            if (movieRepository.existsById(i))
                current.getMovies().add(movieRepository.getById(i));
        }

         repo.save(current);
    }

    @Override
    public void deleteMovie(Integer id){
        if (!repo.existsById(id))
            throw new NotFoundException(id);
        repo.deleteById(id);
    }


}

package com.example.movies.sys.service;

import com.example.movies.sys.entity.Favorite;
import com.example.movies.sys.entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {

    List<Movie> getMovies();
    Optional<Movie> getMovieById(Integer id);
    Movie saveMovie(Movie movie);
    void updateMovie(Movie movie, Integer id );
    void deleteMovie(Integer id);

    List<Movie> findByMovieName(String movieName);
    List<Movie> findByGenres(String genre, List<Movie> movies);
    void deleteMovieFromFavorites(Integer movieId, List<Favorite> favorites);
}

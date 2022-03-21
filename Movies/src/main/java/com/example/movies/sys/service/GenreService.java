package com.example.movies.sys.service;

import com.example.movies.sys.entity.Genre;
import com.example.movies.sys.entity.Movie;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public interface GenreService {

    List<Genre> getGenres();
    Optional<Genre> getGenreById(Integer id);
    Genre saveGenre(Genre genre);
    void updateGenre(Genre genre, Integer id);
    void deleteGenre(Integer id);
    void deleteGenreFromMovies(Integer genreId, List<Movie> movies);
    boolean existsByGenreName(String genreName);
}

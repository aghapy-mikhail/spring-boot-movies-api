package com.example.movies.sys.controller;

import com.example.movies.sys.entity.Movie;
import com.example.movies.sys.exception.InternalServerException;
import com.example.movies.sys.exception.NotExistingGenre;
import com.example.movies.sys.repository.FavoriteRepository;
import com.example.movies.sys.service.GenreService;
import com.example.movies.sys.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class MovieController {

    @Autowired
    MovieService service;



    @Autowired
    FavoriteRepository favoriteRepository;

    @Autowired
    GenreService genreService;

    @GetMapping("/movies")
    public ResponseEntity<Object> getMovies(@RequestParam(value = "movieName", required = false) String movieName,
                                            @RequestParam(value = "genre", required = false) String genre) {

        if (movieName==null && genre==null) {
            return ResponseEntity.ok().body(service.getMovies());
        }
        else if (genre==null){
            return ResponseEntity.ok().body(service.findByMovieName(movieName));
        }
        else {
            if (!genreService.existsByGenreName(genre) )
                throw new NotExistingGenre(genre);
            return ResponseEntity.ok().body(service.findByGenres(genre, service.getMovies()));
        }
    }


    @GetMapping("/movies/{movieId}")
    public ResponseEntity<Object> getMovieById(
        @PathVariable("movieId") Integer movieId){
        return ResponseEntity.ok().body(service.getMovieById(movieId));
    }

    @PostMapping("/movies")
    public ResponseEntity<Object> saveMovie(
            @Valid @RequestBody Movie movie){

        try {
            Integer movieId= service.saveMovie(movie).getMovieId();

            return ResponseEntity
                    .created(new URI(String.format("/movies/%s", movieId)))
                    .build();
        } catch (URISyntaxException e){
            throw new InternalServerException("The URI in the Location header in POST /movies has an error.");
        }

    }

    @PatchMapping("/movies/{movieId}")
    public ResponseEntity<Object> updateMovie(
            @RequestBody Movie movie,
            @PathVariable("movieId") Integer movieId) {
        service.updateMovie(movie, movieId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("movies/{movieId}")
    public ResponseEntity<Object> deleteMovie(
            @PathVariable("movieId") Integer movieId) {

        service.deleteMovieFromFavorites(movieId,favoriteRepository.findAll());
        //
        service.deleteMovie(movieId);
        return ResponseEntity.noContent().build();
    }

}

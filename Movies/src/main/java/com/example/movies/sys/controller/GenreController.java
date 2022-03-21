package com.example.movies.sys.controller;

import com.example.movies.sys.entity.Genre;
import com.example.movies.sys.exception.InternalServerException;
import com.example.movies.sys.service.GenreService;
import com.example.movies.sys.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class GenreController {

    @Autowired
    GenreService service;
    @Autowired
    MovieService movieService;

    @GetMapping("/genres")
    public ResponseEntity<Object> getGenres(){

        return ResponseEntity.ok().body(service.getGenres());
    }

    @GetMapping("/genres/{genreId}")
    public ResponseEntity<Object> getGenreById(
            @PathVariable("genreId") Integer genreId) {

        return ResponseEntity.ok().body(service.getGenreById(genreId));
    }

    @PostMapping("/genres")
    public ResponseEntity<Object> saveGenre(
            @Valid @RequestBody Genre genre){

        try {
            Integer genreId=service.saveGenre(genre).getGenreId();

            return ResponseEntity
                    .created(new URI(String.format("/genres/%s", genreId)))
                    .build();
        } catch (URISyntaxException e){
            throw new InternalServerException("The URI in the Location header in POST /genres has an error.");
        }

    }

    @PatchMapping("/genres/{genreId}")
    public ResponseEntity<Object> updateGenre(
            @RequestBody Genre genre,
            @PathVariable("genreId") Integer genreId) {
        service.updateGenre(genre,genreId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("genres/{genreId}")
    public ResponseEntity<Object> deleteGenre(
            @PathVariable("genreId") Integer genreId) {
        service.deleteGenreFromMovies(genreId,movieService.getMovies());
        service.deleteGenre(genreId);
        return ResponseEntity.noContent().build();
    }
}

package com.example.movies.sys;

import com.example.movies.sys.entity.*;
import com.example.movies.sys.exception.BadRequestException;
import com.example.movies.sys.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
public class Validation {
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    GenreRepository genreRepository;
    @Autowired
    ActorRepository actorRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FavoriteRepository favoriteRepository;


    public void validateMovie(Movie movie){
     if (movie.getMovieName()==null || movie.getGenres()==null ||movie.getReleaseDate()==null){
         throw new BadRequestException();
     }else {
         for (Genre g:movie.getGenres()) {
             if (!genreRepository.existsById(g.getGenreId()) || !genreRepository.existsByGenreName(g.getGenreName()))
                 throw new BadRequestException();
         }
     }
    }

    public void validateGenre(Genre genre){
        if (genre.getGenreName()==null)
            throw new BadRequestException();
    }

    public void validateActor(Actor actor){
        if (actor.getActorName().equals("") || actor.getDateOfBirth().equals(null))
            throw new BadRequestException();
    }

    public void validateUser(User user){
        if (user.getUserFirstName().equals("") || user.getUserLastName().equals("") || user.getEmail().equals(""))
            throw new BadRequestException();
    }

}

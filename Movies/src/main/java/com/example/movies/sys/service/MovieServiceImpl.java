package com.example.movies.sys.service;

import com.example.movies.sys.entity.Favorite;
import com.example.movies.sys.entity.Genre;
import com.example.movies.sys.entity.Movie;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.GenreRepository;
import com.example.movies.sys.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {



    @Autowired
    private MovieRepository repo;

    @Autowired
    private GenreRepository genreRepository;

    public MovieServiceImpl(){}

    @Override
    public  List<Movie> getMovies(){
        return repo.findAll();
    }


    @Override
    public Optional<Movie> getMovieById(Integer id) {
        if (!repo.existsById(id))
            throw  new NotFoundException(id);
        return repo.findById(id);
         }

    @Override
    public Movie saveMovie(Movie movie){


        if (repo.findByMovieNameAndReleaseDate(movie.getMovieName(),movie.getReleaseDate()).size()!=0)
            throw new ExistingRecordException(movie.getMovieName()+" movie already exists");
        for (Integer i: movie.getGenreIds()) {
            if (genreRepository.existsById(i))
                movie.getGenres().add(genreRepository.getById(i));
        }
        return repo.save(movie); }

    @Override
    public void updateMovie(Movie movie, Integer id) {
        //checking if movie exists
        if (!repo.existsById(id))
            throw new NotFoundException(id);

        Movie current=repo.getById(id);
        current.updateTo(movie);
         current.getGenreIds().clear();

        for (Integer i: movie.getGenreIds()) {
            if (genreRepository.existsById(i)) {
                current.getGenres().add(genreRepository.getById(i));
                current.getGenreIds().add(i);
            }
        }


        if (repo.findByMovieNameAndReleaseDate(movie.getMovieName(),movie.getReleaseDate()).size()!=0)
            throw new ExistingRecordException(movie.getMovieName()+" movie already exists");

        repo.save(current);

    }

    @Override
    public void deleteMovie(Integer id){
        if (!repo.existsById(id))
            throw new NotFoundException(id);
        repo.deleteById(id);
    }

    @Override
    public  List<Movie> findByMovieName(String movieName){
        return repo.findByMovieName(movieName);
    }

    @Override
    public List<Movie> findByGenres(String genre, List<Movie> movies){
        List<Movie> moviesByGenre=new ArrayList<>();
        for (Movie m:movies) {
            for (Genre g: m.getGenres()) {
                if (g.getGenreName().equals(genre))
                    moviesByGenre.add(m);
            }
        }
        return moviesByGenre;
    }

    @Override
    public void deleteMovieFromFavorites(Integer movieId, List<Favorite> favorites){
        for (Favorite f: favorites) {
            for (int i=0;i<f.getMovies().size();i++){
                if (f.getMovies().get(i).getMovieId()==movieId)
                    f.getMovies().remove(i);
            }
        }
    }



}

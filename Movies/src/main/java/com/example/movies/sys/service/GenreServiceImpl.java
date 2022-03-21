package com.example.movies.sys.service;

import com.example.movies.sys.entity.Genre;
import com.example.movies.sys.entity.Movie;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {


    @Autowired
    private GenreRepository repo;

    public GenreServiceImpl(){}

    @Override
    public List<Genre> getGenres(){
        return  repo.findAll();
    }

    @Override
    public Optional<Genre> getGenreById(Integer id){
        if (!repo.existsById(id))
            throw new NotFoundException(id);
        return repo.findById(id);
    }

    @Override
    public Genre saveGenre(Genre genre){


        if (repo.findByGenreNameIgnoreCase(genre.getGenreName()).size()!=0)
            throw new ExistingRecordException(genre.getGenreName()+" genre already exists");
        return repo.save(genre);
    }

    @Override
    public void updateGenre(Genre genre,Integer id){
        //checking if the genre exists
        if (!repo.existsById(id))
            throw new NotFoundException(id);

        if (repo.findByGenreNameIgnoreCase(genre.getGenreName()).size()!=0)
            throw new ExistingRecordException(genre.getGenreName()+" genre already exists");

        genre.setGenreId(id);
        repo.save(genre);
    }

    @Override
    public void deleteGenre(Integer id){
        if (!repo.existsById(id))
            throw new NotFoundException(id);
        repo.deleteById(id);
    }

    @Override
    public void deleteGenreFromMovies(Integer genreId, List<Movie> movies){

        for (Movie m:movies) {
            for (int i=0;i<m.getGenres().size();i++){
                if (m.getGenres().get(i).getGenreId()==genreId)
                    m.getGenres().remove(i);
            }
        }

    }
    @Override
    public boolean existsByGenreName(String genreName){
        return repo.existsByGenreName(genreName);
    }
}

package com.example.movies.sys.repository;

import com.example.movies.sys.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    List<Movie> findByMovieName(String movieName);
    List<Movie> findByMovieNameAndReleaseDate(String movieName, LocalDate releaseDate);

}

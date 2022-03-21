package com.example.movies.sys.repository;

import com.example.movies.sys.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    boolean existsByGenreName(String genreName);
    List<Genre> findByGenreNameIgnoreCase(String genreName);



}

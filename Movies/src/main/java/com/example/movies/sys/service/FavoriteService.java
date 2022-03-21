package com.example.movies.sys.service;

import com.example.movies.sys.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

public interface FavoriteService {


    Page<Favorite> getFavorites(Pageable paging);

    Optional<Favorite> getFavoriteById(Integer id);
    Favorite saveFavorite(Favorite favorite);
    void updateFavorite(Favorite favorite, Integer id);
    void deleteMovie(Integer id);


}

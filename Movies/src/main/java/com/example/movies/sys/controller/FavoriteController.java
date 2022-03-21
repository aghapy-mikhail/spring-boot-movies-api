package com.example.movies.sys.controller;

import com.example.movies.sys.entity.Favorite;
import com.example.movies.sys.exception.InternalServerException;
import com.example.movies.sys.repository.FavoriteRepository;
import com.example.movies.sys.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FavoriteController {
    @Autowired
    FavoriteService service;



    @Value("${page}")
    private int pageParam;

    @Value("${limit}")
    private int limitParam;


    @GetMapping("/favorites")
    public ResponseEntity<Object> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int limit){

        try {
            List<Favorite> favorites=new ArrayList<>();
            Pageable paging;
            if (page==0 && limit==0){
                paging=PageRequest.of(pageParam,limitParam);
            }else {
             paging= PageRequest.of(page,limit);}

            Page<Favorite> favoritesPage;
            favoritesPage=service.getFavorites(paging);

            favorites=favoritesPage.getContent();
            Map<String,Object> response=new HashMap<>();
            response.put("favorites", favorites);
            response.put("currentPage", favoritesPage.getNumber());
            response.put("allItems", favoritesPage.getTotalElements());
            response.put("allPages", favoritesPage.getTotalPages());
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

    @GetMapping("/favorites/{favoriteId}")
    public ResponseEntity<Object> getFavoriteByUserId(
            @PathVariable("favoriteId") Integer favoriteId){

        return ResponseEntity.ok().body(service.getFavoriteById(favoriteId));
    }


    @PatchMapping("/favorites/{favoriteId}")
    public ResponseEntity<Object> updateFavorite(
           @Valid @RequestBody Favorite favorite,
            @PathVariable("favoriteId") Integer favoriteId) {
        service.updateFavorite(favorite, favoriteId);

        return ResponseEntity.noContent().build();
    }


}

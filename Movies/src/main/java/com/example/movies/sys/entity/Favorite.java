package com.example.movies.sys.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "favorites")
public class Favorite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int favoriteId;


    @OneToOne(mappedBy = "favorite")
    private User user;

    @NotNull(message = "The movies field cannot be null")
    @ManyToMany
    @JoinTable(
            name = "favorite_MOVIEs",
            joinColumns = @JoinColumn(name = "favorite_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_Id"))
    private List<Movie> movies=new ArrayList<>();

    @ElementCollection(targetClass=Integer.class)
    private  List<Integer> movieIds=new ArrayList<Integer>();


    public Favorite() {}



public Favorite(int favoriteId,User user,  List<Integer> movieIds) {
    this.favoriteId = favoriteId;
    this.user = user;
    this.movieIds = movieIds;
}

public  Favorite(List<Integer> movieIds){
        this.user=new User();
        this.movies= new ArrayList<Movie>();
        this.movieIds=movieIds;
}


    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public void updateTo(Favorite f){

        this.setMovieIds(f.getMovieIds());

    }

    public List<Integer> getMovieIds() {
        return movieIds;
    }

    public void setMovieIds(List<Integer> movieIds) {
        this.movieIds = movieIds;
    }
}

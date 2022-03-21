package com.example.movies.sys.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie implements Serializable {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int movieId;

    @NotNull(message = "The genres field cannot be null")
    @ManyToMany
    @JoinTable(
            name = "movie_GENREs",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_Id"))
    private List<Genre> genres= new ArrayList<>();

    @NotNull(message = "The movieName field cannot be null")
    @NotBlank(message = "The movieName field cannot be blank")
    private String movieName;

    @NotNull(message = "The releaseDate field cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING )
    private LocalDate releaseDate;


    @ManyToMany(mappedBy = "movies")
    private List<Favorite> favorites=new ArrayList<>();






    @ElementCollection(targetClass=Integer.class)
    private  List<Integer> genreIds=new ArrayList<Integer>();

    public Movie(){}

    public Movie(int movieId, List<Integer> genreIds, String movieName, LocalDate releaseDate){
        this.movieId=movieId;
        this.genreIds=genreIds;
        this.movieName=movieName;
        this.releaseDate=releaseDate;
    }



    public int getMovieId(){
        return movieId;
    }
    public void setMovieId(int movieId){
        this.movieId=movieId;
    }
    public List<Genre> getGenres(){
        return genres;
    }
    public void setGenres(List<Genre> genres){
        this.genres=genres;
    }
    public String getMovieName(){
        return movieName;
    }
    public void setMovieName(String movieName){
        this.movieName=movieName;
    }
    public LocalDate getReleaseDate(){
        return releaseDate;
    }
    public void setReleaseDate(LocalDate releaseDate){
        this.releaseDate=releaseDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public void updateTo(Movie m){
        this.setGenres(m.getGenres());
        this.setMovieName(m.getMovieName());
        this.setReleaseDate(m.getReleaseDate());

    }
}

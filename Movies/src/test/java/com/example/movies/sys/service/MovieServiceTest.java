package com.example.movies.sys.service;

import com.example.movies.sys.entity.Genre;
import com.example.movies.sys.entity.Movie;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.FavoriteRepository;
import com.example.movies.sys.repository.MovieRepository;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class MovieServiceTest {

    @MockBean
    private MovieRepository repo;

    @Autowired
    private MovieServiceImpl service;

    @MockBean
    private FavoriteRepository favoriteRepository;

    private Movie mockMovie;
    private List<Movie> movieList;

    private static Logger logger= LoggerFactory.getLogger(MovieServiceTest.class);


    @BeforeEach
    void init(){
        logger.info(()->("== Log me BEFORE each test method =="));
        Genre genre1=new Genre(1,"drama");
        Genre genre2=new Genre(2,"action");
        List<Genre> genres=new ArrayList<>();
        List<Integer> genreIds=new ArrayList<>();

        genreIds.add(genre1.getGenreId());
        genreIds.add(genre2.getGenreId());
        genres.add(genre1);
        genres.add(genre2);
        this.mockMovie=new Movie(1,genreIds,"Men in Black", LocalDate.of(2021,11,2));

        mockMovie.setGenres(genres);

        //Creating the mock list
        List<Genre> genres1= Arrays.asList(new Genre(1,"action"),new Genre(2,"drama"));
        List<Integer> genreIds1=Arrays.asList(1,2);
        List<Genre> genres2=Arrays.asList(new Genre(2,"drama"),new Genre(3,"horror"));
        List<Integer> genreIds2=Arrays.asList(2,3);
        List<Genre> genres3=Arrays.asList(new Genre(1,"action"),new Genre(3,"horror"));
        List<Integer> genreIds3=Arrays.asList(1,3);

        List<Movie> list=
                Arrays.asList(
                        new Movie(1,genreIds1,"Die hard",LocalDate.of(2021,11,2)),
                        new Movie(2,genreIds2,"Iron fist",LocalDate.of(2021,11,2)),
                        new Movie(3,genreIds3,"Jackie Chan",LocalDate.of(2011,10,3))
                );
        movieList=list;
        movieList.get(0).setGenres(genres1);
        movieList.get(1).setGenres(genres2);
        movieList.get(2).setGenres(genres3);

    }



    @Test
    @DisplayName("TEST  getMoviesWithResult")
    void getMoviesWithResults() throws Exception{

        Mockito.when(repo.findAll()).thenReturn(movieList);

        //call the service
  List<Movie> returnedList= (List<Movie>) service.getMovies();

  //Validate the result
        Assertions.assertFalse(returnedList.isEmpty(), "");
        Assertions.assertEquals("Die hard",returnedList.get(0).getMovieName());
        Assertions.assertEquals("2021-11-02",returnedList.get(1).getReleaseDate().toString());
        Assertions.assertEquals("Jackie Chan", returnedList.get(2).getMovieName());

    }

//    @Test
//    @DisplayName("TEST get MoviesByGenres")
//    void getMoviesByGenres(){
//
//        Mockito.when(repo.findAll()).thenReturn(service.findByGenres("drama",movieList));
//        List<Movie> returnedList=(List<Movie>) service.findByGenres("drama",movieList);
//
//        Assertions.assertEquals("Die hard",returnedList.get(0).getMovieName());
//        Assertions.assertEquals("Iron fist",returnedList.get(1).getMovieName());
//        Assertions.assertEquals("horror",returnedList.get(1).getGenres().get(1).getGenreName());
//    }

    @Test
    @DisplayName("TEST getMovieByName ")
    void getMovieByNameFound() throws Exception {

        String movieName = mockMovie.getMovieName();

        Mockito.when(repo.findByMovieName(movieName)).thenReturn(List.of(this.mockMovie));

        List<Movie> returnedMovies = service.findByMovieName(movieName);

        Assertions.assertEquals(this.mockMovie, returnedMovies.get(0));

    }

    @Test
    @DisplayName("TEST get Movies by Genres ")
    void getMoviesByGenre() throws Exception {

        String genreName ="drama";

        Mockito.when(service.findByGenres(genreName,repo.findAll())).thenReturn(List.of(this.mockMovie));

        List<Movie> returnedMovies = service.findByGenres(genreName,repo.findAll());

        Assertions.assertEquals(this.mockMovie, returnedMovies.get(0));

    }


    @Test
    @DisplayName("TEST getMovieByIdFound")
    void getMovieById_Found() throws Exception {
        // Use the mock repo to get mock a return value from existsById() and findById()
        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.when(repo.findById(1)).thenReturn(Optional.of(this.mockMovie));

        // Call the service
        Optional<Movie> returnedMovie = service.getMovieById(1);

        // Validate the result
        Assertions.assertEquals(this.mockMovie,returnedMovie.get());

    }

    @Test
    @DisplayName("TEST getMovieByIdNotFound")
    void getMovieById_NotFound() throws Exception {
        // Use the mock repo to get mock a return value from existsById()
        Mockito.when(repo.existsById(100)).thenReturn(false);

        // throw NotFoundException when resource does not exist
        Assertions.assertThrows(NotFoundException.class, () -> service.getMovieById(100));
    }

    @Test
    @DisplayName("TEST post movie")
    void saveMovie() throws Exception {
        Mockito.when(repo.save(this.mockMovie)).thenReturn(this.mockMovie);

        // Call the service
       service.saveMovie(this.mockMovie);
       Integer movieId=mockMovie.getMovieId();

        // Validate the result
        Assertions.assertEquals(1, movieId);
    }

//    @Test
//    @DisplayName("TEST post movie not successful")
//    void saveMovie_Conflict() {
//        Mockito.when(repo.save(this.mockMovie)).thenReturn(this.mockMovie);
//
//        // Call the service
//        service.saveMovie(this.mockMovie);
//        Integer movieId=mockMovie.getMovieId();
//
//        // Validate the result
//        Assertions.assertEquals(1, movieId);
//    }

    @Test
    @DisplayName("TEST post movie conflict error")
    public void saveMovie_ThrowConflictException() {
        service.saveMovie(movieList.get(0));
        // Mock repository call.
        Mockito.when(service.saveMovie(movieList.get(0))).thenThrow(new ExistingRecordException(movieList.get(0).getMovieName()+" movie already exists"));
        // Validate service call throws.
        Assertions.assertThrows(ExistingRecordException.class, () -> service.saveMovie(movieList.get(0)));
    }

    @Test
    @DisplayName("TEST update movie ")
    public void  updateMovieById(){
        Movie m=Mockito.mock(Movie.class);
        Mockito.when(repo.existsById(mockMovie.getMovieId())).thenReturn(true);
        Mockito.when(repo.getById(mockMovie.getMovieId())).thenReturn(m);

        service.updateMovie(mockMovie,mockMovie.getMovieId());
        Mockito.verify(m).updateTo(mockMovie);
        Mockito.verify(repo).save(m);

    }

    @Test
    @DisplayName("TEST update movie throws not found exception ")
    public void  updateMovieById_NotFound(){
       Mockito.when(repo.existsById(-1)).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> service.getMovieById(-1));


    }

    @Test
    @DisplayName("TEST update movie conflict error")
    void updateMovie_ConflictException() throws Exception {

        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.when(repo.getById(1)).thenReturn(this.mockMovie);
        Mockito.when(repo.save(this.mockMovie)).thenThrow(new ExistingRecordException(this.mockMovie.getMovieName()+" movie already exists"));

        Assertions.assertThrows(ExistingRecordException.class, () -> service.updateMovie(this.mockMovie, 1));
    }

    @Test
    @DisplayName("TEST delete movie")
    public void deleteMovieById() throws Exception {

        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.doNothing().when(repo).deleteById(1);
        service.deleteMovieFromFavorites(1,favoriteRepository.findAll());

        // Call the service
        service.deleteMovie(1);
    }


    @Test
    @DisplayName("TEST delete movie not successful")
    public void deleteMovie_NotFound(){
        Mockito.when(repo.existsById(1)).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, ()->service.deleteMovie(1));


    }



}

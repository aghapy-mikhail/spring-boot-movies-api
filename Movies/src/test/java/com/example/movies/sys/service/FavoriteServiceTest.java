package com.example.movies.sys.service;

import com.example.movies.sys.entity.Favorite;
import com.example.movies.sys.entity.Genre;
import com.example.movies.sys.entity.Movie;
import com.example.movies.sys.entity.User;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.FavoriteRepository;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class FavoriteServiceTest {

    @MockBean
    private FavoriteRepository repo;

    @Autowired
    private FavoriteServiceImpl service;

    @Autowired
    private  FavoriteRepository realFavoriteRepo;

    private Favorite mockFavorite;
    private List<Favorite> favoriteList;

    private static Logger logger= LoggerFactory.getLogger(FavoriteServiceTest.class);


    @BeforeEach
    void init(){
        logger.info(()->("== Log me BEFORE each test method =="));

        this.mockFavorite=new Favorite();
        //Creating the mock list

        List<Genre> genres1= Arrays.asList(new Genre(1,"action"),new Genre(2,"drama"));
        List<Integer> genreIds1=Arrays.asList(1,2);
        List<Genre> genres2=Arrays.asList(new Genre(2,"drama"),new Genre(3,"horror"));
        List<Integer> genreIds2=Arrays.asList(2,3);
        List<Genre> genres3=Arrays.asList(new Genre(1,"action"),new Genre(3,"horror"));
        List<Integer> genreIds3=Arrays.asList(1,3);


        List<Movie> movieList=
                Arrays.asList(
                        new Movie(1,genreIds1,"Die hard",LocalDate.of(2019,10,7)),
                        new Movie(2,genreIds2,"Iron fist",LocalDate.of(2021,11,2)),
                        new Movie(3,genreIds3,"Jackie Chan",LocalDate.of(2021,11,2))
                );
        movieList.get(0).setGenres(genres1);
        movieList.get(0).setGenres(genres2);
        movieList.get(0).setGenres(genres3);

        List<Integer> movieIdsList=Arrays.asList(1,2,3);

        User user1=new User(1,"Mary","May","may@gmail.com");
        User user2=new User(1,"Mary","May","may@gmail.com");

        this.mockFavorite = new Favorite(1,user1,movieIdsList);
        this.mockFavorite.setMovies(movieList);
        List<Favorite> list=
                Arrays.asList(
                        new Favorite(1,user1,movieIdsList),
                        new Favorite(2,user2,movieIdsList));
        favoriteList=list;

        favoriteList.get(0).setMovies(movieList);
        favoriteList.get(1).setMovies(movieList);

    }



    @Test
    @DisplayName("TEST  getFavoritesWithResult")
    void getFavoritesWithResults() throws Exception{

        Mockito.when(repo.findAll()).thenReturn(favoriteList);

        //call the service
        List<Favorite> returnedList= (List<Favorite>) realFavoriteRepo.findAll();

        //Validate the result
        Assertions.assertFalse(returnedList.isEmpty(), "");
        Assertions.assertEquals("Mary",returnedList.get(0).getUser().getUserFirstName());
        Assertions.assertEquals("May",returnedList.get(1).getUser().getUserLastName());
        Assertions.assertEquals(1, returnedList.get(0).getFavoriteId());

    }


    @Test
    @DisplayName("TEST getFavoritesByIdFound")
    void getFavoriteById_Found() throws Exception {
        // Use the mock repo to get mock a return value from existsById() and findById()
        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.when(repo.findById(1)).thenReturn(Optional.of(this.mockFavorite));

        // Call the service
        Optional<Favorite> returnedFavorite = service.getFavoriteById(1);

        // Validate the result
        Assertions.assertEquals(this.mockFavorite,returnedFavorite.get());

    }

    @Test
    @DisplayName("TEST getFavoriteByIdNotFound")
    void getFavoriteById_NotFound() throws Exception {
        // Use the mock repo to get mock a return value from existsById()
        Mockito.when(repo.existsById(100)).thenReturn(false);

        // throw NotFoundException when resource does not exist
        Assertions.assertThrows(NotFoundException.class, () -> service.getFavoriteById(100));
    }

    @Test
    @DisplayName("TEST post Favorite")
    void saveFavorite() throws Exception {
        Mockito.when(repo.save(this.mockFavorite)).thenReturn(this.mockFavorite);

        // Call the service
        service.saveFavorite(this.mockFavorite);
        Integer favoriteId=mockFavorite.getFavoriteId();

        // Validate the result
        Assertions.assertEquals(1, favoriteId);
    }

    @Test
    @DisplayName("TEST post Favorite not successful")
    void saveFavorite_Conflict() {
        Mockito.when(repo.save(this.mockFavorite)).thenReturn(this.mockFavorite);

        // Call the service
        service.saveFavorite(this.mockFavorite);
        Integer favoriteId=mockFavorite.getFavoriteId();

        // Validate the result
        Assertions.assertEquals(1, favoriteId);
    }

    @Test
    @DisplayName("TEST update Favorite ")
    public void  updateFavoriteById(){
        Favorite f=Mockito.mock(Favorite.class);
        Mockito.when(repo.existsById(mockFavorite.getFavoriteId())).thenReturn(true);
        Mockito.when(repo.getById(mockFavorite.getFavoriteId())).thenReturn(f);

        service.updateFavorite(mockFavorite,mockFavorite.getFavoriteId());
        Mockito.verify(f).updateTo(mockFavorite);
        Mockito.verify(repo).save(f);

    }

    @Test
    @DisplayName("TEST update Favorite throws not found exception ")
    public void  updateFavoriteById_NotFound(){
        Mockito.when(repo.existsById(-1)).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> service.getFavoriteById(-1));


    }



    @Test
    @DisplayName("TEST delete movie from Favorite")
    public void deleteFavoriteById() throws Exception {

        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.doNothing().when(repo).deleteById(1);

        // Call the service
        service.deleteMovie(1);
    }


    @Test
    @DisplayName("TEST delete Favorite not successful")
    public void deleteFavorite_NotFound(){
        Mockito.when(repo.existsById(1)).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, ()->service.deleteMovie(1));


    }




}

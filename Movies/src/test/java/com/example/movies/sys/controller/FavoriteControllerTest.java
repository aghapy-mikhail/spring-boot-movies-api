package com.example.movies.sys.controller;

import com.example.movies.sys.Utils;
import com.example.movies.sys.entity.Favorite;
import com.example.movies.sys.entity.Genre;
import com.example.movies.sys.entity.Movie;
import com.example.movies.sys.entity.User;
import com.example.movies.sys.exception.InternalServerException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.FavoriteRepository;
import com.example.movies.sys.service.FavoriteService;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteService service;


    @MockBean
    private FavoriteRepository repo;

    private Favorite mockFavorite;

    private static Logger logger= LoggerFactory.getLogger(FavoriteControllerTest.class);


    @BeforeEach
    void init() {
        logger.info(() -> ("== Log me BEFORE each test method =="));
        List<Genre> genres1= Arrays.asList(new Genre(1,"action"),new Genre(2,"drama"));
        List<Integer> genreIds1=Arrays.asList(1,2);
        List<Genre> genres2=Arrays.asList(new Genre(2,"drama"),new Genre(3,"horror"));
        List<Integer> genreIds2=Arrays.asList(2,3);
        List<Genre> genres3=Arrays.asList(new Genre(1,"action"),new Genre(3,"horror"));
        List<Integer> genreIds3=Arrays.asList(1,3);


        List<Movie> movieList=
                Arrays.asList(
                        new Movie(1,genreIds1,"Die hard",LocalDate.of(2019,10,7)),
                        new Movie(2,genreIds3,"Iron fist",LocalDate.of(2021,11,2)),
                        new Movie(3,genreIds3,"Jackie Chan",LocalDate.of(2021,11,2))
                );
        movieList.get(0).setGenres(genres1);
        movieList.get(1).setGenres(genres2);
        movieList.get(2).setGenres(genres3);

        List<Integer> movieIdsList=Arrays.asList(1,2,3);
        User user=new User(1,"Mary","May","may@gmail.com");
        this.mockFavorite = new Favorite(1,user,movieIdsList);
        this.mockFavorite.setMovies(movieList);
    }



    @Test
    @DisplayName("GET /favorites WITH RESULTS")
    void getFavorites_WithResults() throws Exception{
        //Creating the mock list
        User u11=new User(1,"David","Charming","charming@gmail.com");
        User u22=new User(2,"George","Charming","george@gmail.com");
        List<Genre> genres11= Arrays.asList(new Genre(1,"action"),new Genre(2,"drama"));
        List<Integer> genreIds11=Arrays.asList(1,2);
        List<Genre> genres22=Arrays.asList(new Genre(2,"drama"),new Genre(3,"horror"));
        List<Integer> genreIds22=Arrays.asList(2,3);
        List<Genre> genres33=Arrays.asList(new Genre(1,"action"),new Genre(3,"horror"));
        List<Integer> genreIds33=Arrays.asList(1,3);
        List<Movie> movieList1=
                Arrays.asList(
                        new Movie(1,genreIds11,"Die hard",LocalDate.of(2019,10,7)),
                        new Movie(2,genreIds22,"Iron fist",LocalDate.of(2021,11,2)),
                        new Movie(3,genreIds33,"Jackie Chan",LocalDate.of(2021,11,2))
                );
        movieList1.get(0).setGenres(genres11);
        movieList1.get(1).setGenres(genres22);
        movieList1.get(2).setGenres(genres33);

        List<Integer> movieIds1=Arrays.asList(1,2,3);
        List<Favorite> listt=
                Arrays.asList(
                        new Favorite(1,u11,movieIds1),
                        new Favorite(2,u22,movieIds1));

   listt.get(0).setMovies(movieList1);
   listt.get(1).setMovies(movieList1);
        Page<Favorite> favoritesPage=new PageImpl<Favorite>(listt);
        Mockito.when(service.getFavorites(Mockito.any())).thenReturn(favoritesPage);;

        //execute the request
        mockMvc.perform(get("/favorites"))

                //validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))


                .andExpect(jsonPath("$.favorites[0].user.userLastName").value(listt.get(0).getUser().getUserLastName()))
                .andExpect(jsonPath("$.favorites[0].user.userLastName").value(listt.get(1).getUser().getUserLastName()));

    }



    @Test
    @DisplayName("GET /favorites WITH NO RESULTS")
    void getFavorites_NoResults() throws Exception {

//        Pageable paging= PageRequest.of(0,1);

        Page<Favorite> favoritesPage=new PageImpl<Favorite>(new ArrayList<>());
        Mockito.when(service.getFavorites(Mockito.any())).thenReturn(favoritesPage);

        //execute the request
        mockMvc.perform(get("/favorites"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"favorites\":[],\"allPages\":1,\"allItems\":0,\"currentPage\":0}"));

    }


    @Test
    @DisplayName("GET /favorites/1 is FOUND")
    void getFavoriteById_Found() throws Exception {

        Mockito.when(service.getFavoriteById(1)).thenReturn(Optional.of(this.mockFavorite));

        mockMvc.perform(get("/favorites/{favoriteId}", 1))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.favoriteId").value(1))
                .andExpect(jsonPath("$.user.userFirstName").value(mockFavorite.getUser().getUserFirstName()))
                .andExpect(jsonPath("$.user.userLastName").value(mockFavorite.getUser().getUserLastName()));

    }

    @Test
    @DisplayName("GET /favorites/1 is NOT FOUND")
    void getFavoriteByIdNotFound() throws Exception {

        Integer nonExistingRecordId = 100;
        Mockito.when(service.getFavoriteById(nonExistingRecordId)).thenThrow(new NotFoundException(nonExistingRecordId));

        mockMvc.perform(get("/favorites/{favoriteId}", nonExistingRecordId))

                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.type").value("NotFoundException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Unable to find the record with ID 100 "));
    }

    @Test
    @DisplayName("PATCH /favorites/1 is FOUND and UPDATED")
    void patchFavoriteById_Found() throws Exception {
        Mockito.doNothing().when(service).updateFavorite(mockFavorite, 1);

        mockMvc.perform(patch("/favorites/{favoriteId}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(Utils.getJsonString(mockFavorite)))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("PATCH /favorites/1 is NOT PATCHED SUCCESSFULLY")
    void patchFavoriteById_NotFound() throws Exception {

        Mockito.doThrow(new InternalServerException("favorites")).when(service).updateFavorite(Mockito.any(), Mockito.anyInt());

        mockMvc.perform(patch("/favorites/{favoriteId}", 1).contentType(MediaType.APPLICATION_JSON_VALUE).content(Utils.getJsonString(mockFavorite)))

                .andExpect(status().isInternalServerError())

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))


                .andExpect(jsonPath("$.type").value("InternalServerException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("There is an issue regarding the service: favorites"));

    }














}

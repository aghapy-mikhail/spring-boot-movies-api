package com.example.movies.sys.controller;

import com.example.movies.sys.Utils;
import com.example.movies.sys.entity.Genre;
import com.example.movies.sys.entity.Movie;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.InternalServerException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.service.GenreService;
import com.example.movies.sys.service.MovieService;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService service;


    private Movie mockMovie;

    private static Logger logger = LoggerFactory.getLogger(MovieControllerTest.class);

    @BeforeEach
    void init() {
        logger.info(() -> ("== Log me BEFORE each test method =="));
        Genre genre1 = new Genre(1, "drama");
        Genre genre2 = new Genre(2, "action");
        List<Genre> genres = new ArrayList<>();
        List<Integer> genreIds=new ArrayList<>();

        genreIds.add(genre1.getGenreId());
        genreIds.add(genre2.getGenreId());
        genres.add(genre1);
        genres.add(genre2);

        this.mockMovie = new Movie(1, genreIds, "Men in Black", LocalDate.of(2021, 11, 2));
        mockMovie.setGenres(genres);
    }



      @Test
    @DisplayName("GET /movies WITH RESULTS")
    void getMovies_WithResults() throws Exception{
       //Creating the mock list
          List<Genre> genres1=Arrays.asList(new Genre(1,"action"),new Genre(2,"drama"));
          List<Integer> genreIds1=Arrays.asList(1,2);
          List<Genre> genres2=Arrays.asList(new Genre(2,"drama"),new Genre(3,"horror"));
          List<Integer> genreIds2=Arrays.asList(2,3);
          List<Genre> genres3=Arrays.asList(new Genre(1,"action"),new Genre(3,"horror"));
          List<Integer> genreIds3=Arrays.asList(1,3);


          List<Movie> list=
                  Arrays.asList(
                         new Movie(1,genreIds1,"Die hard",LocalDate.of(2019,10,7)),
                         new Movie(2,genreIds2,"Iron fist",LocalDate.of(2021,11,2)),
                         new Movie(3,genreIds3,"Jackie Chan",LocalDate.of(2021,11,2))
                  );

          //return the mock list of movies when mock service is used to call getMovies()
          Mockito.when(service.getMovies()).thenReturn(list);

          //execute the request
          mockMvc.perform(get("/movies"))

                  //validate the response code and content type
                  .andExpect(status().isOk())
                  .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                    //validate the response body
                  .andExpect(jsonPath("$.[0].movieId").value(1))
                  .andExpect(jsonPath("$.[0].movieName").value("Die hard"))
                  .andExpect(jsonPath("$.[0].releaseDate").value("2019-10-07"));

      }

    @Test
    @DisplayName("GET /movies WITH NO RESULTS")
    void getMovies_NoResults() throws Exception {

        Mockito.when(service.getMovies()).thenReturn(new ArrayList<>());

        //execute the request
        mockMvc.perform(get("/movies"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));

    }

    @Test
    @DisplayName("GET /movies/1 is FOUND")
    void getMovieByIdFound() throws Exception {

        Mockito.when(service.getMovieById(1)).thenReturn(Optional.of(this.mockMovie));

        mockMvc.perform(get("/movies/{movieId}", 1))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.movieId").value(1))
                .andExpect(jsonPath("$.genres.[0].genreName").value(mockMovie.getGenres().get(0).getGenreName()))
                .andExpect(jsonPath("$.movieName").value(mockMovie.getMovieName()))
                .andExpect(jsonPath("$.releaseDate").value(mockMovie.getReleaseDate().toString()));
    }

//    @Test
//    @DisplayName("GET /movies/{$genre} is FOUND")
//    void getMovieByGenre() throws Exception {
//        Genre dramaGenre=new Genre(2,"drama");
//        List<Movie> list=
//                Arrays.asList( mockMovie);
//        Mockito.when(service.findByGenres("drama",service.getMovies())).thenReturn(list);
//
//
//        mockMvc.perform(get("/movies?genre=drama"))
//
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.[0].movieId").value(1))
//                .andExpect(jsonPath("$.[0].movieName").value(list.get(0).getMovieName()))
//                .andExpect(jsonPath("$.[0].releaseDate").value(list.get(0).getReleaseDate().toString()));
//    }

    @Test
    @DisplayName("GET /movies/{$movieName} is FOUND")
    void getMovieByName() throws Exception {

        List<Genre> genres1=Arrays.asList(new Genre(1,"action"),new Genre(2,"drama"));
        List<Integer> genreIds1=Arrays.asList(1,2);
        List<Genre> genres2=Arrays.asList(new Genre(2,"drama"),new Genre(3,"horror"));
        List<Integer> genreIds2=Arrays.asList(2,3);

        List<Movie> list=
                Arrays.asList(
                        new Movie(1,genreIds1,"Die hard",LocalDate.of(1989,10,7)),
                        new Movie(2,genreIds2,"Die hard",LocalDate.of(2021,11,2))
                );
        Mockito.when(service.findByMovieName("Die hard")).thenReturn(list);

        mockMvc.perform(get("/movies?movieName=Die hard"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].movieId").value(1))
                .andExpect(jsonPath("$.[0].movieName").value(list.get(0).getMovieName()))
                .andExpect(jsonPath("$.[0].releaseDate").value(list.get(0).getReleaseDate().toString()));
    }

    @Test
    @DisplayName("GET /movies/1 is NOT FOUND")
    void getMovieByIdNotFound() throws Exception {

        Integer nonExistingRecordId = 100;
        Mockito.when(service.getMovieById(nonExistingRecordId)).thenThrow(new NotFoundException(nonExistingRecordId));

        mockMvc.perform(get("/movies/{movieId}", nonExistingRecordId))

                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("NotFoundException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Unable to find the record with ID 100 "));
    }

    @Test
    @DisplayName("POST /movies is SUCCESSFUL")
    void addMovieSuccess() throws Exception {
//Create a movie for this purpose
        List<Genre> g=Arrays.asList(new Genre(1,"action"));
        List<Integer> gIds=Arrays.asList(1);
        Movie m= new Movie(1,gIds,"Die hard",LocalDate.of(2019,10,7));
        m.setGenres(g);

                Mockito.when(service.saveMovie(Mockito.any())).thenReturn(mockMovie);

        mockMvc.perform(post("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockMovie)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/movies/1"));

    }

    @Test
    @DisplayName("POST /movies is Conflict Exception")
    void addMovie_ConflictException() throws Exception {

        Mockito.when(service.saveMovie(Mockito.any())).thenThrow(new ExistingRecordException(this.mockMovie.getMovieName()));

        mockMvc.perform(post("/movies/").contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockMovie)))

                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.type").value("ExistingRecordException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Conflict Exception: " + this.mockMovie.getMovieName()+" "));

    }

    @Test
    @DisplayName("POST /movies is NOT SUCCESSFUL")
    void postMovie_InternalServerException() throws Exception {
        Mockito.when(service.saveMovie(Mockito.any()))
                .thenAnswer(invocation -> {
                    throw new URISyntaxException("Wrong URI body", "Exception message");
                });
        mockMvc.perform(post("/movies/").contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockMovie)))

                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("InternalServerException"))
                .andExpect(jsonPath("$.message").value(
                        "There is an issue regarding the service: The URI in the Location header in POST /movies has an error."));
    }

    @Test
    @DisplayName("PATCH /movies/1 is FOUND and UPDATED")
    void patchMovieByIdFound() throws Exception {
        Mockito.doNothing().when(service).updateMovie(mockMovie, 1);

        mockMvc.perform(patch("/movies/{movieId}", 1)
        .contentType(MediaType.APPLICATION_JSON_VALUE).content(Utils.getJsonString(mockMovie)))
        .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("PATCH /movies/1 is NOT PATCHED SUCCESSFULLY")
    void patchMovieByIdNotFound() throws Exception {

   Mockito.doThrow(new InternalServerException("movies")).when(service).updateMovie(Mockito.any(), Mockito.anyInt());

        mockMvc.perform(patch("/movies/{movieId}", 1).contentType(MediaType.APPLICATION_JSON_VALUE).content(Utils.getJsonString(mockMovie)))

                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))


                .andExpect(jsonPath("$.type").value("InternalServerException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("There is an issue regarding the service: movies"));

    }


    @Test
    @DisplayName("DELETE /movies/1 is FOUND and DELETED")
    void deleteMovieByIdFound() throws Exception {
        Mockito.doNothing().when(service).deleteMovie(1);

        // Execute the request
        mockMvc.perform(delete("/movies/{movieId}", 1))

                // Validate the response code and content type
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /movies/100 is NOT FOUND")
    void deleteMovieByIdNotFound() throws Exception {
        // Using the mock service, return a NotFoundException when resource is not found
        Integer nonExistingRecord = 100;
        Mockito.doThrow(new NotFoundException(nonExistingRecord)).when(service).deleteMovie(nonExistingRecord);

        // Execute the request
        mockMvc.perform(delete("/movies/{movieId}", nonExistingRecord))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the response body
                .andExpect(jsonPath("$.type").value("NotFoundException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Unable to find the record with ID 100 "));
    }


}

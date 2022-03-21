package com.example.movies.sys.controller;


import com.example.movies.sys.Utils;
import com.example.movies.sys.entity.Genre;

import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.InternalServerException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.service.GenreService;
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


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService service;

    private Genre mockGenre;
    private static Logger logger= LoggerFactory.getLogger(GenreControllerTest.class);


    @BeforeEach
    void init() {
        logger.info(() -> ("== Log me BEFORE each test method =="));
        Genre genre1 = new Genre(1, "drama");

        this.mockGenre=genre1;
       }


    @Test
    @DisplayName("GET /genres WITH RESULTS")
    void getGenresWithResults() throws Exception{

        List<Genre> genreList=Arrays.asList(
                new Genre(1,"action"),
                new Genre(2,"drama"));
        Mockito.when(service.getGenres()).thenReturn(genreList);

        //execute the request
        mockMvc.perform(get("/genres"))

                //validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                //validate the response body
                .andExpect(jsonPath("$.[0].genreId").value(1))
                .andExpect(jsonPath("$.[0].genreName").value("action"));
    }


    @Test
    @DisplayName("GET /genres WITH NO RESULTS")
    void getGenresWithNoResults() throws Exception {

        Mockito.when(service.getGenres()).thenReturn(new ArrayList<>());

        //execute the request
        mockMvc.perform(get("/genres"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));

    }


    @Test
    @DisplayName("GET /genres/1 is FOUND")
    void getGenreByIdFound() throws Exception {

        Mockito.when(service.getGenreById(1)).thenReturn(Optional.of(this.mockGenre));

        mockMvc.perform(get("/genres/{genreId}", 1))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.genreId").value(1))
                .andExpect(jsonPath("$.genreName").value("drama"));

    }

    @Test
    @DisplayName("GET /genres/1 is NOT FOUND")
    void getGenreByIdNotFound() throws Exception {

        Integer nonExistingRecordId = 100;
        Mockito.when(service.getGenreById(nonExistingRecordId)).thenThrow(new NotFoundException(nonExistingRecordId));

        mockMvc.perform(get("/genres/{genreId}", nonExistingRecordId))

                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.type").value("NotFoundException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Unable to find the record with ID 100 "));
    }
    @Test
    @DisplayName("POST /genres is Conflict Exception")
    void addGenre_ConflictException() throws Exception {

        Mockito.when(service.saveGenre(Mockito.any())).thenThrow(new ExistingRecordException(this.mockGenre.getGenreName()));

        mockMvc.perform(post("/genres/").contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockGenre)))

                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.type").value("ExistingRecordException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Conflict Exception: " + this.mockGenre.getGenreName()+" "));

    }


    @Test
    @DisplayName("POST /genres is SUCCESSFUL")
    void addGenreSuccess() throws Exception {

         Genre genre= new Genre(1,"science");

        Mockito.when(service.saveGenre(Mockito.any())).thenReturn(mockGenre);

        mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockGenre)))

                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/genres/1"));

    }



    @Test
    @DisplayName("POST /genres is NOT SUCCESSFUL")
    void postGenre_InternalServerException() throws Exception {
        Mockito.when(service.saveGenre(Mockito.any()))
                .thenAnswer(invocation -> {
                    throw new URISyntaxException("Wrong URI body", "Exception message");
                });
        mockMvc.perform(post("/genres/").contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockGenre)))

                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("InternalServerException"))
                .andExpect(jsonPath("$.message").value(
                        "There is an issue regarding the service: The URI in the Location header in POST /genres has an error."));
    }


    @Test
    @DisplayName("PATCH /genres/1 is FOUND and UPDATED")
    void patchGenreByIdFound() throws Exception {
        Mockito.doNothing().when(service).updateGenre(mockGenre, 1);

        mockMvc.perform(patch("/genres/{genreId}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(Utils.getJsonString(mockGenre)))
                .andExpect(status().isNoContent());

    }


    @Test
    @DisplayName("PATCH /genres/1 is NOT PATCHED SUCCESSFULLY")
    void patchGenreeByIdNotFound() throws Exception {

        Mockito.doThrow(new InternalServerException("genres")).when(service).updateGenre(Mockito.any(), Mockito.anyInt());

        mockMvc.perform(patch("/genres/{genreId}", 1).contentType(MediaType.APPLICATION_JSON_VALUE).content(Utils.getJsonString(mockGenre)))

                .andExpect(status().isInternalServerError())

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))


                .andExpect(jsonPath("$.type").value("InternalServerException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("There is an issue regarding the service: genres"));

    }


    @Test
    @DisplayName("DELETE /genres/1 is FOUND and DELETED")
    void deleteGenreByIdFound() throws Exception {
        Mockito.doNothing().when(service).deleteGenre(1);

        // Execute the request
        mockMvc.perform(delete("/genres/{genreId}", 1))

                // Validate the response code and content type
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("DELETE /genres/100 is NOT FOUND")
    void deleteGenreByIdNotFound() throws Exception {
        // Using the mock service, return a NotFoundException when resource is not found
        Integer nonExistingRecord = 100;
        Mockito.doThrow(new NotFoundException(nonExistingRecord)).when(service).deleteGenre(nonExistingRecord);

        // Execute the request
        mockMvc.perform(delete("/genres/{genreId}", nonExistingRecord))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the response body
                .andExpect(jsonPath("$.type").value("NotFoundException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Unable to find the record with ID 100 "));
    }






}

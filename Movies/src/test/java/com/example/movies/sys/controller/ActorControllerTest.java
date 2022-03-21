package com.example.movies.sys.controller;

import com.example.movies.sys.Utils;
import com.example.movies.sys.entity.Actor;

import com.example.movies.sys.entity.Genre;
import com.example.movies.sys.entity.Movie;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.InternalServerException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.service.ActorService;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ActorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActorService service;

//    @Autowired
//    private ActorService realActorservice;

    private Actor mockActor;

    private static Logger logger= LoggerFactory.getLogger(ActorControllerTest.class);

    @BeforeEach
    void init() {
        logger.info(() -> ("== Log me BEFORE each test method =="));
        this.mockActor = new Actor(1,"Jonny Depp",LocalDate.of(1973,5,4));

    }



    @Test
    @DisplayName("GET /actors WITH RESULTS")
    void getActorsWithResults() throws Exception{

         List<Actor> list=
                Arrays.asList(
                        new Actor(1,"Jessica Alba",LocalDate.of(2010,10,10)),
                        new Actor(2,"John Rich", LocalDate.of(1988,8,8)),
                        new Actor(3,"Anna Mey Li",LocalDate.of(1966,6,6)));

        Mockito.when(service.getActors()).thenReturn(list);

        //execute the request
        mockMvc.perform(get("/actors"))

                //validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                //validate the response body
                .andExpect(jsonPath("$.[0].actorId").value(1))
                .andExpect(jsonPath("$.[0].actorName").value(list.get(0).getActorName()))
                .andExpect(jsonPath("$.[1].actorName").value(list.get(1).getActorName()));

    }


    @Test
    @DisplayName("GET /actors WITH NO RESULTS")
    void getActors_NoResults() throws Exception {

        Mockito.when(service.getActors()).thenReturn(new ArrayList<>());

        //execute the request
        mockMvc.perform(get("/actors"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));

    }


    @Test
    @DisplayName("GET /actors/1 is FOUND")
    void getActorById_Found() throws Exception {

        Mockito.when(service.getActorById(1)).thenReturn(Optional.of(this.mockActor));

        mockMvc.perform(get("/actors/{actorId}", 1))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.actorId").value(1))
                .andExpect(jsonPath("$.actorName").value(mockActor.getActorName()))
                .andExpect(jsonPath("$.dateOfBirth").value(mockActor.getDateOfBirth().toString()));
    }



    @Test
    @DisplayName("GET /actors/1 is NOT FOUND")
    void getActorById_NotFound() throws Exception {

        Integer nonExistingRecordId = 100;
        Mockito.when(service.getActorById(nonExistingRecordId)).thenThrow(new NotFoundException(nonExistingRecordId));

        mockMvc.perform(get("/actors/{actorId}", nonExistingRecordId))

                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.type").value("NotFoundException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Unable to find the record with ID 100 "));
    }


    @Test
    @DisplayName("GET /actors?=actorName is FOUND")
    void getActorByName() throws Exception {

        List<Actor> list=
                Arrays.asList(
                        new Actor(1,"Jessica Alba",LocalDate.of(2010,10,10)),
                        new Actor(2,"Jessica Alba",LocalDate.of(1966,6,6)));

        Mockito.when(service.getActors()).thenReturn(list);

        Mockito.when(service.findByActorName("Jessica Alba")).thenReturn(list);

        mockMvc.perform(get("/actors/?actorName={actorName}", list.get(0).getActorName()))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].actorId").value(1))
                .andExpect(jsonPath("$.[0].actorName").value(list.get(0).getActorName()))
                .andExpect(jsonPath("$.[0].dateOfBirth").value(list.get(0).getDateOfBirth().toString()));
    }


    @Test
    @DisplayName("POST /actors is SUCCESSFUL")
    void addActor_Success() throws Exception {


        Mockito.when(service.saveActor(Mockito.any())).thenReturn(mockActor);

        mockMvc.perform(post("/actors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockActor)))

                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/actors/1"));

    }


    @Test
    @DisplayName("POST /actors is NOT SUCCESSFUL")
    void postActor_InternalServerException() throws Exception {
                Mockito.when(service.saveActor(Mockito.any()))
                .thenAnswer(invocation -> {
                    throw new URISyntaxException("Wrong URI body", "Exception message");
                });
        mockMvc.perform(post("/actors/").contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockActor)))

                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("InternalServerException"))
                .andExpect(jsonPath("$.message").value(
                        "There is an issue regarding the service: The URI in the Location header in POST /actors has an error."));
    }



    @Test
    @DisplayName("POST /actors is Conflict Exception")
    void addActor_ConflictException() throws Exception {

        Mockito.when(service.saveActor(Mockito.any())).thenThrow(new ExistingRecordException(this.mockActor.getActorName()));

        mockMvc.perform(post("/actors/").contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockActor)))

                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.type").value("ExistingRecordException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Conflict Exception: " + this.mockActor.getActorName()+" "));

    }

    @Test
    @DisplayName("PATCH /actors/1 is FOUND and UPDATED")
    void patchActorById_Found() throws Exception {
        Mockito.doNothing().when(service).updateActor(mockActor, 1);

        mockMvc.perform(patch("/actors/{actorId}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(Utils.getJsonString(mockActor)))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("PATCH /actor/1 is NOT PATCHED SUCCESSFULLY")
    void patchActorById_NotFound() throws Exception {

        Mockito.doThrow(new InternalServerException("actors")).when(service).updateActor(Mockito.any(), Mockito.anyInt());

        mockMvc.perform(patch("/actors/{actorId}", 1).contentType(MediaType.APPLICATION_JSON_VALUE).content(Utils.getJsonString(mockActor)))

                .andExpect(status().isInternalServerError())

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))


                .andExpect(jsonPath("$.type").value("InternalServerException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("There is an issue regarding the service: actors"));

    }

    @Test
    @DisplayName("DELETE /actors/1 is FOUND and DELETED")
    void deleteActorById_Found() throws Exception {
        Mockito.doNothing().when(service).deleteActor(1);

        // Execute the request
        mockMvc.perform(delete("/actors/{actorId}", 1))

                // Validate the response code and content type
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /actors/100 is NOT FOUND")
    void deleteActorById_NotFound() throws Exception {
        // Using the mock service, return a NotFoundException when resource is not found
        Integer nonExistingRecord = 100;
        Mockito.doThrow(new NotFoundException(nonExistingRecord)).when(service).deleteActor(nonExistingRecord);

        // Execute the request
        mockMvc.perform(delete("/actors/{actorId}", nonExistingRecord))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the response body
                .andExpect(jsonPath("$.type").value("NotFoundException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Unable to find the record with ID 100 "));
    }


}




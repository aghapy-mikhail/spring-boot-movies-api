package com.example.movies.sys.controller;

import com.example.movies.sys.Utils;

import com.example.movies.sys.entity.User;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.InternalServerException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.service.UserService;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    private User mockUSer;

    private static Logger logger= LoggerFactory.getLogger(UserControllerTest.class);


    @BeforeEach
    void init() {
        logger.info(() -> ("== Log me BEFORE each test method =="));

        this.mockUSer = new User(1,"Mary","Doe","mary.mary@gmail.com");
    }




    @Test
    @DisplayName("GET /users WITH RESULTS")
    void getUsers_WithResults() throws Exception{
          List<User> list=
                Arrays.asList(
                        new User(1,"Jerry","Lee","lee_lee@gmail.com"),
                        new User(2,"Brad","Jolli","jolly_me@gmail.com"),
                        new User(3,"John","Chan","chanyy_y@gmail.com"));

        Mockito.when(service.getUsers()).thenReturn(list);

        //execute the request
        mockMvc.perform(get("/users"))

                //validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                //validate the response body
                .andExpect(jsonPath("$.[0].userId").value(1))
                .andExpect(jsonPath("$.[0].userFirstName").value(list.get(0).getUserFirstName()))
                .andExpect(jsonPath("$.[0].userLastName").value(list.get(0).getUserLastName()))
                .andExpect(jsonPath("$.[1].userLastName").value(list.get(1).getUserLastName()));

    }

    @Test
    @DisplayName("GET /users WITH NO RESULTS")
    void getUsers_NoResults() throws Exception {

        Mockito.when(service.getUsers()).thenReturn(new ArrayList<>());

        //execute the request
        mockMvc.perform(get("/users"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));

    }

    @Test
    @DisplayName("GET /users/1 is FOUND")
    void getUserByIdFound() throws Exception {

        Mockito.when(service.getUserById(1)).thenReturn(Optional.of(this.mockUSer));

        mockMvc.perform(get("/users/{userId}", 1))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.userFirstName").value(mockUSer.getUserFirstName()))
                .andExpect(jsonPath("$.userLastName").value(mockUSer.getUserLastName()))
                .andExpect(jsonPath("$.email").value(mockUSer.getEmail()));
    }

    @Test
    @DisplayName("GET /users/1 is NOT FOUND")
    void getUserById_NotFound() throws Exception {

        Integer nonExistingRecordId = 100;
        Mockito.when(service.getUserById(nonExistingRecordId)).thenThrow(new NotFoundException(nonExistingRecordId));

        mockMvc.perform(get("/users/{userId}", nonExistingRecordId))

                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.type").value("NotFoundException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Unable to find the record with ID 100 "));
    }

    @Test
    @DisplayName("POST /users is SUCCESSFUL")
    void addUser_Success() throws Exception {

        Mockito.when(service.saveUser(Mockito.any())).thenReturn(mockUSer);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockUSer)))

                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/users/1"));

    }

    @Test
    @DisplayName("POST /users is Conflict Exception")
    void addUser_ConflictException() throws Exception {

        Mockito.when(service.saveUser(Mockito.any())).thenThrow(new ExistingRecordException(this.mockUSer.getEmail()));

        mockMvc.perform(post("/users/").contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockUSer)))

                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.type").value("ExistingRecordException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Conflict Exception: mary.mary@gmail.com "));

    }

    @Test
    @DisplayName("POST /users is NOT SUCCESSFUL")
    void postUser_InternalServerException() throws Exception {
        Mockito.when(service.saveUser(Mockito.any()))
                .thenAnswer(invocation -> {
                    throw new URISyntaxException("Wrong URI body", "Exception message");
                });
        mockMvc.perform(post("/users/").contentType(MediaType.APPLICATION_JSON)
                .content(Utils.getJsonString(this.mockUSer)))

                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("InternalServerException"))
                .andExpect(jsonPath("$.message").value(
                        "There is an issue regarding the service: The URI in the location header in POST /users has an error"));
    }



    @Test
    public void saveUser_LocationUriError() throws Exception {
        // Mock service call
        Mockito.when(service.saveUser(Mockito.any())).thenThrow(new InternalServerException("The URI in the Location header in POST /users has an error"));

        // Perform the request.
        mockMvc.perform(post("/users/")
                .contentType(MediaType.APPLICATION_JSON).content(Utils.getJsonString(mockUSer)))
                // Validate status and content type.
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate returned data.
                .andExpect(jsonPath("$.type").value("InternalServerException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("There is an issue regarding the service: The URI in the Location header in POST /users has an error"));
    }

    @Test
    @DisplayName("PATCH /users/1 is FOUND and UPDATED")
    void patchUserById_Found() throws Exception {
        Mockito.doNothing().when(service).updateUser(mockUSer, 1);

        mockMvc.perform(patch("/users/{userId}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(Utils.getJsonString(mockUSer)))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("PATCH /users/1 is NOT PATCHED SUCCESSFULLY")
    void patchUserById_NotFound() throws Exception {

        Mockito.doThrow(new InternalServerException("users")).when(service).updateUser(Mockito.any(), Mockito.anyInt());

        mockMvc.perform(patch("/users/{userId}", 1).contentType(MediaType.APPLICATION_JSON_VALUE).content(Utils.getJsonString(mockUSer)))

                .andExpect(status().isInternalServerError())

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))


                .andExpect(jsonPath("$.type").value("InternalServerException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("There is an issue regarding the service: users"));

    }


    @Test
    @DisplayName("DELETE /users/1 is FOUND and DELETED")
    void deleteUserByIdFound() throws Exception {
        Mockito.doNothing().when(service).deleteUser(1);

        // Execute the request
        mockMvc.perform(delete("/users/{userId}", 1))

                // Validate the response code and content type
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users/100 is NOT FOUND")
    void deleteUserById_NotFound() throws Exception {
        // Using the mock service, return a NotFoundException when resource is not found
        Integer nonExistingRecord = 100;
        Mockito.doThrow(new NotFoundException(nonExistingRecord)).when(service).deleteUser(nonExistingRecord);

        // Execute the request
        mockMvc.perform(delete("/users/{userId}", nonExistingRecord))

                // Validate the response code and content type
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the response body
                .andExpect(jsonPath("$.type").value("NotFoundException"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").value("Unable to find the record with ID 100 "));
    }



}

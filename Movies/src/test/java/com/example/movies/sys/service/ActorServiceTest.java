package com.example.movies.sys.service;

import com.example.movies.sys.entity.Actor;
import com.example.movies.sys.entity.Movie;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.ActorRepository;
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
public class ActorServiceTest {

    @MockBean
    private ActorRepository repo;

    @Autowired
    private ActorServiceImpl service;

    private Actor mockActor;
    private List<Actor> actorList;

    private static Logger logger= LoggerFactory.getLogger(ActorServiceTest.class);


    @BeforeEach
    void init(){
        logger.info(()->("== Log me BEFORE each test method =="));

        this.mockActor=new Actor(1,"Angelina Jolie",LocalDate.of(1977,10,7));

        List<Actor> list=
                Arrays.asList(
                        new Actor(1,"Jessica Alba",LocalDate.of(2010,10,10)),
                        new Actor(2,"John Rich", LocalDate.of(1988,8,8)),
                        new Actor(3,"Anna Mey Li",LocalDate.of(1966,6,6)));

        actorList=list;

    }



    @Test
    @DisplayName("TEST  getActorsWithResult")
    void getActorsWithResults() throws Exception{

        Mockito.when(repo.findAll()).thenReturn(actorList);

        //call the service
        List<Actor> returnedList= (List<Actor>) service.getActors();

        //Validate the result
        Assertions.assertFalse(returnedList.isEmpty(), "");
        Assertions.assertEquals("Jessica Alba",returnedList.get(0).getActorName());
        Assertions.assertEquals("John Rich",returnedList.get(1).getActorName());
        Assertions.assertEquals(3, returnedList.get(2).getActorId());

    }

    @Test
    @DisplayName("TEST getActorById Found")
    void getActorById_Found() throws Exception {

        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.when(repo.findById(1)).thenReturn(Optional.of(this.mockActor));

        // Call the service
        Optional<Actor> returnedActor = service.getActorById(1);

        // Validate the result
        Assertions.assertEquals(this.mockActor,returnedActor.get());

    }

    @Test
    @DisplayName("TEST getActorById Not_Found")
    void getActorById_NotFound() throws Exception {
        // Use the mock repo to get mock a return value from existsById()
        Mockito.when(repo.existsById(100)).thenReturn(false);

        // throw NotFoundException when resource does not exist
        Assertions.assertThrows(NotFoundException.class, () -> service.getActorById(100));
    }


    @Test
    @DisplayName("TEST get Actors by name ")
    void getActorsByName() throws Exception {

        String actorName =mockActor.getActorName();

        Mockito.when(service.findByActorName(actorName)).thenReturn(List.of(this.mockActor));

        List<Actor> returnedMovies = service.findByActorName(actorName);

        Assertions.assertEquals(this.mockActor, returnedMovies.get(0));

    }


    @Test
    @DisplayName("TEST post actor")
    void saveActor() throws Exception {
        Mockito.when(repo.save(this.mockActor)).thenReturn(this.mockActor);

        // Call the service
        service.saveActor(this.mockActor);
        Integer actorId=mockActor.getActorId();

        // Validate the result
        Assertions.assertEquals(1, actorId);
    }

//    @Test
//    @DisplayName("TEST post actor not successful")
//    void saveActor_Conflict() {
//        Mockito.when(repo.save(this.mockActor)).thenReturn(this.mockActor);
//
//        // Call the service
//        service.saveActor(this.mockActor);
//        Integer actorId=mockActor.getActorId();
//
//        // Validate the result
//        Assertions.assertEquals(1, actorId);
//    }

    //working
    @Test
    @DisplayName("TEST post actor conflict error")
    public void saveActor_ThrowConflictException() {
        service.saveActor(actorList.get(0));
        // Mock repository call.
        Mockito.doThrow(new ExistingRecordException("x")).when(repo).findByActorName(mockActor.getActorName());
        // Validate service call throws.
        Assertions.assertThrows(ExistingRecordException.class, () -> repo.findByActorName(mockActor.getActorName()));
    }

    @Test
    @DisplayName("TEST update actor conflict error")
    void updateActor_ConflictException() throws Exception {

        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.when(repo.getById(1)).thenReturn(this.mockActor);
        Mockito.when(repo.save(this.mockActor)).thenThrow(new ExistingRecordException(this.mockActor.getActorName()+" actor already exists"));

        Assertions.assertThrows(ExistingRecordException.class, () -> service.updateActor(this.mockActor, 1));
    }

    @Test
    @DisplayName("TEST update actor ")
    public void  updateActorById(){
        Actor a=Mockito.mock(Actor.class);
        Mockito.when(repo.existsById(mockActor.getActorId())).thenReturn(true);
        Mockito.when(repo.getById(mockActor.getActorId())).thenReturn(a);

        service.updateActor(mockActor,mockActor.getActorId());
        Mockito.verify(a).updateTo(mockActor);
        Mockito.verify(repo).save(a);

    }

    @Test
    @DisplayName("TEST update actor throws not found exception ")
    public void  updateActorById_NotFound(){
        Mockito.when(repo.existsById(1)).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> service.updateActor(mockActor,1));



    }

    @Test
    @DisplayName("TEST delete actor")
    public void deleteActorById() throws Exception {

        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.doNothing().when(repo).deleteById(1);

        // Call the service
        service.deleteActor(1);
    }


    @Test
    @DisplayName("TEST delete actor not successful")
    public void deleteActor_NotFound(){
        Mockito.when(repo.existsById(1)).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, ()->service.deleteActor(1));


    }



}

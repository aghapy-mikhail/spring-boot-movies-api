package com.example.movies.sys.service;

import com.example.movies.sys.entity.Genre;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.GenreRepository;
import com.example.movies.sys.repository.MovieRepository;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class GenreServiceTest {

    @MockBean
    private GenreRepository repo;

    @MockBean
    private MovieRepository movieRepository;

    @Autowired
    private GenreServiceImpl service;

    private Genre mockGenre;
    private List<Genre> genreList;

    private static Logger logger= LoggerFactory.getLogger(GenreServiceTest.class);


    @BeforeEach
    void init(){
        logger.info(()->("== Log me BEFORE each test method =="));

        this.mockGenre=new Genre(1,"science");
        //Creating the mock list

          List<Genre> list=
                Arrays.asList(
                        new Genre(1,"action"),
                        new Genre(2,"drama"),
                        new Genre(3,"horror"));
        genreList=list;

    }


    @Test
    @DisplayName("TEST  getGenresWithResult")
    void getGenresWithResults() throws Exception{

        Mockito.when(repo.findAll()).thenReturn(genreList);

        //call the service
        List<Genre> returnedList= (List<Genre>) service.getGenres();

        //Validate the result
        Assertions.assertFalse(returnedList.isEmpty(), "");
        Assertions.assertEquals("action",returnedList.get(0).getGenreName());
        Assertions.assertEquals("drama",returnedList.get(1).getGenreName());
        Assertions.assertEquals(3, returnedList.get(2).getGenreId());

    }



    @Test
    @DisplayName("TEST getGenreByIdFound")
    void getGenreById_Found() throws Exception {
        // Use the mock repo to get mock a return value from existsById() and findById()
        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.when(repo.findById(1)).thenReturn(Optional.of(this.mockGenre));

        // Call the service
        Optional<Genre> returnedGenre = service.getGenreById(1);

        // Validate the result
        Assertions.assertEquals(this.mockGenre,returnedGenre.get());

    }

    @Test
    @DisplayName("TEST getGenreByIdNotFound")
    void getGenreById_NotFound() throws Exception {
        // Use the mock repo to get mock a return value from existsById()
        Mockito.when(repo.existsById(100)).thenReturn(false);

        // throw NotFoundException when resource does not exist
        Assertions.assertThrows(NotFoundException.class, () -> service.getGenreById(100));
    }

    @Test
    @DisplayName("TEST existsByGenreName")
    void genreExists_byGenreName() throws Exception {
        // Use the mock repo to get mock a return value from existsById()
        Mockito.when(repo.existsByGenreName(genreList.get(0).getGenreName())).thenReturn(true);

        // throw NotFoundException when resource does not exist
        Assertions.assertEquals(service.existsByGenreName(genreList.get(0).getGenreName()), true);
    }

    @Test
    @DisplayName("TEST post genre")
    void saveGenre() throws Exception {
        Mockito.when(repo.save(this.mockGenre)).thenReturn(this.mockGenre);

        // Call the service
        service.saveGenre(this.mockGenre);
        Integer genreId=mockGenre.getGenreId();

        // Validate the result
        Assertions.assertEquals(1, genreId);
    }

//    @Test
//    @DisplayName("TEST post genre not successful")
//    void saveGenre_Conflict() {
//        Mockito.when(repo.save(this.mockGenre)).thenReturn(this.mockGenre);
//
//        // Call the service
//        service.saveGenre(this.mockGenre);
//        Integer genreId=mockGenre.getGenreId();
//
//        // Validate the result
//        Assertions.assertEquals(1, genreId);
//    }

    @Test
    @DisplayName("TEST post genre conflict error")
    public void saveGenre_ThrowConflictException() {
        service.saveGenre(genreList.get(0));
        // Mock repository call.
        Mockito.when(service.saveGenre(genreList.get(0))).thenThrow(new ExistingRecordException(genreList.get(0).getGenreName()+" genre already exists"));
        // Validate service call throws.
        Assertions.assertThrows(ExistingRecordException.class, () -> service.saveGenre(genreList.get(0)));
    }

    @Test
    @DisplayName("TEST update genre ")
    public void  updateGenreById(){
        Genre g=Mockito.mock(Genre.class);
        Mockito.when(repo.existsById(mockGenre.getGenreId())).thenReturn(true);
        Mockito.when(repo.getById(mockGenre.getGenreId())).thenReturn(mockGenre);

        service.updateGenre(mockGenre,mockGenre.getGenreId());


    }

    @Test
    @DisplayName("TEST update genre conflict error")
    void updateGenre_ConflictException() throws Exception {

        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.when(repo.getById(1)).thenReturn(this.mockGenre);
        Mockito.when(repo.save(this.mockGenre)).thenThrow(new ExistingRecordException(this.mockGenre.getGenreName()+" genre already exists"));

        Assertions.assertThrows(ExistingRecordException.class, () -> service.updateGenre(this.mockGenre, 1));
    }

    @Test
    @DisplayName("TEST update genre throws not found exception ")
    public void  updateGenreById_NotFound(){
        Mockito.when(repo.existsById(-1)).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> service.getGenreById(-1));


    }



    @Test
    @DisplayName("TEST delete genre")
    public void deleteMovieById() throws Exception {

        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.doNothing().when(repo).deleteById(1);
        service.deleteGenreFromMovies(1,movieRepository.findAll());

        // Call the service
        service.deleteGenre(1);
    }


    @Test
    @DisplayName("TEST delete genre not successful")
    public void deleteGenre_NotFound(){
        Mockito.when(repo.existsById(1)).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, ()->service.deleteGenre(1));


    }


}

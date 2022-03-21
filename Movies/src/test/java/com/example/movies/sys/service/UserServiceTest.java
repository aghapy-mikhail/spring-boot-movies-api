package com.example.movies.sys.service;

import com.example.movies.sys.entity.User;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.UserRepository;
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
public class UserServiceTest {

    @MockBean
    private UserRepository repo;

    @Autowired
    private UserServiceImpl service;

    private User mockUser;
    private List<User> userList;

    private static Logger logger= LoggerFactory.getLogger(UserServiceTest.class);




    @BeforeEach
    void init(){
        logger.info(()->("== Log me BEFORE each test method =="));

        this.mockUser=new User(1,"Maria", "May","may@gmail.com");

        //Creating the mock list
        List<User> list=
                Arrays.asList(
                        new User(1,"Jerry","Lee","lee_lee@gmail.com"),
                        new User(2,"Brad","Jolli","jolly_me@gmail.com"),
                        new User(3,"John","Chan","chanyy_y@gmail.com"));
        userList=list;

    }

    @Test
    @DisplayName("TEST  getUsersWithResult")
    void getUsersWithResults() throws Exception{

        Mockito.when(repo.findAll()).thenReturn(userList);

        //call the service
        List<User> returnedList= (List<User>) service.getUsers();

        //Validate the result
        Assertions.assertFalse(returnedList.isEmpty(), "");
        Assertions.assertEquals("Jerry",returnedList.get(0).getUserFirstName());
        Assertions.assertEquals("Lee",returnedList.get(0).getUserLastName());
        Assertions.assertEquals(3, returnedList.get(2).getUserId());

    }

    @Test
    @DisplayName("TEST getUserByIdFound")
    void getUserById_Found() throws Exception {
        // Use the mock repo to get mock a return value from existsById() and findById()
        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.when(repo.findById(1)).thenReturn(Optional.of(this.mockUser));

        // Call the service
        Optional<User> returnedUser = service.getUserById(1);

        // Validate the result
        Assertions.assertEquals(this.mockUser,returnedUser.get());

    }


    @Test
    @DisplayName("TEST getUSerByIdNotFound")
    void getUserById_NotFound() throws Exception {
        // Use the mock repo to get mock a return value from existsById()
        Mockito.when(repo.existsById(100)).thenReturn(false);

        // throw NotFoundException when resource does not exist
        Assertions.assertThrows(NotFoundException.class, () -> service.getUserById(100));
    }

    @Test
    @DisplayName("TEST post user")
    void saveUser() throws Exception {
        Mockito.when(repo.save(this.mockUser)).thenReturn(this.mockUser);

        // Call the service
        service.saveUser(this.mockUser);
        Integer userId=mockUser.getUserId();

        // Validate the result
        Assertions.assertEquals(1, userId);
    }

//    @Test
//    @DisplayName("TEST post user not successful")
//    void saveUser_Conflict() {
//        Mockito.when(repo.save(this.mockUser)).thenReturn(this.mockUser);
//
//        // Call the service
//        service.saveUser(this.mockUser);
//        Integer userId=mockUser.getUserId();
//
//        // Validate the result
//        Assertions.assertEquals(1, userId);
//    }

    @Test
    @DisplayName("TEST post user conflict error")
    public void saveUser_ThrowConflictException() {
        service.saveUser(userList.get(0));
        // Mock repository call.
        Mockito.when(service.saveUser(userList.get(0))).thenThrow(new ExistingRecordException(userList.get(0).getEmail()+" user has an account already"));
        // Validate service call throws.
        Assertions.assertThrows(ExistingRecordException.class, () -> service.saveUser(userList.get(0)));
    }

    @Test
    @DisplayName("TEST update user ")
    public void  updateUserById(){
        User u=Mockito.mock(User.class);
        Mockito.when(repo.existsById(mockUser.getUserId())).thenReturn(true);
        Mockito.when(repo.getById(mockUser.getUserId())).thenReturn(u);

        service.updateUser(mockUser,mockUser.getUserId());
        Mockito.verify(u).updateTo(mockUser);
        Mockito.verify(repo).save(u);

    }

    @Test
    @DisplayName("TEST update user throws not found exception ")
    public void  updateUserById_NotFound(){
        Mockito.when(repo.existsById(-1)).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> service.getUserById(-1));


    }

    @Test
    @DisplayName("TEST update user conflict error")
    void updateUser_ConflictException() throws Exception {

        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.when(repo.getById(1)).thenReturn(this.mockUser);
        Mockito.when(repo.save(this.mockUser)).thenThrow(new ExistingRecordException(this.mockUser.getEmail()+" user has an account already"));

        Assertions.assertThrows(ExistingRecordException.class, () -> service.updateUser(this.mockUser, 1));
    }



    @Test
    @DisplayName("TEST delete user")
    public void deleteUserById() throws Exception {

        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.doNothing().when(repo).deleteById(1);

        // Call the service
        service.deleteUser(1);
    }


    @Test
    @DisplayName("TEST delete user not successful")
    public void deleteUser_NotFound(){
        Mockito.when(repo.existsById(1)).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, ()->service.deleteUser(1));


    }












}

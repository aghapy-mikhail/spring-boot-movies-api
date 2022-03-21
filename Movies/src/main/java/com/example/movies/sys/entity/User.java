package com.example.movies.sys.entity;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;




@Entity
@Table(name = "users")

public class User implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @NotBlank(message = "The userFirstName field cannot be blank")
    private String userFirstName;

    @NotBlank(message = "The userLastName field cannot be blank")
    private String userLastName;

    @NotBlank(message = "The email field cannot be blank")
    private String email;







    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId", referencedColumnName ="favoriteId" )
    private Favorite favorite;



    public User(){};
    public User(int userId, String userFirstName, String userLastName, String email){
        this.userId=userId;
        this.userFirstName=userFirstName;
        this.userLastName=userLastName;
        this.email=email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void updateTo(User u){
        this.setUserFirstName(u.getUserFirstName());
        this.setUserLastName(u.getUserLastName());
        this.setEmail(u.getEmail());

    }
}

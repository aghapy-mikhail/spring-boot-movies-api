package com.example.movies.sys.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "actors")
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int actorId;

    @NotBlank(message = "The actorName field cannot be blank")
    private String actorName;

    @NotNull(message = "The dateOfBirth field cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate dateOfBirth;



    public Actor(){};
    public Actor(int actorId, String actorName, LocalDate dateOfBirth){
        this.actorId=actorId;
        this.actorName=actorName;
        this.dateOfBirth=dateOfBirth;
    }

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void updateTo(Actor a){
        this.setActorName(a.getActorName());
        this.setDateOfBirth(a.getDateOfBirth());
    }
}

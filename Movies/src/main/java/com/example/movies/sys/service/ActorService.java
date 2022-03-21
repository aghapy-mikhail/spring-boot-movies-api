package com.example.movies.sys.service;

import com.example.movies.sys.entity.Actor;

import java.util.List;
import java.util.Optional;

public interface ActorService {
    List<Actor> getActors();
    Optional<Actor> getActorById(Integer id);
    Actor saveActor(Actor actor);
    void updateActor(Actor actor, Integer id);
    void deleteActor(Integer id);

    List<Actor> findByActorName(String actorName);
}

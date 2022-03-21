package com.example.movies.sys.repository;

import com.example.movies.sys.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorRepository  extends JpaRepository<Actor, Integer> {

List<Actor> findByActorName(String actorName);

}

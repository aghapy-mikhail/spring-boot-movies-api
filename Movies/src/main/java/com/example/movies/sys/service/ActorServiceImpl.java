package com.example.movies.sys.service;

import com.example.movies.sys.entity.Actor;
import com.example.movies.sys.exception.ExistingRecordException;
import com.example.movies.sys.exception.NotFoundException;
import com.example.movies.sys.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActorServiceImpl implements ActorService {

    @Autowired
    private ActorRepository repo;

    public ActorServiceImpl(){}

    @Override
    public List<Actor> getActors(){
        return repo.findAll();
    }

    @Override
    public Optional<Actor> getActorById(Integer id){
        if (!repo.existsById(id))
            throw new NotFoundException(id);

        return repo.findById(id);
    }
    @Override
    public Actor saveActor(Actor actor){
        if (repo.findByActorName(actor.getActorName()).size()!=0)
            throw new ExistingRecordException(actor.getActorName()+" actor already exists");

        return repo.save(actor); }


    @Override
    public void updateActor(Actor actor, Integer id){
        //checking if the actor exists
        if (!repo.existsById(id))
          throw new NotFoundException(id);

        Actor current=repo.getById(id);
        current.updateTo(actor);
        if (repo.findByActorName(actor.getActorName()).size()!=0)
            throw new ExistingRecordException(actor.getActorName()+" actor already exists");

        repo.save(current);
    }

    @Override
    public void deleteActor(Integer id){
        if (!repo.existsById(id))
            throw new NotFoundException(id);
        repo.deleteById(id);
    }
    @Override
    public List<Actor> findByActorName(String actorName){
        return repo.findByActorName(actorName);
    }


}

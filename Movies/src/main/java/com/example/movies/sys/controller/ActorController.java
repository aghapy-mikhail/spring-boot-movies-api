package com.example.movies.sys.controller;

import com.example.movies.sys.entity.Actor;
import com.example.movies.sys.exception.InternalServerException;
import com.example.movies.sys.service.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class ActorController {

    @Autowired
    ActorService service;

    @GetMapping("/actors")
    public ResponseEntity<Object> getActors(@RequestParam(value = "actorName",required = false) String actorName) {

        if (actorName==null)
        return ResponseEntity.ok().body(service.getActors());
        else {
            return ResponseEntity.ok().body(service.findByActorName(actorName));
        }
    }

    @GetMapping("/actors/{actorId}")
    public ResponseEntity<Object> getActorById(
            @PathVariable("actorId") Integer actorId) {

        return ResponseEntity.ok().body(service.getActorById(actorId));
    }
    @PostMapping("/actors")
    public ResponseEntity<Object> saveActor(
            @Valid @RequestBody Actor actor) {
        try {
            Integer actorId=service.saveActor(actor).getActorId();

            return ResponseEntity
                    .created(new URI(String.format("/actors/%d", actorId)))
                    .build();
        } catch (URISyntaxException e){
            throw new InternalServerException("The URI in the Location header in POST /actors has an error.");
        }

    }

    @PatchMapping("/actors/{actorId}")
    public ResponseEntity<Object> updateActor(
            @RequestBody Actor actor,
            @PathVariable("actorId") Integer actorId) {
        service.updateActor(actor,actorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("actors/{actorId}")
    public ResponseEntity<Object> deleteActor(
            @PathVariable("actorId") Integer actorId) {
        service.deleteActor(actorId);
        return ResponseEntity.noContent().build();
    }
}

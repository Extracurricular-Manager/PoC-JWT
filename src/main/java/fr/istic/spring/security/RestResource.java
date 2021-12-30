package fr.istic.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class RestResource {

    @Autowired
    private ChildDao dao;

    @GetMapping(value = "/child/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Child> getChild(@PathVariable("name") String name) {
        return dao.get(name).map(ResponseEntity::ok).orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping(value = "/child", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postChild(@RequestBody Child child) {
        if(dao.get(child.getName()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        else {
            dao.add(child);
            return ResponseEntity.created(URI.create("/child/" + child.getName())).build();
        }
    }

    @GetMapping(value = "/children", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Child>> getChildren() {
        return ResponseEntity.ok(dao.getAll());
    }

    @DeleteMapping(value = "/child/{name}")
    public ResponseEntity<Void> deleteChild(@PathVariable("name") String name) {
        if(dao.get(name).isEmpty())
            return ResponseEntity.notFound().build();
        else {
            dao.remove(name);
            return ResponseEntity.ok().build();
        }
    }
}

package fr.istic.spring.security;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public class ChildDao {

    private List<Child> children;

    public ChildDao() {
        children = new LinkedList<>();
        children.add(new Child("Astérix", 5));
        children.add(new Child("Obélix", 5));
        children.add(new Child("Idéfix", 8));
    }

    public void add(Child child) {
        children.add(child);
    }

    public void remove(String name) {
        for(var child: List.copyOf(children))
            if(child.getName().equals(name))
                children.remove(child);
    }

    public Optional<Child> get(String name) {
        return children.stream().filter(c -> c.getName().equals(name)).findAny();
    }

    public List<Child> getAll() {
        return Collections.unmodifiableList(children);
    }

}

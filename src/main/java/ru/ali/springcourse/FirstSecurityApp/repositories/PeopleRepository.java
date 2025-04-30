package ru.ali.springcourse.FirstSecurityApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ali.springcourse.FirstSecurityApp.models.Person;

import java.util.Optional;

public interface PeopleRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByUsername(String username);
}
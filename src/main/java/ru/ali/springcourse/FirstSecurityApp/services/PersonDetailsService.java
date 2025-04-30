package ru.ali.springcourse.FirstSecurityApp.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ali.springcourse.FirstSecurityApp.models.Person;
import ru.ali.springcourse.FirstSecurityApp.repositories.PeopleRepository;
import ru.ali.springcourse.FirstSecurityApp.security.PersonDetails;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    public PersonDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = peopleRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь " + username + " не найден"));
        return new PersonDetails(person);
    }
}
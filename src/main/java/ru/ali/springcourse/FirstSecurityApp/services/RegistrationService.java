package ru.ali.springcourse.FirstSecurityApp.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ali.springcourse.FirstSecurityApp.models.Person;
import ru.ali.springcourse.FirstSecurityApp.models.Role;
import ru.ali.springcourse.FirstSecurityApp.repositories.PeopleRepository;
import ru.ali.springcourse.FirstSecurityApp.repositories.RoleRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(PeopleRepository peopleRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(Person person, Set<String> roleNames) {
        // Проверяем, нужно ли кодировать пароль
        String password = person.getPassword();
        if (password != null && !password.isEmpty() && !password.startsWith("$2a$") && !password.startsWith("$2b$")) {
            person.setPassword(passwordEncoder.encode(password));
        }
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role != null) {
                roles.add(role);
            }
        }
        person.setRoles(roles);
        peopleRepository.save(person);
    }

    public boolean usernameExists(String username) {
        return peopleRepository.findByUsername(username).isPresent();
    }
}
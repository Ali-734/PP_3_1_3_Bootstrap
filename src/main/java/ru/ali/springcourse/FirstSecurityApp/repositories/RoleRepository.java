package ru.ali.springcourse.FirstSecurityApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ali.springcourse.FirstSecurityApp.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
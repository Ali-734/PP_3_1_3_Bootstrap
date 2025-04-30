package ru.ali.springcourse.FirstSecurityApp.controllers;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ali.springcourse.FirstSecurityApp.models.Person;
import ru.ali.springcourse.FirstSecurityApp.repositories.PeopleRepository;
import ru.ali.springcourse.FirstSecurityApp.security.PersonDetails;
import ru.ali.springcourse.FirstSecurityApp.services.RegistrationService;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PeopleRepository peopleRepository;
    private final RegistrationService registrationService;

    public AdminController(PeopleRepository peopleRepository, RegistrationService registrationService) {
        this.peopleRepository = peopleRepository;
        this.registrationService = registrationService;
    }

    @GetMapping("/panel")
    public String adminPanel(Model model) {
        model.addAttribute("people", peopleRepository.findAll());
        return "admin/panel";
    }

    @GetMapping("/create")
    public String createUser(Model model) {
        model.addAttribute("person", new Person());
        return "admin/create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("person") @Valid Person person,
                             BindingResult bindingResult,
                             @RequestParam("role") String role,
                             Model model) {
        if (registrationService.usernameExists(person.getUsername())) {
            model.addAttribute("usernameError", "Имя пользователя уже занято");
            return "admin/create";
        }
        if (bindingResult.hasErrors()) {
            return "admin/create";
        }
        Set<String> roleSet = new HashSet<>();
        if (role != null && !role.isEmpty()) {
            roleSet.add(role);
        } else {
            roleSet.add("ROLE_USER");
        }
        registrationService.register(person, roleSet);
        return "redirect:/admin/panel";
    }

    @GetMapping("/edit")
    public String editUser(@RequestParam("id") int id, Model model) {
        Person person = peopleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails currentUserDetails = (PersonDetails) authentication.getPrincipal();
        Person currentUser = currentUserDetails.getPerson();
        boolean isEditingSelf = currentUser.getId() == person.getId();
        model.addAttribute("person", person);
        model.addAttribute("isEditingSelf", isEditingSelf);
        return "admin/edit";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("person") @Valid Person person,
                           BindingResult bindingResult,
                           @RequestParam(value = "role", required = false) String role) {
        if (bindingResult.hasErrors()) {
            return "admin/edit";
        }
        Person existingPerson = peopleRepository.findById(person.getId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        String newPassword = person.getPassword();
        if (newPassword == null || newPassword.trim().isEmpty()) {
            person.setPassword(existingPerson.getPassword());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails currentUserDetails = (PersonDetails) authentication.getPrincipal();
        Person currentUser = currentUserDetails.getPerson();
        Set<String> roleSet = new HashSet<>();
        if (currentUser.getId() == person.getId()) {
            roleSet.add("ROLE_ADMIN");
        } else {
            if (role != null && !role.isEmpty()) {
                roleSet.add(role);
            } else {
                roleSet.add("ROLE_USER");
            }
        }
        registrationService.register(person, roleSet);
        return "redirect:/admin/panel";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") int id) {
        peopleRepository.deleteById(id);
        return "redirect:/admin/panel";
    }
}
package ru.ali.springcourse.FirstSecurityApp.controllers;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ali.springcourse.FirstSecurityApp.models.Person;
import ru.ali.springcourse.FirstSecurityApp.services.RegistrationService;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(@ModelAttribute("person") Person person) {
        return "auth/register";
    }

    @PostMapping("/register")
    public String performRegistration(@ModelAttribute("person") @Valid Person person,
                                      BindingResult bindingResult,
                                      Model model) {
        if (registrationService.usernameExists(person.getUsername())) {
            model.addAttribute("usernameError", "Имя пользователя уже занято");
            return "auth/register";
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        registrationService.register(person, roles);
        return "redirect:/auth/login";
    }
}
package ru.kata.spring.boot_security.demo.controller;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.security.Principal;
import java.util.List;


@Controller
public class UserController {

    private final UserService userService;

    private final RoleRepository roleRepository;

    private String message;

    @Autowired
    public UserController(UserServiceImpl userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping(value = "/admin")
    public String adminPage(ModelMap model, @ModelAttribute User user) {
        model.addAttribute(userService.findAll());
        model.addAttribute("message", message);
        model.addAttribute("user", new User());
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("allRoles", roles);
        return "admin";
    }

    @GetMapping(value = "/user")
    public String userPage(ModelMap model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping("/save_user")
    public String saveUser(@ModelAttribute User user) {
        try {
            userService.save(user);
            message = "Пользователь успешно добавлен";
        } catch (ConstraintViolationException e) {
            message = "Поля ввода не могут быть пустыми";
        }

        return "redirect:/admin";
    }

    @PostMapping("/update_user")
    public String updateUser(@ModelAttribute User user) {
        try {
            userService.update(user);
            message = "Данные пользователя обновлены";
        } catch (EntityNotFoundException e) {
            message = e.getMessage();
        } catch (RuntimeException e) {
            message = "Поля ввода не могут быть пустыми";
        }

        return "redirect:/admin";
    }

    @PostMapping("/delete_user")
    public String deleteUser(@ModelAttribute User user) {
        try {
            userService.deleteById(user.getId());
            message = "Пользователь успешно удален";
        } catch (EntityNotFoundException e) {
            message = e.getMessage();
        }

        return "redirect:/admin";
    }
}
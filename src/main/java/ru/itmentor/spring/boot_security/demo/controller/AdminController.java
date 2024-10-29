package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user-list";
    }

    @GetMapping("/user/new")
    public String createUserForm(Model model) {
        User newUser = new User();
        model.addAttribute("user", newUser);
        model.addAttribute("roles", roleService.findAllRoles());
        return "user-create";
    }

    @PostMapping("/user/new")
    public String createUser(@ModelAttribute("user") User user, @RequestParam("roles") List<Long> roleIds) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Шифрование пароля
        if (roleIds != null && !roleIds.isEmpty()) {
            user.setRoles(roleService.findRolesByIds(roleIds)); // Установка ролей только если они были переданы
        }
        userService.saveUser(user);
        return "redirect:/admin"; // Перенаправление на страницу администрирования
    }

    @GetMapping("/user/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAllRoles());
        return "user-update";
    }

    @PostMapping("/user/edit")
    public String updateUser(@ModelAttribute("user") User user, @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        // Если пароль не изменен, оставляем старый
        User existingUser = userService.findById(user.getId());

        // Шифруем пароль, если он был изменен
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existingUser.getPassword());
        }

        // Обновляем роли, только если они были переданы
        if (roleIds != null && !roleIds.isEmpty()) {
            user.setRoles(roleService.findRolesByIds(roleIds));
        } else {
            user.setRoles(existingUser.getRoles()); // Оставляем существующие роли, если новые не выбраны
        }

        userService.saveUser(user);
        return "redirect:/admin"; // Перенаправление на страницу администрирования
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin"; // Перенаправление на страницу администрирования
    }
}

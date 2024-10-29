package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.repository.RoleRepository;
import ru.itmentor.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RoleService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RoleService(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Проверяем, существуют ли уже роли
        if (roleRepository.count() == 0) {
            createRolesAndUsers();
        }
    }

    private void createRolesAndUsers() {
        try {
            // Создаем роли
            Role adminRole = new Role("ROLE_ADMIN");
            Role userRole = new Role("ROLE_USER");

            // Сохраняем роли в БД
            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            // Создаем набор ролей для администратора
            Set<Role> adminRoles = new HashSet<>(List.of(adminRole, userRole));

            // Создаем администратора с зашифрованным паролем
            User admin = createUser("admin", "admin", "Admin", "Administrator", 30, adminRoles);
            userRepository.save(admin);

            // Создаем набор ролей для обычного пользователя
            Set<Role> userRoles = new HashSet<>(List.of(userRole));

            // Создаем обычного пользователя
            User user = createUser("user", "user", "User", "Simple", 25, userRoles);
            userRepository.save(user);
        } catch (Exception e) {
            // Логируем ошибку при создании ролей и пользователей
            System.err.println("Error creating roles and users: " + e.getMessage());
        }
    }

    private User createUser(String username, String password, String firstName, String lastName, int age, Set<Role> roles) {
        User user = new User(username, passwordEncoder.encode(password), roles);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        return user;
    }

    // Метод для получения всех ролей
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    // Метод для поиска ролей по их ID
    public Set<Role> findRolesByIds(List<Long> ids) {
        return new HashSet<>(roleRepository.findAllById(ids));
    }
}

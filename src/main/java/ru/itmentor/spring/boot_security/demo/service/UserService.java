package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Метод для поиска пользователя по ID
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Метод для получения всех пользователей
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Метод для сохранения или обновления пользователя
    public void saveUser(User user) {
        if (user.getId() == null) {
            // Новый пользователь: шифруем пароль
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // Обновление существующего пользователя
            User existingUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + user.getId()));

            // Шифрование пароля только если он изменен
            if (!user.getPassword().equals(existingUser.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // Оставляем старый пароль, если он не был изменен
                user.setPassword(existingUser.getPassword());
            }
        }
        userRepository.save(user);
    }

    // Метод для удаления пользователя по ID
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    // Метод для поиска пользователя по имени пользователя (username)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
}

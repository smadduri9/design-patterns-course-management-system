package edu.university.cms.web;

import edu.university.cms.application.UserResponse;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/app")
public class AppUserController {

    private final UserRepository userRepository;

    public AppUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/instructor")
    public UserResponse instructor() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.INSTRUCTOR)
                .findFirst()
                .map(UserResponse::from)
                .orElseThrow(() -> new IllegalStateException("Seeded instructor was not found"));
    }

    @GetMapping("/students")
    public List<UserResponse> students() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.STUDENT)
                .sorted(Comparator.comparing(User::getName))
                .map(UserResponse::from)
                .toList();
    }
}

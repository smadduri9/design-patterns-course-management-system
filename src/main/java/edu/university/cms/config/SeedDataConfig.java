package edu.university.cms.config;

import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository) {
        return args -> {
            userRepository.save(new User(
                    UUID.fromString("10000000-0000-0000-0000-000000000001"),
                    "Sriram Madduri",
                    UserRole.INSTRUCTOR
            ));
            userRepository.save(new User(
                    UUID.fromString("20000000-0000-0000-0000-000000000001"),
                    "Demo Student 1",
                    UserRole.STUDENT
            ));
            userRepository.save(new User(
                    UUID.fromString("20000000-0000-0000-0000-000000000002"),
                    "Demo Student 2",
                    UserRole.STUDENT
            ));
            userRepository.save(new User(
                    UUID.fromString("20000000-0000-0000-0000-000000000003"),
                    "Demo Student 3",
                    UserRole.STUDENT
            ));
            userRepository.save(new User(
                    UUID.fromString("20000000-0000-0000-0000-000000000004"),
                    "Demo Student 4",
                    UserRole.STUDENT
            ));
            userRepository.save(new User(
                    UUID.fromString("20000000-0000-0000-0000-000000000005"),
                    "Demo Student 5",
                    UserRole.STUDENT
            ));
        };
    }
}

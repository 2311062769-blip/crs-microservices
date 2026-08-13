package vn.edu.crs.authservice.config;

import vn.edu.crs.authservice.entity.User;
import vn.edu.crs.authservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // Check if admin already exists
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }

        // Create ADMIN user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        userRepository.save(admin);

        // Create STUDENT user
        User student = new User();
        student.setUsername("student1");
        student.setPassword(passwordEncoder.encode("student123"));
        student.setRole("STUDENT");
        userRepository.save(student);

        System.out.println("✓ DataSeeder: Created ADMIN and STUDENT users");
    }
}

package com.vidya.studyapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.unit.DataSize;

import com.vidya.studyapp.entity.Role;
import com.vidya.studyapp.entity.User;
import com.vidya.studyapp.repository.UserRepository;

import jakarta.servlet.MultipartConfigElement;


@SpringBootApplication
public class VidyaStudyCompanionApplication {

	public static void main(String[] args) {
		SpringApplication.run(VidyaStudyCompanionApplication.class, args);
	}
	
	@Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Set individual file size limit
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        // Set total request size limit
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));
        return factory.createMultipartConfig();
    }
	@Bean
	public CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
	    return args -> {
	        if (!userRepository.existsByUsername("admin")) {
	            User admin = User.builder()
	                    .name("Super Admin")
	                    .email("admin@example.com")
	                    .username("admin")
	                    .password(passwordEncoder.encode("admin123")) // Secure it in production
	                    .role(Role.ADMIN)
	                    .enabled(true)
	                    .build();
	            userRepository.save(admin);
	            System.out.println("✅ Admin user created");
	        }
	    };
	}


}

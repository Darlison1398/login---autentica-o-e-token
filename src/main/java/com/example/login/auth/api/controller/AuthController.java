package com.example.login.auth.api.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.login.auth.api.domain.User;
import com.example.login.auth.api.dto.LoginRequestDTO;
import com.example.login.auth.api.dto.RegisterResquestDTO;
import com.example.login.auth.api.dto.ResponseDTO;
import com.example.login.auth.api.infra.security.TokenService;
import com.example.login.auth.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    /* eu poderia usar o @Autowired em cada um. Mas como eu estou usadno o lombok
    basta eu usar a anotação */

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body) {

        User user = this.repository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("user não encontrado"));
        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));

        }

        return ResponseEntity.badRequest().build();

    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterResquestDTO body) {

        Optional<User> user = this.repository.findByEmail(body.email());

        if (user.isEmpty()) {

            User newUser = new User();

            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setName(body.name());

            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));

        }

        return ResponseEntity.badRequest().build();

    }

}

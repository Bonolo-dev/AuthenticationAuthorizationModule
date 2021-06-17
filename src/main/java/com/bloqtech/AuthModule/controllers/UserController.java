package com.bloqtech.AuthModule.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bloqtech.AuthModule.dto.LoginDto;
import com.bloqtech.AuthModule.dto.RegisterUserDto;
import com.bloqtech.AuthModule.dto.UserDto;
import com.bloqtech.AuthModule.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers () throws JsonProcessingException {
        List<UserDto> allUsers = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(allUsers));
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerUserDto) throws JsonProcessingException {
        UserDto newUser = userService.registerUser(registerUserDto);

        //Inside body: Converting the Object to JSONString
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(newUser));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("Deleted");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login (@RequestBody LoginDto loginDto) throws JsonProcessingException {

        UserDto userDetails = userService.login(loginDto);

        //Inside body: Converting the Object to JSONString
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(userDetails));

    }

}

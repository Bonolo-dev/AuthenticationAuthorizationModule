package com.bloqtech.AuthModule.services;

import com.bloqtech.AuthModule.dto.LoginDto;
import com.bloqtech.AuthModule.dto.RegisterUserDto;
import com.bloqtech.AuthModule.dto.UserDto;
import com.bloqtech.AuthModule.entity.ERole;
import com.bloqtech.AuthModule.entity.User;
import com.bloqtech.AuthModule.helpers.PsiberUserDetails;
import com.bloqtech.AuthModule.helpers.jwt.JwtUtils;
import com.bloqtech.AuthModule.repositories.RoleRepository;
import com.bloqtech.AuthModule.repositories.UserRepository;
import com.bloqtech.AuthModule.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository
            , PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager
            ,JwtUtils jwtUtils){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public UserDto login(LoginDto loginDto){

        Authentication authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(loginDto.getUserName(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);


        Object userDetailsObj = authentication.getPrincipal();
        PsiberUserDetails userDetails = (PsiberUserDetails) userDetailsObj;

        //Not used
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        String jwt = jwtUtils.generateJwtToken(authentication);

        return new UserDto(userDetails.getId(),userDetails.getUsername()
                ,userDetails.getLastName(), userDetails.getEmail()
                , userDetails.getCellphone(), jwt);

    }
    public UserDto registerUser(RegisterUserDto registerUser){

        User user = new User();
        user.setUserName(registerUser.getUserName());
        user.setLastName(registerUser.getLastName());
        user.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        user.setEmail(registerUser.getEmail());
        user.setCellphone(registerUser.getCellphone());
        user.setActive(true);

        //by default every user gets user role when registering. Fetching role from db.
        Optional<Role> defaultRole = roleRepository.findByName(ERole.ROLE_USER);
        user.setRoles(Set.of(defaultRole.get()));

        this.userRepository.save(user);

        //Find by email because it is unique
        User newUser = this.userRepository.findByEmail(registerUser.getEmail()).get();

        return new UserDto(newUser.getId(),newUser.getUserName(), newUser.getLastName(), newUser.getEmail(), newUser.getCellphone(),"");
    }
    public void deleteUser(long userId){
        userRepository.deleteById(userId);
    }
    public List<UserDto> getAllUsers(){
        return userRepository.findAll().stream().map(user->new UserDto(user.getId()
                        , user.getUserName(), user.getLastName(),user.getEmail(), user.getCellphone(),""))
                        .collect(Collectors.toList());
    }
}

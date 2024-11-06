package rs.ogisa.notesapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import rs.ogisa.notesapp.dto.CreateUserDto;
import rs.ogisa.notesapp.dto.UserDto;
import rs.ogisa.notesapp.jwt.JwtUtil;
import rs.ogisa.notesapp.models.LoginResponse;
import rs.ogisa.notesapp.models.User;
import rs.ogisa.notesapp.repositories.UserRepository;
import rs.ogisa.notesapp.services.UserService;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;


    @PostMapping("/auth/login")
    @Operation(description = "za Login korisnika")
    public ResponseEntity<?> login(@RequestBody CreateUserDto loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                    loginRequest.getPassword()));
        } catch (Exception e) {
            if(userService.createUser(loginRequest)){
                return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(userService.getUserByUsername(loginRequest.getUsername()))));
            }
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(new LoginResponse(jwtUtil.generateToken(userService.getUserByUsername(loginRequest.getUsername()))));
    }


    @PostMapping("/createUser")
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDto user) {

        if(!userService.createUser(user)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers(){
       return userService.getAllUsers();
    }

}

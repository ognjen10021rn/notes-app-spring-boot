package rs.ogisa.notesapp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ogisa.notesapp.dto.CreateUserDto;
import rs.ogisa.notesapp.dto.UserDto;
import rs.ogisa.notesapp.models.User;
import rs.ogisa.notesapp.repositories.NoteRepository;
import rs.ogisa.notesapp.repositories.UserNoteRepository;
import rs.ogisa.notesapp.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final UserNoteRepository userNoteRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean createUser(CreateUserDto user) {

        User usr = new User();
        System.out.println(user.getUsername() + " " + user.getPassword());
        User usr1 = userRepository.findByUsername(user.getUsername());

        if (usr1 != null) {
            System.out.println("USAOOOOOOOOOOOOO");
            return false;
        }
        usr.setPassword(passwordEncoder.encode(user.getPassword()));
        usr.setUsername(user.getUsername());
        userRepository.save(usr);
        return true;

    }

    public UserDto getUserByUsername(String username) {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User with the username: " + username + " not found");
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public List<User> getAllUsersWithoutId(Long id) {
        return userRepository.findAllUserByUserIdNot(id);
    }
}

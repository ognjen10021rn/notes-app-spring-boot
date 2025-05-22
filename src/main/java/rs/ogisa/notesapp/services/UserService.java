package rs.ogisa.notesapp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ogisa.notesapp.dto.CreateUserDto;
import rs.ogisa.notesapp.dto.UserDto;
import rs.ogisa.notesapp.models.User;
import rs.ogisa.notesapp.models.UserNote;
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

    public List<User>getAllUsersFromNoteUsingNoteId(Long noteId, Long userId) {
        List<UserNote> userNotes = userNoteRepository.findAllByNoteId(noteId);

        List<User> users = new ArrayList<>();
        for (UserNote userNote : userNotes) {
            User user = userRepository.findById(userNote.getUserId()).orElseThrow(() ->
                    new UsernameNotFoundException("User with the id: " + userNote.getUserId() + " not found"));
            if(!user.getUserId().equals(userId) && !userNote.getIsDeleted()){
                users.add(user);
            }
        }

        return users;
    }
    public List<User> getAllUsersThatAreNotInNoteId(Long noteId) {
        List<User> users = userRepository.findAll();
        List<User> foundUsers = new ArrayList<>();
        for(User user : users){
            UserNote userNote1 = userNoteRepository.findByUserIdAndNoteId(user.getUserId(), noteId);
            if(userNote1 == null || userNote1.getIsDeleted()){
                foundUsers.add(user);
            }
        }
        return foundUsers;
    }

    public ResponseEntity<?> removeUserFromNote(Long noteId, Long userId, Long idToRemove) {
        UserNote userNote = userNoteRepository.findByUserIdAndNoteId(userId, noteId);
        System.out.println("UserNote: " + userNote);

        if(userNote == null){
            return ResponseEntity.notFound().build();
        }

        if(!userNote.getIsUserAdmin()){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserNote userNoteToDelete = userNoteRepository.findByUserIdAndNoteId(idToRemove, noteId);
        if(userNoteToDelete == null){
            return ResponseEntity.notFound().build();
        }
        userNoteToDelete.setIsDeleted(true);
        userNoteRepository.save(userNoteToDelete);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> addUserToNote(Long noteId, Long userId, Long idToAdd) {
        UserNote userNote = userNoteRepository.findByUserIdAndNoteId(userId, noteId);
        UserNote userNoteIsAdded = userNoteRepository.findByUserIdAndNoteId(idToAdd, noteId);
        if(userNoteIsAdded != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if(userNote == null){
            return ResponseEntity.notFound().build();
        }

        if(!userNote.getIsUserAdmin()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserNote userNoteToAdd = new UserNote();
        userNoteToAdd.setNoteId(noteId);
        userNoteToAdd.setUserId(idToAdd);
        userNoteToAdd.setIsUserAdmin(false);
        userNoteToAdd.setIsDeleted(false);
        userNoteRepository.save(userNoteToAdd);
        return ResponseEntity.ok().build();
    }
}

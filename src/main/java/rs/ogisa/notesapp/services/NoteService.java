package rs.ogisa.notesapp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ogisa.notesapp.dto.CreateNoteDto;
import rs.ogisa.notesapp.dto.CreateUserDto;
import rs.ogisa.notesapp.dto.EditNoteDto;
import rs.ogisa.notesapp.dto.UserDto;
import rs.ogisa.notesapp.models.Note;
import rs.ogisa.notesapp.models.User;
import rs.ogisa.notesapp.models.UserNote;
import rs.ogisa.notesapp.repositories.NoteRepository;
import rs.ogisa.notesapp.repositories.UserNoteRepository;
import rs.ogisa.notesapp.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final UserNoteRepository userNoteRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean createNote(Long userId, CreateNoteDto createNoteDto) {

        Note note = new Note();
        note.setAdminId(userId);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        note.setIsLocked(false);
        note.setTitle(createNoteDto.getTitle());

        noteRepository.save(note);


        UserNote userNote = new UserNote();
        userNote.setNoteId(note.getNoteId());
        userNote.setUserId(userId);
        userNote.setIsUserAdmin(true);
        userNoteRepository.save(userNote);

        //TODO dodaj proveru da li je user vec dodao te korisnike u note

        List<UserNote> userNoteList = new ArrayList<>();
        for(Long usrId : createNoteDto.getUserIds()){
            UserNote usr = new UserNote();
            usr.setNoteId(note.getNoteId());
            usr.setUserId(usrId);
            usr.setIsUserAdmin(false);
            userNoteList.add(usr);
        }
        userNoteRepository.saveAll(userNoteList);

        return true;

    }

    public List<UserNote> getAllUserNote(){
        return userNoteRepository.findAll();
    }
    public List<Note> getAllNotes(){
        return noteRepository.findAll();
    }

    @Async
    public ResponseEntity<?> editNote(EditNoteDto editNoteDto){

        UserNote userNote = userNoteRepository.findByUserIdAndNoteId(editNoteDto.getUserId(), editNoteDto.getNoteId());
        if(userNote == null){
            return ResponseEntity.status(403).build();
        }

        Note note = noteRepository.findByNoteId(editNoteDto.getNoteId());
        User user = userRepository.findByUserId(editNoteDto.getUserId());
        if(note == null || user == null) {
            return ResponseEntity.notFound().build();
        }
        note.setIsLocked(true);

        note.setTitle(editNoteDto.getTitle());
        note.setContent(editNoteDto.getContent());
        note.setUpdatedAt(LocalDateTime.now());

        noteRepository.save(note);
        return ResponseEntity.ok().build();

    }

}

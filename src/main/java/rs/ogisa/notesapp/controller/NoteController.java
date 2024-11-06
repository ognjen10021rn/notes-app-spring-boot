package rs.ogisa.notesapp.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ogisa.notesapp.dto.CreateNoteDto;
import rs.ogisa.notesapp.dto.CreateUserDto;
import rs.ogisa.notesapp.models.Note;
import rs.ogisa.notesapp.models.UserNote;
import rs.ogisa.notesapp.services.NoteService;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/api/v1/note")
public class NoteController {

    private NoteService noteService;

    @PostMapping("/createNote/{userId}")
    public ResponseEntity<Void> createNote(@PathVariable Long userId, @RequestBody CreateNoteDto createNoteDto) {

        if(!noteService.createNote(userId, createNoteDto)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
    @GetMapping("/getAllUserNote")
    public List<UserNote> getAllUserNote() {
        return noteService.getAllUserNote();
    }
    @GetMapping("/getAllNote")
    public List<Note> getAllNotes() {
        return noteService.getAllNotes();
    }
}

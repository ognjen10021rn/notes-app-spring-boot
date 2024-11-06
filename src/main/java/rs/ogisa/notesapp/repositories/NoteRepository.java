package rs.ogisa.notesapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ogisa.notesapp.models.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    public Note findByTitle(String title);
    public Note findByAdminId(Long adminId);
    public Note findByNoteId(Long noteId);
    public Note findByTitleAndAdminId(String title, Long adminId);
}

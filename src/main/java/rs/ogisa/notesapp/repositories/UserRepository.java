package rs.ogisa.notesapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ogisa.notesapp.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUsername(String username);
    public User findByUsernameAndPassword(String username, String password);
    public User findByUserId(Long userId);


}

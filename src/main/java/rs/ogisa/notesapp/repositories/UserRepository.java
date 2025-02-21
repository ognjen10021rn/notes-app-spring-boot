package rs.ogisa.notesapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ogisa.notesapp.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUsername(String username);
    public User findByUsernameAndPassword(String username, String password);
    public Optional<User> findByUserId(Long userId);

    List<User> findAllUserByUserIdNot(Long userId);


}

package pl.lukbol.dyplom.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lukbol.dyplom.classes.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);


    User deleteUserById(Long id);

    Optional<User> findOptionalByEmail(String email);

    Boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    List<User> findAll();

    List<User> findByNameContainingIgnoreCase(String name);


    List<User> findByEmailContainingIgnoreCase(String email);

    List<User> findByRoles_NameContainingIgnoreCase(String role);


    User findByName(String name);

    List<User> findAllByNameNot(String employeeNameOnOrder);

    List<User> findUsersByRoles_NameIn(String... roleNames);

    List<User> findByIdIn(List<Long> ids);
}
package vn.java.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.java.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u FROM User u WHERE u.status = 'ACTIVE' " +
            "AND (lower(u.firstName) LIKE :keyword OR " +
            "lower(u.lastName) LIKE :keyword OR " +
            "lower(u.username) LIKE :keyword OR " +
            "lower(u.email) LIKE :keyword OR " +
            "lower(u.phone) LIKE :keyword)")
    Page<User> searchByKeyword(String keyword, Pageable pageable);

    User findByEmail(String email);

    User findByUsername(String username);
}

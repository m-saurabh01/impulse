package com.wipro.iaf.email.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wipro.iaf.email.user.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByUserIdOrderByDisplayNameAsc(Long userId);

    List<Contact> findByUserIdAndFavoriteTrue(Long userId);

    Optional<Contact> findByIdAndUserId(Long id, Long userId);

    Optional<Contact> findByUserIdAndEmailIgnoreCase(Long userId, String email);

    boolean existsByUserIdAndEmailIgnoreCase(Long userId, String email);

    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId " +
           "AND (LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "     OR LOWER(c.displayName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY c.favorite DESC, c.displayName ASC")
    List<Contact> searchContacts(@Param("userId") Long userId, @Param("query") String query);

    void deleteByIdAndUserId(Long id, Long userId);
}

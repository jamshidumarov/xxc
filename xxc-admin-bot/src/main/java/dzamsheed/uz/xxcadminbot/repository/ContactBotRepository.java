package dzamsheed.uz.xxcadminbot.repository;

import dzamsheed.uz.xxcadminbot.domain.entity.Contact;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ContactBotRepository extends JpaRepository<Contact, UUID> {

    List<Contact> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    long countByStatus(String status);

    @Modifying
    @Query("UPDATE Contact c SET c.status = :status WHERE c.id = :id")
    void updateStatus(UUID id, String status);

    @Query("SELECT c FROM Contact c WHERE c.status = 'NEW' ORDER BY c.createdAt DESC")
    List<Contact> findNewContacts(Pageable pageable);
}

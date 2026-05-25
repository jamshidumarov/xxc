package dzamsheed.uz.xxcback.repository;

import dzamsheed.uz.xxcback.domain.entity.Contact;
import dzamsheed.uz.xxcback.domain.enums.ContactStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {

    Page<Contact> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Contact> findByStatusOrderByCreatedAtDesc(ContactStatus status, Pageable pageable);

    long countByStatus(ContactStatus status);

    long countByTelegramNotifiedFalse();
}

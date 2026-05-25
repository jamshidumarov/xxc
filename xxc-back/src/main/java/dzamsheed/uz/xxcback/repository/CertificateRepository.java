package dzamsheed.uz.xxcback.repository;

import dzamsheed.uz.xxcback.domain.entity.Certificate;
import dzamsheed.uz.xxcback.domain.enums.CertificateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

    List<Certificate> findByIsActiveTrueOrderBySortOrderAscCreatedAtDesc();

    List<Certificate> findByTypeAndIsActiveTrueOrderBySortOrderAsc(CertificateType type);

    long countByIsActiveTrue();
}

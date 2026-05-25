package dzamsheed.uz.xxcback.repository;

import dzamsheed.uz.xxcback.domain.entity.Product;
import dzamsheed.uz.xxcback.domain.enums.Availability;
import dzamsheed.uz.xxcback.domain.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByCategoryAndIsActiveTrue(Category category, Pageable pageable);

    Page<Product> findByAvailabilityAndIsActiveTrue(Availability availability, Pageable pageable);

    Page<Product> findByCategoryAndAvailabilityAndIsActiveTrue(Category category, Availability availability, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true")
    long countActive();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true AND p.createdAt >= :since")
    long countAddedSince(@Param("since") Instant since);
}

package dzamsheed.uz.xxcadminbot.repository;

import dzamsheed.uz.xxcadminbot.domain.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductBotRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);

    long countByIsActiveTrue();

    @Modifying
    @Query("UPDATE Product p SET p.nameUz = :nameUz WHERE p.id = :id")
    void updateNameUz(@Param("id") UUID id, @Param("nameUz") String nameUz);

    @Modifying
    @Query("UPDATE Product p SET p.price = :price WHERE p.id = :id")
    void updatePrice(@Param("id") UUID id, @Param("price") Integer price);

    @Modifying
    @Query("UPDATE Product p SET p.category = :category WHERE p.id = :id")
    void updateCategory(@Param("id") UUID id, @Param("category") String category);

    @Modifying
    @Query("UPDATE Product p SET p.availability = :availability WHERE p.id = :id")
    void updateAvailability(@Param("id") UUID id, @Param("availability") String availability);

    @Modifying
    @Query("UPDATE Product p SET p.isActive = :active WHERE p.id = :id")
    void updateActive(@Param("id") UUID id, @Param("active") Boolean active);
}

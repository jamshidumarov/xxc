package dzamsheed.uz.xxcadminbot.repository;

import dzamsheed.uz.xxcadminbot.domain.entity.PortfolioItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PortfolioBotRepository extends JpaRepository<PortfolioItem, UUID> {

    List<PortfolioItem> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Modifying
    @Query("UPDATE PortfolioItem p SET p.titleUz = :title WHERE p.id = :id")
    void updateTitle(@Param("id") UUID id, @Param("title") String title);

    @Modifying
    @Query("UPDATE PortfolioItem p SET p.category = :category WHERE p.id = :id")
    void updateCategory(@Param("id") UUID id, @Param("category") String category);

    @Modifying
    @Query("UPDATE PortfolioItem p SET p.year = :year WHERE p.id = :id")
    void updateYear(@Param("id") UUID id, @Param("year") Integer year);

    @Modifying
    @Query("UPDATE PortfolioItem p SET p.isFeatured = :featured WHERE p.id = :id")
    void updateFeatured(@Param("id") UUID id, @Param("featured") Boolean featured);

    @Modifying
    @Query("UPDATE PortfolioItem p SET p.isActive = :active WHERE p.id = :id")
    void updateActive(@Param("id") UUID id, @Param("active") Boolean active);
}

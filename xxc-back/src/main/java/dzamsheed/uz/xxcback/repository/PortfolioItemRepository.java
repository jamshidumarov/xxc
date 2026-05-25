package dzamsheed.uz.xxcback.repository;

import dzamsheed.uz.xxcback.domain.entity.PortfolioItem;
import dzamsheed.uz.xxcback.domain.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, UUID> {

    Page<PortfolioItem> findByIsActiveTrue(Pageable pageable);

    Page<PortfolioItem> findByCategoryAndIsActiveTrue(Category category, Pageable pageable);

    List<PortfolioItem> findByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc();
}

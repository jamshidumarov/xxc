package dzamsheed.uz.xxcadminbot.repository;

import dzamsheed.uz.xxcadminbot.domain.entity.PortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PortfolioImageBotRepository extends JpaRepository<PortfolioImage, UUID> {

    @Query("SELECT COUNT(i) FROM PortfolioImage i WHERE i.portfolioId = :portfolioId")
    long countByPortfolioId(@Param("portfolioId") UUID portfolioId);
}

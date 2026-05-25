package dzamsheed.uz.xxcadminbot.repository;

import dzamsheed.uz.xxcadminbot.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductImageBotRepository extends JpaRepository<ProductImage, UUID> {

    @Query("SELECT COUNT(i) FROM ProductImage i WHERE i.productId = :productId")
    long countByProductId(@Param("productId") UUID productId);
}

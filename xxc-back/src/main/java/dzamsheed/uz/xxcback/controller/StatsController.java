package dzamsheed.uz.xxcback.controller;

import dzamsheed.uz.xxcback.domain.enums.ContactStatus;
import dzamsheed.uz.xxcback.repository.CertificateRepository;
import dzamsheed.uz.xxcback.repository.ContactRepository;
import dzamsheed.uz.xxcback.repository.PortfolioItemRepository;
import dzamsheed.uz.xxcback.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final ProductRepository productRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final ContactRepository contactRepository;
    private final CertificateRepository certificateRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
                "totalProducts", productRepository.countActive(),
                "totalPortfolio", portfolioItemRepository.count(),
                "totalContacts", contactRepository.count(),
                "newContacts", contactRepository.countByStatus(ContactStatus.NEW),
                "completedContacts", contactRepository.countByStatus(ContactStatus.COMPLETED),
                "totalCertificates", certificateRepository.countByIsActiveTrue()
        ));
    }
}

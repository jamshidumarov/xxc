package dzamsheed.uz.xxcback.controller;

import dzamsheed.uz.xxcback.domain.enums.Category;
import dzamsheed.uz.xxcback.dto.request.PortfolioRequest;
import dzamsheed.uz.xxcback.dto.response.PageResponse;
import dzamsheed.uz.xxcback.dto.response.PortfolioResponse;
import dzamsheed.uz.xxcback.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<PageResponse<PortfolioResponse>> getAll(
            @RequestParam(required = false) Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(portfolioService.findAll(category, page, size));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<PortfolioResponse>> getFeatured() {
        return ResponseEntity.ok(portfolioService.findFeatured());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(portfolioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PortfolioResponse> create(@Valid @RequestBody PortfolioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioResponse> update(@PathVariable UUID id,
                                                     @Valid @RequestBody PortfolioRequest request) {
        return ResponseEntity.ok(portfolioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        portfolioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PortfolioResponse> addImages(@PathVariable UUID id,
                                                        @RequestPart List<MultipartFile> files) {
        return ResponseEntity.ok(portfolioService.addImages(id, files));
    }
}

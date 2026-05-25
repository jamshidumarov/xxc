package dzamsheed.uz.xxcback.controller;

import dzamsheed.uz.xxcback.domain.enums.Availability;
import dzamsheed.uz.xxcback.domain.enums.Category;
import dzamsheed.uz.xxcback.dto.request.ProductRequest;
import dzamsheed.uz.xxcback.dto.response.PageResponse;
import dzamsheed.uz.xxcback.dto.response.ProductResponse;
import dzamsheed.uz.xxcback.service.ProductService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAll(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Availability available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "created_desc") String sort
    ) {
        return ResponseEntity.ok(productService.findAll(category, available, page, size, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> addImages(@PathVariable UUID id,
                                                      @RequestPart List<MultipartFile> files) {
        return ResponseEntity.ok(productService.addImages(id, files));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID id, @PathVariable UUID imageId) {
        productService.deleteImage(id, imageId);
        return ResponseEntity.noContent().build();
    }
}

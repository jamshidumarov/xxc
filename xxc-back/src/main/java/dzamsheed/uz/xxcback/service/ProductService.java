package dzamsheed.uz.xxcback.service;

import dzamsheed.uz.xxcback.domain.entity.Product;
import dzamsheed.uz.xxcback.domain.entity.ProductImage;
import dzamsheed.uz.xxcback.domain.enums.Availability;
import dzamsheed.uz.xxcback.domain.enums.Category;
import dzamsheed.uz.xxcback.dto.request.ProductRequest;
import dzamsheed.uz.xxcback.dto.response.PageResponse;
import dzamsheed.uz.xxcback.dto.response.ProductResponse;
import dzamsheed.uz.xxcback.exception.NotFoundException;
import dzamsheed.uz.xxcback.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    public PageResponse<ProductResponse> findAll(Category category, Availability availability,
                                                 int page, int size, String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        Page<Product> result;
        if (category != null && availability != null) {
            result = productRepository.findByCategoryAndAvailabilityAndIsActiveTrue(category, availability, pageable);
        } else if (category != null) {
            result = productRepository.findByCategoryAndIsActiveTrue(category, pageable);
        } else if (availability != null) {
            result = productRepository.findByAvailabilityAndIsActiveTrue(availability, pageable);
        } else {
            result = productRepository.findByIsActiveTrue(pageable);
        }
        return PageResponse.from(result.map(ProductResponse::from));
    }

    public ProductResponse findById(UUID id) {
        return ProductResponse.from(getOrThrow(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .nameUz(request.nameUz())
                .nameRu(request.nameRu())
                .nameEn(request.nameEn())
                .descriptionUz(request.descriptionUz())
                .descriptionRu(request.descriptionRu())
                .descriptionEn(request.descriptionEn())
                .category(request.category())
                .price(request.price())
                .availability(request.availability() != null ? request.availability() : Availability.AVAILABLE)
                .isCustomOrder(request.isCustomOrder() != null ? request.isCustomOrder() : false)
                .dimensions(request.dimensions())
                .weight(request.weight())
                .material(request.material())
                .build();
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = getOrThrow(id);
        product.setNameUz(request.nameUz());
        product.setNameRu(request.nameRu());
        product.setNameEn(request.nameEn());
        product.setDescriptionUz(request.descriptionUz());
        product.setDescriptionRu(request.descriptionRu());
        product.setDescriptionEn(request.descriptionEn());
        product.setCategory(request.category());
        product.setPrice(request.price());
        if (request.availability() != null) product.setAvailability(request.availability());
        if (request.isCustomOrder() != null) product.setIsCustomOrder(request.isCustomOrder());
        product.setDimensions(request.dimensions());
        product.setWeight(request.weight());
        product.setMaterial(request.material());
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void delete(UUID id) {
        Product product = getOrThrow(id);
        product.getImages().forEach(img -> cloudinaryService.delete(img.getCloudinaryId()));
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse addImages(UUID id, List<MultipartFile> files) {
        Product product = getOrThrow(id);
        boolean hasImages = !product.getImages().isEmpty();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            CloudinaryService.UploadResult result = cloudinaryService.upload(file, "products");
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .url(result.url())
                    .cloudinaryId(result.publicId())
                    .isPrimary(!hasImages && i == 0)
                    .sortOrder(product.getImages().size() + i)
                    .build();
            product.getImages().add(image);
        }
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void deleteImage(UUID productId, UUID imageId) {
        Product product = getOrThrow(productId);
        product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .ifPresent(img -> {
                    cloudinaryService.delete(img.getCloudinaryId());
                    product.getImages().remove(img);
                });
        productRepository.save(product);
    }

    private Product getOrThrow(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    private Pageable buildPageable(int page, int size, String sort) {
        Sort sortOrder = switch (sort != null ? sort : "") {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            default -> Sort.by("createdAt").descending();
        };
        return PageRequest.of(page, size, sortOrder);
    }
}

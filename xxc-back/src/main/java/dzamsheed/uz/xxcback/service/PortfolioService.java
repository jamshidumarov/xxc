package dzamsheed.uz.xxcback.service;

import dzamsheed.uz.xxcback.domain.entity.PortfolioImage;
import dzamsheed.uz.xxcback.domain.entity.PortfolioItem;
import dzamsheed.uz.xxcback.domain.enums.Category;
import dzamsheed.uz.xxcback.dto.request.PortfolioRequest;
import dzamsheed.uz.xxcback.dto.response.PageResponse;
import dzamsheed.uz.xxcback.dto.response.PortfolioResponse;
import dzamsheed.uz.xxcback.exception.NotFoundException;
import dzamsheed.uz.xxcback.repository.PortfolioItemRepository;
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
public class PortfolioService {

    private final PortfolioItemRepository portfolioItemRepository;
    private final CloudinaryService cloudinaryService;

    public PageResponse<PortfolioResponse> findAll(Category category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PortfolioItem> result = category != null
                ? portfolioItemRepository.findByCategoryAndIsActiveTrue(category, pageable)
                : portfolioItemRepository.findByIsActiveTrue(pageable);
        return PageResponse.from(result.map(PortfolioResponse::from));
    }

    public List<PortfolioResponse> findFeatured() {
        return portfolioItemRepository.findByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc()
                .stream().map(PortfolioResponse::from).toList();
    }

    public PortfolioResponse findById(UUID id) {
        return PortfolioResponse.from(getOrThrow(id));
    }

    @Transactional
    public PortfolioResponse create(PortfolioRequest request) {
        PortfolioItem item = PortfolioItem.builder()
                .titleUz(request.titleUz())
                .titleRu(request.titleRu())
                .titleEn(request.titleEn())
                .category(request.category())
                .descriptionUz(request.descriptionUz())
                .descriptionRu(request.descriptionRu())
                .descriptionEn(request.descriptionEn())
                .year(request.year())
                .isFeatured(request.isFeatured() != null ? request.isFeatured() : false)
                .build();
        return PortfolioResponse.from(portfolioItemRepository.save(item));
    }

    @Transactional
    public PortfolioResponse update(UUID id, PortfolioRequest request) {
        PortfolioItem item = getOrThrow(id);
        item.setTitleUz(request.titleUz());
        item.setTitleRu(request.titleRu());
        item.setTitleEn(request.titleEn());
        item.setCategory(request.category());
        item.setDescriptionUz(request.descriptionUz());
        item.setDescriptionRu(request.descriptionRu());
        item.setDescriptionEn(request.descriptionEn());
        item.setYear(request.year());
        if (request.isFeatured() != null) item.setIsFeatured(request.isFeatured());
        return PortfolioResponse.from(portfolioItemRepository.save(item));
    }

    @Transactional
    public void delete(UUID id) {
        PortfolioItem item = getOrThrow(id);
        item.getImages().forEach(img -> cloudinaryService.delete(img.getCloudinaryId()));
        portfolioItemRepository.delete(item);
    }

    @Transactional
    public PortfolioResponse addImages(UUID id, List<MultipartFile> files) {
        PortfolioItem item = getOrThrow(id);
        boolean hasImages = !item.getImages().isEmpty();
        for (int i = 0; i < files.size(); i++) {
            CloudinaryService.UploadResult result = cloudinaryService.upload(files.get(i), "portfolio");
            PortfolioImage image = PortfolioImage.builder()
                    .portfolioItem(item)
                    .url(result.url())
                    .cloudinaryId(result.publicId())
                    .isPrimary(!hasImages && i == 0)
                    .sortOrder(item.getImages().size() + i)
                    .build();
            item.getImages().add(image);
        }
        return PortfolioResponse.from(portfolioItemRepository.save(item));
    }

    private PortfolioItem getOrThrow(UUID id) {
        return portfolioItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Portfolio item not found: " + id));
    }
}

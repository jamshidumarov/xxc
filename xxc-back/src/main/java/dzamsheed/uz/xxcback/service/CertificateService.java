package dzamsheed.uz.xxcback.service;

import dzamsheed.uz.xxcback.domain.entity.Certificate;
import dzamsheed.uz.xxcback.domain.enums.CertificateType;
import dzamsheed.uz.xxcback.dto.request.CertificateRequest;
import dzamsheed.uz.xxcback.dto.response.CertificateResponse;
import dzamsheed.uz.xxcback.exception.NotFoundException;
import dzamsheed.uz.xxcback.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CloudinaryService cloudinaryService;

    public List<CertificateResponse> findAll() {
        return certificateRepository.findByIsActiveTrueOrderBySortOrderAscCreatedAtDesc()
                .stream().map(CertificateResponse::from).toList();
    }

    public List<CertificateResponse> findByType(CertificateType type) {
        return certificateRepository.findByTypeAndIsActiveTrueOrderBySortOrderAsc(type)
                .stream().map(CertificateResponse::from).toList();
    }

    @Transactional
    public CertificateResponse create(CertificateRequest request, MultipartFile image) {
        Certificate cert = Certificate.builder()
                .titleUz(request.titleUz())
                .titleRu(request.titleRu())
                .titleEn(request.titleEn())
                .descriptionUz(request.descriptionUz())
                .descriptionRu(request.descriptionRu())
                .descriptionEn(request.descriptionEn())
                .year(request.year())
                .issuedBy(request.issuedBy())
                .type(request.type() != null ? request.type() : CertificateType.CERTIFICATE)
                .sortOrder(request.sortOrder() != null ? request.sortOrder() : 0)
                .build();

        if (image != null && !image.isEmpty()) {
            CloudinaryService.UploadResult result = cloudinaryService.upload(image, "certificates");
            cert.setImageUrl(result.url());
            cert.setCloudinaryId(result.publicId());
        }

        return CertificateResponse.from(certificateRepository.save(cert));
    }

    @Transactional
    public CertificateResponse update(UUID id, CertificateRequest request, MultipartFile image) {
        Certificate cert = getOrThrow(id);
        cert.setTitleUz(request.titleUz());
        cert.setTitleRu(request.titleRu());
        cert.setTitleEn(request.titleEn());
        cert.setDescriptionUz(request.descriptionUz());
        cert.setDescriptionRu(request.descriptionRu());
        cert.setDescriptionEn(request.descriptionEn());
        cert.setYear(request.year());
        cert.setIssuedBy(request.issuedBy());
        if (request.type() != null) cert.setType(request.type());
        if (request.sortOrder() != null) cert.setSortOrder(request.sortOrder());

        if (image != null && !image.isEmpty()) {
            cloudinaryService.delete(cert.getCloudinaryId());
            CloudinaryService.UploadResult result = cloudinaryService.upload(image, "certificates");
            cert.setImageUrl(result.url());
            cert.setCloudinaryId(result.publicId());
        }

        return CertificateResponse.from(certificateRepository.save(cert));
    }

    @Transactional
    public void delete(UUID id) {
        Certificate cert = getOrThrow(id);
        cloudinaryService.delete(cert.getCloudinaryId());
        certificateRepository.delete(cert);
    }

    private Certificate getOrThrow(UUID id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Certificate not found: " + id));
    }
}

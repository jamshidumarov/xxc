package dzamsheed.uz.xxcback.controller;

import dzamsheed.uz.xxcback.domain.enums.CertificateType;
import dzamsheed.uz.xxcback.dto.request.CertificateRequest;
import dzamsheed.uz.xxcback.dto.response.CertificateResponse;
import dzamsheed.uz.xxcback.service.CertificateService;
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
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping
    public ResponseEntity<List<CertificateResponse>> getAll(
            @RequestParam(required = false) CertificateType type
    ) {
        if (type != null) {
            return ResponseEntity.ok(certificateService.findByType(type));
        }
        return ResponseEntity.ok(certificateService.findAll());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CertificateResponse> create(
            @Valid @RequestPart("data") CertificateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.create(request, image));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CertificateResponse> update(
            @PathVariable UUID id,
            @Valid @RequestPart("data") CertificateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(certificateService.update(id, request, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        certificateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package dzamsheed.uz.xxcback.controller;

import dzamsheed.uz.xxcback.domain.enums.ContactStatus;
import dzamsheed.uz.xxcback.dto.request.ContactRequest;
import dzamsheed.uz.xxcback.dto.request.ContactStatusRequest;
import dzamsheed.uz.xxcback.dto.response.ContactResponse;
import dzamsheed.uz.xxcback.dto.response.PageResponse;
import dzamsheed.uz.xxcback.service.ContactService;
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
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ContactResponse> submit(
            @Valid @RequestPart("data") ContactRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.submit(request, files));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ContactResponse>> getAll(
            @RequestParam(required = false) ContactStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(contactService.findAll(status, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(contactService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ContactResponse> updateStatus(@PathVariable UUID id,
                                                         @Valid @RequestBody ContactStatusRequest request) {
        return ResponseEntity.ok(contactService.updateStatus(id, request));
    }
}

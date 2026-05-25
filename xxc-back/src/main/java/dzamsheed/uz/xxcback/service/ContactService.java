package dzamsheed.uz.xxcback.service;

import dzamsheed.uz.xxcback.domain.entity.Contact;
import dzamsheed.uz.xxcback.domain.entity.ContactAttachment;
import dzamsheed.uz.xxcback.domain.enums.ContactStatus;
import dzamsheed.uz.xxcback.dto.request.ContactRequest;
import dzamsheed.uz.xxcback.dto.request.ContactStatusRequest;
import dzamsheed.uz.xxcback.dto.response.ContactResponse;
import dzamsheed.uz.xxcback.dto.response.PageResponse;
import dzamsheed.uz.xxcback.exception.NotFoundException;
import dzamsheed.uz.xxcback.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {

    private final ContactRepository contactRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public ContactResponse submit(ContactRequest request, List<MultipartFile> files) {
        Contact contact = Contact.builder()
                .name(request.name())
                .phone(request.phone())
                .email(request.email())
                .type(request.type())
                .message(request.message())
                .build();

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    CloudinaryService.UploadResult result = cloudinaryService.upload(file, "contacts");
                    ContactAttachment attachment = ContactAttachment.builder()
                            .contact(contact)
                            .url(result.url())
                            .cloudinaryId(result.publicId())
                            .build();
                    contact.getAttachments().add(attachment);
                }
            }
        }

        return ContactResponse.from(contactRepository.save(contact));
    }

    public PageResponse<ContactResponse> findAll(ContactStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Contact> result = status != null
                ? contactRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                : contactRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PageResponse.from(result.map(ContactResponse::from));
    }

    public ContactResponse findById(UUID id) {
        return ContactResponse.from(getOrThrow(id));
    }

    @Transactional
    public ContactResponse updateStatus(UUID id, ContactStatusRequest request) {
        Contact contact = getOrThrow(id);
        contact.setStatus(request.status());
        return ContactResponse.from(contactRepository.save(contact));
    }

    public long countNew() {
        return contactRepository.countByStatus(ContactStatus.NEW);
    }

    private Contact getOrThrow(UUID id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contact not found: " + id));
    }
}

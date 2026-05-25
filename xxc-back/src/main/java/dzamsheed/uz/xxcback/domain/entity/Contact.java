package dzamsheed.uz.xxcback.domain.entity;

import dzamsheed.uz.xxcback.domain.enums.ContactStatus;
import dzamsheed.uz.xxcback.domain.enums.ContactType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 200)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ContactType type;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ContactStatus status = ContactStatus.NEW;

    @Column(name = "telegram_notified", nullable = false)
    @Builder.Default
    private Boolean telegramNotified = false;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ContactAttachment> attachments = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

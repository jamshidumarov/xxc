package dzamsheed.uz.xxcadminbot.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contacts")
@Getter
public class Contact {

    @Id
    private UUID id;

    private String name;
    private String phone;
    private String email;

    @Column(name = "type")
    private String type;

    private String message;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;
}

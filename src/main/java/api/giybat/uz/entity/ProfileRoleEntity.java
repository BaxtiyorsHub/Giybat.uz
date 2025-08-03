package api.giybat.uz.entity;

import api.giybat.uz.enums.ProfileRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "profile_role")
@Entity
@Setter
@Getter
public class ProfileRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "profile_id")
    private String profileId;
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    @ManyToOne
    private ProfileEntity profile;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private ProfileRole role;

    @Column(name = "visible")
    private Boolean visible = true;

    @Column(name = "created_date")
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdDate;
}

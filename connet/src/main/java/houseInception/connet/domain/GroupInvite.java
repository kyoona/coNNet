package houseInception.connet.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GroupInvite extends BaseTime{

    @Column(name = "groupInviteId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String groupUuid;

    @JoinColumn(name = "inviter_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User inviter;

    @JoinColumn(name = "invitee_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User invitee;
}

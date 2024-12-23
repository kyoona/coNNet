package houseInception.connet.domain;

import houseInception.connet.domain.group.GroupUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class groupChat extends BaseTime{

    @Column(name = "groupChatId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "writerId")
    @ManyToOne(fetch = FetchType.LAZY)
    private GroupUser writer;

    @Enumerated(EnumType.STRING)
    private ChatterRole writerRole;

    private Long tapId;
    private String message;

    @Enumerated(EnumType.STRING)
    private ChatterRole chatTarget;

    @Enumerated(EnumType.STRING)
    private Status status;
}

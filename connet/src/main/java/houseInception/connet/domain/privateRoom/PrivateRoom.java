package houseInception.connet.domain.privateRoom;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static houseInception.connet.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
public class PrivateRoom extends BaseTime {

    @Column(name = "privateRoomId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status = ALIVE;
}

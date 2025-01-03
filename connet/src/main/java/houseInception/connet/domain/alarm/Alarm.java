package houseInception.connet.domain.alarm;

import houseInception.connet.domain.BaseTime;
import houseInception.connet.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@DiscriminatorColumn(name = "dtype")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
public abstract class Alarm extends BaseTime {

    @Column(name = "alarmId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private boolean isChecked;

    public Alarm(AlarmType alarmType, User user) {
        this.alarmType = alarmType;
        this.user = user;
        this.isChecked = false;
    }

    public void check(){
        this.isChecked = true;
    }
}

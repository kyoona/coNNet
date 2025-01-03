package houseInception.connet.domain.alarm;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn(name = "dtype")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
public abstract class Alarm {

    @Column(name = "alarmId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private boolean isChecked;

    public Alarm(AlarmType alarmType) {
        this.alarmType = alarmType;
        this.isChecked = false;
    }
}

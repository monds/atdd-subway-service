package nextstep.subway.station.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;

    public Station(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedDate() {
        return super.createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return super.modifiedDate;
    }
}

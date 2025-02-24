package com.beyond.StomachForce.report.domain;

import com.beyond.StomachForce.Common.domain.BaseReservationTimeEntity;
import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.report.domain.select.ReportClass;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
@Setter
public class Report extends BaseReservationTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false)
    private Long reported;

    @Enumerated(EnumType.STRING)
    private ReportClass reportClass;

    @Column(nullable = false)
    private String contents;

    @OneToMany(mappedBy = "report",cascade = CascadeType.ALL)
    @Builder.Default
    private List<ReportPhoto> reportPhotos = new ArrayList<>();


}

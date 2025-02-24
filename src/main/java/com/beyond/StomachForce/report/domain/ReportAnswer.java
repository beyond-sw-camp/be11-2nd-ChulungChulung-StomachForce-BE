package com.beyond.StomachForce.report.domain;

import com.beyond.StomachForce.Common.domain.BaseReservationTimeEntity;
import com.beyond.StomachForce.serviceCenter.domain.ServicePost;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
@Setter
public class ReportAnswer extends BaseReservationTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "report_id")
    private Report report;

    @Column(nullable = false)
    private String contents;
}

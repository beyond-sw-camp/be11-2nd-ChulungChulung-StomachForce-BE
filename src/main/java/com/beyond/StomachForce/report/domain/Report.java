package com.beyond.StomachForce.report.domain;

import com.beyond.StomachForce.User.domain.User;
import com.beyond.StomachForce.report.domain.select.ReportClass;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    @Enumerated(EnumType.STRING)
    private ReportClass reportClass;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String photo;

    @Column(nullable = false)
    private String adminComment;

}

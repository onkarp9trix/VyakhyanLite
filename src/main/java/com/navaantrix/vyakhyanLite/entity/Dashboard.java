package com.navaantrix.vyakhyanLite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@Table(name = "dashboard")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dashboard_id")
    private Long dashboardId;


    @Column(name = "dashboard_name")
    private String dashboardName;

    @Column(name = "save_at")
    private Instant saveAt;

    @Column(name = "dashboard_file_name")
    private String dashboardFileName;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

}

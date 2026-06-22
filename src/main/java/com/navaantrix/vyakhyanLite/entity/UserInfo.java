package com.navaantrix.vyakhyanLite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "user_info")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_info_id")
    private Long UserInfoId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "profile_name")
    private String profileName;

    @Column(name = "profile_path")
    private String profilePath;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;
}

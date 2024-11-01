package com.example.qms.workspace;

import com.example.qms.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "workspaces")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(nullable = false)
    private String title;

    private String businessName;

    private String businessIndustry;

    private String contactEmail;

    private String contactPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

}

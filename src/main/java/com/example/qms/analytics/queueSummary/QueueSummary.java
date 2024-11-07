package com.example.qms.analytics.queueSummary;

import com.example.qms.queue.Queue;
import com.example.qms.user.User;
import com.example.qms.workspace.Workspace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
@Entity
@Table(name = "queue_summary")
@AllArgsConstructor

public class QueueSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "visitors_count", nullable = false)
    private int visitorsCount;

    @Column(name = "average_wait_time", nullable = false)
    private double averageWaitTime;

    @Column(name = "average_serve_time", nullable = false)
    private double averageServeTime;


    @Column(name = "total_served")
    private double totalServed;

    @Override
    public String toString() {
        return "DailyQueueSummary{" +
                "id=" + id +
                ", date=" + date +
                ", visitorsCount=" + visitorsCount +
                ", averageWaitTime=" + averageWaitTime +
                ", averageServeTime=" + averageServeTime +
                '}';
    }
}


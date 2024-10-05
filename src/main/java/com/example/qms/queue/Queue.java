package com.example.qms.queue;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID secret;

    @Column(nullable = false)
    private String title;

    private int counter;

    private int length;

    private boolean isActive;

    public Queue() {
    }

    public Queue(String title, int counter, int length, boolean isActive) {
        this.title = title;
        this.counter = counter;
        this.length = length;
        this.isActive = isActive;
    }

}

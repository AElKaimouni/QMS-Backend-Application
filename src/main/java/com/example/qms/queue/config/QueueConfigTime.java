package com.example.qms.queue.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class QueueConfigTime {
    private List<Integer> days;
    private String startTime;
    private String endTime;

    @JsonIgnore
    public LocalTime getStartLocalTime() {
        return LocalTime.parse(this.startTime);
    }

    @JsonIgnore
    public LocalTime getEndLocalTime() {
        return LocalTime.parse(this.endTime);
    }
}

package com.factory.analytics.machineevents.service;

import com.factory.analytics.machineevents.dto.StatsResponse;
import com.factory.analytics.machineevents.model.MachineEvent;
import com.factory.analytics.machineevents.repository.MachineEventRepository;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class StatsService {
    private final MachineEventRepository repository;

    public StatsService(MachineEventRepository repository) {
        this.repository = repository;
    }

    public StatsResponse getStats(String machineId, Instant start, Instant end) {

        List<MachineEvent> events =
                repository.findByMachineIdAndEventTimeGreaterThanEqualAndEventTimeLessThan(
                        machineId, start, end
                );

        long eventsCount = events.size();

        long defectsCount = events.stream()
                .filter(e -> e.getDefectCount() != -1)
                .mapToLong(MachineEvent::getDefectCount)
                .sum();

        double windowHours =
                Duration.between(start, end).toSeconds() / 3600.0;

        double avgDefectRate =
                windowHours == 0 ? 0 : defectsCount / windowHours;

        String status =
                avgDefectRate < 2.0 ? "Healthy" : "Warning";

        StatsResponse response = new StatsResponse();
        response.setMachineId(machineId);
        response.setStart(start);
        response.setEnd(end);
        response.setEventsCount(eventsCount);
        response.setDefectsCount(defectsCount);
        response.setAvgDefectRate(avgDefectRate);
        response.setStatus(status);

        return response;
    }
}

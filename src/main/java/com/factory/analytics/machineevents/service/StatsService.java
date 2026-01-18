package com.factory.analytics.machineevents.service;

import com.factory.analytics.machineevents.dto.StatsResponse;
import com.factory.analytics.machineevents.model.MachineEvent;
import com.factory.analytics.machineevents.repository.MachineEventRepository;
import com.factory.analytics.machineevents.dto.TopDefectLineResponse;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<TopDefectLineResponse> getTopDefectLines(
            String factoryId,
            Instant from,
            Instant to,
            int limit
    ) {

        List<MachineEvent> events =
                repository.findByFactoryIdAndEventTimeGreaterThanEqualAndEventTimeLessThan(
                        factoryId, from, to
                );

        // group by lineId
        Map<String, List<MachineEvent>> byLine =
                events.stream()
                        .collect(Collectors.groupingBy(MachineEvent::getLineId));

        List<TopDefectLineResponse> result = new ArrayList<>();

        for (Map.Entry<String, List<MachineEvent>> entry : byLine.entrySet()) {

            String lineId = entry.getKey();
            List<MachineEvent> lineEvents = entry.getValue();

            long eventCount = lineEvents.size();

            long totalDefects =
                    lineEvents.stream()
                            .filter(e -> e.getDefectCount() != -1)
                            .mapToLong(MachineEvent::getDefectCount)
                            .sum();

            double defectsPercent =
                    eventCount == 0
                            ? 0
                            : ((double) totalDefects / eventCount) * 100;

            defectsPercent =
                    Math.round(defectsPercent * 100.0) / 100.0;

            result.add(
                    new TopDefectLineResponse(
                            lineId,
                            totalDefects,
                            eventCount,
                            defectsPercent
                    )
            );
        }

        // sort by total defects desc
        result.sort(
                (a, b) -> Long.compare(b.getTotalDefects(), a.getTotalDefects())
        );

        // apply limit
        return result.stream()
                .limit(limit)
                .toList();
    }

}

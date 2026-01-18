package com.factory.analytics.machineevents.controller;

import com.factory.analytics.machineevents.dto.StatsResponse;
import com.factory.analytics.machineevents.dto.TopDefectLineResponse;
import com.factory.analytics.machineevents.service.StatsService;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {
    private final StatsService service;

    public StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping
    public StatsResponse getStats(
            @RequestParam String machineId,
            @RequestParam Instant start,
            @RequestParam Instant end
    ) {
        return service.getStats(machineId, start, end);
    }

    @GetMapping("/top-defect-lines")
    public List<TopDefectLineResponse> getTopDefectLines(
            @RequestParam String factoryId,
            @RequestParam Instant from,
            @RequestParam Instant to,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return service.getTopDefectLines(factoryId, from, to, limit);
    }
}

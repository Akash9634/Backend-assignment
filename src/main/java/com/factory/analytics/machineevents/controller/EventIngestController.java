package com.factory.analytics.machineevents.controller;


import com.factory.analytics.machineevents.dto.BatchIngestResponse;
import com.factory.analytics.machineevents.dto.EventRequest;
import com.factory.analytics.machineevents.service.EventIngestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventIngestController {
    private final EventIngestService service;

    public EventIngestController(EventIngestService service){
        this.service = service;
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchIngestResponse> ingest(@RequestBody List<EventRequest>  events){
        BatchIngestResponse response = service.ingest(events);
        return ResponseEntity.ok(response);
    }
}

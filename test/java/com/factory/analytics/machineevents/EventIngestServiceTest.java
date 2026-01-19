package com.factory.analytics.machineevents;

import com.factory.analytics.machineevents.dto.BatchIngestResponse;
import com.factory.analytics.machineevents.dto.EventRequest;
import com.factory.analytics.machineevents.model.MachineEvent;
import com.factory.analytics.machineevents.repository.MachineEventRepository;
import com.factory.analytics.machineevents.service.EventIngestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EventIngestServiceTest {
    @Autowired
    private EventIngestService service;

    @Autowired
    private MachineEventRepository repository;

    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    private EventRequest baseEvent(String eventId) {
        EventRequest req = new EventRequest();
        req.setEventId(eventId);
        req.setFactoryId("F01");
        req.setLineId("L01");
        req.setMachineId("M-001");
        req.setEventTime(Instant.now());
        req.setDurationMs(1000);
        req.setDefectCount(1);
        return req;
    }

    @Test
    void identical_event_should_be_deduped() {

        EventRequest e1 = baseEvent("E-1");
        EventRequest e2 = baseEvent("E-1");

        BatchIngestResponse response =
                service.ingest(List.of(e1, e2));

        assertEquals(1, response.getAccepted());
        assertEquals(1, response.getDeduped());
        assertEquals(1, repository.count());
    }

    @Test
    void newer_payload_should_update_existing_event() throws Exception {

        EventRequest e1 = baseEvent("E-2");
        service.ingest(List.of(e1));

        Thread.sleep(5);

        EventRequest updated = baseEvent("E-2");
        updated.setDefectCount(5);

        BatchIngestResponse response =
                service.ingest(List.of(updated));

        MachineEvent saved =
                repository.findByEventId("E-2").get();

        assertEquals(5, saved.getDefectCount());
    }

    @Test
    void older_payload_should_be_ignored() {

        Instant now = Instant.now();

        EventRequest newer = baseEvent("E-3");
        newer.setEventTime(now);

        service.ingest(List.of(newer));

        EventRequest older = baseEvent("E-3");
        older.setEventTime(now.minusSeconds(60));

        service.ingest(List.of(older));

        MachineEvent saved =
                repository.findByEventId("E-3").get();

        assertEquals(newer.getDefectCount(), saved.getDefectCount());
    }

    @Test
    void invalid_duration_should_be_rejected() {

        EventRequest bad = baseEvent("E-4");
        bad.setDurationMs(-10);

        BatchIngestResponse response =
                service.ingest(List.of(bad));

        assertEquals(1, response.getRejected());
        assertEquals(0, repository.count());
    }

    @Test
    void future_event_time_should_be_rejected() {

        EventRequest bad = baseEvent("E-5");
        bad.setEventTime(Instant.now().plus(20, ChronoUnit.MINUTES));

        BatchIngestResponse response =
                service.ingest(List.of(bad));

        assertEquals(1, response.getRejected());
        assertEquals(0, repository.count());
    }





}

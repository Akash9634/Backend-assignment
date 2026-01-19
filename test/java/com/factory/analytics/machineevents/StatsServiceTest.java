package com.factory.analytics.machineevents;

import com.factory.analytics.machineevents.dto.StatsResponse;
import com.factory.analytics.machineevents.model.MachineEvent;
import com.factory.analytics.machineevents.repository.MachineEventRepository;
import com.factory.analytics.machineevents.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class StatsServiceTest {
    @Autowired
    private StatsService statsService;

    @Autowired
    private MachineEventRepository repository;

    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    @Test
    void defect_minus_one_should_be_ignored() {

        MachineEvent e1 = new MachineEvent();
        e1.setEventId("E-10");
        e1.setMachineId("M-001");
        e1.setEventTime(Instant.now());
        e1.setDefectCount(-1);
        e1.setDurationMs(1000);
        e1.setPayloadHash("hash-E1");
        e1.setReceivedTime(Instant.now());

        MachineEvent e2 = new MachineEvent();
        e2.setEventId("E-11");
        e2.setMachineId("M-001");
        e2.setEventTime(Instant.now());
        e2.setDefectCount(3);
        e2.setDurationMs(1000);
        e2.setPayloadHash("hash-E1");
        e2.setReceivedTime(Instant.now());

        repository.saveAll(List.of(e1, e2));

        StatsResponse stats =
                statsService.getStats(
                        "M-001",
                        Instant.now().minusSeconds(60),
                        Instant.now().plusSeconds(60)
                );

        assertEquals(3, stats.getDefectsCount());
    }

    @Test
    void start_inclusive_end_exclusive_should_work() {

        Instant start = Instant.now();
        Instant end = start.plusSeconds(10);

        MachineEvent atStart = new MachineEvent();
        atStart.setEventId("E-20");
        atStart.setMachineId("M-001");
        atStart.setEventTime(start);
        atStart.setDefectCount(1);
        atStart.setDurationMs(1000);
        atStart.setPayloadHash("hash-20");
        atStart.setReceivedTime(Instant.now());

        MachineEvent atEnd = new MachineEvent();
        atEnd.setEventId("E-21");
        atEnd.setMachineId("M-001");
        atEnd.setEventTime(end);
        atEnd.setDefectCount(1);
        atEnd.setDurationMs(1000);
        atEnd.setPayloadHash("hash-21");
        atEnd.setReceivedTime(Instant.now());

        repository.saveAll(List.of(atStart, atEnd));

        StatsResponse stats =
                statsService.getStats("M-001", start, end);

        assertEquals(1, stats.getEventsCount());
    }


}

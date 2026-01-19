package com.factory.analytics.machineevents;

import com.factory.analytics.machineevents.dto.EventRequest;
import com.factory.analytics.machineevents.model.MachineEvent;
import com.factory.analytics.machineevents.repository.MachineEventRepository;
import com.factory.analytics.machineevents.service.EventIngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrencyIngestTest {
    @Autowired
    private EventIngestService service;

    @Autowired
    private MachineEventRepository repository;

    // helper method
    private EventRequest buildEvent(String eventId, int defect) {
        EventRequest req = new EventRequest();
        req.setEventId(eventId);
        req.setFactoryId("F01");
        req.setLineId("L01");
        req.setMachineId("M-001");
        req.setEventTime(Instant.now());
        req.setDurationMs(1000);
        req.setDefectCount(defect);
        return req;
    }

    @Test
    void concurrent_ingestion_should_not_create_duplicates() throws Exception {

        int threads = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {

            executor.submit(() -> {
                try {
                    List<EventRequest> batch = List.of(
                            buildEvent("E-500", 1),
                            buildEvent("E-501", 2)
                    );

                    service.ingest(batch);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        List<MachineEvent> all = repository.findAll();

        long countE500 =
                all.stream().filter(e -> e.getEventId().equals("E-500")).count();

        long countE501 =
                all.stream().filter(e -> e.getEventId().equals("E-501")).count();

        assertEquals(1, countE500);
        assertEquals(1, countE501);
    }


}

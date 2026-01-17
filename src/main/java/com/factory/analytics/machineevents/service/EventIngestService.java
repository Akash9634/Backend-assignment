package com.factory.analytics.machineevents.service;


import com.factory.analytics.machineevents.dto.BatchIngestResponse;
import com.factory.analytics.machineevents.dto.EventRequest;
import com.factory.analytics.machineevents.dto.RejectionReason;
import com.factory.analytics.machineevents.model.MachineEvent;
import com.factory.analytics.machineevents.repository.MachineEventRepository;
import com.factory.analytics.machineevents.util.PayloadHashUtil;
import jdk.jfr.Event;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventIngestService {
    private final MachineEventRepository repository;

    public EventIngestService(MachineEventRepository repository){
        this.repository = repository;
    }

    @Transactional
    public BatchIngestResponse ingest(List<EventRequest> events){

        int accepted = 0;
        int deduped = 0;
        int updated = 0;
        int rejected = 0;

        List<RejectionReason> rejections = new ArrayList<>();

        for(EventRequest req : events){

            //validation
            String validationError = validate(req);
            if(validationError != null){
                rejected++;
                rejections.add(
                        new RejectionReason(req.getEventId(), validationError)
                );
                continue;
            }

            //backend received time
            Instant receivedTime = Instant.now();

            //payload hash
            String payloadHash = PayloadHashUtil.hash(req);

            //dedup / update logic
            Optional<MachineEvent> existingOpt = repository.findByEventId(req.getEventId());

            //new event
            if(existingOpt.isEmpty()){
                MachineEvent event = new MachineEvent();

                event.setEventId(req.getEventId());
                event.setMachineId(req.getMachineId());
                event.setEventTime(req.getEventTime());
                event.setReceivedTime(receivedTime);
                event.setDurationMs(req.getDurationMs());
                event.setDefectCount(req.getDefectCount());
                event.setPayloadHash(payloadHash);
                event.setFactoryId(req.getFactoryId());
                event.setLineId(req.getLineId());

                repository.save(event);
                accepted++;
            }
            //existing eventId
            else{
                MachineEvent existing = existingOpt.get();

                // identical payload → dedupe
                if (existing.getPayloadHash().equals(payloadHash)) {
                    deduped++;
                }
                // different payload → maybe update
                else {
                    if (receivedTime.isAfter(existing.getReceivedTime())) {

                        existing.setMachineId(req.getMachineId());
                        existing.setEventTime(req.getEventTime());
                        existing.setDurationMs(req.getDurationMs());
                        existing.setDefectCount(req.getDefectCount());
                        existing.setReceivedTime(receivedTime);
                        existing.setPayloadHash(payloadHash);
                        existing.setLineId(req.getLineId());
                        existing.setFactoryId(req.getFactoryId());

                        repository.save(existing);
                        updated++;
                    }
                    // else: older update → ignore
                }
            }
        }

        BatchIngestResponse response = new BatchIngestResponse();
        response.setAccepted(accepted);
        response.setDeduped(deduped);
        response.setUpdated(updated);
        response.setRejected(rejected);
        response.setRejections(rejections);

        return response;
    }

    private String validate(EventRequest req) {

        // duration < 0 OR > 6 hours
        if (req.getDurationMs() < 0 ||
                req.getDurationMs() > 6 * 60 * 60 * 1000) {
            return "INVALID_DURATION";
        }

        // eventTime > now + 15 minutes
        if (req.getEventTime().isAfter(
                Instant.now().plus(15, ChronoUnit.MINUTES))) {
            return "FUTURE_EVENT_TIME";
        }

        return null;
    }

        }



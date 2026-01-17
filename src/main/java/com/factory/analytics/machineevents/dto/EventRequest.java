package com.factory.analytics.machineevents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class EventRequest {

    @NotBlank
    private String eventId;

    @NotNull
    private Instant eventTime;

    @NotBlank
    private String machineId;

    private long durationMs;
    private int defectCount;

    private String factoryId;
    private String lineId;

    //no-args constructor
    public EventRequest() {}

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public int getDefectCount() {
        return defectCount;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public void setDefectCount(int defectCount) {
        this.defectCount = defectCount;
    }
}

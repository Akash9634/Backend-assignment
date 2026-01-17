package com.factory.analytics.machineevents.dto;

public class RejectionReason {
    private String eventId;
    private String reason;

    public RejectionReason(String eventId, String reason) {
        this.eventId = eventId;
        this.reason = reason;
    }

    public RejectionReason(){}

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

package com.factory.analytics.machineevents.dto;

import java.util.List;

public class BatchIngestResponse {

    private int accepted;
    private int deduped;
    private int updated;
    private int rejected;

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

    public int getDeduped() {
        return deduped;
    }

    public void setDeduped(int deduped) {
        this.deduped = deduped;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public int getRejected() {
        return rejected;
    }

    public void setRejected(int rejected) {
        this.rejected = rejected;
    }

    public List<RejectionReason> getRejections() {
        return rejections;
    }

    public void setRejections(List<RejectionReason> rejections) {
        this.rejections = rejections;
    }

    private List<RejectionReason> rejections;

}

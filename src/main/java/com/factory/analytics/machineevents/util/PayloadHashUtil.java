package com.factory.analytics.machineevents.util;

import com.factory.analytics.machineevents.dto.EventRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PayloadHashUtil {
    public static String hash(EventRequest req){
        String raw = req.getEventId() + "|" +
                req.getMachineId() + "|" +
                req.getEventTime() + "|" +
                req.getDurationMs() + "|" +
                req.getDefectCount();

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(raw.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : encoded) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        }
        catch(NoSuchAlgorithmException e){
            throw new RuntimeException("Unable to hash payload", e);
        }
    }
}

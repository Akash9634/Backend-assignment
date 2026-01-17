package com.factory.analytics.machineevents.repository;

import com.factory.analytics.machineevents.model.MachineEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MachineEventRepository extends JpaRepository<MachineEvent, Long> {

    Optional<MachineEvent> findByEventId(String eventId);

}

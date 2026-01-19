# Machine Events Backend

## 1. Architecture

The system follows a layered architecture:

Machines → Controller → Service → Repository → Database

- Controller layer handles request/response mapping.
- Service layer contains all business logic.
- Repository layer abstracts database operations.
- Database acts as the single source of truth.

Ingestion and analytics are intentionally separated to keep responsibilities clear.

---

## 2. Deduplication & Update Logic

Each event is uniquely identified by `eventId`.

Rules:

- Same eventId + identical payload → duplicate → ignored
- Same eventId + different payload → treated as update
- When updating, the record with the latest `receivedTime` wins

Payload comparison is performed using a SHA-256 hash generated from relevant event fields.

---

## 3. Thread Safety

The system is designed to support concurrent ingestion from multiple sensors.

Thread safety is ensured by:

- Database-level unique constraint on `eventId`
- Transactional ingestion logic
- Database acting as the concurrency coordinator

No explicit Java locks or synchronized blocks are used.

Concurrency behavior was verified using parallel ingestion tests.

---

## 4. Data Model

### MachineEvent table

| Column | Description |
|------|-------------|
| id | Auto-generated primary key |
| event_id | Unique event identifier |
| factory_id | Factory identifier |
| line_id | Production line identifier |
| machine_id | Machine identifier |
| event_time | Time event occurred |
| received_time | Time backend received event |
| duration_ms | Event duration |
| defect_count | Defects produced (-1 = unknown) |
| payload_hash | SHA-256 payload hash |

---

## 5. Performance Strategy

To meet the requirement of ingesting 1,000 events under 1 second:

- Events are processed in a single batch loop.
- Database writes occur within one transaction.
- Payload comparison uses hashing instead of deep comparisons.
- In-memory database minimizes I/O overhead.

This allows fast and predictable ingestion.

---

## 6. Edge Cases & Assumptions

- `defectCount = -1` represents unknown defects and is excluded from defect calculations.
- `eventTime` is used for all analytics windows.
- `receivedTime` is set by the backend and client-provided values are ignored.
- Schema includes `factoryId` and `lineId` to support top-defect-lines analytics.
- Start time is inclusive; end time is exclusive.

These assumptions were made to align with analytics requirements.

---

## 7. Setup & Run Instructions

### Prerequisites
- Java 17+
- Maven

### Run Application

mvn spring-boot:run

H2 console available at:
http://localhost:8080/h2-console


---

## 8. Future Improvements

With additional time, the following could be improved:

- Replace H2 with PostgreSQL.
- Add database-level aggregation queries for large datasets.
- Introduce pagination for analytics endpoints.
- Add metrics and monitoring.

---

## Summary

This system demonstrates:

- safe batch ingestion
- deduplication and update handling
- concurrency correctness
- analytics over time windows
- clean backend architecture

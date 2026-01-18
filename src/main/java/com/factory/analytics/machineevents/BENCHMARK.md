# Benchmark Results

## Objective

Validate that the system can ingest a batch of 1,000 machine events within 1 second as required.

---

## Test Environment

- OS: Windows 11
- CPU: Intel i5 
- RAM: 16 GB
- Java Version: Java 23
- Spring Boot: 3.5.9
- Database: H2 (in-memory)
- Build Tool: Maven

---

## Benchmark Command

The application was started locally using:

mvn spring-boot:run


Benchmark was executed using a custom batch request of 1,000 events sent to:


---

## Test Method

1. Generated a JSON array of 1,000 events programmatically.
2. Sent the batch in a single request.
3. Measured execution time at the service layer using system timestamps.
4. All validations, deduplication, and persistence logic were enabled.

---

## Measured Results

| Metric | Value |
|------|-------|
| Batch size | 1,000 events |
| Processing time | ~350 ms |
| Requirement | < 1000 ms |
| Status | ✅ Passed |

Measurements were taken across multiple runs and averaged.

---

## Optimizations Applied

- Batch ingestion handled in a single transaction.
- Payload comparison uses precomputed SHA-256 hash instead of deep object comparison.
- In-memory H2 database used to minimize I/O overhead.
- No synchronized blocks or thread locks used.

---

## Conclusion

The system consistently processed 1,000 events well under the required 1 second threshold.

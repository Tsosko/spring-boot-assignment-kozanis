package nl.gerimedica.assignment.services.utils;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HospitalUtils {

    // Using AtomicInteger for thread safety.
    private final AtomicInteger usageCounter = new AtomicInteger(0);

    /**
     * Records usage of hospital services with context information.
     * Thread-safe implementation.
     * 
     * @param context Description of the operation being performed
     */
    public void recordUsage(String context) {
        int currentCount = usageCounter.incrementAndGet();
        log.info("Hospital service used. Counter: {} | Context: {}", currentCount, context);
    }
}

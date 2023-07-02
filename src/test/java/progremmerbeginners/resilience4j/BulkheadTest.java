package progremmerbeginners.resilience4j;

import io.github.resilience4j.bulkhead.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
public class BulkheadTest {

    private AtomicLong  counter = new AtomicLong(0L);

    @SneakyThrows
    void slow(){
        var value = counter.incrementAndGet();
        log.info("slow : " + value);
        Thread.sleep(1_000);
    }

    @Test
    @SneakyThrows
    void testSemaphore() {
        Bulkhead bulkhead = Bulkhead.ofDefaults("wanda");

        for (int i = 0; i < 1000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead,() -> slow());
            new Thread(runnable).start();
        }
        Thread.sleep(10_000);
    }

    @Test
    void testThreadPool() {
        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.ofDefaults("wanda");
        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

        for (int i = 0; i < 1000; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }
    }
    @Test
    @SneakyThrows
    void testSemaphoreConfig() {

        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(6)
                .maxWaitDuration(Duration.ofSeconds(5L))
                .build();

        Bulkhead bulkhead = Bulkhead.of("wanda",config);

        for (int i = 0; i < 10; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead,() -> slow());
            new Thread(runnable).start();
        }
        Thread.sleep(10_000);
    }
    @Test
    @SneakyThrows
    void testThreadPoolConfig() {

        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(5)
                .coreThreadPoolSize(5)
                .build();

        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.of("wanda",config);
        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));

        for (int i = 0; i < 20; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }

        Thread.sleep(10_000);
    }
    @Test
    @SneakyThrows
    void testSemaphoreRegistry() {

        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(6)
                .maxWaitDuration(Duration.ofSeconds(5L))
                .build();

        BulkheadRegistry registry=BulkheadRegistry.ofDefaults();
        registry.addConfiguration("config" ,config);

        Bulkhead bulkhead = registry.bulkhead("wanda","config");

        for (int i = 0; i < 10; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead,() -> slow());
            new Thread(runnable).start();
        }
        Thread.sleep(10_000);
    }
    @Test
    @SneakyThrows
    void testThreadPoolRegistry() {

        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(5)
                .coreThreadPoolSize(5)
                .build();

        ThreadPoolBulkheadRegistry registry =ThreadPoolBulkheadRegistry.ofDefaults();
        registry.addConfiguration("config",config);

        ThreadPoolBulkhead bulkhead = registry.bulkhead("wanda","config");

        for (int i = 0; i < 20; i++) {
            Supplier<CompletionStage<Void>> supplier = ThreadPoolBulkhead.decorateRunnable(bulkhead, () -> slow());
            supplier.get();
        }

        Thread.sleep(10_000);
    }


}


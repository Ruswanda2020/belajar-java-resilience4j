package progremmerbeginners.resilience4j;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TimeLimiterTest {

    @SneakyThrows
    public String slow(){
        log.info("SLOW");
        Thread.sleep(5000L);
        return "wanda";
    }

    @Test
    @SneakyThrows
    void testTimeLimiter() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = service.submit(() -> slow());

        TimeLimiter timeLimiter =TimeLimiter.ofDefaults("wanda");
        Callable<String> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> future);
        callable.call();

    }
    @Test
    @SneakyThrows
    void testTimeLimiterConfig() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = service.submit(() -> slow());

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5L))
                .cancelRunningFuture(true)
                .build();

        TimeLimiter timeLimiter =TimeLimiter.of("wanda",config);
        Callable<String> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> future);
        callable.call();

    }
    @Test
    @SneakyThrows
    void testTimeLimiterRegistry() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<String> future = service.submit(() -> slow());

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5L))
                .cancelRunningFuture(true)
                .build();

        TimeLimiterRegistry registry =TimeLimiterRegistry.ofDefaults();
        registry.addConfiguration("config",config);

        TimeLimiter timeLimiter =registry.timeLimiter("wanda","config");
        Callable<String> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> future);
        callable.call();

    }
}

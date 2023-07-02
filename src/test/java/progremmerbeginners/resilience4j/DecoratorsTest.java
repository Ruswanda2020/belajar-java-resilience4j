package progremmerbeginners.resilience4j;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class DecoratorsTest {
    @SneakyThrows
    void slow(){
        log.info("slow " );
        Thread.sleep(1_000);
    }
    @SneakyThrows
    public String sayHallo(){
        log.info("say hallo");
        Thread.sleep(1_000);
        throw new IllegalArgumentException("UPSS");
    }

    @Test
    @SneakyThrows
    void decorators() {

        RateLimiter limiter = RateLimiter.of("pg-ratelimiter", RateLimiterConfig.custom()
                .limitForPeriod(5)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .build());

        Retry retry =Retry.of("pg-Retry", RetryConfig.custom()
                .maxAttempts(10)
                .waitDuration(Duration.ofMillis(10))
                .build());

        Runnable runnable = Decorators.ofRunnable(() -> slow())
                .withRetry(retry)
                .withRateLimiter(limiter)
                .decorate();

        for (int i = 0; i < 100; i++) {
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }
    @Test
    @SneakyThrows
    void fallback() {

        RateLimiter limiter = RateLimiter.of("pg-ratelimiter", RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .build());

        Retry retry = Retry.of("pg-Retry", RetryConfig.custom()
                .maxAttempts(11)
                .waitDuration(Duration.ofMillis(10))
                .build());

        Supplier<String> supplier = Decorators.ofSupplier(() -> sayHallo())
                .withRetry(retry)
                .withRateLimiter(limiter)
                .withFallback(throwable -> "hallo guest")
                .decorate();

        System.out.println(supplier.get());

    }
}

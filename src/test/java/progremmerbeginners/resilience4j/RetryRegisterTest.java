package progremmerbeginners.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Slf4j
public class RetryRegisterTest {

    void callMe(){
        log.info("try call me");
        throw new IllegalArgumentException("upss eror");
    }

    @Test
    void testRetryRegistry() {

        RetryRegistry registry=RetryRegistry.ofDefaults();

        Retry retry=registry.retry("WANDA");
        Retry retry1=registry.retry("WANDA");

        Assertions.assertSame(retry,retry1);
    }

    @Test
    void testRetryRegistryConfig() {
        RetryConfig config=RetryConfig.custom()
                .maxAttempts(5)
                .waitDuration(Duration.ofSeconds(2L))
                .build();

        RetryRegistry registry=RetryRegistry.ofDefaults();
        registry.addConfiguration("config",config);

        Retry retry1=registry.retry("wanda","config");
        Retry retry2=registry.retry("wanda");

        Assertions.assertSame(retry1,retry2);

        Runnable runnable=Retry.decorateRunnable(retry1,() -> callMe());
        runnable.run();

    }
}

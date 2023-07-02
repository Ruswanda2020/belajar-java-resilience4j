package progremmerbeginners.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
public class RetryConfigTest {

    String hello(){
        log.info("call say hallo");
        throw new RuntimeException("upss eror");
    }

    @Test
    void retryConfig() {

        RetryConfig retryConfig= RetryConfig.custom()
                .maxAttempts(4)
                .waitDuration(Duration.ofSeconds(2L))
                //.ignoreExceptions(IllegalArgumentException.class)
               // .retryExceptions(IllegalArgumentException.class)
                .build();

        Retry retry=Retry.of("wanda",retryConfig);
        Supplier<String> supplier=Retry.decorateSupplier(retry,() -> hello());
        supplier.get();


    }
}

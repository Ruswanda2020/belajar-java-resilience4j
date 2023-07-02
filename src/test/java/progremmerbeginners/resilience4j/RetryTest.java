package progremmerbeginners.resilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class RetryTest {

    void callMe(){
        log.info("try call me");
        throw new IllegalArgumentException("upss eror");
    }

    @Test
    void createNewRetry() {
        Retry retry=Retry.ofDefaults("wanda");
       Runnable runnable= Retry.decorateRunnable(retry,() -> callMe());

       runnable.run();
    }

    String hello(){
        log.info("call say hello");
        throw new IllegalArgumentException("ups eror say hallo");
    }

    @Test
    void createRetrySupplier() {

        Retry retry=Retry.ofDefaults("wanda");
        Supplier<String> supplier=Retry.decorateSupplier(retry,() -> hello());
        supplier.get();
    }
}

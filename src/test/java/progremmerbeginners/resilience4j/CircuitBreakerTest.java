package progremmerbeginners.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CircuitBreakerTest {

    public void callMe(){
        log.info("call me");
        throw new IllegalArgumentException("ups");
    }

    @Test
    void testCircuitBreaker() {
        CircuitBreaker circuitBreaker =CircuitBreaker.ofDefaults("wanda");

        for (int i = 0; i < 200; i++) {
            try {
                Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> callMe());
                runnable.run();
            }catch (Exception e){
                log.error("eror : {}",e.getMessage());
            }
        }
    }
    @Test
    void testCircuitBreakerConfig() {

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .failureRateThreshold(10f)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(10)
                .build();
        CircuitBreaker circuitBreaker =CircuitBreaker.of("wanda",config);

        for (int i = 0; i < 200; i++) {
            try {
                Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> callMe());
                runnable.run();
            }catch (Exception e){
                log.error("eror : {}",e.getMessage());
            }
        }
    }
    @Test
    void testCircuitBreakerRegistry() {

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .failureRateThreshold(10f)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(10)
                .build();

        CircuitBreakerRegistry registry =CircuitBreakerRegistry.ofDefaults();
        registry.addConfiguration("config",config);
        CircuitBreaker circuitBreaker =registry.circuitBreaker("wanda","config");

        for (int i = 0; i < 200; i++) {
            try {
                Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, () -> callMe());
                runnable.run();
            }catch (Exception e){
                log.error("eror : {}",e.getMessage());
            }
        }
    }
}

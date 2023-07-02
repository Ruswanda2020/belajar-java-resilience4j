package progremmerbeginners.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;
@Slf4j
public class EvenPublisherTest {
    @Test
    void retry() {
        /*
        * sama di matric evenpublisher semua modul ada evenpublisher
        * dan beda beda evenpublisher tergantung modul yang kita gunakan
        * */
        Retry retry =Retry.ofDefaults("pg");
        retry.getEventPublisher().onRetry(event -> log.info("try to Retry"));

        try {
            Supplier<String> supplier = Retry.decorateSupplier(retry, () -> hallo());
            supplier.get();
        }catch (Exception e){
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt());
        }
    }

    private String hallo(){
        throw new IllegalArgumentException("upss");
    }

    @Test
    void registry() {
        RetryRegistry registry = RetryRegistry.ofDefaults();
        registry.getEventPublisher().onEntryAdded(event ->
                log.info("add new entry {}",event.getAddedEntry().getName()));


        registry.retry("pg");
        registry.retry("pg");
        registry.retry("pg2");
    }
}

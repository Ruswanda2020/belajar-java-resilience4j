package progremmerbeginners.resilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class MetricTest {
    /*
    * setiap modul ada metricnya
    * dan beda beda matricnya tergantung modul yang kita gunakan
    * */

    @Test
    void retry() {
        Retry retry =Retry.ofDefaults("pg");

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

}

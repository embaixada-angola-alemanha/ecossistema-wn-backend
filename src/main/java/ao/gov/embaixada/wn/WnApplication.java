package ao.gov.embaixada.wn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "ao.gov.embaixada.wn",
        "ao.gov.embaixada.commons"
})
@EnableScheduling
public class WnApplication {

    public static void main(String[] args) {
        SpringApplication.run(WnApplication.class, args);
    }
}

package id.ac.ui.cs.advprog.gatra;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.google.client-id=dummy-id",
        "spring.security.oauth2.client.registration.google.client-secret=dummy-secret",
        "app.frontend.url=http://localhost:5173"
})
@ActiveProfiles("test")
class GatraApplicationTests {

    @Test
    void contextLoads() {
    }

}

package be.vlaanderen.informatievlaanderen.ldes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Unit test to ensure that the Spring context loads.
 */
@SpringBootTest
@ActiveProfiles("test")
class ApplicationTest {

    /**
     * Test that the context loads.
     */
    @Test
    void contextLoads() {
    }

}

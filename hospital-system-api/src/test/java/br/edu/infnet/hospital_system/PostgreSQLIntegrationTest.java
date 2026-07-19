package br.edu.infnet.hospital_system;

import br.edu.infnet.hospital_system.config.TestcontainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public abstract class PostgreSQLIntegrationTest {
}

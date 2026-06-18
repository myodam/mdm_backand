package com.ux.mbm;

import com.ux.mbm.global.ai.AiServerClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        // H2 인메모리 DB로 강제 지정
        "spring.datasource.url=jdbc:h2:mem:mbm_test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        // Flyway H2 호환 SQL 경로
        "spring.flyway.locations=classpath:db/migration-h2",
        "spring.flyway.baseline-on-migrate=true",
        "spring.flyway.enabled=true",
        // JPA H2 dialect
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        // AI 서버 더미값
        "ai.server.url=http://localhost:9999"
})
class MbmApplicationTests {

    @MockitoBean
    private AiServerClient aiServerClient;

    @Test
    void contextLoads() {
    }
}
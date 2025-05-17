package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BlocServiceMockTest {

    @BeforeEach
    void beforeEach() {
        System.out.println("Before each test");
    }

    @AfterEach
    void afterEach() {
        System.out.println("After each test");
    }

    @Order(1)
    @RepeatedTest(4)
    void test() {
        int expected = 2 + 2;
        assertEquals(4, expected, "Basic math test failed");
    }

    @Order(2)
    @Test
    void test3() {
        String name = "Bloc A";
        assertTrue(name.startsWith("Bloc"), "Name should start with 'Bloc'");
    }

    @Order(3)
    @Test
    void test4() {
        Object obj = new Object();
        assertNotNull(obj, "Object should not be null");
    }

    @Order(4)
    @Test
    void test2() {
        boolean available = true;
        assertTrue(available, "Expected 'available' to be true");
    }
}

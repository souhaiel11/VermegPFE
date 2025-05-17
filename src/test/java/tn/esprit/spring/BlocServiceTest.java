package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BlocServiceTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before all tests");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After all tests");
    }

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
        int result = 2 + 3;
        assertEquals(5, result, "2 + 3 should equal 5");
    }

    @Order(2)
    @Test
    void test3() {
        String blocName = "Bloc A";
        assertTrue(blocName.startsWith("Bloc"), "Bloc name should start with 'Bloc'");
    }

    @Order(3)
    @Test
    void test4() {
        Object o = new Object();
        assertNotNull(o, "Object should not be null");
    }

    @Order(4)
    @Test
    void test2() {
        boolean isActive = true;
        assertTrue(isActive, "isActive should be true");
    }
}

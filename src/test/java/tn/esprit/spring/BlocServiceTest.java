package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tn.esprit.spring.Services.Bloc.BlocService;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.entities.Foyer;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlocServiceTest {

    @Autowired
    private BlocService blocService;

    private Bloc bloc;
    private Foyer testFoyer;

    @BeforeAll
    void setUp() {
        Chambre ch1 = Chambre.builder()
                .numeroChambre(101)
                .typeC(TypeChambre.SIMPLE)
                .build();

        Chambre ch2 = Chambre.builder()
                .numeroChambre(102)
                .typeC(TypeChambre.DOUBLE)
                .build();

        bloc = Bloc.builder()
                .nomBloc("Bloc Alpha")
                .capaciteBloc(150)
                .chambres(Arrays.asList(ch1, ch2))
                .build();

        testFoyer = Foyer.builder()
                .nomFoyer("Test Foyer")
                .capaciteFoyer(200)
                .build();

        bloc = blocService.addOrUpdate(bloc);
    }

    @Test
    @Order(1)
    void testAddOrUpdate_IdBlocGenerated() {
        assertTrue(bloc.getIdBloc() > 0, "Bloc ID should be greater than 0");
    }

    @Test
    @Order(2)
    void testAddOrUpdate_NomBloc() {
        assertEquals("Bloc Alpha", bloc.getNomBloc(), "Bloc name should match");
    }

    @Test
    @Order(3)
    void testAddOrUpdate_CapaciteBloc() {
        assertEquals(150, bloc.getCapaciteBloc(), "Bloc capacity should match");
    }

    @Test
    @Order(4)
    void testChambresAjoutees() {
        List<Chambre> chambres = bloc.getChambres();
        assertNotNull(chambres, "Chambres list should not be null");
        assertEquals(2, chambres.size(), "Should have 2 chambres");
        assertTrue(chambres.stream().anyMatch(c -> c.getNumeroChambre() == 101),
                "Should contain chambre 101");
        assertTrue(chambres.stream().anyMatch(c -> c.getNumeroChambre() == 102),
                "Should contain chambre 102");
    }

    @Test
    @Order(5)
    void testFindById() {
        Bloc foundBloc = blocService.findById(bloc.getIdBloc());
        assertNotNull(foundBloc, "Should find bloc by ID");
        assertEquals(bloc.getIdBloc(), foundBloc.getIdBloc(), "IDs should match");
    }



    @Test
    @Order(7)
    void testFindAll() {
        List<Bloc> blocs = blocService.findAll();
        assertFalse(blocs.isEmpty(), "Should return at least one bloc");
        assertTrue(blocs.stream().anyMatch(b -> b.getIdBloc() == bloc.getIdBloc()),
                "Should contain our test bloc");
    }

    @AfterAll
    void cleanUp() {
        if (bloc != null && bloc.getIdBloc() > 0) {
            blocService.deleteById(bloc.getIdBloc());
        }
    }
}
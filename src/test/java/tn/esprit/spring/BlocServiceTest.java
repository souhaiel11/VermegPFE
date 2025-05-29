package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tn.esprit.spring.Services.Bloc.BlocService;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.entities.TypeChambre;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlocServiceTest {

    @Autowired
    private BlocService blocService;

    private Bloc bloc;
    private Foyer foyer;
    private static final String TEST_BLOC_NAME = "Bloc Alpha";
    private static final String TEST_FOYER_NAME = "Foyer Test";

    @BeforeAll
    void init() {
        // 1) Create test foyer
        foyer = Foyer.builder()
                .nomFoyer(TEST_FOYER_NAME)
                .capaciteFoyer(100)
                .build();

        // 2) Create test bloc with 2 chambres
        Chambre c1 = Chambre.builder()
                .numeroChambre(201)
                .typeC(TypeChambre.SIMPLE)
                .build();

        Chambre c2 = Chambre.builder()
                .numeroChambre(202)
                .typeC(TypeChambre.DOUBLE)
                .build();

        bloc = Bloc.builder()
                .nomBloc(TEST_BLOC_NAME)
                .capaciteBloc(150)
                .chambres(Arrays.asList(c1, c2))
                .build();

        bloc = blocService.addOrUpdate(bloc);
    }

    @Test
    @Order(1)
    void testAddOrUpdateWithCascade_shouldPersistChambres() {
        // Given
        Bloc newBloc = Bloc.builder()
                .nomBloc("CascadeBloc")
                .capaciteBloc(50)
                .chambres(bloc.getChambres())
                .build();

        // When
        Bloc result = blocService.addOrUpdate2(newBloc);

        // Then
        assertNotNull(result, "Bloc should not be null");
        result.getChambres().forEach(c -> {
            assertNotNull(c.getBloc(), "Chambre's bloc should not be null");
            assertEquals(result, c.getBloc(), "Chambre should reference the parent bloc");
        });
    }

    @Test
    @Order(2)
    void testAffecterChambresABloc_shouldUpdateChambres() {
        // Given
        Bloc newBloc = Bloc.builder()
                .nomBloc("AffectBloc")
                .capaciteBloc(20)
                .build();
        newBloc = blocService.addOrUpdate(newBloc);

        List<Long> chambreNumbers = List.of(201L, 202L);

        // When
        Bloc affected = blocService.affecterChambresABloc(chambreNumbers, TEST_BLOC_NAME);

        // Then
        assertEquals(TEST_BLOC_NAME, affected.getNomBloc(), "Bloc name should remain unchanged");
        assertEquals(2, affected.getChambres().size(), "Bloc should have 2 chambres");
        assertTrue(affected.getChambres().stream()
                .anyMatch(c -> c.getNumeroChambre() == 201), "Should contain chambre 201");
        assertTrue(affected.getChambres().stream()
                .anyMatch(c -> c.getNumeroChambre() == 202), "Should contain chambre 202");
    }

    @Test
    @Order(3)
    void testAffecterBlocAFoyer_shouldSetFoyer() {
        // When
        Bloc affected = blocService.affecterBlocAFoyer(TEST_BLOC_NAME, TEST_FOYER_NAME);

        // Then
        assertNotNull(affected.getFoyer(), "Foyer should be set");
        assertEquals(TEST_FOYER_NAME, affected.getFoyer().getNomFoyer(),
                "Foyer name should match");
    }

    @Test
    @Order(4)
    void testAjouterBlocEtSesChambres_shouldPersistCascade() {
        // Given
        Chambre newChambre = Chambre.builder()
                .numeroChambre(301)
                .typeC(TypeChambre.TRIPLE)
                .build();

        Bloc newBloc = Bloc.builder()
                .nomBloc("AddCascade")
                .capaciteBloc(30)
                .chambres(List.of(newChambre))
                .build();

        // When
        Bloc saved = blocService.ajouterBlocEtSesChambres(newBloc);

        // Then
        assertNotNull(saved.getIdBloc(), "Bloc should have an ID after save");
        assertEquals(1, saved.getChambres().size(), "Should have 1 chambre");
        assertEquals(301, saved.getChambres().get(0).getNumeroChambre(),
                "Chambre number should match");
        assertEquals(saved, saved.getChambres().get(0).getBloc(),
                "Chambre should reference parent bloc");
    }

    @Test
    @Order(5)
    void testAjouterBlocEtAffecterAFoyer_shouldSetFoyer() {
        // Given
        Bloc newBloc = Bloc.builder()
                .nomBloc("AddAndFoyer")
                .capaciteBloc(40)
                .build();

        // When
        Bloc saved = blocService.ajouterBlocEtAffecterAFoyer(newBloc, TEST_FOYER_NAME);

        // Then
        assertNotNull(saved.getFoyer(), "Foyer should be set");
        assertEquals(TEST_FOYER_NAME, saved.getFoyer().getNomFoyer(),
                "Foyer name should match");
    }

    @Test
    @Order(6)
    void testDeleteOperations_shouldRemoveBloc() {
        // Given
        Bloc tempBloc = Bloc.builder()
                .nomBloc("ToDelete")
                .capaciteBloc(10)
                .build();
        tempBloc = blocService.addOrUpdate(tempBloc);
        Long blocId = tempBloc.getIdBloc();

        // When/Then for deleteById
        blocService.deleteById(blocId);
        assertThrows(NoSuchElementException.class, () -> blocService.findById(blocId),
                "Bloc should be deleted");

        // When/Then for delete(Bloc)
        Bloc tempBloc2 = blocService.addOrUpdate(tempBloc);
        blocService.delete(tempBloc2);
        assertThrows(NoSuchElementException.class, () -> blocService.findById(tempBloc2.getIdBloc()),
                "Bloc should be deleted");
    }

    @AfterAll
    void cleanUp() {

            blocService.deleteById(bloc.getIdBloc());

    }
}
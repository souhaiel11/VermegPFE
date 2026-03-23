package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import tn.esprit.spring.Services.Bloc.BlocService;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.FoyerRepository;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


//@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunWith(SpringRunner.class)
@TestMethodOrder(MethodOrderer.class)
@SpringBootTest
class BlocServiceTest {

    @Autowired
    private BlocService blocService;

    @Autowired
    private FoyerRepository foyerRepository;

    private Bloc bloc;
    private Foyer foyer;
    private static final String TEST_BLOC_NAME = "BlocAlpha";
    private static final String TEST_FOYER_NAME = "FoyerTest";

    @BeforeAll
    void setUp() {
        // 1) Create and persist test foyer
        foyer = foyerRepository.save(
                Foyer.builder()
                        .nomFoyer(TEST_FOYER_NAME)
                        .capaciteFoyer(100)
                        .build()
        );

        // 2) Create test bloc with 2 chambres
        Chambre c1 = Chambre.builder()
                .numeroChambre(101)
                .typeC(TypeChambre.SIMPLE)
                .build();

        Chambre c2 = Chambre.builder()
                .numeroChambre(102)
                .typeC(TypeChambre.DOUBLE)
                .build();

        bloc = blocService.addOrUpdate(
                Bloc.builder()
                        .nomBloc(TEST_BLOC_NAME)
                        .capaciteBloc(50)
                        .chambres(Arrays.asList(c1, c2))
                        .build()
        );
    }

    @Test
    @Order(1)
    void testAddOrUpdate_cascadeChambres() {
        // Given
        Bloc newBloc = Bloc.builder()
                .nomBloc("Cascade")
                .capaciteBloc(10)
                .chambres(bloc.getChambres())
                .build();

        // When
        Bloc savedBloc = blocService.addOrUpdate(newBloc);

        // Then
        assertTrue(savedBloc.getIdBloc() > 0, "Bloc ID should be positive");
        assertNotNull(savedBloc.getChambres(), "Chambres list should not be null");

        savedBloc.getChambres().forEach(chambre -> {
            assertNotNull(chambre.getBloc(), "Chambre should reference a bloc");
            assertEquals(savedBloc.getIdBloc(), chambre.getBloc().getIdBloc(),
                    "Chambre should reference the parent bloc");
        });
    }

    @Test
    @Order(2)
    void testAffecterBlocAFoyer() {
        // When
        Bloc affected = blocService.affecterBlocAFoyer(TEST_BLOC_NAME, TEST_FOYER_NAME);

        // Then
        assertNotNull(affected.getFoyer(), "Foyer should be set");
        assertEquals(TEST_FOYER_NAME, affected.getFoyer().getNomFoyer(),
                "Foyer name should match");
    }

    @Test
    @Order(3)
    void testAjouterBlocEtSesChambres() {
        // Given
        Chambre newChambre = Chambre.builder()
                .numeroChambre(201)
                .typeC(TypeChambre.TRIPLE)
                .build();

        // When
        Bloc result = blocService.ajouterBlocEtSesChambres(
                Bloc.builder()
                        .nomBloc("AddChambres")
                        .capaciteBloc(20)
                        .chambres(List.of(newChambre))
                        .build()
        );

        // Then
        assertTrue(result.getIdBloc() > 0, "Bloc ID should be positive");
        assertEquals(1, result.getChambres().size(), "Should have 1 chambre");
    }

    @Test
    @Order(4)
    void testAjouterBlocEtAffecterAFoyer() {
        // When
        Bloc result = blocService.ajouterBlocEtAffecterAFoyer(
                Bloc.builder()
                        .nomBloc("AddAndFoyer")
                        .capaciteBloc(30)
                        .build(),
                TEST_FOYER_NAME
        );

        // Then
        assertNotNull(result.getFoyer(), "Foyer should be set");
        assertEquals(TEST_FOYER_NAME, result.getFoyer().getNomFoyer(),
                "Foyer name should match");
    }

    @Test
    @Order(5)
    void testDeleteOperations() {
        // Given
        Bloc tempBloc = blocService.addOrUpdate(
                Bloc.builder()
                        .nomBloc("ToDelete")
                        .capaciteBloc(5)
                        .build()
        );
        Long blocId = tempBloc.getIdBloc();

        // When/Then for deleteById
        blocService.deleteById(blocId);
        assertThrows(NoSuchElementException.class,
                () -> blocService.findById(blocId),
                "Bloc should be deleted");
    }

    @AfterAll
    void tearDown() {

            blocService.deleteById(bloc.getIdBloc());

    }
}
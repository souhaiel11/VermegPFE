package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tn.esprit.spring.Services.Bloc.BlocService;
import tn.esprit.spring.dao.repositories.FoyerRepository;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.entities.Foyer;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlocServiceTest {

    @Autowired private BlocService blocService;
    @Autowired private FoyerRepository foyerRepository;

    private Bloc bloc;
    private Foyer foyer;

    @BeforeAll
    void setUp() {
        // 1) on crée et persiste un foyer
        foyer = foyerRepository.save(
                Foyer.builder()
                        .nomFoyer("FoyerTest")
                        .capaciteFoyer(100)
                        .build()
        );

        // 2) on crée un bloc avec 2 chambres
        Chambre c1 = Chambre.builder().numeroChambre(101).typeC(TypeChambre.SIMPLE).build();
        Chambre c2 = Chambre.builder().numeroChambre(102).typeC(TypeChambre.DOUBLE).build();
        bloc = blocService.addOrUpdate(
                Bloc.builder()
                        .nomBloc("BlocAlpha")
                        .capaciteBloc(50)
                        .chambres(Arrays.asList(c1, c2))
                        .build()
        );
    }

    @Test
    @Order(1)
    void testAddOrUpdate2_cascadeChambres() {
        // Given - Create a new bloc with existing chambres
        Bloc newBloc = Bloc.builder()
                .nomBloc("Cascade")
                .capaciteBloc(10)
                .chambres(bloc.getChambres())
                .build();

        // When - Save with cascade
        Bloc savedBloc = blocService.addOrUpdate(newBloc);

        // Then - Verify persistence and relationships
        assertNotNull(savedBloc.getIdBloc(), "Bloc should have an ID after save");
        assertNotNull(savedBloc.getChambres(), "Chambres list should not be null");

        savedBloc.getChambres().forEach(chambre -> {
            assertNotNull(chambre.getBloc(), "Chambre should reference a bloc");
            assertEquals(savedBloc.getIdBloc(), chambre.getBloc().getIdBloc(),
                    "Chambre should reference the parent bloc");
        });
    }



    @Test @Order(3)
    void testAffecterBlocAFoyer() {
        var affected = blocService.affecterBlocAFoyer("BlocAlpha", "FoyerTest");
        assertEquals("FoyerTest", affected.getFoyer().getNomFoyer());
    }

    @Test @Order(4)
    void testAjouterBlocEtSesChambres() {
        Chambre c3 = Chambre.builder().numeroChambre(201).typeC(TypeChambre.TRIPLE).build();
        var b2 = blocService.ajouterBlocEtSesChambres(
                Bloc.builder()
                        .nomBloc("AddChambres")
                        .capaciteBloc(20)
                        .chambres(List.of(c3))
                        .build()
        );
        assertNotNull(b2.getIdBloc());
        assertEquals(1, b2.getChambres().size());
    }

    @Test @Order(5)
    void testAjouterBlocEtAffecterAFoyer() {
        var b3 = blocService.ajouterBlocEtAffecterAFoyer(
                Bloc.builder()
                        .nomBloc("AddAndFoyer")
                        .capaciteBloc(30)
                        .build(),
                "FoyerTest"
        );
        assertEquals("FoyerTest", b3.getFoyer().getNomFoyer());
    }

    @Test @Order(6)
    void testDeleteById_and_delete() {
        var tmp = blocService.addOrUpdate(
                Bloc.builder().nomBloc("ToDelete").capaciteBloc(5).build()
        );
        // deleteById
        blocService.deleteById(tmp.getIdBloc());
        assertThrows(NoSuchElementException.class, () -> blocService.findById(tmp.getIdBloc()));

    }

    @AfterAll
    void tearDown() {
        blocService.deleteById(bloc.getIdBloc());
    }
}

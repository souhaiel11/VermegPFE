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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BlocServiceTest {

    @Autowired
    private BlocService blocService;

    private Bloc bloc;

    @BeforeAll
    public void initBloc() {
        Chambre ch1 = Chambre.builder().numeroChambre(101).typeC(TypeChambre.SIMPLE).build();
        Chambre ch2 = Chambre.builder().numeroChambre(102).typeC(TypeChambre.DOUBLE).build();

        bloc = Bloc.builder()
                .nomBloc("Bloc Alpha")
                .capaciteBloc(150)
                .chambres(Arrays.asList(ch1, ch2))
                .build();

        bloc = blocService.addOrUpdate(bloc);
    }


    @Test
    @Order(1)
    public void testaddOrUpdate2_IdBlocPresent() {
        assertNotNull(bloc.getIdBloc());
    }

    @Test
    @Order(2)
    public void testaddOrUpdate2_NomBlocNonVide() {
        assertEquals("Bloc Alpha", bloc.getNomBloc());
    }

    @Test
    @Order(3)
    public void testaddOrUpdate2_CapaciteBloc() {
        assertTrue(bloc.getCapaciteBloc() <= 150);
    }

    @Test
    @Order(4)
    public void testChambresAjoutees() {
        assertNotNull(bloc.getChambres());
        assertEquals(2, bloc.getChambres().size());
        assertTrue(bloc.getChambres().stream().anyMatch(c -> c.getNumeroChambre() == 101));
    }

    @AfterAll
    public void deleteBloc() {

        blocService.deleteById(bloc.getIdBloc());
    }
}

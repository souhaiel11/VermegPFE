package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.Services.Bloc.BlocService;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.BlocRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
 class BlocServiceMockTest {

    @Mock
    private BlocRepository blocRepository;

    @InjectMocks
    private BlocService blocService;

    private static Bloc bloc;

    @BeforeAll
    public static void init() {
        Chambre ch1 = Chambre.builder().numeroChambre(101).typeC(TypeChambre.SIMPLE).build();
        Chambre ch2 = Chambre.builder().numeroChambre(102).typeC(TypeChambre.DOUBLE).build();

        bloc = Bloc.builder()
                .nomBloc("Bloc Alpha")
                .capaciteBloc(150)
                .chambres(Arrays.asList(ch1, ch2))
                .build();
    }

    @Test
    @Order(1)
     void BlocServiceMockTest_createBloc() {
        when(blocRepository.save(Mockito.any(Bloc.class))).thenReturn(bloc);

        Bloc savedBloc = blocService.addOrUpdate(bloc);
        assertEquals("Bloc Alpha", savedBloc.getNomBloc());
    }

    @Test
    @Order(2)
     void BlocServiceMockTest_getAllBloc() {
        when(blocRepository.findAll()).thenReturn(List.of(bloc));

        List<Bloc> blocs = blocService.findAll();
        assertNotNull(blocs);
        assertEquals(1, blocs.size());
        assertEquals("Bloc Alpha", blocs.get(0).getNomBloc());
    }

    @Test
    @Order(3)
     void BlocServiceMockTest_findById() {
        when(blocRepository.findById(120L)).thenReturn(Optional.of(bloc));

        Bloc result = blocService.findById(120L);
        assertNotNull(result);
        assertEquals("Bloc Alpha", result.getNomBloc());
    }
}

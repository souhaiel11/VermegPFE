package tn.esprit.spring;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.Services.Bloc.BlocService;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.repositories.BlocRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BlocRepoMockTest {
    @Autowired
    private BlocRepository blocRepository;
    @Autowired
    private BlocService blocService;



    @Test
    @Order(1)
    public void blocReposetory_testadd_SaveBloc () {
        //Arrange
        Bloc bloc = Bloc.builder().nomBloc("Bloc Alpha").capaciteBloc(150).build();
        //Act
        Bloc savedBloc = blocRepository.save(bloc);
        //Assert
        Assertions.assertNotNull(savedBloc);
        assertTrue(savedBloc.getIdBloc() > 0);


    }

    @Test
    public void blocReposetory_test_GetAllBloc () {
        //Arrange
        Bloc bloc = Bloc.builder().nomBloc("Bloc Alpha").capaciteBloc(150).build();
        Bloc bloc1 = Bloc.builder().nomBloc("Bloc Beta").capaciteBloc(160).build();
        blocService.addOrUpdate(bloc);
        //List<Bloc> blocsList = blocRepository.findAll();
       List<Bloc> blocsList = blocService.findAll();
        Assertions.assertNotNull(blocsList);
        assertEquals(2, blocsList.size());

    }







    @BeforeEach
    public void cleanDatabase() {
        blocRepository.deleteAll(); // 🔄 Vide la table t_bloc avant chaque test
    }

}








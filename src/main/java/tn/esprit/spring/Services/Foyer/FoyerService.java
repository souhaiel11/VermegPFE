package tn.esprit.spring.Services.Foyer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.entities.Universite;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.FoyerRepository;
import tn.esprit.spring.dao.repositories.UniversiteRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Transactional
public class FoyerService implements IFoyerService {

    private final FoyerRepository foyerRepository;
    private final UniversiteRepository universiteRepository;
    private final BlocRepository blocRepository;

    @Override
    public Foyer addOrUpdate(Foyer foyer) {
        if (foyer == null) {
            throw new IllegalArgumentException("Foyer cannot be null");
        }
        return foyerRepository.save(foyer);
    }

    @Override
    public List<Foyer> findAll() {
        return foyerRepository.findAll();
    }

    @Override
    public Foyer findById(long id) {
        return foyerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Foyer not found with id: " + id));
    }

    @Override
    public void deleteById(long id) {
        if (!foyerRepository.existsById(id)) {
            throw new NoSuchElementException("Foyer not found with id: " + id);
        }
        foyerRepository.deleteById(id);
    }

    @Override
    public void delete(Foyer foyer) {
        if (foyer == null) {
            throw new IllegalArgumentException("Foyer cannot be null");
        }
        foyerRepository.delete(foyer);
    }

    @Override
    public Universite affecterFoyerAUniversite(long idFoyer, String nomUniversite) {
        if (nomUniversite == null || nomUniversite.isBlank()) {
            throw new IllegalArgumentException("University name cannot be null or empty");
        }

        Foyer foyer = findById(idFoyer);
        Universite universite = universiteRepository.findByNomUniversite(nomUniversite);

        if (universite == null) {
            throw new NoSuchElementException("University not found with name: " + nomUniversite);
        }

        universite.setFoyer(foyer);
        return universiteRepository.save(universite);
    }

    @Override
    public Foyer ajouterFoyerEtAffecterAUniversite(Foyer foyer, long idUniversite) {
        if (foyer == null) {
            throw new IllegalArgumentException("Foyer cannot be null");
        }

        List<Bloc> blocs = foyer.getBlocs();
        Foyer savedFoyer = foyerRepository.save(foyer);

        Universite universite = universiteRepository.findById(idUniversite)
                .orElseThrow(() -> new NoSuchElementException("University not found with id: " + idUniversite));

        if (blocs != null) {
            blocs.forEach(bloc -> {
                bloc.setFoyer(savedFoyer);
                blocRepository.save(bloc);
            });
        }

        universite.setFoyer(savedFoyer);
        universiteRepository.save(universite);
        return savedFoyer;
    }

    @Override
    public Foyer ajoutFoyerEtBlocs(Foyer foyer) {
        if (foyer == null) {
            throw new IllegalArgumentException("Foyer cannot be null");
        }

        List<Bloc> blocs = foyer.getBlocs();
        Foyer savedFoyer = foyerRepository.save(foyer);

        if (blocs != null) {
            blocs.forEach(bloc -> {
                bloc.setFoyer(savedFoyer);
                blocRepository.save(bloc);
            });
        }

        return savedFoyer;
    }

    @Override
    public Universite affecterFoyerAUniversite(long idFoyer, long idUniversite) {
        Universite universite = universiteRepository.findById(idUniversite)
                .orElseThrow(() -> new NoSuchElementException("University not found with id: " + idUniversite));

        Foyer foyer = foyerRepository.findById(idFoyer)
                .orElseThrow(() -> new NoSuchElementException("Foyer not found with id: " + idFoyer));

        universite.setFoyer(foyer);
        return universiteRepository.save(universite);
    }

    @Override
    public Universite desaffecterFoyerAUniversite(long idUniversite) {
        Universite universite = universiteRepository.findById(idUniversite)
                .orElseThrow(() -> new NoSuchElementException("University not found with id: " + idUniversite));

        universite.setFoyer(null);
        return universiteRepository.save(universite);
    }
}
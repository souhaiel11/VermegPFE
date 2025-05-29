package tn.esprit.spring.Services.Bloc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dao.entities.Bloc;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Foyer;
import tn.esprit.spring.dao.repositories.BlocRepository;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.FoyerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class BlocService implements IBlocService {

    private final ChambreRepository chambreRepository;
    private final BlocRepository blocRepository;
    private final FoyerRepository foyerRepository;

    @Override
    public Bloc addOrUpdate2(Bloc bloc) {
        if (bloc == null) {
            throw new IllegalArgumentException("Bloc cannot be null");
        }

        List<Chambre> chambres = bloc.getChambres();
        if (chambres != null) {
            chambres.forEach(chambre -> {
                chambre.setBloc(bloc);
                chambreRepository.save(chambre);
            });
        }
        return blocRepository.save(bloc);
    }

    @Override
    public Bloc addOrUpdate(Bloc bloc) {
        if (bloc == null) {
            throw new IllegalArgumentException("Bloc cannot be null");
        }

        List<Chambre> chambres = bloc.getChambres();
        bloc.setChambres(null);
        var savedBloc = blocRepository.save(bloc);

        if (chambres != null) {
            chambres.forEach(chambre -> chambre.setBloc(savedBloc));
            savedBloc.setChambres(chambres);
            blocRepository.save(savedBloc);
        }
        return savedBloc;
    }

    @Override
    public List<Bloc> findAll() {
        return blocRepository.findAll();
    }

    @Override
    public Bloc findById(long id) {
        return blocRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bloc not found with id: " + id));
    }

    @Override
    public void deleteById(long id) {
        var bloc = blocRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bloc not found with id: " + id));

        chambreRepository.deleteAll(bloc.getChambres());
        blocRepository.delete(bloc);
    }

    @Override
    public void delete(Bloc bloc) {
        if (bloc == null) {
            throw new IllegalArgumentException("Bloc cannot be null");
        }
        chambreRepository.deleteAll(bloc.getChambres());
        blocRepository.delete(bloc);
    }

    @Override
    public Bloc affecterChambresABloc(List<Long> numChambre, String nomBloc) {
        if (nomBloc == null || nomBloc.isBlank()) {
            throw new IllegalArgumentException("NomBloc cannot be null or empty");
        }

        var bloc = blocRepository.findByNomBloc(nomBloc);
        if (bloc == null) {
            throw new NoSuchElementException("Bloc not found with name: " + nomBloc);
        }

        var chambres = new ArrayList<Chambre>();
        if (numChambre != null) {
            numChambre.forEach(num -> {
                var chambre = chambreRepository.findByNumeroChambre(num);
                if (chambre != null) {
                    chambres.add(chambre);
                }
            });
        }

        chambres.forEach(chambre -> {
            chambre.setBloc(bloc);
            chambreRepository.save(chambre);
        });

        return bloc;
    }

    @Override
    public Bloc affecterBlocAFoyer(String nomBloc, String nomFoyer) {
        if (nomBloc == null || nomBloc.isBlank()) {
            throw new IllegalArgumentException("NomBloc cannot be null or empty");
        }
        if (nomFoyer == null || nomFoyer.isBlank()) {
            throw new IllegalArgumentException("NomFoyer cannot be null or empty");
        }

        var bloc = blocRepository.findByNomBloc(nomBloc);
        if (bloc == null) {
            throw new NoSuchElementException("Bloc not found with name: " + nomBloc);
        }

        var foyer = foyerRepository.findByNomFoyer(nomFoyer);
        if (foyer == null) {
            throw new NoSuchElementException("Foyer not found with name: " + nomFoyer);
        }

        bloc.setFoyer(foyer);
        return blocRepository.save(bloc);
    }

    @Override
    public Bloc ajouterBlocEtSesChambres(Bloc bloc) {
        if (bloc == null) {
            throw new IllegalArgumentException("Bloc cannot be null");
        }

        var chambres = bloc.getChambres();
        if (chambres != null) {
            chambres.forEach(chambre -> {
                chambre.setBloc(bloc);
                chambreRepository.save(chambre);
            });
        }
        return blocRepository.save(bloc);
    }

    @Override
    public Bloc ajouterBlocEtAffecterAFoyer(Bloc bloc, String nomFoyer) {
        if (bloc == null) {
            throw new IllegalArgumentException("Bloc cannot be null");
        }
        if (nomFoyer == null || nomFoyer.isBlank()) {
            throw new IllegalArgumentException("NomFoyer cannot be null or empty");
        }

        var foyer = foyerRepository.findByNomFoyer(nomFoyer);
        if (foyer == null) {
            throw new NoSuchElementException("Foyer not found with name: " + nomFoyer);
        }

        bloc.setFoyer(foyer);
        return blocRepository.save(bloc);
    }



}
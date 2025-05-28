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
import java.util.Optional;

@Service
@AllArgsConstructor
public class BlocService implements IBlocService {

    ChambreRepository chambreRepository;
    BlocRepository blocRepository;
    FoyerRepository foyerRepository;

    @Override
    public Bloc addOrUpdate2(Bloc b) {
        List<Chambre> chambres = b.getChambres();
        if (chambres != null) {
            for (Chambre c : chambres) {
                c.setBloc(b);
                chambreRepository.save(c);
            }
        }
        return b;
    }

    @Override
    public Bloc addOrUpdate(Bloc b) {
        List<Chambre> chambres = b.getChambres();
        b.setChambres(null);
        Bloc savedBloc = blocRepository.save(b);

        if (chambres != null) {
            for (Chambre chambre : chambres) {
                chambre.setBloc(savedBloc);
                chambreRepository.save(chambre);
            }
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
                .orElseThrow(() -> new IllegalArgumentException("Bloc non trouvé pour l'id: " + id));
    }

    @Override
    public void deleteById(long id) {
        Bloc b = blocRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bloc non trouvé pour suppression, id: " + id));

        chambreRepository.deleteAll(b.getChambres());
        blocRepository.delete(b);
    }

    @Override
    public void delete(Bloc b) {
        if (b.getChambres() != null) {
            chambreRepository.deleteAll(b.getChambres());
        }
        blocRepository.delete(b);
    }

    @Override
    public Bloc affecterChambresABloc(List<Long> numChambre, String nomBloc) {
        Bloc b = blocRepository.findByNomBloc(nomBloc);
        List<Chambre> chambres = new ArrayList<>();

        for (Long nu : numChambre) {
            Chambre chambre = chambreRepository.findByNumeroChambre(nu);
            if (chambre != null) {
                chambres.add(chambre);
            }
        }

        for (Chambre cha : chambres) {
            cha.setBloc(b);
            chambreRepository.save(cha);
        }

        return b;
    }

    @Override
    public Bloc affecterBlocAFoyer(String nomBloc, String nomFoyer) {
        Bloc b = blocRepository.findByNomBloc(nomBloc);
        Foyer f = foyerRepository.findByNomFoyer(nomFoyer);
        b.setFoyer(f);
        return blocRepository.save(b);
    }

    @Override
    public Bloc ajouterBlocEtSesChambres(Bloc b) {
        for (Chambre c : b.getChambres()) {
            c.setBloc(b);
            chambreRepository.save(c);
        }
        return b;
    }

    @Override
    public Bloc ajouterBlocEtAffecterAFoyer(Bloc b, String nomFoyer) {
        Foyer f = foyerRepository.findByNomFoyer(nomFoyer);
        b.setFoyer(f);
        return blocRepository.save(b);
    }
}

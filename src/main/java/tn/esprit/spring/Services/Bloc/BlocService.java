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

@Service
@AllArgsConstructor
public class BlocService implements IBlocService {
    BlocRepository repo;
    ChambreRepository chambreRepository;
    BlocRepository blocRepository;
    FoyerRepository foyerRepository;

    @Override
    public Bloc addOrUpdate2(Bloc b) { //Cascade
        List<Chambre> chambres = b.getChambres();
        for (Chambre c : chambres) {
            c.setBloc(b);
            chambreRepository.save(c);
        }
        return b;
    }

    @Override
    public Bloc addOrUpdate(Bloc b) {
        List<Chambre> chambres = b.getChambres();

        // Ne pas essayer de sauvegarder directement les chambres si le bloc n'a pas encore été persisté
        b.setChambres(null); // Temporairement désassocier les chambres

        Bloc savedBloc = blocRepository.save(b); // Bloc persisté avec ID

        // Associer le bloc persisté à chaque chambre
        if (chambres != null) {
            for (Chambre chambre : chambres) {
                chambre.setBloc(savedBloc);
            }
            savedBloc.setChambres(chambres); // Réassocier la liste au bloc
            blocRepository.save(savedBloc);  // Mise à jour avec les chambres
        }

        return savedBloc;
    }


    @Override
    public List<Bloc> findAll() {
        return repo.findAll();
    }

    @Override
    public Bloc findById(long id) {
        return repo.findById(id).get();
    }

    @Override
    public void deleteById(long id) {
        Bloc b =repo.findById(id).get();
        chambreRepository.deleteAll(b.getChambres());
        repo.delete(b);
    }

    @Override
    public void delete(Bloc b) {
        chambreRepository.deleteAll(b.getChambres());
        repo.delete(b);
    }

    @Override
    public Bloc affecterChambresABloc(List<Long> numChambre, String nomBloc) {
        //1
        Bloc b = repo.findByNomBloc(nomBloc);
        List<Chambre> chambres = new ArrayList<>();
        for (Long nu : numChambre) {
            Chambre chambre = chambreRepository.findByNumeroChambre(nu);
            chambres.add(chambre);
        }
        // Keyword (2ème méthode)
        //2 Parent==>Chambre  Child==> Bloc
        for (Chambre cha : chambres) {
            //3 On affecte le child au parent
            cha.setBloc(b);
            //4 save du parent
            chambreRepository.save(cha);
        }
        return b;
    }

    @Override
    public Bloc affecterBlocAFoyer(String nomBloc, String nomFoyer) {
        Bloc b = blocRepository.findByNomBloc(nomBloc); //Parent
        Foyer f = foyerRepository.findByNomFoyer(nomFoyer); //Child
        //On affecte le child au parent
        b.setFoyer(f);
        return blocRepository.save(b);
    }

    @Override
    public Bloc ajouterBlocEtSesChambres(Bloc b) {
        // Activer l'option cascade au niveau parent
        for (Chambre c : b.getChambres()) {
            c.setBloc(b);
            chambreRepository.save(c);
        }
        return b;
    }

    @Override
    public Bloc ajouterBlocEtAffecterAFoyer(Bloc b, String nomFoyer) {
        // Foyer: child , Bloc: Parent
        Foyer f= foyerRepository.findByNomFoyer(nomFoyer);
        b.setFoyer(f);
        return blocRepository.save(b);
    }



}
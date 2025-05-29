package tn.esprit.spring.Services.Reservation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.dao.entities.Chambre;
import tn.esprit.spring.dao.entities.Etudiant;
import tn.esprit.spring.dao.entities.Reservation;
import tn.esprit.spring.dao.entities.TypeChambre;
import tn.esprit.spring.dao.repositories.ChambreRepository;
import tn.esprit.spring.dao.repositories.EtudiantRepository;
import tn.esprit.spring.dao.repositories.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final ChambreRepository chambreRepository;
    private final EtudiantRepository etudiantRepository;

    @Override
    public Reservation addOrUpdate(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation findById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found with id: " + id));
    }

    @Override
    public void deleteById(String id) {
        if (!reservationRepository.existsById(id)) {
            throw new NoSuchElementException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
    }

    @Override
    public void delete(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        reservationRepository.delete(reservation);
    }

    private LocalDate getDateDebutAU() {
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            return LocalDate.of(Integer.parseInt("20" + (year - 1)), 9, 15);
        }
        return LocalDate.of(Integer.parseInt("20" + year), 9, 15);
    }

    private LocalDate getDateFinAU() {
        int year = LocalDate.now().getYear() % 100;
        if (LocalDate.now().getMonthValue() <= 7) {
            return LocalDate.of(Integer.parseInt("20" + year), 6, 30);
        }
        return LocalDate.of(Integer.parseInt("20" + (year + 1)), 6, 30);
    }

    @Override
    public Reservation ajouterReservationEtAssignerAChambreEtAEtudiant(Long numChambre, long cin) {
        Chambre chambre = chambreRepository.findByNumeroChambre(numChambre);
        if (chambre == null) {
            throw new NoSuchElementException("Chambre not found with number: " + numChambre);
        }

        Etudiant etudiant = etudiantRepository.findByCin(cin);
        if (etudiant == null) {
            throw new NoSuchElementException("Etudiant not found with CIN: " + cin);
        }

        int nombreReservations = chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(
                chambre.getIdChambre(), getDateDebutAU(), getDateFinAU());

        int capaciteMaximale = switch (chambre.getTypeC()) {
            case SIMPLE -> 1;
            case DOUBLE -> 2;
            case TRIPLE -> 3;
        };

        if (nombreReservations >= capaciteMaximale) {
            log.warn("Chambre {} is full! Capacity: {}", chambre.getTypeC(), capaciteMaximale);
            throw new IllegalStateException("Chambre is full");
        }

        String idReservation = String.format("%d/%d-%s-%d-%d",
                getDateDebutAU().getYear(),
                getDateFinAU().getYear(),
                chambre.getBloc().getNomBloc(),
                chambre.getNumeroChambre(),
                etudiant.getCin());

        Reservation reservation = Reservation.builder()
                .estValide(true)
                .anneeUniversitaire(LocalDate.now())
                .idReservation(idReservation)
                .build();

        reservation.getEtudiants().add(etudiant);
        reservation = reservationRepository.save(reservation);
        chambre.getReservations().add(reservation);
        chambreRepository.save(chambre);

        return reservation;
    }

    @Override
    public long getReservationParAnneeUniversitaire(LocalDate debutAnnee, LocalDate finAnnee) {
        if (debutAnnee == null || finAnnee == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        return reservationRepository.countByAnneeUniversitaireBetween(debutAnnee, finAnnee);
    }

    @Override
    public String annulerReservation(long cinEtudiant) {
        Reservation reservation = reservationRepository.findByEtudiantsCinAndEstValide(cinEtudiant, true);
        if (reservation == null) {
            throw new NoSuchElementException("No active reservation found for student with CIN: " + cinEtudiant);
        }

        Chambre chambre = chambreRepository.findByReservationsIdReservation(reservation.getIdReservation());
        if (chambre != null) {
            chambre.getReservations().remove(reservation);
            chambreRepository.save(chambre);
        }

        reservationRepository.delete(reservation);
        return String.format("Reservation %s cancelled successfully", reservation.getIdReservation());
    }

    @Override
    public void affectReservationAChambre(String idRes, long idChambre) {
        Reservation reservation = reservationRepository.findById(idRes)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found with id: " + idRes));

        Chambre chambre = chambreRepository.findById(idChambre)
                .orElseThrow(() -> new NoSuchElementException("Chambre not found with id: " + idChambre));

        chambre.getReservations().add(reservation);
        chambreRepository.save(chambre);
    }

    @Override
    public void deaffectReservationAChambre(String idRes, long idChambre) {
        Reservation reservation = reservationRepository.findById(idRes)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found with id: " + idRes));

        Chambre chambre = chambreRepository.findById(idChambre)
                .orElseThrow(() -> new NoSuchElementException("Chambre not found with id: " + idChambre));

        chambre.getReservations().remove(reservation);
        chambreRepository.save(chambre);
    }

    @Override
    public void annulerReservations() {
        LocalDate dateDebutAU = getDateDebutAU();
        LocalDate dateFinAU = getDateFinAU();

        reservationRepository.findByEstValideAndAnneeUniversitaireBetween(true, dateDebutAU, dateFinAU)
                .forEach(reservation -> {
                    reservation.setEstValide(false);
                    reservationRepository.save(reservation);
                    log.info("Reservation {} cancelled automatically", reservation.getIdReservation());
                });
    }
}
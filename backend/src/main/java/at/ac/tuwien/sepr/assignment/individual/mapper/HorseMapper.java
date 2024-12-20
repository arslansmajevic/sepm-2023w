package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;
import at.ac.tuwien.sepr.assignment.individual.entity.Race;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Convert a horse entity object to a {@link HorseListDto}.
   * The given map of owners needs to contain the owner of {@code horse}.
   *
   * @param horse the horse to convert
   * @param breeds a map of breeds identified by their id, required for mapping horses
   * @return the converted {@link HorseListDto}
   */
  public HorseListDto entityToListDto(Horse horse, Map<Long, BreedDto> breeds) {
    LOG.trace("entityToListDto({})", horse);
    if (horse == null) {
      return null;
    }

    if (horse.getBreedId() == 0) {
      return new HorseListDto(
              horse.getId(),
              horse.getName(),
              horse.getSex(),
              horse.getDateOfBirth(),
              null
      );
    }

    var breed = Optional.of(breeds.get(horse.getBreedId()))
        .orElseThrow(() -> new FatalException(
            "Saved horse with id " + horse.getId() + " refers to non-existing breed with id " + horse.getBreedId()));

    return new HorseListDto(
        horse.getId(),
        horse.getName(),
        horse.getSex(),
        horse.getDateOfBirth(),
        breed
    );
  }

  /**
   * Convert a horse entity object to a {@link HorseListDto}.
   * The given map of owners needs to contain the owner of {@code horse}.
   *
   * @param horse the horse to convert
   * @return the converted {@link HorseListDto}
   */
  public HorseDetailDto entityToDetailDto(Horse horse, Map<Long, BreedDto> breeds) {
    LOG.trace("entityToDto({})", horse);
    if (horse == null) {
      return null;
    }

    var breed = horse.getBreedId() != null && horse.getBreedId() != 0 ? Optional.of(breeds.get(horse.getBreedId()))
        .orElseThrow(() -> new FatalException(
            "Saved horse with id " + horse.getId() + " refers to non-existing breed with id " + horse.getBreedId()))
            : null;

    return new HorseDetailDto(
        horse.getId(),
        horse.getName(),
        horse.getSex(),
        horse.getDateOfBirth(),
        horse.getHeight(),
        horse.getWeight(),
        breed
    );
  }

  public TournamentDetailParticipantDto[] entityToTournamentDetailParticipantDto(Collection<Horse> horses,
                                                                                 Collection<Race> races,
                                                                                 Collection<Participation> participations) {

    Collection<TournamentDetailParticipantDto> resultCollection = new ArrayList<>();

    for (Horse h : horses) {
      for (Race r : races) {
        if (Objects.equals(h.getId(), r.getFirstPlace()) || Objects.equals(h.getId(), r.getSecondPlace())) {
          // r.getRound()
          for (Participation p : participations) {
            if (Objects.equals(h.getId(), p.getHorseId())) {
              // p.getEntry()
              resultCollection.add(new TournamentDetailParticipantDto(
                      h.getId(),
                      h.getName(),
                      h.getDateOfBirth(),
                      p.getEntry(),
                      Math.toIntExact(r.getRound())
              ));
            }
          }
        }
      }


    }


    return resultCollection.toArray(new TournamentDetailParticipantDto[0]);
  }
}

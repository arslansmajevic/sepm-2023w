package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;
import at.ac.tuwien.sepr.assignment.individual.entity.Race;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import java.util.Collection;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {

  /**
   * Get the horses that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in horse.
   *
   * @param searchParameters the parameters to use in searching.
   * @return the horses where all given parameters match.
   */
  Collection<Horse> search(HorseSearchDto searchParameters);


  /**
   * Update the horse with the ID given in {@code horse}
   *  with the data given in {@code horse}
   *  in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse update(HorseDetailDto horse) throws NotFoundException;

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Creates a new horse and assigns it a new id.
   *
   * @param horse the horse
   * @return the newly created horse
   */
  Horse create(HorseDetailDto horse);

  String delete(long id, String name);

  Collection<Horse> getTournamentHorses(Long tournamentId);

  Collection<Race> getHorseRaces(Long tournamentId);

  Collection<Participation> getTournamentParticipations(Long tournamentId);
}

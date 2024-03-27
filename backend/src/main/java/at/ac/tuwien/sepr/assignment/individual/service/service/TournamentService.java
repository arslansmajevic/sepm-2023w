package at.ac.tuwien.sepr.assignment.individual.service.service;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

public interface TournamentService {

  /**
   * Search for tournaments in the persistent data store matching the provided search criteria.
   *
   * @param searchDto the search criteria to use in filtering tournaments.
   * @return a stream of tournaments where the given criteria match.
   */
  Stream<TournamentListDto> search(TournamentSearchDto searchDto);

  /**
   * Create a new tournament entry in the persistent data store.
   *
   * @param tournament the tournament data to create
   * @return the created tournament
   * @throws ValidationException if the provided tournament data is invalid
   */
  TournamentDetailDto create(TournamentCreateDto tournament) throws ValidationException;
}

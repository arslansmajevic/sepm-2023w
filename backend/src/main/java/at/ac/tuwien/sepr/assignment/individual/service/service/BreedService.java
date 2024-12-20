package at.ac.tuwien.sepr.assignment.individual.service.service;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Service for working with breeds.
 */
public interface BreedService {
  /**
   * Retrieve all breeds from the persistent data store.
   *
   * @return a stream of all stored breeds.
   */
  Stream<BreedDto> allBreeds();

  /**
   * Retrieve all stored breeds, that have one of the given IDs.
   * Note that if for one ID no breed is found, this method does not throw an error.
   *
   * @param breedIds the set of IDs to find breeds for.
   * @return a stream of all found breeds with an ID in {@code breedIds}
   */
  Stream<BreedDto> findBreedsByIds(Set<Long> breedIds);

  /**
   * Retrieve all stored breeds, that match the given parameters.
   * The parameters may include a limit on the amount of results to return.
   *
   * @param searchParams parameters to search breeds by
   * @return a stream of breeds matching the parameters
   */
  Stream<BreedDto> search(BreedSearchDto searchParams);

  /**
   * Create a new breed entry in the persistent data store.
   * Throws a {@link ValidationException} if the provided horse data is invalid.
   *
   * @param horse the breed data to create
   * @return the created breed
   * @throws ValidationException if the provided horse data is invalid
   */
  BreedDto create(BreedDto horse) throws ValidationException;
}

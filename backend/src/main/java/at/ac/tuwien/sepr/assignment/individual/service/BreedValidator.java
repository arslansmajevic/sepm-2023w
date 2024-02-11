package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class BreedValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void validateForCreate(BreedDto breed, Stream<BreedDto> breedDtoStream) throws ValidationException {
    LOG.trace("validateForCreate({})", breed);
    breed.name();

    List<String> validationErrors = new ArrayList<>();

    if (breed.name() == null || breed.name().equals("")) {
      validationErrors.add("No name given!");
      throw new ValidationException("Validation of the create request failed", validationErrors);
    }

    Optional<BreedDto> existingBreedOptional = breedDtoStream.filter(dto -> dto.name().equals(breed.name()))
            .findFirst();

    if (existingBreedOptional.isPresent()) {
      BreedDto found = existingBreedOptional.get();
      validationErrors.add("Breed with the same name already exists with ID: " + found.id());
      throw new ValidationException("Validation of the create request failed", validationErrors);
    }
  }
}

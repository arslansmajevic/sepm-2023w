package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void validateForUpdate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }

    // TODO this is not complete…

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  public void validateCreateNewHorse(HorseDetailDto horse, Stream<BreedDto> existingBreed) throws ValidationException{
    LOG.trace("validateCreateNewHorse({})", horse);

    List<String> validationErrors = new ArrayList<>();

    if(horse.name() == null){
      validationErrors.add("Horse name was not defined");
    }

    if(horse.sex() == null){
      validationErrors.add("Horse sex was not defined");
    }
    else{
      if(!horse.sex().equals(Sex.FEMALE)){
        if(!horse.sex().equals(Sex.MALE)){
          validationErrors.add("Horse sex is an unvalid sex");
        }
      }
    }

    if(horse.dateOfBirth() == null){
      validationErrors.add("Horse date of birth is not defined");
    }

    if(horse.height() == 0.0){
      validationErrors.add("Horse height can not be 0 / is not defines");
    }

    if(horse.weight() == 0.0){
      validationErrors.add("Horse weight can not be 0 / is not defined");
    }

    if(existingBreed != null){
      int size = (int) existingBreed.count();
      if(size > 1){ // should not ever happen
        validationErrors.add("There are too many breeds with this breed Id" + horse.breed().id());
      }

      if(size == 0){
        validationErrors.add("No such breed Id: " + horse.breed().id());
      }
    }

    if(!validationErrors.isEmpty()){
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }

}

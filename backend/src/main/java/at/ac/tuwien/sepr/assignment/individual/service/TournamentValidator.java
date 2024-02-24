package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.service.HorseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class TournamentValidator {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final int NUMBER_OF_PARTICIPANTS = 8;
  private final HorseService horseService;

  public TournamentValidator(HorseService horseService) {
    this.horseService = horseService;
  }

  public void validateCreateNewTournament(TournamentCreateDto tournament) throws ValidationException {
    LOG.trace("validateCreateNewTournament({})", tournament);

    List<String> validationErrors = new ArrayList<>();

    if (tournament.participants() != null) {
      if (tournament.participants().length != NUMBER_OF_PARTICIPANTS) {
        validationErrors.add("The tournament does not have 8 participants");
      }

      int counter = 1;
      for (HorseSelectionDto participantDto : tournament.participants()) {
        if (participantDto != null) {
          try {
            horseService.getById(participantDto.id());
          } catch (NotFoundException notFoundException) {
            validationErrors.add(String.format("The horse with id %d is not present",
                    participantDto.id()));
          } catch (NullPointerException nullPointerException) {
            validationErrors.add(String.format("Horse at position %d with no id",
                    counter));
          }
        } else {
          validationErrors.add(String.format("Horse at position %d is not defined",
                  counter));
        }
        counter++;

      }
    } else {
      validationErrors.add("No participants were defined");
    }

    if (tournament.endDate() == null) {
      validationErrors.add("The tournament does not have an end date");
    } else {
      if (tournament.startDate() == null) {
        validationErrors.add("The tournament does not have a start date");
      } else {
        if (!tournament.startDate().isBefore(tournament.endDate())) {
          validationErrors.add("The start date is after the end date");
        }
      }
    }

    if (tournament.name() == null) {
      validationErrors.add("The name was not specified");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }
  }
}

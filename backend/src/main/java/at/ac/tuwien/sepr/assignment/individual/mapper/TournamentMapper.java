package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class TournamentMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public TournamentListDto entityToListDto(Tournament tournament) {
    LOG.trace("entityToListDto({})", tournament);

    if (tournament == null) {
      return null;
    }

    return new TournamentListDto(
            tournament.getId(),
            tournament.getName(),
            tournament.getDateOfStart(),
            tournament.getDateOfEnd()
    );
  }

}

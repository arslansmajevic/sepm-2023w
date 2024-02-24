package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = TournamentEndpoint.BASE_PATH)
public class TournamentEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/tournaments";

  private final TournamentService service;

  public TournamentEndpoint(TournamentService tournamentService) {
    this.service = tournamentService;
  }

  @GetMapping
  public Stream<TournamentListDto> searchTournaments(TournamentSearchDto searchDto) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchDto);

    return service.search(searchDto);
  }

  @PostMapping
  public TournamentDetailDto create(@RequestBody TournamentCreateDto newTournament) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("request parameters: {}", newTournament);

    try {
      return service.create(newTournament);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Parsed tournament failed validation", v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    }
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}

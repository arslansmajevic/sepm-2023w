package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.service.BreedService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = BreedEndpoint.BASE_PATH)
public class BreedEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  public static final String BASE_PATH = "/breeds";

  private final BreedService service;

  public BreedEndpoint(BreedService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<BreedDto> search(BreedSearchDto searchParams) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("Request Params: {}", searchParams);
    return service.search(searchParams);
  }

  @PostMapping
  public BreedDto create(@RequestBody BreedDto breed) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("Request Body: {}", breed);

    try {
      return service.create(breed);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Breed could not be validated", v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    }

  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}

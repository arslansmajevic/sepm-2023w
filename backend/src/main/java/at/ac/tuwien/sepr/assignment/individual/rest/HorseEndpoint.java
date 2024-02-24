package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.service.HorseService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";

  private final HorseService service;

  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    // Stream<HorseListDto> myhorses = service.search(searchParameters);
    return service.search(searchParameters);
  }

  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }


  @PutMapping("{id}")
  public HorseDetailDto update(@PathVariable long id, @RequestBody HorseDetailDto toUpdate) throws ValidationException, ConflictException {
    LOG.info("PUT " + BASE_PATH + "/{}", id);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      return service.update(toUpdate.withId(id));
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Updating horse did not validate", v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    }
  }

  @PostMapping()
  public HorseDetailDto create(@RequestBody HorseDetailDto newHorse) {
    LOG.info("POST " + BASE_PATH + "/{}", newHorse);
    LOG.debug("Body of request:\n{}", newHorse);

    try {
      return service.create(newHorse);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Parsed horse failed validation", v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    }
  }

  @DeleteMapping("{id}")
  public String delete(@PathVariable long id) {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);

    try {
      return service.delete(id);
    } catch (NotFoundException n) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse could not be found", n);
      throw new ResponseStatusException(status, n.getMessage(), n);
    }
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}

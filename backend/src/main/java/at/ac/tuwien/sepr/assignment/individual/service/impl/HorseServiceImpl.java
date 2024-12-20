package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import at.ac.tuwien.sepr.assignment.individual.service.HorseValidator;
import at.ac.tuwien.sepr.assignment.individual.service.service.BreedService;
import at.ac.tuwien.sepr.assignment.individual.service.service.HorseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final BreedService breedService;

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, BreedService breedService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.breedService = breedService;
  }

  @Override
  public Stream<HorseListDto> search(HorseSearchDto searchParameters) {
    var horses = dao.search(searchParameters);
    // First get all breed ids…
    var breeds = horses.stream()
        .map(Horse::getBreedId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    // … then get the breeds all at once.
    var breedsPerId = breedMapForHorses(breeds);

    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, breedsPerId));
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);

    validator.validateForUpdate(horse, horse.breed() != null ? breedService.findBreedsByIds(Collections.singleton(horse.breed().id())) : null);

    var updatedHorse = dao.update(horse);
    var breeds = breedMapForSingleHorse(updatedHorse);
    return mapper.entityToDetailDto(updatedHorse, breeds);
  }

  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    var breeds = breedMapForSingleHorse(horse);
    return mapper.entityToDetailDto(horse, breeds);
  }

  @Override
  public HorseDetailDto create(HorseDetailDto horse) throws ValidationException {
    LOG.trace("creating({})", horse);

    validator.validateCreateNewHorse(horse, horse.breed() != null ? breedService.findBreedsByIds(Collections.singleton(horse.breed().id())) : null);

    var horseResult = dao.create(horse);
    var breeds = breedMapForSingleHorse(horseResult);

    return mapper.entityToDetailDto(horseResult, breeds);
  }

  @Override
  public String delete(long id) throws NotFoundException {
    LOG.trace("deleting horse({})", id);
    Horse horse;
    try {
      horse = dao.getById(id);
    } catch (NotFoundException n) {
      throw new NotFoundException("No horse with id %d found".formatted(id));
    }

    return dao.delete(horse.getId(), horse.getName());
  }

  @Override
  public TournamentDetailParticipantDto[] getTournamentHorses(Long tournamentId) {
    LOG.trace("getting tournament horses on tournament id({})", tournamentId);

    var horses = dao.getTournamentHorses(tournamentId);
    var races = dao.getHorseRaces(tournamentId);
    var participation = dao.getTournamentParticipations(tournamentId);

    return mapper.entityToTournamentDetailParticipantDto(horses, races, participation);
  }

  private Map<Long, BreedDto> breedMapForSingleHorse(Horse horse) {
    return breedMapForHorses(Collections.singleton(horse.getBreedId()));
  }

  private Map<Long, BreedDto> breedMapForHorses(Set<Long> horse) {
    return breedService.findBreedsByIds(horse)
        .collect(Collectors.toUnmodifiableMap(BreedDto::id, Function.identity()));
  }
}

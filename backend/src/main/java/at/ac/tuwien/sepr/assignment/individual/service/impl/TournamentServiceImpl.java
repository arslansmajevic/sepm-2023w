package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentValidator;
import at.ac.tuwien.sepr.assignment.individual.service.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final TournamentDao tournamentDao;
  private final TournamentMapper mapper;
  private final HorseMapper horseMapper;
  private final TournamentValidator validator;
  private final HorseService horseService;

  public TournamentServiceImpl(TournamentDao tournamentDao,
                               TournamentMapper mapper,
                               TournamentValidator validator,
                               HorseService horseService,
                               HorseMapper horseMapper) {
    this.tournamentDao = tournamentDao;
    this.mapper = mapper;
    this.validator = validator;
    this.horseService = horseService;
    this.horseMapper = horseMapper;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchDto) {
    var tournaments = tournamentDao.search(searchDto);
    return tournaments.stream()
            .map(mapper::entityToListDto)
            .sorted((t1, t2) -> t2.startDate().compareTo(t1.startDate()));
  }

  @Override
  public TournamentDetailDto create(TournamentCreateDto tournament) throws ValidationException {
    LOG.trace("creating({})", tournament);

    validator.validateCreateNewTournament(tournament);

    var tournamentResult = tournamentDao.create(tournament);
    var participants = horseService.getTournamentHorses(tournamentResult.getId());

    return mapper.entityToTournamentDetailDto(tournamentResult, participants);
  }
}

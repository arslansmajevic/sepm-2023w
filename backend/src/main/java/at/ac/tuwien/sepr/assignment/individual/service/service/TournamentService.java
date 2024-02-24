package at.ac.tuwien.sepr.assignment.individual.service.service;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

public interface TournamentService {

  Stream<TournamentListDto> search(TournamentSearchDto searchDto);

  TournamentDetailDto create(TournamentCreateDto tournament) throws ValidationException;
}

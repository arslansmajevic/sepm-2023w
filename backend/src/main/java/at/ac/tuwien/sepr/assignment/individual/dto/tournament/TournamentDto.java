package at.ac.tuwien.sepr.assignment.individual.dto.tournament;

import java.time.LocalDate;

public record TournamentDto(
    Long id,
    String name,
    LocalDate dateOfStart,
    LocalDate dateOfEnd
) {
}

package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

public record TournamentDto(
    Long id,
    String name,
    LocalDate dateOfStart,
    LocalDate dateOfEnd
) {
}

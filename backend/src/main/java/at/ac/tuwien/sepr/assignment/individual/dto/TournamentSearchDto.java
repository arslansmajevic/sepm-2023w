package at.ac.tuwien.sepr.assignment.individual.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record TournamentSearchDto(
        String name,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dateOfStart,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dateOfEnd,
        Integer limit
) {
}

package com.jonathanfoucher.csvapiexample.data.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MovieDto {
    private Long id;
    private String title;
    private LocalDate releaseDate;

    public static final String MOVIE_CSV_HEADERS = "id,title,release_date\n";

    @Override
    public String toString() {
        return String.format("{ id=%s, title=\"%s\", release_date=%s }", id, title, releaseDate);
    }
}

package com.jonathanfoucher.csvapiexample.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.jonathanfoucher.csvapiexample.common.errors.MovieCsvParsingException;
import com.jonathanfoucher.csvapiexample.common.errors.MovieCsvWritingException;
import com.jonathanfoucher.csvapiexample.data.dto.MovieDto;
import com.jonathanfoucher.csvapiexample.data.model.Movie;
import com.jonathanfoucher.csvapiexample.data.repository.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Stream;

import static com.jonathanfoucher.csvapiexample.data.dto.MovieDto.MOVIE_CSV_HEADERS;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final CsvMapper mapper;

    @Transactional
    public void saveAll(InputStreamResource resource) {
        MappingIterator<MovieDto> iterator;
        try {
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());

            CsvSchema schema = CsvSchema.emptySchema()
                    .withHeader();

            iterator = mapper.readerFor(MovieDto.class)
                    .with(schema)
                    .readValues(reader);

        } catch (IOException exception) {
            throw new MovieCsvParsingException(exception);
        }

        while (iterator.hasNext()) {
            Movie movie = convertDtoToEntity(iterator.next());
            movieRepository.save(movie);
        }
    }

    @Transactional
    public void findAll(OutputStream outputStream) {
        Stream.concat(Stream.of(MOVIE_CSV_HEADERS),
                        movieRepository.findAllBy()
                                .map(this::convertEntityToDto)
                                .map(movie -> {
                                    try {
                                        return mapper.writerWithSchemaFor(MovieDto.class)
                                                .writeValueAsString(movie);
                                    } catch (JsonProcessingException exception) {
                                        throw new MovieCsvWritingException(exception);
                                    }
                                })
                )
                .forEach(str -> {
                    byte[] bytes = str.getBytes(UTF_8);
                    try {
                        outputStream.write(bytes);
                    } catch (IOException exception) {
                        throw new MovieCsvWritingException(exception);
                    }
                });
    }

    private Movie convertDtoToEntity(MovieDto dto) {
        Movie movie = new Movie();
        movie.setId(dto.getId());
        movie.setTitle(dto.getTitle());
        movie.setReleaseDate(dto.getReleaseDate());
        return movie;
    }

    private MovieDto convertEntityToDto(Movie entity) {
        MovieDto dto = new MovieDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setReleaseDate(entity.getReleaseDate());
        return dto;
    }
}

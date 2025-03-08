package com.jonathanfoucher.csvapiexample.services;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.jonathanfoucher.csvapiexample.common.configs.JacksonConfig;
import com.jonathanfoucher.csvapiexample.data.dto.MovieDto;
import com.jonathanfoucher.csvapiexample.data.model.Movie;
import com.jonathanfoucher.csvapiexample.data.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(MovieService.class)
@SpringBootTest(classes = JacksonConfig.class)
class MovieServiceTest {
    @Autowired
    private MovieService movieService;
    @MockitoBean
    private MovieRepository movieRepository;
    @Autowired
    private CsvMapper mapper;

    private static final String MOVIES_CSV_FILE_PATH = "/csv/movies.csv";
    private static final Long ID = 15L;
    private static final String TITLE = "Some movie";
    private static final LocalDate RELEASE_DATE = LocalDate.of(2022, 7, 19);

    @Test
    void saveAll() throws IOException {
        // GIVEN
        File file = new ClassPathResource(MOVIES_CSV_FILE_PATH).getFile();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // WHEN
        movieService.saveAll(resource);

        // THEN
        ArgumentCaptor<Movie> capturedMovie = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository, times(3)).save(capturedMovie.capture());

        List<Movie> results = capturedMovie.getAllValues();
        assertEquals(3, results.size());

        Movie result0 = results.get(0);
        assertNotNull(result0);
        assertEquals(1L, result0.getId());
        assertEquals("Some movie", result0.getTitle());
        assertEquals(LocalDate.of(2022, 2, 22), result0.getReleaseDate());

        Movie result1 = results.get(1);
        assertNotNull(result1);
        assertEquals(2L, result1.getId());
        assertEquals("Some other movie", result1.getTitle());
        assertEquals(LocalDate.of(2022, 2, 23), result1.getReleaseDate());

        Movie result2 = results.get(2);
        assertNotNull(result2);
        assertEquals(3L, result2.getId());
        assertEquals("Some third movie", result2.getTitle());
        assertEquals(LocalDate.of(2018, 2, 22), result2.getReleaseDate());
    }

    @Test
    void findAll() throws IOException {
        // GIVEN
        OutputStream outputStream = spy(new ByteArrayOutputStream());
        Movie movie = initMovie();

        when(movieRepository.findAllBy())
                .thenReturn(Stream.of(movie));

        // WHEN
        movieService.findAll(outputStream);

        // THEN
        ArgumentCaptor<byte[]> capturedBytes = ArgumentCaptor.forClass(byte[].class);
        verify(movieRepository, times(1)).findAllBy();
        verify(outputStream, times(2)).write(capturedBytes.capture());

        List<byte[]> results = capturedBytes.getAllValues();
        assertEquals(2, results.size());

        byte[] expectedHeader = MovieDto.MOVIE_CSV_HEADERS.getBytes();
        byte[] headerResult = results.get(0);
        assertNotNull(headerResult);
        assertEquals(expectedHeader.length, headerResult.length);
        for (int i = 0; i < headerResult.length; i++) {
            assertEquals(expectedHeader[i], headerResult[i]);
        }

        byte[] expectedMovie = "15,\"Some movie\",2022-07-19\n".getBytes();
        byte[] movieResult = results.get(1);
        assertNotNull(movieResult);
        assertEquals(expectedMovie.length, movieResult.length);
        for (int i = 0; i < movieResult.length; i++) {
            assertEquals(expectedMovie[i], movieResult[i]);
        }
    }

    private Movie initMovie() {
        Movie movie = new Movie();
        movie.setId(ID);
        movie.setTitle(TITLE);
        movie.setReleaseDate(RELEASE_DATE);
        return movie;
    }
}

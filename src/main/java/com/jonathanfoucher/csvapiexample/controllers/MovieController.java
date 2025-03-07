package com.jonathanfoucher.csvapiexample.controllers;

import com.jonathanfoucher.csvapiexample.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

@RequiredArgsConstructor
@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    private static final String CSV_TYPE = "text/csv";
    private static final String FILE_NAME = "movies_extraction.csv";

    @GetMapping
    public ResponseEntity<StreamingResponseBody> findAll() {
        StreamingResponseBody responseBody = movieService::findAll;
        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=" + FILE_NAME)
                .contentType(APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

    @PostMapping(consumes = CSV_TYPE)
    public void save(@RequestBody InputStreamResource movies) {
        movieService.save(movies);
    }
}

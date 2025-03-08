package com.jonathanfoucher.csvapiexample.controllers;

import com.jonathanfoucher.csvapiexample.data.dto.MovieDto;
import com.jonathanfoucher.csvapiexample.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(MovieController.class)
class MovieControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private MovieController movieController;
    @MockitoBean
    private MovieService movieService;

    private static final String CSV_TYPE = "text/csv";
    private static final String FILE_NAME = "movies_extraction.csv";
    private static final String MOVIES_PATH = "/movies";
    private static final String MOVIES_CSV_FILE_PATH = "/csv/movies.csv";

    @BeforeEach
    void initEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(movieController)
                .setMessageConverters(
                        new MappingJackson2HttpMessageConverter(),
                        new ResourceHttpMessageConverter()
                )
                .build();
    }

    @Test
    void findAll() throws Exception {
        // GIVEN
        doAnswer(invocation -> {
            OutputStream outputStream = invocation.getArgument(0);
            outputStream.write(MovieDto.MOVIE_CSV_HEADERS.getBytes());
            outputStream.write("15,\"Some movie\",2022-07-19\n".getBytes());
            return null;
        }).when(movieService).findAll(any());

        // WHEN / THEN
        mockMvc.perform(get(MOVIES_PATH))
                .andExpect(status().isOk())
                .andExpect(header().string(CONTENT_DISPOSITION, "attachment; filename=" + FILE_NAME))
                .andExpect(header().string(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(content().string(MovieDto.MOVIE_CSV_HEADERS + "15,\"Some movie\",2022-07-19\n"));

        verify(movieService, times(1)).findAll(any());
    }

    @Test
    void saveAll() throws Exception {
        // GIVEN
        File file = new ClassPathResource(MOVIES_CSV_FILE_PATH).getFile();
        byte[] bytes = Files.readAllBytes(file.toPath());

        // WHEN / THEN
        mockMvc.perform(post(MOVIES_PATH)
                        .characterEncoding(UTF_8)
                        .contentType(CSV_TYPE)
                        .content(bytes)
                )
                .andExpect(status().isOk());

        ArgumentCaptor<InputStreamResource> capturedInputStreamResource = ArgumentCaptor.forClass(InputStreamResource.class);
        verify(movieService, times(1)).saveAll(capturedInputStreamResource.capture());

        InputStreamResource resource = capturedInputStreamResource.getValue();
        byte[] savedBytes = resource.getInputStream().readAllBytes();
        assertNotNull(resource);
        assertEquals(bytes.length, savedBytes.length);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], savedBytes[i]);
        }
    }
}

package com.jonathanfoucher.csvapiexample.common.configs;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.dataformat.csv.CsvParser.Feature.SKIP_EMPTY_LINES;

@Configuration
public class JacksonConfig {
    @Bean
    public CsvMapper csvMapper() {
        return CsvMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .addModule(new JavaTimeModule())
                .enable(SKIP_EMPTY_LINES)
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .disable(SORT_PROPERTIES_ALPHABETICALLY)
                .build();
    }
}

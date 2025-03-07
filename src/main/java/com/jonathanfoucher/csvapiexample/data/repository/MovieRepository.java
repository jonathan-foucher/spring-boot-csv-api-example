package com.jonathanfoucher.csvapiexample.data.repository;

import com.jonathanfoucher.csvapiexample.data.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Stream<Movie> findAllBy();
}

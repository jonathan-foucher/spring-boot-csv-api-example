package com.jonathanfoucher.csvapiexample.common.errors;

public class MovieCsvParsingException extends RuntimeException {
    public MovieCsvParsingException(Exception exception) {
        super("Error while parsing movie data", exception);
    }
}

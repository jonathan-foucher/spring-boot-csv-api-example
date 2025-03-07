package com.jonathanfoucher.csvapiexample.common.errors;

public class MovieCsvWritingException extends RuntimeException {
    public MovieCsvWritingException(Exception exception) {
        super("Error while writing movie data", exception);
    }
}

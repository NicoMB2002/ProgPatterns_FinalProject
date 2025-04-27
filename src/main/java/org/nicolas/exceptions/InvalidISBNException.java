package org.nicolas.exceptions;

public class InvalidISBNException extends RuntimeException {
    public InvalidISBNException(String message) {
        super(message);
    }
}

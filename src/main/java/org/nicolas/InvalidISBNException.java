package org.nicolas;

public class InvalidISBNException extends RuntimeException {
    public InvalidISBNException(String message) {
        super(message);
    }
}

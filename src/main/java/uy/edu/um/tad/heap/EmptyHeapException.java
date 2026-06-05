package uy.edu.um.tad.heap;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmptyHeapException extends RuntimeException {
    public EmptyHeapException(String message) {
        super(message);
    }
}

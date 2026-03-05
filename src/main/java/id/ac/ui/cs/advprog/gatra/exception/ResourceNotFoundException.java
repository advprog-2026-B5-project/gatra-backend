package id.ac.ui.cs.advprog.gatra.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s dengan ID %s tidak ditemukan", resourceName, id));
    }
}

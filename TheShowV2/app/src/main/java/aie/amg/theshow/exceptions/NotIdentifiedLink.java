package aie.amg.theshow.exceptions;

public class NotIdentifiedLink extends Exception {

    public NotIdentifiedLink() {
        super();

    }

    public NotIdentifiedLink(String message) {
        super(message);
    }

    public NotIdentifiedLink(String message, Throwable throwable) {
        super(message, throwable);
    }
}

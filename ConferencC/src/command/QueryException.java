package command;

/**
 * Qery Exception throws when server return answer which was not expected.
 * <p>Exception message contains information about query was sent to server.</p>
 * 
 */
public class QueryException extends Exception {
    public QueryException() {
        super("Bad query.");
    }
    
    public QueryException(String msg) {
        super(msg);
    }
}

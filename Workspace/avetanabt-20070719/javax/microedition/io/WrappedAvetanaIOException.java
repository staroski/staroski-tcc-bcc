package javax.microedition.io;

import java.io.IOException;

/**
 * 
 * Contributed to Avetana bluetooth
 * 
 * Does not belong in javax.microedition.io pakage here just temporary.
 *   
 * @author vlads
 */
public class WrappedAvetanaIOException extends IOException {

	private static final long serialVersionUID = 1L;
	
	Throwable cause;
	
	public WrappedAvetanaIOException(Throwable cause) {
		this.cause = cause;
	}
	
	public Throwable getCause() {
        return (cause==this ? null : cause);
    }
}

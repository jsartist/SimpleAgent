package Agent;

import java.io.Serializable;
import java.security.Key;

public class KeyData implements Serializable{
	private Key key = null;
	
	public KeyData(Key key) {
		this.key = key;
	}
	
	public Key getKey() {
		return this.key;
	}
}

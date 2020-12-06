package Agent;

import java.io.Serializable;

public class EncryptedData implements Serializable{
	private byte[] data = null;
	
	public EncryptedData(byte[] data) {
		this.data = data;
	}
	
	public byte[] getbyte() {
		return data;
	}
}

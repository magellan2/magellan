package magellan.io.cr;

import java.io.Reader;

public class Scanner {
	
	private Reader in;
	public Scanner(Reader in) {
		this.in = in;
	}

	public String[] tokens;
	public int count;

	public boolean isBlock() {
		return count > 0 && tokens[0].equals(tokens[0].toUpperCase());
	}
	
	public boolean isIdBlock() {
		return isBlock() && count ==2;
	}

	public boolean next() {
		return false;
	}
}

package com.eressea.io;

import java.io.IOException;
import java.io.Reader;

import com.eressea.GameData;

public interface GameDataIO {
	public GameData read(Reader in, GameData world) throws IOException;
}

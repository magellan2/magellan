package com.eressea.io;

import java.io.IOException;

import com.eressea.io.file.FileType;

public interface GameNameIO {
	/**
	 * Returns the game data found in the given FileType
	 */ 
	public String getGameName(FileType file) throws IOException;
}

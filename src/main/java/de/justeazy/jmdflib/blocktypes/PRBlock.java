package de.justeazy.jmdflib.blocktypes;

/**
 * <p>
 * Basically holds all the information of PRBlocks.
 * </p>
 * 
 * @author Henrik Peters <henrik.peters@tu-clausthal.de>
 */
public class PRBlock {

	private String blockTypeIdentifier;

	private int blockSize;

	private String programSpecificData;

	public String getBlockTypeIdentifier() {
		return blockTypeIdentifier;
	}

	public void setBlockTypeIdentifier(String blockTypeIdentifier) {
		this.blockTypeIdentifier = blockTypeIdentifier;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public String getProgramSpecificData() {
		return programSpecificData;
	}

	public void setProgramSpecificData(String programSpecificData) {
		this.programSpecificData = programSpecificData;
	}

}

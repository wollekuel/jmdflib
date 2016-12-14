package de.justeazy.jmdflib.blocktypes;

/**
 * <p>
 * Basically holds all the information of TXBlocks.
 * </p>
 * 
 * <p>
 * This class is a re-implementation of the implementation on
 * {@link https://sourceforge.net/projects/jmdflib/}.
 * </p>
 * 
 * @author Henrik Peters <henrik.peters@tu-clausthal.de>
 */
public class TXBlock {
	
	private String blockTypeIdentifier;
	
	private int blockSize;
	
	private String text;

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	

}

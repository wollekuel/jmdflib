package de.justeazy.jmdflib.blocktypes;

import de.justeazy.jmdflib.enums.ExtensionType;

/**
 * <p>
 * Basically holds all the information of CEBlocks.
 * </p>
 * 
 * <p>
 * This class is a re-implementation of the implementation on
 * {@link https://sourceforge.net/projects/jmdflib/}.
 * </p>
 * 
 * @author Henrik Peters <henrik.peters@tu-clausthal.de>
 */
public class CEBlock {

	protected CEBlock() {
	};

	private String blockTypeIdentifier;

	private int blockSize;

	private ExtensionType extensionType;

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

	public ExtensionType getExtensionType() {
		return extensionType;
	}

	public void setExtensionType(ExtensionType extensionType) {
		this.extensionType = extensionType;
	}

}

package de.justeazy.jmdflib.blocktypes;

public class TRBlock {
	
	private String blockTypeIdentifier;
	
	private int blockSize;
	
	private long pointerToTXBlock;
	
	private int numberOfTriggerEvents;

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

	public long getPointerToTXBlock() {
		return pointerToTXBlock;
	}

	public void setPointerToTXBlock(long pointerToTXBlock) {
		this.pointerToTXBlock = pointerToTXBlock;
	}

	public int getNumberOfTriggerEvents() {
		return numberOfTriggerEvents;
	}

	public void setNumberOfTriggerEvents(int numberOfTriggerEvents) {
		this.numberOfTriggerEvents = numberOfTriggerEvents;
	}

}

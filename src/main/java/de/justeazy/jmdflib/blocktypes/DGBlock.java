package de.justeazy.jmdflib.blocktypes;

import java.util.ArrayList;

import de.justeazy.jmdflib.enums.NumberOfRecordIDs;

public class DGBlock {

	private String blockTypeIdentifier;

	private int blockSize;

	private long pointerToNextDGBlock;

	private long pointerToFirstCGBlock;
	
	private ArrayList<CGBlock> cgBlocks;

	private long pointerToTRBlock;
	
	private TRBlock trBlock;

	private long pointerToDataBlock;

	private int numberOfChannelGroups;

	private NumberOfRecordIDs numberOfRecordIDs;

	private long reserved;

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

	public long getPointerToNextDGBlock() {
		return pointerToNextDGBlock;
	}

	public void setPointerToNextDGBlock(long pointerToNextDGBlock) {
		this.pointerToNextDGBlock = pointerToNextDGBlock;
	}

	public long getPointerToFirstCGBlock() {
		return pointerToFirstCGBlock;
	}

	public void setPointerToFirstCGBlock(long pointerToFirstCGBlock) {
		this.pointerToFirstCGBlock = pointerToFirstCGBlock;
	}

	public long getPointerToTRBlock() {
		return pointerToTRBlock;
	}

	public void setPointerToTRBlock(long pointerToTRBlock) {
		this.pointerToTRBlock = pointerToTRBlock;
	}

	public long getPointerToDataBlock() {
		return pointerToDataBlock;
	}

	public void setPointerToDataBlock(long pointerToDataBlock) {
		this.pointerToDataBlock = pointerToDataBlock;
	}

	public int getNumberOfChannelGroups() {
		return numberOfChannelGroups;
	}

	public void setNumberOfChannelGroups(int numberOfChannelGroups) {
		this.numberOfChannelGroups = numberOfChannelGroups;
	}

	public NumberOfRecordIDs getNumberOfRecordIDs() {
		return numberOfRecordIDs;
	}

	public void setNumberOfRecordIDs(NumberOfRecordIDs numberOfRecordIDs) {
		this.numberOfRecordIDs = numberOfRecordIDs;
	}

	public long getReserved() {
		return reserved;
	}

	public void setReserved(long reserved) {
		this.reserved = reserved;
	}

	public TRBlock getTRBlock() {
		return trBlock;
	}

	public void setTRBlock(TRBlock trBlock) {
		this.trBlock = trBlock;
	}

	public ArrayList<CGBlock> getCgBlocks() {
		return cgBlocks;
	}

	public void setCGBlocks(ArrayList<CGBlock> cgBlocks) {
		this.cgBlocks = cgBlocks;
	}

}

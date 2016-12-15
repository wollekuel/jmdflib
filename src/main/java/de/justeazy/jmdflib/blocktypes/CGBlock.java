package de.justeazy.jmdflib.blocktypes;

import java.util.ArrayList;

public class CGBlock {

	private String blockTypeIdentifier;
	
	private int blockSize;
	
	private long pointerToNextCGBlock;
	
	private long pointerToFirstCNBlock;
	
	private ArrayList<CNBlock> cnBlocks;
	
	private long pointerToTXBlock;
	
	private int recordID;
	
	private int numberOfChannels;
	
	private int sizeOfDataRecord;
	
	private long numberOfRecords;
	
	private long pointerToFirstSRBlock;

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

	public long getPointerToNextCGBlock() {
		return pointerToNextCGBlock;
	}

	public void setPointerToNextCGBlock(long pointerToNextCGBlock) {
		this.pointerToNextCGBlock = pointerToNextCGBlock;
	}

	public long getPointerToFirstCNBlock() {
		return pointerToFirstCNBlock;
	}

	public void setPointerToFirstCNBlock(long pointerToFirstCNBlock) {
		this.pointerToFirstCNBlock = pointerToFirstCNBlock;
	}

	public long getPointerToTXBlock() {
		return pointerToTXBlock;
	}

	public void setPointerToTXBlock(long pointerToTXBlock) {
		this.pointerToTXBlock = pointerToTXBlock;
	}

	public int getRecordID() {
		return recordID;
	}

	public void setRecordID(int recordID) {
		this.recordID = recordID;
	}

	public int getNumberOfChannels() {
		return numberOfChannels;
	}

	public void setNumberOfChannels(int numberOfChannels) {
		this.numberOfChannels = numberOfChannels;
	}

	public int getSizeOfDataRecord() {
		return sizeOfDataRecord;
	}

	public void setSizeOfDataRecord(int sizeOfDataRecord) {
		this.sizeOfDataRecord = sizeOfDataRecord;
	}

	public long getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(long numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	public long getPointerToFirstSRBlock() {
		return pointerToFirstSRBlock;
	}

	public void setPointerToFirstSRBlock(long pointerToFirstSRBlock) {
		this.pointerToFirstSRBlock = pointerToFirstSRBlock;
	}

	public ArrayList<CNBlock> getCNBlocks() {
		return cnBlocks;
	}

	public void setCNBlocks(ArrayList<CNBlock> cnBlocks) {
		this.cnBlocks = cnBlocks;
	} 
	
}

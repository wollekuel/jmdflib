package de.justeazy.jmdflib.blocktypes;

import de.justeazy.jmdflib.enums.ChannelType;
import de.justeazy.jmdflib.enums.SignalDataType;

public class CNBlock {

	private String blockTypeIdentifier;

	private int blockSize;

	private long pointerToNextCNBlock;

	private long pointerToCCBlock;

	private CCBlock ccBlock;

	private long pointerToCEBlock;
	
	private CEBlock ceBlock;

	private long pointerToCDBlock;

	private long pointerToTXBlock;

	private TXBlock txBlock;

	private ChannelType channelType;

	private String shortSignalName;

	private String signalDescription;

	private int startOffsetInBits;

	private int numberOfBits;

	private SignalDataType signalDataType;

	private boolean valueRangeValid;

	private double minimumSignalValue;

	private double maximumSignalValue;

	private double samplingRate;

	private long pointerToTXBlockLongSignalName;

	private TXBlock txBlockLongSignalName;

	private long pointerToTXBlockDisplayName;

	private TXBlock txBlockDisplayName;

	private int additionalByteOffset;

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

	public long getPointerToNextCNBlock() {
		return pointerToNextCNBlock;
	}

	public void setPointerToNextCNBlock(long pointerToNextCNBlock) {
		this.pointerToNextCNBlock = pointerToNextCNBlock;
	}

	public long getPointerToCCBlock() {
		return pointerToCCBlock;
	}

	public void setPointerToCCBlock(long pointerToCCBlock) {
		this.pointerToCCBlock = pointerToCCBlock;
	}

	public long getPointerToCEBlock() {
		return pointerToCEBlock;
	}

	public void setPointerToCEBlock(long pointerToCEBlock) {
		this.pointerToCEBlock = pointerToCEBlock;
	}

	public long getPointerToCDBlock() {
		return pointerToCDBlock;
	}

	public void setPointerToCDBlock(long pointerToCDBlock) {
		this.pointerToCDBlock = pointerToCDBlock;
	}

	public long getPointerToTXBlock() {
		return pointerToTXBlock;
	}

	public void setPointerToTXBlock(long pointerToTXBlock) {
		this.pointerToTXBlock = pointerToTXBlock;
	}

	public ChannelType getChannelType() {
		return channelType;
	}

	public void setChannelType(ChannelType channelType) {
		this.channelType = channelType;
	}

	public String getShortSignalName() {
		return shortSignalName;
	}

	public void setShortSignalName(String shortSignalName) {
		this.shortSignalName = shortSignalName;
	}

	public String getSignalDescription() {
		return signalDescription;
	}

	public void setSignalDescription(String signalDescription) {
		this.signalDescription = signalDescription;
	}

	public int getStartOffsetInBits() {
		return startOffsetInBits;
	}

	public void setStartOffsetInBits(int startOffsetInBits) {
		this.startOffsetInBits = startOffsetInBits;
	}

	public int getNumberOfBits() {
		return numberOfBits;
	}

	public void setNumberOfBits(int numberOfBits) {
		this.numberOfBits = numberOfBits;
	}

	public SignalDataType getSignalDataType() {
		return signalDataType;
	}

	public void setSignalDataType(SignalDataType signalDataType) {
		this.signalDataType = signalDataType;
	}

	public boolean isValueRangeValid() {
		return valueRangeValid;
	}

	public void setValueRangeValid(boolean valueRangeValid) {
		this.valueRangeValid = valueRangeValid;
	}

	public double getMinimumSignalValue() {
		return minimumSignalValue;
	}

	public void setMinimumSignalValue(double minimumSignalValue) {
		this.minimumSignalValue = minimumSignalValue;
	}

	public double getMaximumSignalValue() {
		return maximumSignalValue;
	}

	public void setMaximumSignalValue(double maximumSignalValue) {
		this.maximumSignalValue = maximumSignalValue;
	}

	public double getSamplingRate() {
		return samplingRate;
	}

	public void setSamplingRate(double samplingRate) {
		this.samplingRate = samplingRate;
	}

	public long getPointerToTXBlockLongSignalName() {
		return pointerToTXBlockLongSignalName;
	}

	public void setPointerToTXBlockLongSignalName(long pointerToTXBlockLongSignalName) {
		this.pointerToTXBlockLongSignalName = pointerToTXBlockLongSignalName;
	}

	public long getPointerToTXBlockDisplayName() {
		return pointerToTXBlockDisplayName;
	}

	public void setPointerToTXBlockDisplayName(long pointerToTXBlockDisplayName) {
		this.pointerToTXBlockDisplayName = pointerToTXBlockDisplayName;
	}

	public int getAdditionalByteOffset() {
		return additionalByteOffset;
	}

	public void setAdditionalByteOffset(int additionalByteOffset) {
		this.additionalByteOffset = additionalByteOffset;
	}

	public TXBlock getTXBlock() {
		return txBlock;
	}

	public void setTxBlock(TXBlock txBlock) {
		this.txBlock = txBlock;
	}

	public TXBlock getTxBlockLongSignalName() {
		return txBlockLongSignalName;
	}

	public void setTxBlockLongSignalName(TXBlock txBlockLongSignalName) {
		this.txBlockLongSignalName = txBlockLongSignalName;
	}

	public TXBlock getTxBlockDisplayName() {
		return txBlockDisplayName;
	}

	public void setTxBlockDisplayName(TXBlock txBlockDisplayName) {
		this.txBlockDisplayName = txBlockDisplayName;
	}

	public CCBlock getCcBlock() {
		return ccBlock;
	}

	public void setCcBlock(CCBlock ccBlock) {
		this.ccBlock = ccBlock;
	}

	public CEBlock getCeBlock() {
		return ceBlock;
	}

	public void setCeBlock(CEBlock ceBlock) {
		this.ceBlock = ceBlock;
	}

}

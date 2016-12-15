package de.justeazy.jmdflib.blocktypes;

import java.math.BigInteger;

import de.justeazy.jmdflib.enums.TimeQualityClass;

/**
 * <p>
 * Basically holds all the information of HDBlocks.
 * </p>
 * 
 * <p>
 * This class is a re-implementation of the implementation on
 * {@link https://sourceforge.net/projects/jmdflib/}.
 * </p>
 * 
 * @author Henrik Peters <henrik.peters@tu-clausthal.de>
 */
public class HDBlock {

	private String blockTypeIdentifier;

	private int blockSize;

	private long pointerToFirstDGBlock;

	private long pointerToTXBlock;
	
	private TXBlock txBlock;

	private long pointerToPRBlock;

	private int numberOfDataGroups;

	private String recordingStartDate;

	private String recordingStartTime;

	private String authorsName;

	private String organizationsName;

	private String projectsName;

	private String measurementObject;
	
	private BigInteger recordingStartTimestamp;
	
	private int utcTimeOffset;
	
	private TimeQualityClass timeQualityClass;
	
	private String timerIdentification;

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

	public long getPointerToFirstDGBlock() {
		return pointerToFirstDGBlock;
	}

	public void setPointerToFirstDGBlock(long pointerToFirstDGBlock) {
		this.pointerToFirstDGBlock = pointerToFirstDGBlock;
	}

	public long getPointerToTXBlock() {
		return pointerToTXBlock;
	}

	public void setPointerToTXBlock(long pointerToTXBlock) {
		this.pointerToTXBlock = pointerToTXBlock;
	}

	public long getPointerToPRBlock() {
		return pointerToPRBlock;
	}

	public void setPointerToPRBlock(long pointerToPRBlock) {
		this.pointerToPRBlock = pointerToPRBlock;
	}

	public int getNumberOfDataGroups() {
		return numberOfDataGroups;
	}

	public void setNumberOfDataGroups(int numberOfDataGroups) {
		this.numberOfDataGroups = numberOfDataGroups;
	}

	public String getRecordingStartDate() {
		return recordingStartDate;
	}

	public void setRecordingStartDate(String recordingStartDate) {
		this.recordingStartDate = recordingStartDate;
	}

	public String getRecordingStartTime() {
		return recordingStartTime;
	}

	public void setRecordingStartTime(String recordingStartTime) {
		this.recordingStartTime = recordingStartTime;
	}

	public String getAuthorsName() {
		return authorsName;
	}

	public void setAuthorsName(String authorsName) {
		this.authorsName = authorsName;
	}

	public String getOrganizationsName() {
		return organizationsName;
	}

	public void setOrganizationsName(String organizationsName) {
		this.organizationsName = organizationsName;
	}

	public String getProjectsName() {
		return projectsName;
	}

	public void setProjectsName(String projectsName) {
		this.projectsName = projectsName;
	}

	public String getMeasurementObject() {
		return measurementObject;
	}

	public void setMeasurementObject(String measurementObject) {
		this.measurementObject = measurementObject;
	}

	public BigInteger getRecordingStartTimestamp() {
		return recordingStartTimestamp;
	}

	public void setRecordingStartTimestamp(BigInteger recordingStartTimestamp) {
		this.recordingStartTimestamp = recordingStartTimestamp;
	}

	public int getUtcTimeOffset() {
		return utcTimeOffset;
	}

	public void setUtcTimeOffset(int utcTimeOffset) {
		this.utcTimeOffset = utcTimeOffset;
	}

	public TimeQualityClass getTimeQualityClass() {
		return timeQualityClass;
	}

	public void setTimeQualityClass(TimeQualityClass timeQualityClass) {
		this.timeQualityClass = timeQualityClass;
	}

	public String getTimerIdentification() {
		return timerIdentification;
	}

	public void setTimerIdentification(String timerIdentification) {
		this.timerIdentification = timerIdentification;
	}

	public TXBlock getTXBlock() {
		return txBlock;
	}

	public void setTXBlock(TXBlock txBlock) {
		this.txBlock = txBlock;
	}

}

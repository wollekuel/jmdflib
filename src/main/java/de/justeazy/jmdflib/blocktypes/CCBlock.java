package de.justeazy.jmdflib.blocktypes;

import de.justeazy.jmdflib.enums.ConversionType;

/**
 * <p>
 * Basically holds all the information of CCBlocks.
 * </p>
 * 
 * <p>
 * This class is a re-implementation of the implementation on
 * {@link https://sourceforge.net/projects/jmdflib/}.
 * </p>
 * 
 * @author Henrik Peters <henrik.peters@tu-clausthal.de>
 */
public class CCBlock {

	private String blockTypeIdentifier;

	private int blockSize;

	private boolean physicalValueRangeValid;

	private double minimumPhysicalSignalValue;

	private double maximumPhysicalSignalValue;

	private String physicalUnit;

	private ConversionType conversionType;

	private int sizeInformation;
	
	protected CCBlock() {};

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

	public boolean isPhysicalValueRangeValid() {
		return physicalValueRangeValid;
	}

	public void setPhysicalValueRangeValid(boolean physicalValueRangeValid) {
		this.physicalValueRangeValid = physicalValueRangeValid;
	}

	public double getMinimumPhysicalSignalValue() {
		return minimumPhysicalSignalValue;
	}

	public void setMinimumPhysicalSignalValue(double minimumPhysicalSignalValue) {
		this.minimumPhysicalSignalValue = minimumPhysicalSignalValue;
	}

	public double getMaximumPhysicalSignalValue() {
		return maximumPhysicalSignalValue;
	}

	public void setMaximumPhysicalSignalValue(double maximumPhysicalSignalValue) {
		this.maximumPhysicalSignalValue = maximumPhysicalSignalValue;
	}

	public String getPhysicalUnit() {
		return physicalUnit;
	}

	public void setPhysicalUnit(String physicalUnit) {
		this.physicalUnit = physicalUnit;
	}

	public ConversionType getConversionType() {
		return conversionType;
	}

	public void setConversionType(ConversionType conversionType) {
		this.conversionType = conversionType;
	}

	public int getSizeInformation() {
		return sizeInformation;
	}

	public void setSizeInformation(int sizeInformation) {
		this.sizeInformation = sizeInformation;
	}

}

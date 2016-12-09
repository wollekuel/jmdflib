package de.justeazy.jmdflib.blocktypes;

import java.nio.ByteOrder;
import de.justeazy.jmdflib.enums.FloatingPointFormat;

/**
 * <p>
 * Basically holds all the information of IDBlocks.
 * </p>
 * 
 * <p>
 * This class is a re-implementation of the implementation on
 * {@link https://sourceforge.net/projects/jmdflib/}.
 * </p>
 * 
 * @author Henrik Peters <henrik.peters@tu-clausthal.de>
 */
public class IDBlock {

	private String fileIdentifier;

	private String formatIdentifier;

	private String programIdentifier;

	private ByteOrder defaultByteOrder;

	private FloatingPointFormat defaultFloatingPointFormat;

	private int versionNumber;

	private int codePage;
	
	private String reservedStructure1;
	
	private String reservedStructure2;
	
	private int standardFlags;
	
	private int customFlags;

	public String getFileIdentifier() {
		return fileIdentifier;
	}

	public void setFileIdentifier(String fileIdentifier) {
		this.fileIdentifier = fileIdentifier;
	}

	public String getFormatIdentifier() {
		return formatIdentifier;
	}

	public void setFormatIdentifier(String formatIdentifier) {
		this.formatIdentifier = formatIdentifier;
	}

	public String getProgramIdentifier() {
		return programIdentifier;
	}

	public void setProgramIdentifier(String programIdentifier) {
		this.programIdentifier = programIdentifier;
	}

	public ByteOrder getDefaultByteOrder() {
		return defaultByteOrder;
	}

	public void setDefaultByteOrder(ByteOrder defaultByteOrder) {
		this.defaultByteOrder = defaultByteOrder;
	}

	public FloatingPointFormat getDefaultFloatingPointFormat() {
		return defaultFloatingPointFormat;
	}

	public void setDefaultFloatingPointFormat(FloatingPointFormat defaultFloatingPointFormat) {
		this.defaultFloatingPointFormat = defaultFloatingPointFormat;
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public int getCodePage() {
		return codePage;
	}

	public void setCodePage(int codePage) {
		this.codePage = codePage;
	}

	public String getReservedStructure1() {
		return reservedStructure1;
	}

	public void setReservedStructure1(String reservedStructure1) {
		this.reservedStructure1 = reservedStructure1;
	}

	public String getReservedStructure2() {
		return reservedStructure2;
	}

	public void setReservedStructure2(String reservedStructure2) {
		this.reservedStructure2 = reservedStructure2;
	}

	public int getStandardFlags() {
		return standardFlags;
	}

	public void setStandardFlags(int standardFlags) {
		this.standardFlags = standardFlags;
	}

	public int getCustomFlags() {
		return customFlags;
	}

	public void setCustomFlags(int customFlags) {
		this.customFlags = customFlags;
	}

}

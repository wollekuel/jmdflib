package de.justeazy.jmdflib.blocktypes;

import de.justeazy.jmdflib.enums.ByteOrder;
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

}

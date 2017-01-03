package de.justeazy.jmdflib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.justeazy.jmdflib.blocktypes.CCBlock;
import de.justeazy.jmdflib.blocktypes.CCBlockLinear2Parameters;
import de.justeazy.jmdflib.blocktypes.CCBlockOneToOne;
import de.justeazy.jmdflib.blocktypes.CDBlock;
import de.justeazy.jmdflib.blocktypes.CEBlock;
import de.justeazy.jmdflib.blocktypes.CGBlock;
import de.justeazy.jmdflib.blocktypes.CNBlock;
import de.justeazy.jmdflib.blocktypes.DGBlock;
import de.justeazy.jmdflib.blocktypes.HDBlock;
import de.justeazy.jmdflib.blocktypes.IDBlock;
import de.justeazy.jmdflib.blocktypes.PRBlock;
import de.justeazy.jmdflib.blocktypes.TRBlock;
import de.justeazy.jmdflib.blocktypes.TXBlock;

import java.nio.ByteOrder;

import de.justeazy.jmdflib.enums.ChannelType;
import de.justeazy.jmdflib.enums.ConversionType;
import de.justeazy.jmdflib.enums.FloatingPointFormat;
import de.justeazy.jmdflib.enums.NumberOfRecordIDs;
import de.justeazy.jmdflib.enums.SignalDataType;
import de.justeazy.jmdflib.enums.TimeQualityClass;

/**
 * <p>
 * Reads a MDF file as an input stream.
 * </p>
 * 
 * <p>
 * This class is a re-implementation of the implementation on
 * {@link https://sourceforge.net/projects/jmdflib/}.
 * </p>
 * 
 * @author Henrik Peters <henrik.peters@tu-clausthal.de>
 */
public class MDFInputStream extends FileInputStream {

	/**
	 * Logging framework
	 */
	private static Logger l = LogManager.getLogger(MDFInputStream.class);

	/**
	 * Size of the reading buffer
	 */
	private final static int BUFFER_SIZE = 1048576;

	/**
	 * MDF file
	 */
	private File mdfFile;

	/**
	 * File content
	 */
	private byte[] content;

	/**
	 * File pointer
	 */
	private int filePointer = 0;

	/**
	 * IDBlock
	 */
	private IDBlock idBlock;

	/**
	 * HDBlock
	 */
	private HDBlock hdBlock;

	/**
	 * PRBlock
	 */
	private PRBlock prBlock;

	/**
	 * DGBlocks
	 */
	private ArrayList<DGBlock> dgBlocks;

	/**
	 * <p>
	 * A {@code MDFInputStream} reads MDF files by means of a
	 * {@link FileInputStream}.
	 * </p>
	 * <ul>
	 * <li>Maximum file length is {@code Integer.MAX_Value}</li>
	 * </ul>
	 * 
	 * @param mdfFile
	 *            MDF file to read
	 * @throws IOException
	 *             if file is too large
	 */
	public MDFInputStream(File mdfFile) throws IOException {
		super(mdfFile);

		this.mdfFile = mdfFile;
		if (mdfFile.length() >= Integer.MAX_VALUE) {
			throw new IOException("File too large.");
		}

		this.content = new byte[(int) mdfFile.length()];
	}

	/**
	 * <p>
	 * Reads the file and processes it afterwards.
	 * </p>
	 * 
	 * @throws IOException
	 *             if there is an error while reading the file
	 */
	public void readFile() throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int c = 0;
		while (this.mdfFile.length() > c + BUFFER_SIZE) {
			this.read(buffer);
			System.arraycopy(buffer, 0, this.content, c, BUFFER_SIZE);
			c += BUFFER_SIZE;
		}
		int remainingBytes = this.read(buffer);
		System.arraycopy(buffer, 0, this.content, c, remainingBytes);

		processFile();
		readData();
	}

	/**
	 * <p>
	 * Processes the file.
	 * </p>
	 * 
	 * @throws IOException
	 *             if there is an error processing the file
	 * 
	 */
	private void processFile() throws IOException {
		readIDBlock();
		readHDBlock();
		readPRBlock();
		readDGBlocks();
	}

	/**
	 * <p>
	 * Reads the identification block (identification of the file as MDF file
	 * and MDF version).
	 * </p>
	 * 
	 * @throws IOException
	 */
	private void readIDBlock() throws IOException {
		Matcher m;
		idBlock = new IDBlock();

		// file identifier
		String fileIdentifier = readChar(8);
		if (!fileIdentifier.equals("MDF     ")) {
			throw new IOException("Wrong file identifier (should be \"MDF     \", but was \"" + fileIdentifier + "\")");
		}
		idBlock.setFileIdentifier(fileIdentifier);
		l.trace("fileIdentifier = " + fileIdentifier);

		// format identifier
		String formatIdentifier = readChar(8);
		Pattern pFormatIdentifier = Pattern.compile("\\d+\\x2E\\d{2}\\s{4}");
		m = pFormatIdentifier.matcher(formatIdentifier);
		if (!m.find()) {
			throw new IOException("Wrong format identifier (" + formatIdentifier + ").");
		}
		idBlock.setFormatIdentifier(formatIdentifier);
		l.trace("formatIdentifier = \"" + formatIdentifier + "\"");

		// program identifier
		String programIdentifier = readChar(8);
		idBlock.setProgramIdentifier(programIdentifier);
		l.trace("programIdentifier = \"" + programIdentifier + "\"");

		// default byte order
		ByteOrder defaultByteOrder = readUint16() == 0 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
		idBlock.setDefaultByteOrder(defaultByteOrder);
		l.trace("defaultByteOrder = " + defaultByteOrder);
		if (defaultByteOrder != ByteOrder.LITTLE_ENDIAN) {
			throw new IOException(
					"Wrong byte order (should be LITTLE_ENDIAN, but was BIG_ENDIAN). BIG_ENDIAN is not implemented yet");
		}

		// default floating point format
		int floatingPointFormat = readUint16();
		switch (floatingPointFormat) {
		case 0:
			idBlock.setDefaultFloatingPointFormat(FloatingPointFormat.IEEE_754);
			break;
		case 1:
			idBlock.setDefaultFloatingPointFormat(FloatingPointFormat.G_FLOAT);
			break;
		default:
			idBlock.setDefaultFloatingPointFormat(FloatingPointFormat.D_FLOAT);
		}
		l.trace("idBlock.defaultFloatingPointFormat = " + idBlock.getDefaultFloatingPointFormat());

		// version number
		int versionNumber = readUint16();
		idBlock.setVersionNumber(versionNumber);
		l.trace("versionNumber = " + versionNumber);

		// code page
		int codePage = readUint16();
		idBlock.setCodePage(codePage);
		l.trace("codePage = " + codePage);

		// first reserved structure
		String reservedStructure1 = readChar(2);
		idBlock.setReservedStructure1(reservedStructure1);
		l.trace("reservedStructure1 = \"" + reservedStructure1 + "\"");

		// second reserved structure
		String reservedStructure2 = readChar(26);
		idBlock.setReservedStructure2(reservedStructure2);
		l.trace("reservedStructure2 = \"" + reservedStructure2 + "\"");

		// standard flags for unfinalized mdfs
		int standardFlags = readUint16();
		idBlock.setStandardFlags(standardFlags);
		l.trace("standardFlags = " + standardFlags);
		if (standardFlags != 0) {
			throw new IOException("Wrong standard flags (should be 0, but was \"" + standardFlags
					+ "\"). Unfinalized MDFs are not supported yet.");
		}

		// custom flags for unfinalized mdfs
		int customFlags = readUint16();
		idBlock.setCustomFlags(customFlags);
		l.trace("customFlags = " + customFlags);
		if (customFlags != 0) {
			throw new IOException("Wrong custom flags (should be 0, but was \"" + customFlags
					+ "\"). Unfinalized MDFs are not supported yet.");
		}
	}

	/**
	 * <p>
	 * Returns the IDBlock.
	 * </p>
	 * 
	 * @return IDBlock
	 */
	public IDBlock getIDBlock() {
		return idBlock;
	}

	/**
	 * <p>
	 * Reads the header block (general information of the file).
	 * </p>
	 * 
	 * @throws IOException
	 */
	private void readHDBlock() throws IOException {
		this.filePointer = 64;
		hdBlock = new HDBlock();

		// block type identifier
		String blockTypeIdentifier = readChar(2);
		if (!blockTypeIdentifier.equals("HD")) {
			throw new IOException(
					"Wrong block type identifier (should be \"HD\", but was " + blockTypeIdentifier + "\").");
		}
		hdBlock.setBlockTypeIdentifier(blockTypeIdentifier);
		l.trace("blockTypeIdentifier = " + blockTypeIdentifier);

		// block size
		int blockSize = readUint16();
		hdBlock.setBlockSize(blockSize);
		l.trace("blockSize = " + blockSize);

		// pointer to first DGBlock
		long pointerToFirstDGBlock = readUint32();
		hdBlock.setPointerToFirstDGBlock(pointerToFirstDGBlock);
		l.trace("pointerToFirstDGBlock = " + pointerToFirstDGBlock);

		// pointer to TXBlock (nil allowed)
		long pointerToTXBlock = readUint32();
		hdBlock.setPointerToTXBlock(pointerToTXBlock);
		l.trace("pointerToTXBlock = " + pointerToTXBlock);

		// pointer to PRBlock (nil allowed)
		long pointerToPRBlock = readUint32();
		hdBlock.setPointerToPRBlock(pointerToPRBlock);
		l.trace("pointerToPRBlock = \"" + pointerToPRBlock + "\"");

		// number of data groups
		int numberOfDataGroups = readUint16();
		hdBlock.setNumberOfDataGroups(numberOfDataGroups);
		l.trace("numberOfDataGroups = " + numberOfDataGroups);

		// recording start date
		String recordingStartDate = readChar(10);
		hdBlock.setRecordingStartDate(recordingStartDate);
		l.trace("recordingStartDate = " + recordingStartDate);

		// recording start time
		String recordingStartTime = readChar(8);
		hdBlock.setRecordingStartTime(recordingStartTime);
		l.trace("recordingStartTime = " + recordingStartTime);

		// authors name
		String authorsName = readChar(32);
		hdBlock.setAuthorsName(authorsName);
		l.trace("authorsName = " + authorsName);

		// organizations name
		String organizationsName = readChar(32);
		hdBlock.setOrganizationsName(organizationsName);
		l.trace("organizationsName = " + organizationsName);

		// projects name
		String projectsName = readChar(32);
		hdBlock.setProjectsName(projectsName);
		l.trace("projectsName = " + projectsName);

		// measurement object
		String measurementObject = readChar(32);
		hdBlock.setMeasurementObject(measurementObject);
		l.trace("measurementObject = " + measurementObject);

		// recording start timestamp
		BigInteger recordingStartTimestamp = readUint64();
		hdBlock.setRecordingStartTimestamp(recordingStartTimestamp);
		l.trace("recordingStartTimestamp = " + recordingStartTimestamp);

		// UTC time offset
		int utcTimeOffset = readSint16();
		hdBlock.setUtcTimeOffset(utcTimeOffset);
		l.trace("utcTimeOffset = " + utcTimeOffset);

		// time quality class
		int timeQualityClass = readUint16();
		switch (timeQualityClass) {
		case 0:
			hdBlock.setTimeQualityClass(TimeQualityClass.LOCAL_PC_REFERENCE_TIME);
			break;
		case 10:
			hdBlock.setTimeQualityClass(TimeQualityClass.EXTERNAL_TIME_SOURCE);
			break;
		case 16:
			hdBlock.setTimeQualityClass(TimeQualityClass.EXTERNAL_ABSOLUTE_SYNCHRONIZED_TIME);
			break;
		default:
			throw new IOException("Wrong time quality class (" + timeQualityClass + ").");
		}
		l.trace("hdBlock.timeQualityClass = " + hdBlock.getTimeQualityClass());

		// timer identification
		String timerIdentification = readChar(32);
		hdBlock.setTimerIdentification(timerIdentification);
		l.trace("timerIdentification = " + timerIdentification);

		TXBlock txBlock;
		if (hdBlock.getPointerToTXBlock() != 0) {
			this.filePointer = (int) hdBlock.getPointerToTXBlock();
			txBlock = readTXBlock();
		} else {
			txBlock = null;
		}
		hdBlock.setTXBlock(txBlock);
	}

	/**
	 * <p>
	 * Returns the HDBlock.
	 * </p>
	 * 
	 * @return HDBlock
	 */
	public HDBlock getHDBlock() {
		return hdBlock;
	}

	/**
	 * <p>
	 * Reads the optional comment for the measured data file.
	 * </p>
	 * 
	 * @throws IOException
	 */
	private TXBlock readTXBlock() throws IOException {
		TXBlock txBlock = new TXBlock();
		this.filePointer = (int) hdBlock.getPointerToTXBlock();

		String blockTypeIdentifier = readChar(2);
		if (!blockTypeIdentifier.equals("TX")) {
			throw new IOException(
					"Wrong block type identifier (should be \"TX\", but was \"" + blockTypeIdentifier + "\").");
		}
		txBlock.setBlockTypeIdentifier(blockTypeIdentifier);
		l.trace("blockTypeIdentifier = " + blockTypeIdentifier);

		int blockSize = readUint16();
		txBlock.setBlockSize(blockSize);
		l.trace("blockSize = " + blockSize);

		String text = "";
		for (int i = filePointer; i < filePointer + blockSize - 3; i++) {
			if (content[i] == 0x00) {
				break;
			}
			text += (char) content[i];
		}
		txBlock.setText(text);
		l.trace("text = " + text);

		return txBlock;
	}

	/**
	 * <p>
	 * Reads the optional non-standardized data to exchange between the
	 * acquisition program and the evaluation program.
	 * </p>
	 * 
	 * @throws IOException
	 */
	private void readPRBlock() throws IOException {
		if (hdBlock.getPointerToPRBlock() != 0) {
			prBlock = new PRBlock();
			throw new IOException("Since there weren't any PRBlocks in the test files, this is not implemented yet.");
		} else {
			prBlock = null;
		}
	}

	/**
	 * <p>
	 * Returns the PRBlock.
	 * </p>
	 * 
	 * @return PRBlock
	 */
	public PRBlock getPRBlock() {
		return prBlock;
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void readDGBlocks() throws IOException {
		if (hdBlock.getPointerToFirstDGBlock() != 0) {
			this.filePointer = (int) hdBlock.getPointerToFirstDGBlock();
			dgBlocks = new ArrayList<DGBlock>();

			DGBlock dgBlock;
			do {
				dgBlock = new DGBlock();

				String blockTypeIdentifier = readChar(2);
				if (!blockTypeIdentifier.equals("DG")) {
					throw new IOException(
							"Wrong block type identifier (should be\"DG\", but was \"" + blockTypeIdentifier + "\").");
				}
				dgBlock.setBlockTypeIdentifier(blockTypeIdentifier);
				l.trace("blockTypeIdentifier = " + blockTypeIdentifier);

				int blockSize = readUint16();
				dgBlock.setBlockSize(blockSize);
				l.trace("blockSize = " + blockSize);

				long pointerToNextDGBlock = readUint32();
				dgBlock.setPointerToNextDGBlock(pointerToNextDGBlock);
				l.trace("pointerToNextDGBlock = " + pointerToNextDGBlock);

				long pointerToFirstCGBlock = readUint32();
				dgBlock.setPointerToFirstCGBlock(pointerToFirstCGBlock);
				l.trace("pointerToFirstCGBlock = " + pointerToFirstCGBlock);

				long pointerToTRBlock = readUint32();
				dgBlock.setPointerToTRBlock(pointerToTRBlock);
				l.trace("pointerToTRBlock = " + pointerToTRBlock);

				long pointerToDataBlock = readUint32();
				dgBlock.setPointerToDataBlock(pointerToDataBlock);
				l.trace("pointerToDGBlock = " + pointerToDataBlock);

				int numberOfChannelGroups = readUint16();
				dgBlock.setNumberOfChannelGroups(numberOfChannelGroups);
				l.trace("numberOfChannelGroups = " + numberOfChannelGroups);

				int numberOfRecordIDs = readUint16();
				if (numberOfRecordIDs == 0) {
					dgBlock.setNumberOfRecordIDs(NumberOfRecordIDs.DATA_RECORDS_WITHOUT_RECORD_ID);
				} else {
					throw new IOException("Wrong number of record IDs (should be \"0\", but was \"" + numberOfRecordIDs
							+ "\"). Not implemented yet.");
				}
				l.trace("dgBlock.numberOfRecordIDs = " + dgBlock.getNumberOfRecordIDs());

				long reserved = readUint32();
				dgBlock.setReserved(reserved);
				l.trace("reserved = " + reserved);

				// set file pointer to TRBlock and read it
				if (dgBlock.getPointerToTRBlock() != 0) {
					filePointer = (int) dgBlock.getPointerToTRBlock();
					dgBlock.setTRBlock(readTRBlock());
				} else {
					dgBlock.setTRBlock(null);
				}

				// set file pointer to first CGBlock and read while there are no
				// more CGBlocks
				ArrayList<CGBlock> cgBlocks;
				if (dgBlock.getPointerToFirstCGBlock() != 0) {
					cgBlocks = new ArrayList<CGBlock>();
					this.filePointer = (int) dgBlock.getPointerToFirstCGBlock();

					CGBlock cgBlock;
					do {
						cgBlock = readCGBlock();
						cgBlocks.add(cgBlock);
						this.filePointer = (int) cgBlock.getPointerToNextCGBlock();
					} while (cgBlock.getPointerToNextCGBlock() != 0);
				} else {
					cgBlocks = null;
				}
				dgBlock.setCGBlocks(cgBlocks);

				dgBlocks.add(dgBlock);

				this.filePointer = (int) dgBlock.getPointerToNextDGBlock();
			} while (dgBlock.getPointerToNextDGBlock() != 0);
		} else {
			dgBlocks = null;
		}
	}

	/**
	 * <p>
	 * Returns the DGBlocks.
	 * </p>
	 * 
	 * @return List of DGBlocks
	 */
	public ArrayList<DGBlock> getDGBlocks() {
		return dgBlocks;
	}

	private TRBlock readTRBlock() throws IOException {
		TRBlock trBlock = new TRBlock();

		String blockTypeIdentifier = readChar(2);
		if (!blockTypeIdentifier.equals("TR")) {
			throw new IOException(
					"Wrong block type identifier (should be \"TR\", but was \"" + blockTypeIdentifier + "\").");
		}
		trBlock.setBlockTypeIdentifier(blockTypeIdentifier);
		l.debug("blockTypeIdentifier = " + blockTypeIdentifier);

		return trBlock;
	}

	private CGBlock readCGBlock() throws IOException {
		CGBlock cgBlock = new CGBlock();

		String blockTypeIdentifier = readChar(2);
		if (!blockTypeIdentifier.equals("CG")) {
			throw new IOException(
					"Wrong block type identifier (should be \"CG\", but was \"" + blockTypeIdentifier + "\"");
		}
		cgBlock.setBlockTypeIdentifier(blockTypeIdentifier);
		l.trace("blockTypeIdentifier = " + blockTypeIdentifier);

		int blockSize = readUint16();
		cgBlock.setBlockSize(blockSize);
		l.trace("blockSize = " + blockSize);

		long pointerToNextCGBlock = readUint32();
		cgBlock.setPointerToNextCGBlock(pointerToNextCGBlock);
		l.trace("pointerToNextCGBlock = " + pointerToNextCGBlock);

		long pointerToFirstCNBlock = readUint32();
		cgBlock.setPointerToFirstCNBlock(pointerToFirstCNBlock);
		l.trace("pointerToFirstCNBlock = " + pointerToFirstCNBlock);

		long pointerToTXBlock = readUint32();
		cgBlock.setPointerToTXBlock(pointerToTXBlock);
		l.trace("pointerToTXBlock = " + pointerToTXBlock);

		int recordID = readUint16();
		cgBlock.setRecordID(recordID);
		l.trace("recordID = " + recordID);

		int numberOfChannels = readUint16();
		cgBlock.setNumberOfChannels(numberOfChannels);
		l.trace("numberOfChannels = " + numberOfChannels);

		int sizeOfDataRecord = readUint16();
		cgBlock.setSizeOfDataRecord(sizeOfDataRecord);
		l.trace("sizeOfDataRecord = " + sizeOfDataRecord);

		long numberOfRecords = readUint32();
		cgBlock.setNumberOfRecords(numberOfRecords);
		l.trace("numberOfRecords = " + numberOfRecords);

		long pointerToFirstSRBlock = readUint32();
		cgBlock.setPointerToFirstSRBlock(pointerToFirstSRBlock);
		l.trace("pointerToFirstSRBlock = " + pointerToFirstSRBlock);

		ArrayList<CNBlock> cnBlocks;
		if (cgBlock.getPointerToFirstCNBlock() != 0) {
			this.filePointer = (int) cgBlock.getPointerToFirstCNBlock();
			cnBlocks = new ArrayList<CNBlock>();

			CNBlock cnBlock;
			do {
				cnBlock = readCNBlock();
				cnBlocks.add(cnBlock);
				this.filePointer = (int) cnBlock.getPointerToNextCNBlock();
			} while (cnBlock.getPointerToNextCNBlock() != 0);
		} else {
			cnBlocks = null;
		}
		cgBlock.setCNBlocks(cnBlocks);

		return cgBlock;
	}

	private CNBlock readCNBlock() throws IOException {
		CNBlock cnBlock = new CNBlock();

		String blockTypeIdentifier = readChar(2);
		if (!blockTypeIdentifier.equals("CN")) {
			throw new IOException(
					"Wrong block type identifier (should be \"CN\", but was \"" + blockTypeIdentifier + "\").");
		}
		cnBlock.setBlockTypeIdentifier(blockTypeIdentifier);
		l.trace("blockTypeIdentifier = " + blockTypeIdentifier);

		int blockSize = readUint16();
		cnBlock.setBlockSize(blockSize);
		l.trace("blockSize = " + blockSize);

		long pointerToNextCNBlock = readUint32();
		cnBlock.setPointerToNextCNBlock(pointerToNextCNBlock);
		l.trace("pointerToNextCNBlock = " + pointerToNextCNBlock);

		long pointerToCCBlock = readUint32();
		cnBlock.setPointerToCCBlock(pointerToCCBlock);
		l.trace("pointerToCCBlock = " + pointerToCCBlock);

		long pointerToCEBlock = readUint32();
		cnBlock.setPointerToCEBlock(pointerToCEBlock);
		l.trace("pointerToCEBlock = " + pointerToCEBlock);

		long pointerToCDBlock = readUint32();
		cnBlock.setPointerToCDBlock(pointerToCDBlock);
		l.trace("pointerToCDBlock = " + pointerToCDBlock);

		long pointerToTXBlock = readUint32();
		cnBlock.setPointerToTXBlock(pointerToTXBlock);
		l.trace("pointerToTXBlock = " + pointerToTXBlock);

		int channelType = readUint16();
		if (channelType == 1) {
			cnBlock.setChannelType(ChannelType.TIME_CHANNEL);
		} else {
			cnBlock.setChannelType(ChannelType.DATA_CHANNEL);
		}
		l.trace("cnBlock.channelType = " + cnBlock.getChannelType());

		String shortSignalName = readChar(32);
		cnBlock.setShortSignalName(shortSignalName);
		l.trace("shortSignalName = " + shortSignalName);

		String signalDescription = readChar(128);
		cnBlock.setSignalDescription(signalDescription);
		l.trace("signalDescription = \"" + signalDescription + "\"");

		int startOffsetInBits = readUint16();
		cnBlock.setStartOffsetInBits(startOffsetInBits);
		l.trace("startOffsetInBits = " + startOffsetInBits);

		int numberOfBits = readUint16();
		cnBlock.setNumberOfBits(numberOfBits);
		l.trace("numberOfBits = " + numberOfBits);

		int signalDataType = readUint16();
		switch (signalDataType) {
		case 0:
			cnBlock.setSignalDataType(SignalDataType.UNSIGNED_INTEGER);
			break;
		case 1:
			cnBlock.setSignalDataType(SignalDataType.SIGNED_INTEGER);
			break;
		case 2:
			cnBlock.setSignalDataType(SignalDataType.IEEE_754_FLOATING_POINT_FORMAT_FLOAT);
			break;
		case 3:
			cnBlock.setSignalDataType(SignalDataType.IEEE_754_FLOATING_POINT_FORMAT_DOUBLE);
			break;
		case 4:
			cnBlock.setSignalDataType(SignalDataType.VAX_FLOATING_POINT_FORMAT_F_FLOAT);
			break;
		case 5:
			cnBlock.setSignalDataType(SignalDataType.VAX_FLOATING_POINT_FORMAT_G_FLOAT);
			break;
		case 6:
			cnBlock.setSignalDataType(SignalDataType.VAX_FLOATING_POINT_FORMAT_D_FLOAT);
			break;
		case 7:
			cnBlock.setSignalDataType(SignalDataType.STRING);
			break;
		case 8:
			cnBlock.setSignalDataType(SignalDataType.BYTE_ARRAY);
			break;
		case 9:
			cnBlock.setSignalDataType(SignalDataType.UNSIGNED_INTEGER_BIG_ENDIAN);
			break;
		case 10:
			cnBlock.setSignalDataType(SignalDataType.SIGNED_INTEGER_BIG_ENDIAN);
			break;
		case 11:
			cnBlock.setSignalDataType(SignalDataType.IEEE_754_FLOATING_POINT_FORMAT_FLOAT_BIG_ENDIAN);
			break;
		case 12:
			cnBlock.setSignalDataType(SignalDataType.IEEE_754_FLOATING_POINT_FORMAT_DOUBLE_BIG_ENDIAN);
			break;
		case 13:
			cnBlock.setSignalDataType(SignalDataType.UNSIGNED_INTEGER_LITTLE_ENDIAN);
			break;
		case 14:
			cnBlock.setSignalDataType(SignalDataType.SIGNED_INTEGER_LITTLE_ENDIAN);
			break;
		case 15:
			cnBlock.setSignalDataType(SignalDataType.IEEE_754_FLOATING_POINT_FORMAT_FLOAT_LITTLE_ENDIAN);
			break;
		case 16:
			cnBlock.setSignalDataType(SignalDataType.IEEE_754_FLOATING_POINT_FORMAT_DOUBLE_LITTLE_ENDIAN);
			break;
		default:
			throw new IOException(
					"Wrong signal data type (should be between 0 (inclusive) and 17 (exclusive), but was \""
							+ signalDataType + "\").");
		}
		l.trace("cnBlock.signalDataType = " + cnBlock.getSignalDataType());

		boolean valueRangeValid;
		valueRangeValid = readUint16() > 0;
		cnBlock.setValueRangeValid(valueRangeValid);
		l.trace("valueRangeValid = " + valueRangeValid);

		double minimumSignalValue = readDouble();
		cnBlock.setMinimumSignalValue(minimumSignalValue);
		l.trace("minimumSignalValue = " + minimumSignalValue);

		double maximumSignalValue = readDouble();
		cnBlock.setMaximumSignalValue(maximumSignalValue);
		l.trace("maximumSignalValue = " + maximumSignalValue);

		double samplingRate = readDouble();
		cnBlock.setSamplingRate(samplingRate);
		l.trace("samplingRate = " + samplingRate);

		long pointerToTXBlockLongSignalName = readUint32();
		cnBlock.setPointerToTXBlockLongSignalName(pointerToTXBlockLongSignalName);
		l.trace("pointerToTXBlockLongSignalName = " + pointerToTXBlockLongSignalName);

		long pointerToTXBlockDisplayName = readUint32();
		cnBlock.setPointerToTXBlockDisplayName(pointerToTXBlockDisplayName);
		l.trace("pointerToTXBlockDisplayName = " + pointerToTXBlockDisplayName);

		int additionalByteOffset = readUint16();
		cnBlock.setAdditionalByteOffset(additionalByteOffset);
		l.trace("additionalByteOffset = " + additionalByteOffset);

		CCBlock ccBlock;
		if (cnBlock.getPointerToCCBlock() != 0) {
			this.filePointer = (int) cnBlock.getPointerToCCBlock();
			ccBlock = readCCBlock();
		} else {
			ccBlock = null;
		}
		cnBlock.setCcBlock(ccBlock);

		CEBlock ceBlock;
		if (cnBlock.getPointerToCEBlock() != 0) {
			filePointer = (int) cnBlock.getPointerToCEBlock();
			ceBlock = readCEBlock();
		} else {
			ceBlock = null;
		}
		cnBlock.setCeBlock(ceBlock);

		CDBlock cdBlock;
		if (cnBlock.getPointerToCDBlock() != 0) {
			filePointer = (int) cnBlock.getPointerToCDBlock();
			cdBlock = readCDBlock();
		} else {
			cdBlock = null;
		}
		cnBlock.setCdBlock(cdBlock);

		TXBlock txBlock;
		if (cnBlock.getPointerToTXBlock() != 0) {
			this.filePointer = (int) cnBlock.getPointerToTXBlock();
			txBlock = readTXBlock();
		} else {
			txBlock = null;
		}
		cnBlock.setTxBlock(txBlock);

		if (cnBlock.getPointerToTXBlockLongSignalName() != 0) {
			filePointer = (int) cnBlock.getPointerToTXBlockLongSignalName();
			txBlock = readTXBlock();
		} else {
			txBlock = null;
		}
		cnBlock.setTxBlockLongSignalName(txBlock);

		if (cnBlock.getPointerToTXBlockDisplayName() != 0) {
			filePointer = (int) cnBlock.getPointerToTXBlockDisplayName();
			txBlock = readTXBlock();
		} else {
			txBlock = null;
		}
		cnBlock.setTxBlockDisplayName(txBlock);

		return cnBlock;
	}

	private CCBlock readCCBlock() throws IOException {
		CCBlock result = null;

		String blockTypeIdentifier = readChar(2);
		if (!blockTypeIdentifier.equals("CC")) {
			throw new IOException(
					"Wrong block type identifier (should be \"CC\", but was \"" + blockTypeIdentifier + "\").");
		}
		l.trace("blockTypeIdentifier = " + blockTypeIdentifier);

		int blockSize = readUint16();
		l.trace("blockSize = " + blockSize);

		boolean physicalValueRangeValid = readUint16() > 0;
		l.trace("physicalValueRangeValid = " + physicalValueRangeValid);

		double minimumPhysicalSignalValue = readDouble();
		l.trace("minimumPhysicalSignalValue = " + minimumPhysicalSignalValue);

		double maximumPhysicalSignalValue = readDouble();
		l.trace("maximumPhysicalSignalvalue = " + maximumPhysicalSignalValue);

		String physicalUnit = readChar(20);
		l.trace("physicalUnit = \"" + physicalUnit + "\"");

		int conversionType = readUint16();
		l.trace("conversionType = " + conversionType);

		int sizeInformation = readUint16();
		l.trace("sizeInformation = " + sizeInformation);

		switch (conversionType) {
		case 0:
			if (sizeInformation == 2) {
				result = new CCBlockLinear2Parameters();

				double p1 = readDouble();
				((CCBlockLinear2Parameters) result).setP1(p1);
				l.trace("p1 = " + p1);

				double p2 = readDouble();
				((CCBlockLinear2Parameters) result).setP2(p2);
				l.trace("p2 = " + p2);
			} else {
				throw new IOException("Wrong size information (" + sizeInformation + "). Not implemented yet.");
			}
			result.setConversionType(ConversionType.PARAMETRIC_LINEAR);
			break;
		case 65535:
			result = new CCBlockOneToOne();
			result.setConversionType(ConversionType.ONE_TO_ONE_FORMULA);
			break;
		default:
			throw new IOException("Wrong conversion type (" + conversionType + "). Not implemented yet.");
		}

		result.setBlockTypeIdentifier(blockTypeIdentifier);
		result.setBlockSize(blockSize);
		result.setPhysicalValueRangeValid(physicalValueRangeValid);
		result.setMinimumPhysicalSignalValue(minimumPhysicalSignalValue);
		result.setMaximumPhysicalSignalValue(maximumPhysicalSignalValue);
		result.setPhysicalUnit(physicalUnit);
		result.setSizeInformation(sizeInformation);

		return result;
	}

	private CEBlock readCEBlock() throws IOException {
		throw new IOException("Not implemented yet.");
	}

	private CDBlock readCDBlock() throws IOException {
		throw new IOException("Not implemented yet.");
	}

	private void readData() throws IOException {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}

		for (DGBlock dgBlock : dgBlocks) {
			if (dgBlock.getNumberOfRecordIDs() != NumberOfRecordIDs.DATA_RECORDS_WITHOUT_RECORD_ID) {
				throw new IOException("Data records without record IDs not implemented yet.");
			}
			l.trace("dgBlock.numberOfRecordIDs = " + dgBlock.getNumberOfRecordIDs());
			ArrayList<CGBlock> cgBlocks = dgBlock.getCgBlocks();
			for (CGBlock cgBlock : cgBlocks) {
				l.trace(" cgBlock.sizeOfDataRecord = " + cgBlock.getSizeOfDataRecord());
				l.trace(" cgBlock.numberOfRecords = " + cgBlock.getNumberOfRecords());
				ArrayList<CNBlock> cnBlocks = cgBlock.getCNBlocks();
				for (CNBlock cnBlock : cnBlocks) {
					l.trace("  cnBlock.shortSignalName = " + cnBlock.getShortSignalName());

					int position = (int) dgBlock.getPointerToDataBlock();
					l.trace("   position = " + position);

					l.trace("   cnBlock.additionalByteOffset = " + cnBlock.getAdditionalByteOffset());
					position += cnBlock.getAdditionalByteOffset();
					l.trace("   position = " + position);
					l.trace("   cnBlock.startOffsetInBits = " + cnBlock.getStartOffsetInBits());
					if (cnBlock.getStartOffsetInBits() % 8 != 0) {
						throw new IOException("Unaligned bytes not implemented yet.");
					}
					position += (cnBlock.getStartOffsetInBits() / 8);
					l.trace("   position = " + position);

					l.trace("   cnBlock.signalDataType = " + cnBlock.getSignalDataType());
					l.trace("   cnBlock.numberOfBits = " + cnBlock.getNumberOfBits());

					for (int i = 0; i < cgBlock.getNumberOfRecords(); i++) {
						if (cnBlock.getSignalDataType().equals(SignalDataType.IEEE_754_FLOATING_POINT_FORMAT_DOUBLE)) {
							if (cnBlock.getNumberOfBits() == 64) {
								// l.debug("position = " + position);
								BigInteger result = readUint64(content[position], content[position + 1],
										content[position + 2], content[position + 3], content[position + 4],
										content[position + 5], content[position + 6], content[position + 7], byteOrder);
								// l.debug("result = " + result);
								position += cgBlock.getSizeOfDataRecord();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * Reads one or more chars with {@code count} given bytes starting at
	 * {@code filePointer}.
	 * </p>
	 * 
	 * @param filePointer
	 *            position to start reading
	 * @param count
	 *            number of bytes to read
	 * @return chars read
	 */
	private String readChar(int count) {
		String result = "";
		for (int i = filePointer; i < filePointer + count; i++) {
			result += (char) this.content[i];
		}
		filePointer += count;
		return result;
	}

	/**
	 * <p>
	 * Reads a uint16 from two bytes starting at {@code filePointer} with
	 * default byte order (or little endian if default byte order is not set
	 * yet).
	 * </p>
	 * <p>
	 * Automatically increases {@code filePointer} by 2.
	 * </p>
	 * 
	 * @param filePointer
	 *            position to start reading
	 * @return uin16
	 * @throws IOException
	 *             if there is an error reading the uin16
	 */
	private int readUint16() throws IOException {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}
		int result = readUint16(this.content[filePointer], this.content[filePointer + 1], byteOrder);
		filePointer += 2;
		return result;
	}

	/**
	 * <p>
	 * Reads a uint16 from two bytes.
	 * </p>
	 * 
	 * @param byte1
	 *            first byte
	 * @param byte2
	 *            second byte
	 * @param byteOrder
	 *            byte order
	 * @return uint16
	 * @throws IOException
	 *             if there is an error reading the uint16
	 */
	private static int readUint16(byte byte1, byte byte2, ByteOrder byteOrder) throws IOException {
		int result = 0;

		if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
			int int1 = byte1;
			if (byte1 < 0) {
				int1 = byte1 << 24;
				int1 = int1 >>> 24;
			}
			int int2 = byte2;
			if (byte2 < 0) {
				int2 = byte2 << 24;
				int2 = int2 >>> 24;
			}
			int2 = int2 << 8;

			result = int1 + int2;
		} else {
			throw new IOException(
					"Wrong byte order (should be LITTLE_ENDIAN, but was BIG_ENDIAN). Not implemented yet.");
		}

		return result;
	}

	private short readSint16() {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}
		short result = readSint16(content[filePointer], content[filePointer + 1], byteOrder);
		filePointer += 2;
		return result;
	}

	private static short readSint16(byte byte1, byte byte2, ByteOrder byteOrder) {
		ByteBuffer bb = ByteBuffer.allocateDirect(2);
		bb.put(byte1);
		bb.put(byte2);
		if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
			bb.order(ByteOrder.LITTLE_ENDIAN);
		} else {
			bb.order(ByteOrder.BIG_ENDIAN);
		}
		bb.flip();
		return bb.getShort();
	}

	private long readUint32() throws IOException {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}
		long result = readUint32(content[filePointer], content[filePointer + 1], content[filePointer + 2],
				content[filePointer + 3], byteOrder);
		filePointer += 4;
		return result;
	}

	private static long readUint32(byte byte1, byte byte2, byte byte3, byte byte4, ByteOrder byteOrder)
			throws IOException {
		if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.put(byte1);
			bb.put(byte2);
			bb.put(byte3);
			bb.put(byte4);
			bb.put((byte) 0x00);
			bb.put((byte) 0x00);
			bb.put((byte) 0x00);
			bb.put((byte) 0x00);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.flip();
			return bb.getLong();
		} else {
			throw new IOException(
					"Wrong byte order (should be LITTLE_ENDIAN, but was BIG_ENDIAN). Not implemented yet.");
		}
	}

	private BigInteger readUint64() throws IOException {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}
		BigInteger result = readUint64(content[filePointer], content[filePointer + 1], content[filePointer + 2],
				content[filePointer + 3], content[filePointer + 4], content[filePointer + 5], content[filePointer + 6],
				content[filePointer + 7], byteOrder);
		filePointer += 8;
		return result;
	}

	private static BigInteger readUint64(byte byte1, byte byte2, byte byte3, byte byte4, byte byte5, byte byte6,
			byte byte7, byte byte8, ByteOrder byteOrder) throws IOException {
		if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
			BigInteger result = new BigInteger(((long) byte1 & 0xFF) + "");
			result = result.add(new BigInteger((((long) byte2 & 0xFF) << 8) + ""));
			result = result.add(new BigInteger((((long) byte3 & 0xFF) << 16) + ""));
			result = result.add(new BigInteger((((long) byte4 & 0xFF) << 24) + ""));
			result = result.add(new BigInteger((((long) byte5 & 0xFF) << 32) + ""));
			result = result.add(new BigInteger((((long) byte6 & 0xFF) << 40) + ""));
			result = result.add(new BigInteger((((long) byte7 & 0xFF) << 48) + ""));
			result = result.add(new BigInteger((((long) byte8 & 0xFF) << 56) + ""));
			return result;
			/*
			 * long l = (long)b[0] & 0xFF; l += ((long)b[1] & 0xFF) << 8; l +=
			 * ((long)b[2] & 0xFF) << 16; l += ((long)b[3] & 0xFF) << 24;
			 */
		} else {
			throw new IOException(
					"Wrong byte order (should be LITTLE_ENDIAN, but was BIG_ENDIAN). Not implemented yet.");
		}
	}

	private double readDouble() {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}
		double result = readDouble(content[filePointer], content[filePointer + 1], content[filePointer + 2],
				content[filePointer + 3], content[filePointer + 4], content[filePointer + 5], content[filePointer + 6],
				content[filePointer + 7], byteOrder);
		filePointer += 8;
		return result;
	}

	private static double readDouble(byte byte1, byte byte2, byte byte3, byte byte4, byte byte5, byte byte6, byte byte7,
			byte byte8, ByteOrder byteOrder) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(byte1);
		buffer.put(byte2);
		buffer.put(byte3);
		buffer.put(byte4);
		buffer.put(byte5);
		buffer.put(byte6);
		buffer.put(byte7);
		buffer.put(byte8);
		buffer.order(byteOrder);
		buffer.flip();
		return buffer.getDouble();
	}

}

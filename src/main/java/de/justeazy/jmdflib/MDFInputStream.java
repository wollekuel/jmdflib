package de.justeazy.jmdflib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.justeazy.jmdflib.blocktypes.HDBlock;
import de.justeazy.jmdflib.blocktypes.IDBlock;
import java.nio.ByteOrder;
import de.justeazy.jmdflib.enums.FloatingPointFormat;
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
		String fileIdentifier = "";
		for (int i = 0; i < 8; i++) {
			fileIdentifier += (char) this.content[i];
		}
		this.filePointer += 8;
		if (!fileIdentifier.equals("MDF     ")) {
			throw new IOException("Wrong file identifier (should be \"MDF     \", but was \"" + fileIdentifier + "\")");
		}
		idBlock.setFileIdentifier(fileIdentifier);
		l.trace("fileIdentifier = " + fileIdentifier);

		// format identifier
		String formatIdentifier = "";
		for (int i = this.filePointer; i < (this.filePointer + 8); i++) {
			formatIdentifier += (char) this.content[i];
		}
		this.filePointer += 8;
		Pattern pFormatIdentifier = Pattern.compile("\\d+\\x2E\\d{2}\\s{4}");
		m = pFormatIdentifier.matcher(formatIdentifier);
		if (!m.find()) {
			throw new IOException("Wrong format identifier (" + formatIdentifier + ").");
		}
		idBlock.setFormatIdentifier(formatIdentifier);
		l.trace("formatIdentifier = \"" + formatIdentifier + "\"");

		// program identifier
		String programIdentifier = "";
		for (int i = this.filePointer; i < (this.filePointer + 8); i++) {
			programIdentifier += (char) this.content[i];
		}
		this.filePointer += 8;
		idBlock.setProgramIdentifier(programIdentifier);
		l.trace("programIdentifier = \"" + programIdentifier + "\"");

		// default byte order
		ByteOrder defaultByteOrder = readUint16(this.content[this.filePointer], this.content[this.filePointer + 1]) == 0
				? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
		this.filePointer += 2;
		idBlock.setDefaultByteOrder(defaultByteOrder);
		l.trace("defaultByteOrder = " + defaultByteOrder);
		if (defaultByteOrder != ByteOrder.LITTLE_ENDIAN) {
			throw new IOException(
					"Wrong byte order (should be LITTLE_ENDIAN, but was BIG_ENDIAN). BIG_ENDIAN is not implemented yet");
		}

		// default floating point format
		int floatingPointFormat = readUint16(this.content[this.filePointer], this.content[this.filePointer + 1]);
		this.filePointer += 2;
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
		int versionNumber = readUint16(this.content[this.filePointer], this.content[this.filePointer + 1]);
		this.filePointer += 2;
		idBlock.setVersionNumber(versionNumber);
		l.trace("versionNumber = " + versionNumber);

		// code page
		int codePage = readUint16(this.content[this.filePointer], this.content[this.filePointer + 1]);
		this.filePointer += 2;
		idBlock.setCodePage(codePage);
		l.trace("codePage = " + codePage);

		// first reserved structure
		String reservedStructure1 = "";
		for (int i = this.filePointer; i < this.filePointer + 2; i++) {
			reservedStructure1 += (char) this.content[i];
		}
		this.filePointer += 2;
		idBlock.setReservedStructure1(reservedStructure1);
		l.trace("reservedStructure1 = \"" + reservedStructure1 + "\"");

		// second reserved structure
		String reservedStructure2 = "";
		for (int i = this.filePointer; i < this.filePointer + 26; i++) {
			reservedStructure2 += (char) this.content[i];
		}
		this.filePointer += 26;
		idBlock.setReservedStructure2(reservedStructure2);
		l.trace("reservedStructure2 = \"" + reservedStructure2 + "\"");

		// standard flags for unfinalized mdfs
		int standardFlags = readUint16(this.content[this.filePointer], this.content[this.filePointer + 1]);
		this.filePointer += 2;
		idBlock.setStandardFlags(standardFlags);
		l.trace("standardFlags = " + standardFlags);
		if (standardFlags != 0) {
			throw new IOException("Wrong standard flags (should be 0, but was \"" + standardFlags
					+ "\"). Unfinalized MDFs are not supported yet.");
		}

		// custom flags for unfinalized mdfs
		int customFlags = readUint16(this.content[this.filePointer], this.content[this.filePointer + 1]);
		this.filePointer += 2;
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
	public IDBlock getIdBlock() {
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
		int count;
		this.filePointer = 64;
		hdBlock = new HDBlock();

		// block type identifier
		count = 2;
		String blockTypeIdentifier = readChar(filePointer, count);
		hdBlock.setBlockTypeIdentifier(blockTypeIdentifier);
		this.filePointer += count;
		l.trace("blockTypeIdentifier = " + blockTypeIdentifier);

		// block size
		int blockSize = readUint16(this.content[this.filePointer], this.content[this.filePointer + 1]);
		this.filePointer += 2;
		hdBlock.setBlockSize(blockSize);
		l.trace("blockSize = " + blockSize);

		// pointer to first DGBlock
		long pointerToFirstDGBlock = readUint32(this.content[this.filePointer], this.content[this.filePointer + 1],
				this.content[this.filePointer + 2], this.content[this.filePointer + 3]);
		this.filePointer += 4;
		hdBlock.setPointerToFirstDGBlock(pointerToFirstDGBlock);
		l.trace("pointerToFirstDGBlock = " + pointerToFirstDGBlock);

		// pointer to TXBlock (nil allowed)
		long pointerToTXBlock = readUint32(this.content[this.filePointer], this.content[this.filePointer + 1],
				this.content[this.filePointer + 2], this.content[this.filePointer + 3]);
		this.filePointer += 4;
		hdBlock.setPointerToTXBlock(pointerToTXBlock);
		l.trace("pointerToTXBlock = " + pointerToTXBlock);

		// pointer to PRBlock (nil allowed)
		long pointerToPRBlock = readUint32(this.content[this.filePointer], this.content[this.filePointer + 1],
				this.content[this.filePointer + 2], this.content[this.filePointer + 3]);
		this.filePointer += 4;
		hdBlock.setPointerToPRBlock(pointerToPRBlock);
		l.trace("pointerToPRBlock = " + pointerToPRBlock);

		// number of data groups
		int numberOfDataGroups = readUint16(this.content[this.filePointer], this.content[this.filePointer + 1]);
		this.filePointer += 2;
		hdBlock.setNumberOfDataGroups(numberOfDataGroups);
		l.trace("numberOfDataGroups = " + numberOfDataGroups);

		// recording start date
		count = 10;
		String recordingStartDate = readChar(filePointer, count);
		hdBlock.setRecordingStartDate(recordingStartDate);
		this.filePointer += count;
		l.trace("recordingStartDate = " + recordingStartDate);

		// recording start time
		count = 8;
		String recordingStartTime = readChar(filePointer, count);
		hdBlock.setRecordingStartTime(recordingStartTime);
		this.filePointer += count;
		l.trace("recordingStartTime = " + recordingStartTime);

		// authors name
		count = 32;
		String authorsName = readChar(filePointer, count);
		hdBlock.setAuthorsName(authorsName);
		this.filePointer += count;
		l.trace("authorsName = " + authorsName);

		// organizations name
		count = 32;
		String organizationsName = readChar(filePointer, count);
		hdBlock.setOrganizationsName(organizationsName);
		this.filePointer += count;
		l.trace("organizationsName = " + organizationsName);

		// projects name
		count = 32;
		String projectsName = readChar(filePointer, count);
		hdBlock.setProjectsName(projectsName);
		filePointer += count;
		l.trace("projectsName = " + projectsName);

		// measurement object
		count = 32;
		String measurementObject = readChar(this.filePointer, count);
		hdBlock.setMeasurementObject(measurementObject);
		this.filePointer += count;
		l.trace("measurementObject = " + measurementObject);

		// recording start timestamp
		BigInteger recordingStartTimestamp = readUint64(this.content[this.filePointer],
				this.content[this.filePointer + 1], this.content[this.filePointer + 2],
				this.content[this.filePointer + 3], this.content[this.filePointer + 4],
				this.content[this.filePointer + 5], this.content[this.filePointer + 6],
				this.content[this.filePointer + 7]);
		this.filePointer += 8;
		hdBlock.setRecordingStartTimestamp(recordingStartTimestamp);
		l.trace("recordingStartTimestamp = " + recordingStartTimestamp);

		// UTC time offset
		int utcTimeOffset = readSint16(this.content[this.filePointer], this.content[this.filePointer + 1]);
		this.filePointer += 2;
		hdBlock.setUtcTimeOffset(utcTimeOffset);
		l.trace("utcTimeOffset = " + utcTimeOffset);

		// time quality class
		int timeQualityClass = readUint16(this.content[this.filePointer], this.content[this.filePointer + 1]);
		this.filePointer += 2;
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
		count = 32;
		String timerIdentification = readChar(this.filePointer, count);
		hdBlock.setTimerIdentification(timerIdentification);
		this.filePointer += count;
		l.trace("timerIdentification = " + timerIdentification);
	}

	/**
	 * <p>
	 * Returns the HDBlock.
	 * </p>
	 * 
	 * @return HDBlock
	 */
	public HDBlock getHdBlock() {
		return hdBlock;
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
	private String readChar(int filePointer, int count) {
		String result = "";
		for (int i = filePointer; i < filePointer + count; i++) {
			result += (char) this.content[i];
		}
		return result;
	}

	/**
	 * <p>
	 * Reads a uint16 from two bytes starting at {@code filePointer} with
	 * default byte order (or little endian if default byte order is not set
	 * yet).
	 * </p>
	 * 
	 * @param filePointer
	 *            position to start reading
	 * @return uin16
	 * @throws IOException
	 *             if there is an error reading the uin16
	 */
	private int readUint16(int filePointer) throws IOException {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}
		return readUint16(this.content[filePointer], this.content[filePointer + 1], byteOrder);
	}

	/**
	 * <p>
	 * Reads a uint16 from two bytes with default byte order (or little endian
	 * if default byte order is not set yet).
	 * </p>
	 * 
	 * @param byte1
	 *            first byte
	 * @param byte2
	 *            second byte
	 * @return uint16
	 * @throws IOException
	 *             if there is an error reading the uint16
	 */
	private int readUint16(byte byte1, byte byte2) throws IOException {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}
		return readUint16(byte1, byte2, byteOrder);
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

	private short readSint16(byte byte1, byte byte2) {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}
		return readSint16(byte1, byte2, byteOrder);
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

	private long readUint32(byte byte1, byte byte2, byte byte3, byte byte4) throws IOException {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}
		return readUint32(byte1, byte2, byte3, byte4, byteOrder);
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

	private BigInteger readUint64(byte byte1, byte byte2, byte byte3, byte byte4, byte byte5, byte byte6, byte byte7,
			byte byte8) throws IOException {
		ByteOrder byteOrder;
		if (this.idBlock == null || idBlock.getDefaultByteOrder() == null) {
			byteOrder = ByteOrder.LITTLE_ENDIAN;
		} else {
			byteOrder = idBlock.getDefaultByteOrder();
		}
		return readUint64(byte1, byte2, byte3, byte4, byte5, byte6, byte7, byte8, byteOrder);
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

}

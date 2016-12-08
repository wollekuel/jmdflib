package de.justeazy.jmdflib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.justeazy.jmdflib.blocktypes.IDBlock;
import de.justeazy.jmdflib.enums.ByteOrder;
import de.justeazy.jmdflib.enums.FloatingPointFormat;

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
		if (defaultByteOrder == ByteOrder.BIG_ENDIAN) {
			throw new IOException("Not implemented yet");
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
	 * Reads a uint16 from two bytes with little endian byte order.
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
	private static int readUint16(byte byte1, byte byte2) throws IOException {
		return readUint16(byte1, byte2, ByteOrder.LITTLE_ENDIAN);
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

		switch (byteOrder) {
		case LITTLE_ENDIAN:
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
			break;
		default:
			throw new IOException("Not implemented yet.");
		}

		return result;
	}

}

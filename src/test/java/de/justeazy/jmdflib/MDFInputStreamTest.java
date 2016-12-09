package de.justeazy.jmdflib;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.BigInteger;

import de.justeazy.jmdflib.blocktypes.HDBlock;
import de.justeazy.jmdflib.blocktypes.IDBlock;
import java.nio.ByteOrder;
import de.justeazy.jmdflib.enums.FloatingPointFormat;
import de.justeazy.jmdflib.enums.TimeQualityClass;
import junit.framework.TestCase;

public class MDFInputStreamTest extends TestCase {

	MDFInputStream is;

	public MDFInputStreamTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		is = new MDFInputStream(new File("src/test/resources/junit1.dat"));
		is.readFile();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		is.close();
	}

	public void testMDFFileTooLarge() throws Exception {
		// TODO write test case
	}

	public void testReadFile() throws Exception {
		// TODO write test case
	}

	public void testIdBlock() throws Exception {
		IDBlock idBlock = is.getIdBlock();
		assertThat(idBlock.getFileIdentifier()).isEqualTo("MDF     ");
		assertThat(idBlock.getFormatIdentifier()).isEqualTo("3.30    ");
		assertThat(idBlock.getProgramIdentifier()).isEqualTo("MDA v7.1");
		assertThat(idBlock.getDefaultByteOrder()).isEqualTo(ByteOrder.LITTLE_ENDIAN);
		assertThat(idBlock.getDefaultFloatingPointFormat()).isEqualTo(FloatingPointFormat.IEEE_754);
		assertThat(idBlock.getVersionNumber()).isEqualTo(330);
		assertThat(idBlock.getCodePage()).isEqualTo(0);
		assertThat(idBlock.getReservedStructure1().length()).isEqualTo(2);
		assertThat(idBlock.getReservedStructure1().trim()).isEqualTo("");
		assertThat(idBlock.getReservedStructure2().length()).isEqualTo(26);
		assertThat(idBlock.getReservedStructure2().trim()).isEqualTo("");
		assertThat(idBlock.getStandardFlags()).isEqualTo(0);
		assertThat(idBlock.getCustomFlags()).isEqualTo(0);
	}

	public void testHdBlock() throws Exception {
		HDBlock hdBlock = is.getHdBlock();
		assertThat(hdBlock.getBlockTypeIdentifier()).isEqualTo("HD");
		assertThat(hdBlock.getBlockSize()).isEqualTo(208);
		assertThat(hdBlock.getPointerToFirstDGBlock()).isEqualTo(1188);
		assertThat(hdBlock.getPointerToTXBlock()).isEqualTo(567);
		assertThat(hdBlock.getPointerToPRBlock()).isEqualTo(0);
		assertThat(hdBlock.getNumberOfDataGroups()).isEqualTo(1);
		assertThat(hdBlock.getRecordingStartDate()).isEqualTo("08:12:2016");
		assertThat(hdBlock.getRecordingStartTime()).isEqualTo("17:28:22");
		assertThat(hdBlock.getAuthorsName().length()).isEqualTo(32);
		assertThat(hdBlock.getAuthorsName().trim()).isEmpty();
		assertThat(hdBlock.getOrganizationsName().length()).isEqualTo(32);
		assertThat(hdBlock.getOrganizationsName().trim()).isEmpty();
		assertThat(hdBlock.getProjectsName().length()).isEqualTo(32);
		assertThat(hdBlock.getProjectsName().trim()).isEmpty();
		assertThat(hdBlock.getMeasurementObject().length()).isEqualTo(32);
		assertThat(hdBlock.getMeasurementObject().trim()).isEmpty();
		assertThat(hdBlock.getRecordingStartTimestamp()).isEqualTo(new BigInteger("1481214502000000000"));
		assertThat(hdBlock.getUtcTimeOffset()).isEqualTo(1);
		assertThat(hdBlock.getTimeQualityClass()).isEqualTo(TimeQualityClass.LOCAL_PC_REFERENCE_TIME);
		assertThat(hdBlock.getTimerIdentification().length()).isEqualTo(32);
		assertThat(hdBlock.getTimerIdentification().trim()).isEqualTo("0");
	}

}

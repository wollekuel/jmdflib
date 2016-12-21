package de.justeazy.jmdflib;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.BigInteger;

import de.justeazy.jmdflib.blocktypes.CCBlock;
import de.justeazy.jmdflib.blocktypes.CCBlockLinear2Parameters;
import de.justeazy.jmdflib.blocktypes.CCBlockOneToOne;
import de.justeazy.jmdflib.blocktypes.CGBlock;
import de.justeazy.jmdflib.blocktypes.CNBlock;
import de.justeazy.jmdflib.blocktypes.DGBlock;
import de.justeazy.jmdflib.blocktypes.HDBlock;
import de.justeazy.jmdflib.blocktypes.IDBlock;
import de.justeazy.jmdflib.blocktypes.TXBlock;

import java.nio.ByteOrder;
import java.util.ArrayList;

import de.justeazy.jmdflib.enums.ChannelType;
import de.justeazy.jmdflib.enums.ConversionType;
import de.justeazy.jmdflib.enums.FloatingPointFormat;
import de.justeazy.jmdflib.enums.NumberOfRecordIDs;
import de.justeazy.jmdflib.enums.SignalDataType;
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
		IDBlock idBlock = is.getIDBlock();
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
		HDBlock hdBlock = is.getHDBlock();
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

		TXBlock txBlock = hdBlock.getTXBlock();
		assertThat(txBlock.getBlockTypeIdentifier()).isEqualTo("TX");
		assertThat(txBlock.getBlockSize()).isEqualTo(5);
		assertThat(txBlock.getText()).isEmpty();
	}

	public void testDgBlocks() throws Exception {
		ArrayList<DGBlock> dgBlocks = is.getDGBlocks();
		assertThat(dgBlocks).hasSize(1);
		DGBlock dgBlock = dgBlocks.get(0);
		assertThat(dgBlock.getBlockTypeIdentifier()).isEqualTo("DG");
		assertThat(dgBlock.getBlockSize()).isEqualTo(28);
		assertThat(dgBlock.getPointerToNextDGBlock()).isEqualTo(0);
		assertThat(dgBlock.getPointerToFirstCGBlock()).isEqualTo(1158);
		assertThat(dgBlock.getPointerToTRBlock()).isEqualTo(0);
		assertThat(dgBlock.getPointerToDataBlock()).isEqualTo(1216);
		assertThat(dgBlock.getNumberOfChannelGroups()).isEqualTo(1);
		assertThat(dgBlock.getNumberOfRecordIDs()).isEqualTo(NumberOfRecordIDs.DATA_RECORDS_WITHOUT_RECORD_ID);
		assertThat(dgBlock.getReserved()).isEqualTo(0);

		assertThat(dgBlock.getTRBlock()).isNull();

		ArrayList<CGBlock> cgBlocks = dgBlock.getCgBlocks();
		assertThat(cgBlocks).hasSize(1);
		CGBlock cgBlock = cgBlocks.get(0);
		assertThat(cgBlock.getBlockTypeIdentifier()).isEqualTo("CG");
		assertThat(cgBlock.getBlockSize()).isEqualTo(30);
		assertThat(cgBlock.getPointerToNextCGBlock()).isEqualTo(0);
		assertThat(cgBlock.getPointerToFirstCNBlock()).isEqualTo(930);
		assertThat(cgBlock.getPointerToTXBlock()).isEqualTo(528);
		assertThat(cgBlock.getRecordID()).isEqualTo(1);
		assertThat(cgBlock.getNumberOfChannels()).isEqualTo(2);
		assertThat(cgBlock.getSizeOfDataRecord()).isEqualTo(16);
		assertThat(cgBlock.getNumberOfRecords()).isEqualTo(76326);
		assertThat(cgBlock.getPointerToFirstSRBlock()).isEqualTo(0);

		ArrayList<CNBlock> cnBlocks = cgBlock.getCNBlocks();
		assertThat(cnBlocks).hasSize(2);
		CNBlock cnBlock0 = cnBlocks.get(0);
		assertThat(cnBlock0.getBlockTypeIdentifier()).isEqualTo("CN");
		assertThat(cnBlock0.getBlockSize()).isEqualTo(228);
		assertThat(cnBlock0.getPointerToNextCNBlock()).isEqualTo(692);
		assertThat(cnBlock0.getPointerToCCBlock()).isEqualTo(572);
		assertThat(cnBlock0.getPointerToCEBlock()).isEqualTo(0);
		assertThat(cnBlock0.getPointerToCDBlock()).isEqualTo(0);
		assertThat(cnBlock0.getPointerToTXBlock()).isEqualTo(920);
		assertThat(cnBlock0.getChannelType()).isEqualTo(ChannelType.TIME_CHANNEL);
		assertThat(cnBlock0.getShortSignalName().length()).isEqualTo(32);
		assertThat(cnBlock0.getShortSignalName().trim()).isEqualTo("t");
		assertThat(cnBlock0.getSignalDescription().isEmpty());
		assertThat(cnBlock0.getStartOffsetInBits()).isEqualTo(0);
		assertThat(cnBlock0.getNumberOfBits()).isEqualTo(64);
		assertThat(cnBlock0.getSignalDataType()).isEqualTo(SignalDataType.IEEE_754_FLOATING_POINT_FORMAT_DOUBLE);
		assertThat(cnBlock0.isValueRangeValid()).isFalse();
		assertThat(cnBlock0.getMinimumSignalValue()).isEqualTo(0.0);
		assertThat(cnBlock0.getMaximumSignalValue()).isEqualTo(0.0);
		assertThat(cnBlock0.getSamplingRate()).isEqualTo(0.0);
		assertThat(cnBlock0.getPointerToTXBlockLongSignalName()).isEqualTo(553);
		assertThat(cnBlock0.getPointerToTXBlockDisplayName()).isEqualTo(925);
		assertThat(cnBlock0.getAdditionalByteOffset()).isEqualTo(0);

		CCBlock ccBlock0 = cnBlock0.getCcBlock();
		assertThat(ccBlock0.getBlockTypeIdentifier()).isEqualTo("CC");
		assertThat(ccBlock0.getBlockSize()).isEqualTo(62);
		assertThat(ccBlock0.isPhysicalValueRangeValid()).isFalse();
		assertThat(ccBlock0.getMinimumPhysicalSignalValue()).isEqualTo(0.0);
		assertThat(ccBlock0.getMaximumPhysicalSignalValue()).isEqualTo(0.0);
		assertThat(ccBlock0.getPhysicalUnit()).hasSize(20);
		assertThat(ccBlock0.getPhysicalUnit().trim()).isEqualTo("s");
		assertThat(ccBlock0.getConversionType()).isEqualTo(ConversionType.PARAMETRIC_LINEAR);
		assertThat(ccBlock0.getSizeInformation()).isEqualTo(2);
		assertThat(ccBlock0).isInstanceOf(CCBlockLinear2Parameters.class);
		assertThat(((CCBlockLinear2Parameters) ccBlock0).getP1()).isEqualTo(0.0);
		assertThat(((CCBlockLinear2Parameters) ccBlock0).getP2()).isEqualTo(1.0);

		TXBlock txBlock0 = cnBlock0.getTXBlock();
		assertThat(txBlock0.getBlockTypeIdentifier()).isEqualTo("TX");
		assertThat(txBlock0.getBlockSize()).isEqualTo(5);
		assertThat(txBlock0.getText()).isEmpty();

		TXBlock txBlock0LongSignalName = cnBlock0.getTxBlockLongSignalName();
		assertThat(txBlock0LongSignalName.getBlockTypeIdentifier()).isEqualTo("TX");
		assertThat(txBlock0LongSignalName.getBlockSize()).isEqualTo(5);
		assertThat(txBlock0LongSignalName.getText()).isEmpty();

		TXBlock txBlock0DisplayName = cnBlock0.getTxBlockDisplayName();
		assertThat(txBlock0DisplayName.getBlockTypeIdentifier()).isEqualTo("TX");
		assertThat(txBlock0DisplayName.getBlockSize()).isEqualTo(5);
		assertThat(txBlock0DisplayName.getText()).isEmpty();

		CNBlock cnBlock1 = cnBlocks.get(1);
		assertThat(cnBlock1.getBlockTypeIdentifier()).isEqualTo("CN");
		assertThat(cnBlock1.getBlockSize()).isEqualTo(228);
		assertThat(cnBlock1.getPointerToNextCNBlock()).isEqualTo(0);
		assertThat(cnBlock1.getPointerToCCBlock()).isEqualTo(634);
		assertThat(cnBlock1.getPointerToCEBlock()).isEqualTo(0);
		assertThat(cnBlock1.getPointerToCDBlock()).isEqualTo(0);
		assertThat(cnBlock1.getPointerToTXBlock()).isEqualTo(680);
		assertThat(cnBlock1.getChannelType()).isEqualTo(ChannelType.DATA_CHANNEL);
		assertThat(cnBlock1.getShortSignalName().length()).isEqualTo(32);
		assertThat(cnBlock1.getShortSignalName().trim()).isEqualTo("v");
		assertThat(cnBlock1.getSignalDescription().length()).isEqualTo(128);
		assertThat(cnBlock1.getSignalDescription().trim()).isEqualTo("v");
		assertThat(cnBlock1.getStartOffsetInBits()).isEqualTo(64);
		assertThat(cnBlock1.getNumberOfBits()).isEqualTo(64);
		assertThat(cnBlock1.getSignalDataType()).isEqualTo(SignalDataType.IEEE_754_FLOATING_POINT_FORMAT_DOUBLE);
		assertThat(cnBlock1.isValueRangeValid()).isFalse();
		assertThat(cnBlock1.getMinimumSignalValue()).isEqualTo(0.0);
		assertThat(cnBlock1.getMaximumSignalValue()).isEqualTo(0.0);
		assertThat(cnBlock1.getSamplingRate()).isEqualTo(0.0);
		assertThat(cnBlock1.getPointerToTXBlockLongSignalName()).isEqualTo(560);
		assertThat(cnBlock1.getPointerToTXBlockDisplayName()).isEqualTo(685);
		assertThat(cnBlock1.getAdditionalByteOffset()).isEqualTo(0);

		CCBlock ccBlock1 = cnBlock1.getCcBlock();
		assertThat(ccBlock1.getBlockTypeIdentifier()).isEqualTo("CC");
		assertThat(ccBlock1.getBlockSize()).isEqualTo(46);
		assertThat(ccBlock1.isPhysicalValueRangeValid()).isFalse();
		assertThat(ccBlock1.getMinimumPhysicalSignalValue()).isEqualTo(0.0);
		assertThat(ccBlock1.getMaximumPhysicalSignalValue()).isEqualTo(0.0);
		assertThat(ccBlock1.getPhysicalUnit()).hasSize(20);
		assertThat(ccBlock1.getPhysicalUnit().trim()).isEmpty();
		assertThat(ccBlock1.getConversionType()).isEqualTo(ConversionType.ONE_TO_ONE_FORMULA);
		assertThat(ccBlock1.getSizeInformation()).isEqualTo(0);
		assertThat(ccBlock1).isInstanceOf(CCBlockOneToOne.class);

		TXBlock txBlock1 = cnBlock1.getTXBlock();
		assertThat(txBlock1.getBlockTypeIdentifier()).isEqualTo("TX");
		assertThat(txBlock1.getBlockSize()).isEqualTo(5);
		assertThat(txBlock1.getText()).isEmpty();

		TXBlock txBlock1LongSignalName = cnBlock1.getTxBlockLongSignalName();
		assertThat(txBlock1LongSignalName.getBlockTypeIdentifier()).isEqualTo("TX");
		assertThat(txBlock1LongSignalName.getBlockSize()).isEqualTo(5);
		assertThat(txBlock1LongSignalName.getText()).isEmpty();

		TXBlock txBlock1DisplayName = cnBlock1.getTxBlockDisplayName();
		assertThat(txBlock1DisplayName.getBlockTypeIdentifier()).isEqualTo("TX");
		assertThat(txBlock1DisplayName.getBlockSize()).isEqualTo(5);
		assertThat(txBlock1DisplayName.getText()).isEmpty();
	}

}

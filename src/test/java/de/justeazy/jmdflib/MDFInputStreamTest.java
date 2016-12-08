package de.justeazy.jmdflib;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import de.justeazy.jmdflib.blocktypes.IDBlock;
import de.justeazy.jmdflib.enums.ByteOrder;
import de.justeazy.jmdflib.enums.FloatingPointFormat;
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
	}

}

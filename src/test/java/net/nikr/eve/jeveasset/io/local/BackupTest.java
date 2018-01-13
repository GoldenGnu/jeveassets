/*
 * Copyright 2009-2018 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.eve.jeveasset.io.local;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import net.nikr.eve.jeveasset.TestUtil;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class BackupTest extends TestUtil {

	private static final Logger LOG = LoggerFactory.getLogger(BackupTest.class);

	private static File targetFile;
	private static File bacFile;
	private static File newFile;

	private static File getFile(String extension) {
		return new File(new File(FileUtil.getPathRunJar()).getParentFile().getAbsolutePath() + File.separator + "test." + extension);
	}

	@BeforeClass
	public static void setUpClass() {
		targetFile = getFile("xml");
		newFile = getFile("new");
		bacFile = getFile("bac");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		targetFile.delete();
		newFile.delete();
		bacFile.delete();
		File file;
		int count = 0;
		boolean next = true;
		while (next) {
			count++;
			file = getFile("error" + count);
			next = file.exists();
			file.delete();
		}
	}

	@Test
	public void testCreateBackup() {
		LOG.info("testCreateBackup");
		cleanUp();
		SimpleWriter writer = new SimpleWriter();
		SimpleReader reader = new SimpleReader();
		//First run
		assertTrue(writer.write(targetFile, true));
		assertTrue(targetFile.exists());
		assertFalse(newFile.exists());
		assertFalse(bacFile.exists());
		assertTrue(reader.read(targetFile));
		//Secound run
		assertTrue(writer.write(targetFile, true));
		assertTrue(targetFile.exists());
		assertFalse(newFile.exists());
		assertTrue(bacFile.exists());
		assertTrue(reader.read(targetFile));
	}

	@Test
	public void testRestoreBackup() {
		LOG.info("testRestoreBackup");
		SimpleWriter writer = new SimpleWriter();
		SimpleReader reader = new SimpleReader();
		SimpleWriterCorrupted writerCorrupted = new SimpleWriterCorrupted();

		//Test working
		cleanUp();
		assertTrue(writer.write(targetFile, true));	//xml = OK
		assertTrue(reader.read(targetFile));		//Read target

		//Test .new (1 of 2)
		cleanUp();
		writerCorrupted.write(targetFile);			//xml = Corrupted
		assertTrue(writer.write(newFile, false));	//new = OK
		writerCorrupted.write(bacFile);				//bac = Corrupted
		assertTrue(reader.read(targetFile));		//Read target

		//Test .new (2 of 2)
		cleanUp();
		writerCorrupted.write(targetFile);			//xml = Corrupted
		assertTrue(writer.write(newFile, false));	//new = OK
													//bac = Does not exist
		assertTrue(reader.read(targetFile));		//Read target

		//Test .bac (1 of 2)
		cleanUp();
		writerCorrupted.write(targetFile);			//xml = Corrupted
		writerCorrupted.write(newFile);				//new = Corrupted
		assertTrue(writer.write(bacFile, false));	//bac = OK
		assertTrue(reader.read(targetFile));		//Read target

		//Test .bac (2 of 2)
		cleanUp();
		writerCorrupted.write(targetFile);			//xml = Corrupted
													//new = Does not exist
		assertTrue(writer.write(bacFile, false));	//bac = OK
		assertTrue(reader.read(targetFile));		//Read target
	}

	@Test
	public void testCorrupted() {
		SimpleReader reader = new SimpleReader();
		SimpleWriterCorrupted writerCorrupted = new SimpleWriterCorrupted();

		//Test All Corrupted
		cleanUp();
		writerCorrupted.write(targetFile);			//xml = Corrupted
		writerCorrupted.write(bacFile);				//bac = Corrupted
		writerCorrupted.write(newFile);				//bac = Corrupted
		assertFalse(reader.read(targetFile));		//Read target

		//Test bac missing
		cleanUp();
		writerCorrupted.write(targetFile);			//xml = Corrupted
		writerCorrupted.write(newFile);				//new = Corrupted
													//bac = Does not exist
		assertFalse(reader.read(targetFile));		//Read target

		//Test new missing
		cleanUp();
		writerCorrupted.write(targetFile);			//xml = Corrupted
													//new = Does not exist
		writerCorrupted.write(newFile);				//bac = Corrupted
		assertFalse(reader.read(targetFile));		//Read target

		//Test bac & new missing
		cleanUp();
		writerCorrupted.write(targetFile);			//xml = Corrupted
													//new = Does not exist
													//bac = Does not exist
		assertFalse(reader.read(targetFile));		//Read target

		//Test all missing
		cleanUp();
													//xml = Does not exist
													//new = Does not exist
													//bac = Does not exist
		assertFalse(reader.read(targetFile));		//Read target
	}

	private void cleanUp() {
		if (targetFile.exists()) {
			targetFile.delete();
		}
		if (newFile.exists()) {
			newFile.delete();
		}
		if (bacFile.exists()) {
			bacFile.delete();
		}
	}

	private static class SimpleWriter extends AbstractXmlWriter {
		private boolean write(File file, boolean createBackup) {
			Document xmldoc;
			try {
				xmldoc = getXmlDocument("test");
			} catch (XmlException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
			}
			try {
				writeXmlFile(xmldoc, file.getAbsolutePath(), createBackup);
				return true;
			} catch (XmlException ex) {
				LOG.error(ex.getMessage(), ex);
				return false;
			}
		}
	}

	private static class SimpleWriterCorrupted {
		private void write(File file) {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(file));
				writer.write("Corrupted");
			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (Exception ex) {
						LOG.error(ex.getMessage(), ex);
					}
			}
			}
		}
	}

	private static class SimpleReader extends AbstractXmlReader<Boolean> {

		private boolean read(File file) {
			return read("Simple reader", file.getAbsolutePath(), AbstractXmlReader.XmlType.DYNAMIC);
		}

		@Override
		protected Boolean parse(Element element) throws XmlException {
			return true;
		}

		@Override
		protected Boolean failValue() {
			return false;
		}

		@Override
		protected Boolean doNotExistValue() {
			return false;
		}
	}
}

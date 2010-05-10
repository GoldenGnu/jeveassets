/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nikr.eve.jeveassets.tests;

import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.industryjobs.IndustryJobsResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import net.nikr.log.Log;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Niklas
 */
public class TestApiConverter {

	static Settings settings;
	List<ApiIndustryJob> industryJobs;

	@BeforeClass
	public static void oneTimeSetUp(){
		Log.init(TestApiConverter.class, "");

		//Config log4j
		BasicConfigurator.configure();
		Logger.getLogger("com.beimin.eveapi").setLevel(Level.INFO);
		Logger.getLogger("uk.me.candle").setLevel(Level.INFO);
		Logger.getLogger("org.apache.commons").setLevel(Level.INFO);

		settings = new Settings();
	}

	@AfterClass
	public static void oneTimeTearDown() {
		settings = null;
	}

	@Before
	public void setUp() {
		InputStream is = TestApiConverter.class.getResourceAsStream("jobs.xml");
		IndustryJobsResponse response;
		try {
			response = com.beimin.eveapi.character.industryjobs.IndustryJobsParser.getInstance().getResponse(is);
			industryJobs = new ArrayList<ApiIndustryJob>(response.getIndustryJobs());
		} catch (IOException ex) {
			fail("IOException: "+ex.getMessage());
		} catch (SAXException ex) {
			fail("SAXException: "+ex.getMessage());
		}
	}

	@After
	public void tearDown() {
		industryJobs.clear();
		industryJobs = null;
	}

	@Test
	public void testApiIndustryJob(){
		Human human = new Human(null, "TESTCASE", 0, "TEST CORP");
		List<EveAsset> assets = ApiConverter.apiIndustryJob(industryJobs, human, true, settings);
		for (int a = 0; a < assets.size(); a++){
			assertFalse("Job asset location not found", assets.get(a).getLocation().contains("Error !"));
			assertFalse("Job asset location not found", assets.get(a).getLocation().equals("Unknown"));
		}

	}
	@Test
	public void testApiIndustryJobsToIndustryJobs(){
		List<IndustryJob> jobs;
		jobs = ApiConverter.apiIndustryJobsToIndustryJobs(industryJobs, "TEST CORP", settings);
		for (int a = 0; a < jobs.size(); a++){
			assertFalse("Job location not found", jobs.get(a).getLocation().contains("Error !"));
			assertFalse("Job location not found", jobs.get(a).getLocation().equals("Unknown"));
		}
	}
}

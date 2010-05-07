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
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.xml.sax.SAXException;

/**
 *
 * @author Niklas
 */
public class TestApiConverter {

	Settings settings;
	List<ApiIndustryJob> industryJobs;

	@Before
	public void setup() {
		Log.init(TestApiConverter.class, "");

		//Config log4j
		BasicConfigurator.configure();
		Logger.getLogger("com.beimin.eveapi").setLevel(Level.INFO);
		Logger.getLogger("uk.me.candle").setLevel(Level.INFO);
		Logger.getLogger("org.apache.commons").setLevel(Level.INFO);

		settings = new Settings();
		InputStream is = TestApiConverter.class.getResourceAsStream("jobs.xml");
		try {
			IndustryJobsResponse response = com.beimin.eveapi.character.industryjobs.IndustryJobsParser.getInstance().getResponse(is);
			industryJobs = new ArrayList<ApiIndustryJob>(response.getIndustryJobs());
		} catch (IOException ex) {
			fail("IOException: "+ex.getMessage());
		} catch (SAXException ex) {
			fail("SAXException: "+ex.getMessage());
		}
	}

	@Test
	public void testApiIndustryJob(){
		Human human = new Human(null, "TESTCASE", 0, "TEST CORP");
		List<EveAsset> assets = ApiConverter.apiIndustryJob(industryJobs, human, true, settings);
		assertEquals(assets.get(0).getLocation(), "Baviasi");
	}
	@Test
	public void testApiIndustryJobsToIndustryJobs(){
		List<IndustryJob> jobs = ApiConverter.apiIndustryJobsToIndustryJobs(industryJobs, "TEST CORP", settings);
		assertEquals(jobs.get(0).getLocation(), "Baviasi");
	}
}

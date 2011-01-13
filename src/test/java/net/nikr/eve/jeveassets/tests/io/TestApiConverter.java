/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

package net.nikr.eve.jeveassets.tests.io;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.industryjobs.IndustryJobsResponse;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Niklas
 */
@Ignore
public class TestApiConverter {
	static {
		System.setProperty("log.home", "."+File.separator);
		System.setProperty("log.level", "DEBUG");
	}
	private final static Logger LOG = LoggerFactory.getLogger(TestApiConverter.class);

	static Settings settings;
	List<ApiIndustryJob> industryJobs;

	@BeforeClass
	public static void oneTimeSetUp(){
		settings = new ApiSettings();
	}

	@AfterClass
	public static void oneTimeTearDown() {
		settings = null;
	}

	@Before
	public void setUp() {
		InputStream is = TestApiConverter.class.getResourceAsStream("jobs.xml");
		IndustryJobsResponse response;
//		response = com.beimin.eveapi.character.industryjobs.IndustryJobsParser.getInstance().getResponse(is);
//		industryJobs = new ArrayList<ApiIndustryJob>(response.getIndustryJobs());
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
			assertFalse("Job asset location not found", assets.get(a).getSystem().contains("Error !"));
			assertFalse("Job asset location not found", assets.get(a).getSystem().equals("Unknown"));
			assertFalse("Job asset location not found", assets.get(a).getRegion().contains("Error !"));
			assertFalse("Job asset location not found", assets.get(a).getRegion().equals("Unknown"));
			assertFalse("Job asset location not found", assets.get(a).getSecurity().contains("Error !"));
			assertTrue("Job asset location not found", assets.get(a).getSolarSystemID() > 0);
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

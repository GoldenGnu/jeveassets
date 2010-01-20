/*
 * Copyright 2009
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.industry.ApiIndustryJob;
import com.beimin.eveapi.industry.Parser;
import com.beimin.eveapi.industry.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


public class IndustryJobsGetter extends AbstractApiGetter<Response> {

	private Human human;

	public void load(List<Account> accounts, boolean forceUpdate){
		Log.info("Industry jobs updating:");
		boolean updated = false;
		boolean updateFailed = false;
		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			List<Human> humans = account.getHumans();
			for (int b = 0; b < humans.size(); b++){
				if (!Program.FORCE_NO_UPDATE) {
					load(humans.get(b), forceUpdate);
					if (isCharacterUpdated() || isCorporationUpdated()){
						updated = true;
					} else {
						updateFailed = true;
					}
				}
			}
		}
		if (updated && !updateFailed){
			Log.info("	Industry jobs updated (ALL)");
		} else if(updated && updateFailed) {
			Log.info("	Industry jobs updated (SOME)");
		} else {
			Log.info("	Industry jobs not updated (NONE)");
		}
	}


	public void load(Human human, boolean forceUpdate){
		this.human = human;
		Date nextUpdate = human.getIndustryJobsNextUpdate();
		load(nextUpdate, forceUpdate, false, "Industry jobs", human.getName());
		if (human.isUpdateCorporationAssets()){
			load(nextUpdate, forceUpdate, true, "Corporation industry jobs", human.getCorporation()+" by "+human.getName());
		}
	}


	@Override
	protected Response getResponse(boolean bCorp) throws IOException, SAXException {
		Parser parser = new Parser();
		Response response = parser.getInustryJobs(Human.getApiAuthorization(human), bCorp);
		human.setIndustryJobsNextUpdate(response.getCachedUntil());
		return response;
	}

	@Override
	protected void ok(Response response, boolean bCorp) {
		List<ApiIndustryJob> industryJobs = new Vector<ApiIndustryJob>(response.getIndustryJobs());
		if (bCorp){
			human.setIndustryJobsCorporation(industryJobs);
		} else {
			human.setIndustryJobs(industryJobs);
		}
	}



	/*
	private IndustryJobsGetter() {}

	public static void load(SettingsInterface settings, List<Account> accounts){
		Log.info("Industry jobs updating:");
		boolean updated = false;
		boolean updateFailed = false;
		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			List<Human> humans = account.getHumans();
			for (int b = 0; b < humans.size(); b++){
				boolean returned;
				if (!Program.FORCE_NO_UPDATE) {
					returned = load(settings, humans.get(b), false);
					if (returned){
						updated = true;
					} else {
						updateFailed = true;
					}
				}
			}
		}
		if (updated && !updateFailed){
			Log.info("	Industry jobs updated (ALL)");
		} else if(updated && updateFailed) {
			Log.info("	Industry jobs updated (SOME)");
		} else {
			Log.info("	Industry jobs not updated (NONE)");
		}
	}

	private static boolean load(SettingsInterface settings, Human human, boolean bCorp){
		if (settings.isUpdatable(human.getIndustryJobsNextUpdate()) || bCorp){
			if (human.isUpdateCorporationAssets() && !bCorp){
				load(settings, human, true);
			}
			Parser industryJobsParser = new Parser();
			Response industryJobsResponse = null;
			try {
				industryJobsResponse = industryJobsParser.getInustryJobs(Human.getApiAuthorization(human), bCorp);
				human.setIndustryJobsNextUpdate(industryJobsResponse.getCachedUntil());
				if (!industryJobsResponse.hasError()){
					List<ApiIndustryJob> industryJobs = new Vector<ApiIndustryJob>(industryJobsResponse.getIndustryJobs());
					if (bCorp){
						human.setIndustryJobsCorporation(industryJobs);
					} else {
						human.setIndustryJobs(industryJobs);
					}
					if (bCorp) {
						Log.info("	Corporation industry jobs updated for: "+human.getCorporation()+" by "+human.getName());
					} else {
						Log.info("	Industry jobs updated for: "+human.getName());
					}
					return true;
				} else {
					ApiError apiError = industryJobsResponse.getError();
					if (bCorp) {
						Log.info("	Failed to update corporation industry jobs for: "+human.getCorporation()+" by "+human.getName()+" (API ERROR: code: "+apiError.getCode()+" :: "+apiError.getError()+")");
					} else {
						Log.info("	Failed to update industry jobs for: "+human.getName()+" (API ERROR: code: "+apiError.getCode()+" :: "+apiError.getError()+")");
					}
				}
			} catch (IOException ex) {

			} catch (SAXException ex) {
				if (bCorp) {
					Log.error("	Corporation industry jobs failed to update for: "+human.getCorporation()+" by "+human.getName()+" (PARSER ERROR)", ex);
				} else {
					Log.error("	Industry jobs failed to update for: "+human.getName()+" (PARSER ERROR)", ex);
				}
			}
		}
		return false;
	}
	 *
	 */
}

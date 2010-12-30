/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nikr.eve.jeveasset.gui.tabs.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.i18n.TabsJobs;
import net.nikr.eve.jeveasset.io.shared.ApiConverter;

/**
 *
 * @author Andrew
 */
public class IndustryJobData {

	/**
	 * List of all known industry jobs.
	 */
	private List<IndustryJob> all;
	/**
	 * map from character or corporation name to the list of jobs that
	 * belong to them.
	 */
	private Map<String, List<IndustryJob>> jobs;
	/**
	 * List of character and corporation names.
	 * This is a Vector because it needs to be passed to a GUI component that
	 * only takes a Vector rather then a List.
	 */
	private Vector<String> characters;

	Program program;

	public IndustryJobData(Program program) {
		this.program = program;
	}

	public void updateData() {
		characters = new Vector<String>();
		//characters.add("All");
		jobs = new HashMap<String, List<IndustryJob>>();
		all = new ArrayList<IndustryJob>();
		List<Account> accounts = program.getSettings().getAccounts();
		for (int a = 0; a < accounts.size(); a++){
			List<Human> tempHumans = accounts.get(a).getHumans();
			for (int b = 0; b < tempHumans.size(); b++){
				Human human = tempHumans.get(b);
				if (human.isShowAssets()){
					characters.add(human.getName());
					List<IndustryJob> characterIndustryJobs = ApiConverter.apiIndustryJobsToIndustryJobs(human.getIndustryJobs(), human.getName(), program.getSettings());
					jobs.put(human.getName(), characterIndustryJobs);
					all.addAll(characterIndustryJobs);
					if (human.isUpdateCorporationAssets()){
						String corpKey = TabsJobs.get().whitespace(human.getCorporation());
						if (!characters.contains(corpKey)){
							characters.add(corpKey);
							jobs.put(corpKey, new ArrayList<IndustryJob>());
						}
						List<IndustryJob> corporationIndustryJobs = ApiConverter.apiIndustryJobsToIndustryJobs(human.getIndustryJobsCorporation(), human.getCorporation(), program.getSettings());
						jobs.get(corpKey).addAll(corporationIndustryJobs);
						all.addAll(corporationIndustryJobs);
					}
				}
			}
		}
	}

	public List<IndustryJob> getAll() {
		return Collections.unmodifiableList(all);
	}

	public Vector<String> getCharacters() {
		return characters;
	}

	public Map<String, List<IndustryJob>> getJobs() {
		return Collections.unmodifiableMap(jobs);
	}
}

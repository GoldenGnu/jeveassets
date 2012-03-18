/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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
		List<String> unique = new ArrayList<String>();
		characters = new Vector<String>();
		//characters.add("All");
		jobs = new HashMap<String, List<IndustryJob>>();
		all = new ArrayList<IndustryJob>();
		for (Account account : program.getSettings().getAccounts()){
			for (Human human : account.getHumans()){
				if (human.isShowAssets()){
					String name;
					if (human.isCorporation()){
						name = TabsJobs.get().whitespace(human.getName());
					} else {
						name = human.getName();
					}
					//Only add names once
					if (!characters.contains(name)){
						characters.add(name);
						jobs.put(name, new ArrayList<IndustryJob>()); //Make sure empty is not null
					}
					//Only add once and don't add empty jobs
					List<IndustryJob> characterIndustryJobs = ApiConverter.apiIndustryJobsToIndustryJobs(human.getIndustryJobs(), human.getName(), program.getSettings());
					if (!unique.contains(name) && !characterIndustryJobs.isEmpty()){
						jobs.put(name, characterIndustryJobs);
						all.addAll(characterIndustryJobs);
						unique.add(name);
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

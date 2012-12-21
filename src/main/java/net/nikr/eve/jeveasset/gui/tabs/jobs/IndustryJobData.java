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

import java.util.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Owner;
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
	private Vector<String> owners;

	private Program program;

	public IndustryJobData(final Program program) {
		this.program = program;
	}

	public void updateData() {
		List<String> unique = new ArrayList<String>();
		owners = new Vector<String>();
		jobs = new HashMap<String, List<IndustryJob>>();
		all = new ArrayList<IndustryJob>();
		for (Account account : program.getSettings().getAccounts()) {
			for (Owner owner : account.getOwners()) {
				if (owner.isShowAssets()) {
					String name;
					if (owner.isCorporation()) {
						name = TabsJobs.get().whitespace(owner.getName());
					} else {
						name = owner.getName();
					}
					//Only add names once
					if (!owners.contains(name)) {
						owners.add(name);
						jobs.put(name, new ArrayList<IndustryJob>()); //Make sure empty is not null
					}
					//Only add once and don't add empty jobs
					List<IndustryJob> industryJobs = ApiConverter.apiIndustryJobsToIndustryJobs(owner.getIndustryJobs(), owner.getName(), program.getSettings());
					if (!unique.contains(name) && !industryJobs.isEmpty()) {
						jobs.put(name, industryJobs);
						all.addAll(industryJobs);
						unique.add(name);
					}
				}
			}
		}
	}

	public List<IndustryJob> getAll() {
		return Collections.unmodifiableList(all);
	}

	public Vector<String> getOwners() {
		return owners;
	}

	public Map<String, List<IndustryJob>> getJobs() {
		return Collections.unmodifiableMap(jobs);
	}
}

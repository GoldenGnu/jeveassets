/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.i18n;

import java.util.Locale;
import uk.me.candle.translations.Bundle;


public abstract class TabsSkills extends Bundle {

	public static TabsSkills get() {
		return BundleServiceFactory.getBundleService().get(TabsSkills.class);
	}

	public TabsSkills(final Locale locale) {
		super(locale);
	}

	public abstract String add();
	public abstract String deleteSkillPlan();
	public abstract String deleteSkillPlans(int size);
	public abstract String enterName();
	public abstract String manage();
	public abstract String manageTitle();
	public abstract String merge();
	public abstract String noValidSkills();
	public abstract String overwrite();
	public abstract String rename();
	public abstract String skillPlans();
	public abstract String skills();
	public abstract String skillsOverview();
	public abstract String tableToolTipCompleted();
	public abstract String tableToolTipMissing(int missing);
	public abstract String tableToolTipSkill(String name, int approximateLevel, int targetLevel, String percent);
	public abstract String tableToolTipTruncated();
	public abstract String total();
	public abstract String columnSkill();
	public abstract String columnGroup();
	public abstract String columnCharacter();
	public abstract String columnActive();
	public abstract String columnTrained();
	public abstract String columnTotal();
	public abstract String columnUnallocated();
}

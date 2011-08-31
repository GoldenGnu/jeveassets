/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.account;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;


public class HumanTableFormat implements AdvancedTableFormat<Object>, WritableTableFormat<Object>{

	private List<String> columnNames;

	public HumanTableFormat() {
		columnNames = new ArrayList<String>();
		columnNames.add("");
		columnNames.add(DialoguesAccount.get().tableFormatName());
		columnNames.add(DialoguesAccount.get().tableFormatCorporation());
		columnNames.add(DialoguesAccount.get().tableFormatAssetList());
		columnNames.add(DialoguesAccount.get().tableFormatAccountBalance());
		columnNames.add(DialoguesAccount.get().tableFormatIndustryJobs());
		columnNames.add(DialoguesAccount.get().tableFormatMarketOrders());
		columnNames.add(DialoguesAccount.get().tableFormatExpires());
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames.get(column);
	}

	@Override
	public Object getColumnValue(Object baseObject, int column) {
		if (baseObject instanceof Human){
			Human human = (Human) baseObject;
			switch (column) {
				case 0: return human.isShowAssets();
				case 1: return human.getName();
				case 2: return new YesNo(human.isCorporation());
				case 3: return new YesNo(human.getParentAccount().isAssetList());
				case 4: return new YesNo(human.getParentAccount().isAccountBalance());
				case 5: return new YesNo(human.getParentAccount().isIndustryJobs());
				case 6: return new YesNo(human.getParentAccount().isMarketOrders());
				case 7: return new ExpirerDate(human.getParentAccount().getExpires());
			}
		}
		return new Object();
	}

	@Override
	public Class getColumnClass(int column) {
		switch (column) {
			case 0: return Boolean.class;
			case 1: return String.class;
			case 2: return YesNo.class;
			case 3: return YesNo.class;
			case 4: return YesNo.class;
			case 5: return YesNo.class;
			case 6: return YesNo.class;
			case 7: return ExpirerDate.class;
		}
		return Object.class;
	}

	@Override
	public Comparator getColumnComparator(int column) {
		return GlazedLists.comparableComparator();
	}

	@Override
	public boolean isEditable(Object baseObject, int column) {
		if (baseObject instanceof Human && column == 0){
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Object setColumnValue(Object baseObject, Object editedValue, int column) {
		if (editedValue instanceof Boolean && baseObject instanceof Human && column == 0){
			Human human = (Human) baseObject;
			boolean value = (Boolean) editedValue;
			human.setShowAssets(value);
			return baseObject;
		}
		return null;	
	}
	
	public class YesNo{

		private boolean b;

		public YesNo(boolean b) {
			this.b = b;
		}
		
		@Override
		public String toString(){
			return b ? DialoguesAccount.get().tableFormatYes() : DialoguesAccount.get().tableFormatNo();
		}
		
	}
	
	public class ExpirerDate{
		private Date expirer;

		public ExpirerDate(Date expirer) {
			this.expirer = expirer;
		}
		
		@Override
		public String toString(){
			if (expirer == null){
				return "Never";
			} else if (Settings.getGmtNow().after(expirer)){
				return "Expired";
			} else {
				return Formater.dateOnly(expirer);
			}
		}
	}

}

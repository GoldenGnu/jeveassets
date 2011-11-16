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
import java.util.Comparator;
import java.util.Date;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.DialoguesAccount;


enum HumanTableFormat implements EnumTableColumn<Human> {
	SHOW_ASSETS(Boolean.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return "";
		}
		@Override
		public Object getColumnValue(Human from) {
			return from.isShowAssets();
		}
		@Override
		public boolean isColumnEditable(Object baseObject) {
			return true;
		}
		@Override
		public Human setColumnValue(Object baseObject, Object editedValue) {
			if ((editedValue instanceof Boolean) && (baseObject instanceof Human)){
				Human human = (Human) baseObject;
				boolean value = (Boolean) editedValue;
				human.setShowAssets(value);
				return human;
			}
			return null;
		}
	},
	NAME(String.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatName();
		}
		@Override
		public Object getColumnValue(Human from) {
			return from.getName();
		}
	},
	CORPORATION(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatCorporation();
		}
		@Override
		public Object getColumnValue(Human from) {
			return new YesNo(from.isCorporation());
		}
	},
	ASSET_LIST(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatAssetList();
		}
		@Override
		public Object getColumnValue(Human from) {
			return new YesNo(from.getParentAccount().isAssetList());
		}
	},
	ACCOUNT_BALANCE(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatAccountBalance();
		}
		@Override
		public Object getColumnValue(Human from) {
			return new YesNo(from.getParentAccount().isAccountBalance());
		}
	},
	INDUSTRY_JOBS(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatIndustryJobs();
		}
		@Override
		public Object getColumnValue(Human from) {
			return new YesNo(from.getParentAccount().isIndustryJobs());
		}
	},
	MARKET_ORDERS(YesNo.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatMarketOrders();
		}
		@Override
		public Object getColumnValue(Human from) {
			return new YesNo(from.getParentAccount().isMarketOrders());
		}
	},
	EXPIRES(ExpirerDate.class, GlazedLists.comparableComparator()) {
		@Override
		public String getColumnName() {
			return DialoguesAccount.get().tableFormatExpires();
		}
		@Override
		public Object getColumnValue(Human from) {
			return new ExpirerDate(from.getParentAccount().getExpires());
		}
	},
	;

	Class type;
	Comparator<?> comparator;
	private HumanTableFormat(Class type, Comparator<?> comparator) {
		this.type = type;
		this.comparator = comparator;
	}
	@Override
	public Class getType() {
		return type;
	}
	@Override
	public Comparator getComparator() {
		return comparator;
	}
	@Override
	public String getColumnName() {
		return getColumnName();
	}
	@Override public boolean isColumnEditable(Object baseObject) {
		return false;
	}
	@Override public Human setColumnValue(Object baseObject, Object editedValue) {
		return null;
	}

	public class YesNo implements Comparable<YesNo> {

		private boolean b;

		public YesNo(boolean b) {
			this.b = b;
		}
		
		@Override
		public String toString(){
			return b ? DialoguesAccount.get().tableFormatYes() : DialoguesAccount.get().tableFormatNo();
		}

		@Override
		public int compareTo(YesNo o) {
			return this.toString().compareTo(o.toString()); 
		}
		
	}
	
	public class ExpirerDate implements Comparable<ExpirerDate>{
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

		@Override
		public int compareTo(ExpirerDate o) {
			return this.expirer.compareTo(o.expirer);
		}
	}
}
	/*
	private List<String> columnNames;

	

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
				case 0: 
				case 1: 
				case 2: 
				case 3: 
				case 4: return new YesNo(human.getParentAccount().());
				case 5: return new YesNo(human.getParentAccount().());
				case 6: return new YesNo(human.getParentAccount().());
				case 7: return new ExpirerDate(human.getParentAccount().());
			}
		}
		return new Object();
	}

	@Override
	public Class getColumnClass(int column) {
		switch (column) {
			case 0: return ;
			case 1: return String.class;
			case 2: return YesNo.class;
			case 3: return YesNo.class;
			case 4: return YesNo.class;
			case 5: return YesNo.class;
			case 6: return YesNo.class;
			case 7: return .class;
		}
		return Object.class;
	}

	@Override
	public Comparator getColumnComparator(int column) {
		return GlazedLists.comparableComparator();
	}

	
	
	
 }
	
	 * 
	 */

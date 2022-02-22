/*
 * Copyright 2009-2022 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.settings;

import eve.nikr.net.client.model.Feedback.SecurityEnum;
import eve.nikr.net.client.model.Prices;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.nikr.eve.jeveasset.data.api.my.MyIndustryJob;
import net.nikr.eve.jeveasset.data.settings.types.BlueprintType;
import net.nikr.eve.jeveasset.i18n.DataContractPrices;
import net.nikr.eve.jeveasset.io.local.ContractPriceReader;
import net.nikr.eve.jeveasset.io.local.ContractPriceWriter;

public class ContractPriceManager {

	// https://app.swaggerhub.com/apis/rihanshazih/contracts-appraisal/
	private static volatile ContractPriceManager PRICE_GETTER = null;

	private final ContractPriceData contractPriceData;

	public static void load() {
		get();
	}

	public static ContractPriceManager get() {
		ContractPriceManager priceGetter = ContractPriceManager.PRICE_GETTER;
		if (priceGetter == null) {
			synchronized (ContractPriceManager.class) {
				priceGetter = ContractPriceManager.PRICE_GETTER;
				if (priceGetter == null) {
					ContractPriceManager.PRICE_GETTER = priceGetter = new ContractPriceManager();
				}
			}
		}
		return priceGetter;
	}
	
	private ContractPriceManager() {
		contractPriceData = ContractPriceReader.load();
	}

	public void save() {
		ContractPriceWriter.save(contractPriceData);
	}

	public void addPrices(ReturnData returnData) {
		contractPriceData.add(returnData);
	}

	public boolean isFailed(ContractPriceItem contractPriceType) {
		if (contractPriceType == null) {
			return false;
		}
		return contractPriceData.isFailed(contractPriceType);
	}

	public boolean haveContractPrice(ContractPriceItem contractPriceItem) {
		if (contractPriceItem == null) {
			return false;
		}
		Prices prices = contractPriceData.getPrices(contractPriceItem);
		if (prices == null) {
			return false;
		}
		Double price = Settings.get().getContractPriceSettings().getContractPriceMode().getPrice(prices);
		if (price == null) {
			return false;
		}
		return true;
	}

	public double getContractPrice(ContractPriceItem contractPriceItem) {
		return getContractPrice(contractPriceItem, false);
	}

	public double getContractPrice(ContractPriceItem contractPriceItem, boolean perUnit) {
		if (contractPriceItem == null) {
			return 0;
		}
		Prices prices = contractPriceData.getPrices(contractPriceItem);
		if (prices == null) {
			return 0;
		}
		Double price = Settings.get().getContractPriceSettings().getContractPriceMode().getPrice(prices);
		if (price == null) {
			return 0;
		} else if (perUnit){
			return price;
		} else if (contractPriceItem.getRuns() > 0){
			return price * contractPriceItem.getRuns();
		} else {
			return price;
		}
	}

	public Date getNextUpdate() {
		return contractPriceData.getDate();
	}

	public static class ContractPriceData {
		private Date date = Settings.getNow();
		private final Map<ContractPriceItem, Prices> prices = new HashMap<>();
		private final Map<ContractPriceItem, Date> failed = new HashMap<>();

		public synchronized Date getDate() {
			return date;
		}

		public synchronized void add(ReturnData returnData) {
			if (returnData.isEmpty()) {
				failed.put(returnData.getContractPriceType(), returnData.getExpire());
			} else {
				prices.put(returnData.getContractPriceType(), returnData.getPrices());
				failed.remove(returnData.getContractPriceType());
				Date expire = returnData.getExpire();
				if (returnData.isAll() && expire.after(date)) {
					date = expire;
				}
			}
		}

		public Prices getPrices(ContractPriceItem contractPriceType) {
			return prices.get(contractPriceType);
		}

		public boolean isFailed(ContractPriceItem contractPriceType) {
			Date expire = failed.get(contractPriceType);
			if (expire == null) {
				return false;
			}
			return expire.after(Settings.getNow());
		}
	}

	public static class ContractPriceSettings {

		public static enum ContractPriceMode {
			MINIMUM() {
				@Override
				public Double getPrice(Prices prices) {
					return prices.getMinimum();
				}
				@Override
				public String getText() {
					return DataContractPrices.get().minimum();
				}
			},
			FIVE_PERCENT() {
				@Override
				public Double getPrice(Prices prices) {
					return prices.getFivePercent();
				}
				@Override
				public String getText() {
					return DataContractPrices.get().fivepercent();
				}
			},
			MEDIAN() {
				@Override
				public Double getPrice(Prices prices) {
					return prices.getMedian();
				}
				@Override
				public String getText() {
					return DataContractPrices.get().median();
				}
			},
			AVERAGE() {
				@Override
				public Double getPrice(Prices prices) {
					return prices.getAverage();
				}
				@Override
				public String getText() {
					return DataContractPrices.get().average();
				}
			},
			MAXIMUM() {
				@Override
				public Double getPrice(Prices prices) {
					return prices.getMaximum();
				}
				@Override
				public String getText() {
					return DataContractPrices.get().maximum();
				}
			},
			;
			public abstract Double getPrice(Prices prices);

			public abstract String getText();

			@Override
			public String toString() {
				return getText();
			}

		}
		public static enum ContractPriceSecurity {
			HIGH_SEC("highsec", SecurityEnum.HIGHSEC) {
				@Override
				public String getText() {
					return DataContractPrices.get().highSec();
				}
			},
			LOW_SEC("lowsec", SecurityEnum.LOWSEC) {
				@Override
				public String getText() {
					return DataContractPrices.get().lowSec();
				}
			},
			NULL_SEC("nullsec", SecurityEnum.NULLSEC) {
				@Override
				public String getText() {
					return DataContractPrices.get().nullSec();
				}
			},
			WORMHOLE("wormhole", SecurityEnum.WORMHOLE) {
				@Override
				public String getText() {
					return DataContractPrices.get().wormhole();
				}
			},
			;

			private final String value;
			private final SecurityEnum securityEnum;

			private ContractPriceSecurity(String value, SecurityEnum securityEnum) {
				this.value = value;
				this.securityEnum = securityEnum;
			}

			public String getValue() {
				return value;
			}

			public SecurityEnum getSecurityEnum() {
				return securityEnum;
			}

			public abstract String getText();

			@Override
			public String toString() {
				return getText();
			}

		}

		private boolean feedback = false;
		private boolean feedbackAsked = false;
		private boolean includePrivate = false;
		private boolean defaultBPC = true;
		private ContractPriceMode contractPriceMode = ContractPriceMode.FIVE_PERCENT;
		private Set<ContractPriceSecurity> contractPriceSecurity = new HashSet<>(Collections.singleton(ContractPriceSecurity.HIGH_SEC));

		public ContractPriceMode getContractPriceMode() {
			return contractPriceMode;
		}

		public boolean isFeedback() {
			return feedback;
		}

		public void setFeedback(boolean feedback) {
			this.feedback = feedback;
		}

		public boolean isFeedbackAsked() {
			return feedbackAsked;
		}

		public void setFeedbackAsked(boolean feedbackAsked) {
			this.feedbackAsked = feedbackAsked;
		}

		public void setContractPriceMode(ContractPriceMode contractPriceMode) {
			this.contractPriceMode = contractPriceMode;
		}

		public boolean isDefaultBPC() {
			return defaultBPC;
		}

		public void setDefaultBPC(boolean defaultBPC) {
			this.defaultBPC = defaultBPC;
		}

		public boolean isIncludePrivate() {
			return includePrivate;
		}

		public void setIncludePrivate(boolean includePrivate) {
			this.includePrivate = includePrivate;
		}

		public Set<ContractPriceSecurity> getContractPriceSecurity() {
			return contractPriceSecurity;
		}

		public void setContractPriceSecurity(Set<ContractPriceSecurity> contractPriceSecurity) {
			this.contractPriceSecurity = contractPriceSecurity;
			if (contractPriceSecurity.isEmpty()) {
				contractPriceSecurity.add(ContractPriceSecurity.HIGH_SEC);
			}
		}

		public List<String> getSecurityValues() {
			if (contractPriceSecurity.size() == ContractPriceSecurity.values().length) {
				return null;
			}
			List<String> values = new ArrayList<>();
			for (ContractPriceSecurity security : contractPriceSecurity) {
				values.add(security.getValue());
			}
			return values;
		}

		public List<SecurityEnum> getSecurityEnums() {
			List<SecurityEnum> values = new ArrayList<>();
			for (ContractPriceSecurity security : contractPriceSecurity) {
				values.add(security.getSecurityEnum());
			}
			return values;
		}

		public String getSecurityString() {
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (ContractPriceSecurity security : contractPriceSecurity) {
				if (first) {
					first = false;
				} else {
					builder.append(",");
				}
				builder.append(security.name());
			}
			return builder.toString();
		}
	}

	public static class ContractPriceItem {

		public static ContractPriceItem create(BlueprintType blueprintType) {
			if (blueprintType == null) {
				return null;
			}
			if (blueprintType.getTypeID() == null) {
				return null;
			}
			return new ContractPriceItem(blueprintType.getTypeID(), blueprintType.isBPC(), blueprintType.isBPO(), blueprintType.getMaterialEfficiency(), blueprintType.getTimeEfficiency(), blueprintType.getRuns());
		}

		public static ContractPriceItem create(MyIndustryJob industryJob, boolean product) {
			if (product) {
				Integer productTypeID = industryJob.getProductTypeID();
				if (productTypeID == null) {
					return null;
				}
				return new ContractPriceItem(productTypeID, industryJob.isCopying(), false, industryJob.getMaterialEfficiency(), industryJob.getTimeEfficiency(), industryJob.getRuns() * industryJob.getLicensedRuns());
			} else {
				return new ContractPriceItem(industryJob.getBlueprintTypeID(), industryJob.isBPC(), industryJob.isBPO(), industryJob.getMaterialEfficiency(), industryJob.getTimeEfficiency(), industryJob.getRuns());
			}
		}

		private final int typeID;
		private final boolean bpc;
		private final boolean bpo;
		private final int me;
		private final int te;
		private final int runs;

		public ContractPriceItem() {
			this(0, false, false, 0, 0, 0);
		}

		public ContractPriceItem(int typeID, boolean bpc, boolean bpo, int me, int te, int runs) {
			this.typeID = typeID;
			this.bpc = bpc;
			this.bpo = bpo;
			this.me = me;
			this.te = te;
			this.runs = runs;
		}

		public int getTypeID() {
			return typeID;
		}

		public boolean isBpc() {
			return bpc;
		}

		public boolean isBpo() {
			return bpo;
		}

		public Integer getMe() {
			if (bpc || bpo) {
				return me;
			} else {
				return null;
			}
		}

		public Integer getTe() {
			if (bpc || bpo) {
				return te;
			} else {
				return null;
			}
		}

		public int getRuns() {
			return runs;
		}

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 89 * hash + this.typeID;
			hash = 89 * hash + (this.bpc ? 1 : 0);
			hash = 89 * hash + (this.bpo ? 1 : 0);
			hash = 89 * hash + this.me;
			hash = 89 * hash + this.te;
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ContractPriceItem other = (ContractPriceItem) obj;
			if (this.typeID != other.typeID) {
				return false;
			}
			if (this.bpc != other.bpc) {
				return false;
			}
			if (this.bpo != other.bpo) {
				return false;
			}
			if (this.me != other.me) {
				return false;
			}
			if (this.te != other.te) {
				return false;
			}
			return true;
		}

	}

	public static class ReturnData {
		private final ContractPriceItem contractPriceType;
		private final Prices prices;
		private final Date expire;
		private final boolean all;

		public ReturnData(ContractPriceItem contractPriceType, Date expire) {
			this.contractPriceType = contractPriceType;
			this.expire = expire;
			this.prices = null;
			this.all = false;
		}

		public ReturnData(ContractPriceItem contractPriceType, Date expire, Prices prices, boolean all) {
			this.contractPriceType = contractPriceType;
			this.expire = expire;
			this.prices = prices;
			this.all = all;
		}

		public ContractPriceItem getContractPriceType() {
			return contractPriceType;
		}

		public Date getExpire() {
			return expire;
		}

		public Prices getPrices() {
			return prices;
		}

		public boolean isAll() {
			return all;
		}

		public boolean isEmpty() {
			return prices == null;
		}
	}

}

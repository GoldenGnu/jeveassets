/*
 * Copyright 2009-2017 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.orders;

import com.beimin.eveapi.model.shared.MarketOrder;
import java.util.Date;
import javax.management.timer.Timer;
import net.nikr.eve.jeveasset.data.Item;
import net.nikr.eve.jeveasset.data.MarketPriceData;
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.api.OwnerType;
import net.nikr.eve.jeveasset.data.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Quantity;
import net.nikr.eve.jeveasset.i18n.TabsOrders;


public class MyMarketOrder extends MarketOrder implements Comparable<MyMarketOrder>, EditableLocationType, ItemType, PriceType  {

	public enum OrderStatus {
		ACTIVE() {
			@Override
			String getI18N() {
				return TabsOrders.get().statusActive();
			}
		},
		CLOSED() {
			@Override
			String getI18N() {
				return TabsOrders.get().statusClosed();
			}
		},
		FULFILLED() {
			@Override
			String getI18N() {
				return TabsOrders.get().statusFulfilled();
			}
		},
		EXPIRED() {
			@Override
			String getI18N() {
				return TabsOrders.get().statusExpired();
			}
		},
		PARTIALLY_FULFILLED() {
			@Override
			String getI18N() {
				return TabsOrders.get().statusPartiallyFulfilled();
			}
		},
		CANCELLED() {
			@Override
			String getI18N() {
				return TabsOrders.get().statusCancelled();
			}
		},
		PENDING() {
			@Override
			String getI18N() {
				return TabsOrders.get().statusPending();
			}
		},
		CHARACTER_DELETED() {
			@Override
			String getI18N() {
				return TabsOrders.get().statusCharacterDeleted();
			}
		};

		abstract String getI18N();
		@Override
		public String toString() {
			return getI18N();
		}
	}

	private Item item;
	private MyLocation location;
	private String rangeFormated;
	private OrderStatus status;
	private OwnerType owner;
	private Quantity quantity;
	private double price;
	private double lastTransactionPrice;
	private double lastTransactionValue;
	private Percent lastTransactionPercent;

	public MyMarketOrder(final MarketOrder apiMarketOrder, final Item item, final MyLocation location, final OwnerType owner) {
		this.setAccountKey(apiMarketOrder.getAccountKey());
		this.setBid(apiMarketOrder.getBid());
		this.setCharID(apiMarketOrder.getCharID());
		this.setDuration(apiMarketOrder.getDuration());
		this.setEscrow(apiMarketOrder.getEscrow());
		this.setIssued(apiMarketOrder.getIssued());
		this.setMinVolume(apiMarketOrder.getMinVolume());
		this.setOrderID(apiMarketOrder.getOrderID());
		this.setOrderState(apiMarketOrder.getOrderState());
		this.setPrice(apiMarketOrder.getPrice());
		this.setRange(apiMarketOrder.getRange());
		this.setStationID(apiMarketOrder.getStationID());
		this.setTypeID(apiMarketOrder.getTypeID());
		this.setVolEntered(apiMarketOrder.getVolEntered());
		this.setVolRemaining(apiMarketOrder.getVolRemaining());
		this.item = item;
		this.location = location;
		this.owner = owner;
		quantity = new Quantity(getVolEntered(), getVolRemaining());
		rangeFormated = "";
		if (this.getRange() == -1) {
			rangeFormated = TabsOrders.get().rangeStation();
		}
		if (this.getRange() == 0) {
			rangeFormated = TabsOrders.get().rangeSolarSystem();
		}
		if (this.getRange() == 32767) {
			rangeFormated = TabsOrders.get().rangeRegion();
		}
		if (this.getRange() == 1) {
			rangeFormated = TabsOrders.get().rangeJump();
		}
		if (this.getRange() > 1 && this.getRange() < 32767) {
			rangeFormated = TabsOrders.get().rangeJumps(this.getRange());
		}
		if (isExpired()) { //expired (status may be out-of-date)
			if (this.getVolRemaining() == 0) {
				status = OrderStatus.FULFILLED;
			} else if (this.getVolRemaining() == this.getVolEntered()) {
				status = OrderStatus.EXPIRED;
			} else {
				status = OrderStatus.PARTIALLY_FULFILLED;
			}
		} else {
			switch (this.getOrderState()) {
				case 0: //open/active
					status = OrderStatus.ACTIVE;
					break;
				case 1: //closed
					status = OrderStatus.CLOSED;
					break;
				case 2: //expired (or fulfilled)
					if (this.getVolRemaining() == 0) {
						status = OrderStatus.FULFILLED;
					} else if (this.getVolRemaining() == this.getVolEntered()) {
						status = OrderStatus.EXPIRED;
					} else {
						status = OrderStatus.PARTIALLY_FULFILLED;
					}
					break;
				case 3: //cancelled
					status = OrderStatus.CANCELLED;
					break;
				case 4: //pending
					status = OrderStatus.PENDING;
					break;
				case 5: //character deleted
					status = OrderStatus.CHARACTER_DELETED;
					break;
			}
		}
	}

	@Override
	public int compareTo(final MyMarketOrder o) {
		Long thisID = this.getOrderID();
		Long thatID = o.getOrderID();
		return thisID.compareTo(thatID);
	}

	public Date getExpires() {
		long expires = (this.getIssued().getTime() + ((this.getDuration()) * Timer.ONE_DAY));
		return new Date(expires);
	}

	public final boolean isExpired() {
		return getExpires().before(new Date());
	}

	public boolean isActive() {
		return getOrderState() == 0 && !isExpired();
	}

	public void setDynamicPrice(double price) {
		this.price = price;
	}

	@Override
	public Double getDynamicPrice() {
		return price;
	}

	public double getLastTransactionPrice() {
		return lastTransactionPrice;
	}

	public double getLastTransactionValue() {
		return lastTransactionValue;
	}

	public Percent getLastTransactionPercent() {
		return lastTransactionPercent;
	}

	public void setLastTransaction(MarketPriceData lastTransaction) {
		if (lastTransaction != null) {
			this.lastTransactionPrice = lastTransaction.getLatest();
			if (getBid() > 0) { //Buy
				this.lastTransactionValue = this.lastTransactionPrice - getPrice();
				this.lastTransactionPercent = new Percent(this.lastTransactionPrice / getPrice());
			} else { //Sell
				this.lastTransactionValue = getPrice() - this.lastTransactionPrice;
				this.lastTransactionPercent = new Percent(getPrice() / this.lastTransactionPrice);
			}
		} else {
			this.lastTransactionPrice = 0;
			this.lastTransactionValue = 0;
			this.lastTransactionPercent = new Percent(0);
		}
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public MyLocation getLocation() {
		return location;
	}

	@Override
	public void setLocation(MyLocation location) {
		this.location = location;
	}

	public String getRangeFormated() {
		return rangeFormated;
	}

	public OwnerType getOwner() {
		return owner;
	}

	public String getOwnerName() {
		return owner.getOwnerName();
	}

	public long getOwnerID() {
		return owner.getOwnerID();
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public boolean isCorporation() {
		return owner.isCorporation();
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 13 * hash + (int) (this.owner.getOwnerID() ^ (this.owner.getOwnerID() >>> 32));
		hash = 13 * hash + (int) (this.getOrderID() ^ (this.getOrderID() >>> 32));
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
		final MyMarketOrder other = (MyMarketOrder) obj;
		if (this.owner.getOwnerID() != other.owner.getOwnerID()) {
			return false;
		}
		if (this.getOrderID() != other.getOrderID()) {
			return false;
		}
		return true;
	}
}

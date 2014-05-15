/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.MyLocation;
import net.nikr.eve.jeveasset.data.Owner;
import net.nikr.eve.jeveasset.data.types.ItemType;
import net.nikr.eve.jeveasset.data.types.LocationType;
import net.nikr.eve.jeveasset.data.types.PriceType;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Quantity;
import net.nikr.eve.jeveasset.i18n.TabsOrders;


public class MyMarketOrder extends MarketOrder implements Comparable<MyMarketOrder>, LocationType, ItemType, PriceType  {

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
	private Owner owner;
	private Quantity quantity;
	private double price;

	public MyMarketOrder(final MarketOrder apiMarketOrder, final Item item, final MyLocation location, final Owner owner) {
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
		//0 = open/active, 1 = closed, 2 = expired (or fulfilled), 3 = cancelled, 4 = pending, 5 = character deleted.
		switch (this.getOrderState()) {
			case 0:
				status = OrderStatus.ACTIVE;
				break;
			case 1:
				status = OrderStatus.CLOSED;
				break;
			case 2:
				if (this.getVolRemaining() == 0) {
					status = OrderStatus.FULFILLED;
				} else if (this.getVolRemaining() == this.getVolEntered()) {
					status = OrderStatus.EXPIRED;
				} else {
					status = OrderStatus.PARTIALLY_FULFILLED;
				}
				break;
			case 3:
				status = OrderStatus.CANCELLED;
				break;
			case 4:
				status = OrderStatus.PENDING;
				break;
			case 5:
				status = OrderStatus.CHARACTER_DELETED;
				break;
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

	public void setDynamicPrice(double price) {
		this.price = price;
	}

	@Override
	public Double getDynamicPrice() {
		return price;
	}


	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public MyLocation getLocation() {
		return location;
	}

	public String getRangeFormated() {
		return rangeFormated;
	}

	public String getOwner() {
		return owner.getName();
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
		hash = 97 * hash + (this.owner != null ? this.owner.hashCode() : 0);
		hash = 97 * hash + (int) (this.getOrderID() ^ (this.getOrderID() >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MyMarketOrder other = (MyMarketOrder) obj;
		if (this.owner != other.owner && (this.owner == null || !this.owner.equals(other.owner))) {
			return false;
		}
		if (this.getOrderID() != other.getOrderID()) {
			return false;
		}
		return true;
	}
}

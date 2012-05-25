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

package net.nikr.eve.jeveasset.data;

import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import java.util.Date;
import javax.management.timer.Timer;
import net.nikr.eve.jeveasset.i18n.TabsOrders;


public class MarketOrder extends ApiMarketOrder implements Comparable<MarketOrder>  {

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

	private String name;
	private String location;
	private String system;
	private String region;
	private String rangeFormated;
	private OrderStatus status;
	private String owner;
	private Quantity quantity;


	public MarketOrder(final ApiMarketOrder apiMarketOrder, final String name, final String location, final String system, final String region, final String owner) {
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
		this.name = name;
		this.location = location;
		this.system = system;
		this.region = region;
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
	public int compareTo(final MarketOrder o) {
		Long thisID = this.getOrderID();
		Long thatID = o.getOrderID();
		return thisID.compareTo(thatID);
	}

	public Date getExpires() {
		long expires = (this.getIssued().getTime() + ((this.getDuration()) * Timer.ONE_DAY));
		return new Date(expires);
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public String getRegion() {
		return region;
	}

	public String getSystem() {
		return system;
	}

	public String getRangeFormated() {
		return rangeFormated;
	}

	public String getOwner() {
		return owner;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public class Quantity implements Comparable<Quantity> {
		private int quantityEntered;
		private int quantityRemaining;

		public Quantity(final int quantityEntered, final int quantityRemaining) {
			this.quantityEntered = quantityEntered;
			this.quantityRemaining = quantityRemaining;
		}

		@Override
		public String toString() {
			return quantityRemaining + "/" + quantityEntered;
		}

		public int getQuantityEntered() {
			return quantityEntered;
		}

		public int getQuantityRemaining() {
			return quantityRemaining;
		}

		@Override
		public int compareTo(final Quantity o) {
			Integer thatQuantityRemaining = o.getQuantityRemaining();
			Integer thisQuantityRemaining = quantityRemaining;
			int result = thatQuantityRemaining.compareTo(thisQuantityRemaining);
			if (result != 0) {
				return result;
			}
			Integer thatQuantityEntered = o.getQuantityEntered();
			Integer thisQuantityEntered = quantityEntered;
			return thatQuantityEntered.compareTo(thisQuantityEntered);
		}

	}
}

/*
 * Copyright 2009-2018 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.data.api.my;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import javax.management.timer.Timer;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.raw.RawMarketOrder;
import net.nikr.eve.jeveasset.data.sde.Item;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.MarketPriceData;
import net.nikr.eve.jeveasset.data.settings.types.EditableLocationType;
import net.nikr.eve.jeveasset.data.settings.types.EditablePriceType;
import net.nikr.eve.jeveasset.data.settings.types.ItemType;
import net.nikr.eve.jeveasset.data.settings.types.OwnersType;
import net.nikr.eve.jeveasset.gui.shared.table.containers.Percent;
import net.nikr.eve.jeveasset.i18n.TabsOrders;

public class MyMarketOrder extends RawMarketOrder implements Comparable<MyMarketOrder>, EditableLocationType, ItemType, EditablePriceType, OwnersType {

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
		},
		UNKNOWN() {
			@Override
			String getI18N() {
				return TabsOrders.get().statusUnknown();
			}
		};

		abstract String getI18N();

		@Override
		public String toString() {
			return getI18N();
		}
	}

	private final Set<Long> owners;
	private Item item;
	private MyLocation location;
	private String rangeFormated;
	private OrderStatus status;
	private OwnerType owner;
	private double price;
	private double lastTransactionPrice;
	private double lastTransactionValue;
	private Percent lastTransactionPercent;

	public MyMarketOrder(final RawMarketOrder rawMarketOrder, final Item item, final OwnerType owner) {
		super(rawMarketOrder);
		this.item = item;
		this.owner = owner;
		this.owners = Collections.singleton(owner.getOwnerID());
		rangeFormated = "";
		switch (this.getRange()) {
			case STATION:
				rangeFormated = TabsOrders.get().rangeStation();
				break;
			case SOLARSYSTEM:
				rangeFormated = TabsOrders.get().rangeSolarSystem();
				break;
			case REGION:
				rangeFormated = TabsOrders.get().rangeRegion();
				break;
			case _1:
				rangeFormated = TabsOrders.get().rangeJump();
				break;
			default:
				rangeFormated = TabsOrders.get().rangeJumps(this.getRange().toString());
				break;
		}
		if (isExpired()) { //expired (status may be out-of-date)
			if (this.getVolumeRemain() == 0) {
				status = OrderStatus.FULFILLED;
			} else if (Objects.equals(this.getVolumeRemain(), this.getVolumeTotal())) {
				status = OrderStatus.EXPIRED;
			} else {
				status = OrderStatus.PARTIALLY_FULFILLED;
			}
		} else {
			switch (getState()) {
				case OPEN: //open/active
					status = OrderStatus.ACTIVE;
					break;
				case CLOSED: //closed
					status = OrderStatus.CLOSED;
					break;
				case EXPIRED: //expired (or fulfilled)
					if (this.getVolumeRemain() == 0) {
						status = OrderStatus.FULFILLED;
					} else if (Objects.equals(this.getVolumeRemain(), this.getVolumeTotal())) {
						status = OrderStatus.EXPIRED;
					} else {
						status = OrderStatus.PARTIALLY_FULFILLED;
					}
					break;
				case CANCELLED: //cancelled
					status = OrderStatus.CANCELLED;
					break;
				case PENDING: //pending
					status = OrderStatus.PENDING;
					break;
				case CHARACTER_DELETED: //character deleted
					status = OrderStatus.CHARACTER_DELETED;
					break;
				case UNKNOWN: //character deleted
					status = OrderStatus.UNKNOWN;
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
		return getState() == MarketOrderState.OPEN && !isExpired();
	}

	@Override
	public boolean isBPC() {
		return false; //Market Orders are always BPO
	}

	@Override
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
			if (isBuyOrder()) { //Buy
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
	public Set<Long> getOwners() {
		return owners;
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

	public OrderStatus getStatus() {
		return status;
	}

	public boolean isCorporation() {
		return owner.isCorporation();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.getOrderID()); //OrderID is globaly unique
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
		return Objects.equals(this.getOrderID(), other.getOrderID()); //OrderID is globaly unique
	}
}

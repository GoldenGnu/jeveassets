/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.tabs.tracker;

import java.util.Date;


public class TrackerData {
	private Date date;
	private double total;
	private double walletBalance;
	private double assets;
	private double sellOrders;
	private double escrows;
	private double escrowsToCover;

	public TrackerData(Date date, double total, double walletBalance, double assets, double sellOrders, double escrows, double escrowsToCover) {
		this.date = date;
		this.total = total;
		this.walletBalance = walletBalance;
		this.assets = assets;
		this.sellOrders = sellOrders;
		this.escrows = escrows;
		this.escrowsToCover = escrowsToCover;
	}

	public Date getDate() {
		return date;
	}

	public double getTotal() {
		return total;
	}

	public double getWalletBalance() {
		return walletBalance;
	}

	public double getAssets() {
		return assets;
	}

	public double getSellOrders() {
		return sellOrders;
	}

	public double getEscrows() {
		return escrows;
	}

	public double getEscrowsToCover() {
		return escrowsToCover;
	}
}

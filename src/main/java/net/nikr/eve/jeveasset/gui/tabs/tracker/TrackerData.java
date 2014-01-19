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

package net.nikr.eve.jeveasset.gui.tabs.tracker;

import java.util.Date;


public class TrackerData {
	private Date date;
	private double walletBalance;
	private double assets;
	private double sellOrders;
	private double escrows;
	private double escrowsToCover;
	private double manufacturing;

	public TrackerData(Date date, double walletBalance, double assets, double sellOrders, double escrows, double escrowsToCover, double manufacturing) {
		this.date = date;
		this.walletBalance = walletBalance;
		this.assets = assets;
		this.sellOrders = sellOrders;
		this.escrows = escrows;
		this.escrowsToCover = escrowsToCover;
		this.manufacturing = manufacturing;
	}

	public TrackerData(Date date) {
		this.date = date;
		this.walletBalance = 0.0;
		this.assets = 0.0;
		this.sellOrders = 0.0;
		this.escrows = 0.0;
		this.escrowsToCover = 0.0;
		this.manufacturing = 0.0;
	}

	public void addWalletBalance(double walletBalance) {
		this.walletBalance = this.walletBalance + walletBalance;
	}

	public void addAssets(double assets) {
		this.assets = this.assets + assets;
	}

	public void addSellOrders(double sellOrders) {
		this.sellOrders = this.sellOrders + sellOrders;
	}

	public void addEscrows(double escrows) {
		this.escrows = this.escrows + escrows;
	}

	public void addEscrowsToCover(double escrowsToCover) {
		this.escrowsToCover = this.escrowsToCover + escrowsToCover;
	}

	public void addManufacturing(double manufacturing) {
		this.manufacturing = this.manufacturing + manufacturing;
	}

	public Date getDate() {
		return date;
	}

	public double getTotal() {
		return getAssets() + getWalletBalance() + getSellOrders() + getEscrows() + getManufacturing();
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

	public double getManufacturing() {
		return manufacturing;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setWalletBalance(double walletBalance) {
		this.walletBalance = walletBalance;
	}

	public void setAssets(double assets) {
		this.assets = assets;
	}

	public void setSellOrders(double sellOrders) {
		this.sellOrders = sellOrders;
	}

	public void setEscrows(double escrows) {
		this.escrows = escrows;
	}

	public void setEscrowsToCover(double escrowsToCover) {
		this.escrowsToCover = escrowsToCover;
	}

	public void setManufacturing(double manufacturing) {
		this.manufacturing = manufacturing;
	}
}

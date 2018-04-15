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
package net.nikr.eve.jeveasset.data.api.raw;

import net.troja.eve.esi.model.CorporationWalletsResponse;

public class RawAccountBalance {

	private Double balance;
	private Integer accountKey;

	/**
	 * New
	 */
	private RawAccountBalance() {
	}

	public static RawAccountBalance create() {
		return new RawAccountBalance();
	}

	/**
	 * Raw
	 *
	 * @param balance
	 */
	protected RawAccountBalance(RawAccountBalance balance) {
		this.balance = balance.balance;
		this.accountKey = balance.accountKey;
	}

	/**
	 * ESI Character
	 *
	 * @param balance
	 * @param accountKey
	 */
	public RawAccountBalance(Double balance, Integer accountKey) {
		this.balance = balance;
		this.accountKey = accountKey;
	}

	/**
	 * ESI Corporation
	 *
	 * @param response
	 */
	public RawAccountBalance(CorporationWalletsResponse response) {
		this.balance = response.getBalance();
		this.accountKey = response.getDivision() + 999;
	}

	/**
	 * EveKit
	 *
	 * @param balance
	 */
	public RawAccountBalance(enterprises.orbital.evekit.client.model.AccountBalance balance) {
		this.balance = balance.getBalance();
		this.accountKey = balance.getDivision() + 999;
	}

	/**
	 * EveAPI
	 *
	 * @param balance
	 */
	public RawAccountBalance(com.beimin.eveapi.model.shared.AccountBalance balance) {
		this.balance = balance.getBalance();
		this.accountKey = balance.getAccountKey();
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(Integer accountKey) {
		this.accountKey = accountKey;
	}
}

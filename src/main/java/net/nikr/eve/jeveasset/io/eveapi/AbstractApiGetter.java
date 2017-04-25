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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.handler.ApiError;
import com.beimin.eveapi.response.ApiResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.eveapi.EveApiAccount;
import net.nikr.eve.jeveasset.data.eveapi.EveApiOwner;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractApiGetter<T extends ApiResponse> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractApiGetter.class);

	private static final String INVALID_ACCOUNT = "HTTP response code: 403";

	private String taskName;
	private EveApiAccount account;
	private EveApiOwner owner;
	private boolean forceUpdate;
	private boolean updated;
	private boolean updateOwner;
	private boolean updateAccount;
	private UpdateTask updateTask;
	private Map<String, EveApiOwner> owners;
	private List<EveApiOwner> failOwners;
	private String error;

	protected AbstractApiGetter(final String name) {
		this(name, false, false);
	}

	protected AbstractApiGetter(final String taskName, final boolean updateOwner, final boolean updateAccount) {
		this.taskName = taskName;
		this.updateOwner = updateOwner;
		this.updateAccount = updateAccount;
	}

	protected int getProgressStart() {
		return 0;
	}

	protected int getProgressEnd() {
		return 100;
	}

	protected void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	protected void loadEve(final UpdateTask updateTask, final boolean forceUpdate, final String updateName) {
		init(updateTask, forceUpdate, null, null);
		load(getNextUpdate(), false, updateName);
	}

	protected void loadOwner(final UpdateTask updateTask, final boolean forceUpdate, final EveApiOwner owner) {
		init(updateTask, forceUpdate, owner, null);
		loadOwner();
	}

	protected void loadAccount(final UpdateTask updateTask, final boolean forceUpdate, final EveApiAccount account) {
		init(updateTask, forceUpdate, null, account);
		loadAccount();
	}

	protected void loadAccounts(final UpdateTask updateTask, final boolean forceUpdate, final List<EveApiAccount> accounts) {
		init(updateTask, forceUpdate, null, null);
		LOG.info("{} updating:", taskName);
		//Calc size
		int ownerSize = 0;
		if (updateTask != null) { //Only relevant when tracking progress
			for (EveApiAccount countAccount : accounts) {
				ownerSize = ownerSize + countAccount.getOwners().size();
			}
		}
		int ownerCount = 0;
		int accountCount = 0;
		for (EveApiAccount accountLoop : accounts) {
			this.account = accountLoop;
			if (updateAccount) {
				if (updateTask != null) {
					if (updateTask.isCancelled()) {
						addError(String.valueOf(accountLoop.getKeyID()), "Cancelled");
					} else {
						loadAccount();
					}
					accountCount++;
					updateTask.setTaskProgress(accounts.size(), accountCount, getProgressStart(), getProgressEnd());
				} else {
					loadAccount();
				}
			}
			if (updateOwner) {
				for (EveApiOwner ownerLoop : accountLoop.getOwners()) {
					this.owner = ownerLoop;
					if (updateTask != null) {
						if (updateTask.isCancelled()) {
							addError(owner.getOwnerName(), "Cancelled");
						} else {
							loadOwner();
						}
						ownerCount++;
						updateTask.setTaskProgress(ownerSize, ownerCount, getProgressStart(), getProgressEnd());
					} else {
						loadOwner();
					}
				}
			}
		}
		//Set data for duplicated/failed owners
		if (updateOwner) {
			for (EveApiOwner failOwner : failOwners) {
				EveApiOwner okOwner = owners.get(failOwner.getOwnerName());
				if (okOwner != null) {
					updateFailed(okOwner, failOwner);
				}
			}
		}
		if (updated && updateTask != null && !updateTask.hasError()) {
			LOG.info("	{} updated (ALL)", taskName);
		} else if (updated && updateTask != null && updateTask.hasError()) {
			LOG.info("	{} updated (SOME)", taskName);
		} else {
			LOG.info("	{} not updated (NONE)", taskName);
		}
	}

	/**
	 * Init all values
	 * @param updateTask	UpdateTask from the UpdateDialog (can be null)
	 * @param forceUpdate	Ignore cachedUntil
	 * @param owner			Single char/corp (can be null)
	 * @param account		Single account (can be null)
	 */
	private void init(final UpdateTask updateTask, final boolean forceUpdate, final EveApiOwner owner, final EveApiAccount account) {
		this.forceUpdate = forceUpdate;
		this.updateTask = updateTask;
		this.owner = owner;
		this.account = account;
		this.updated = false;
		this.owners = new HashMap<String, EveApiOwner>();
		this.failOwners = new ArrayList<EveApiOwner>();
		this.error = null;
		if (updateTask != null && getProgressStart() == 0) {
			updateTask.resetTaskProgress();
		}
	}

	private void loadOwner() {
		boolean updatedOK = false;
		String name = owner.getOwnerName();
		//Ignore hidden owners && don't update the same owner twice
		if (owner.isShowOwner() && !owners.containsKey(name)) {
			updatedOK = load(getNextUpdate(), owner.isCorporation(), name); //Update...
		}
		if (updatedOK) {
			owners.put(name, owner); //If updated ok: don't update the same owner again...
		} else {
			failOwners.add(owner); //Save duplicated/failed owners
		}
	}

	private void loadAccount() {
		String name;
		if (account.getName().equals(Integer.toString(account.getKeyID()))) {
			name = "Account #" + Integer.toString(account.getKeyID());
		} else {
			name = "Account " +  account.getName() + " (#" + Integer.toString(account.getKeyID()) + ")";
		}
		load(getNextUpdate(), false, name);
	}

	protected boolean load(final Date nextUpdate, final boolean updateCorporation, final String updateName) {
		return loadAPI(nextUpdate, updateCorporation, updateName);
	}

	private boolean loadAPI(final Date nextUpdate, final boolean updateCorporation, final String updateName) {
		//Check if API key is invalid (still update when editing account AKA forceUpdate)
		if (isInvalid() && !forceUpdate) {
			errorInvalid(updateName);
			return false;
		}
		//Check if API key is expired (still update when editing account AKA forceUpdate)
		if (isExpired() && !forceUpdate) {
			errorExpired(updateName);
			return false;
		}
		//Check API key access mask
		if ((getAccessMask() & requestMask(updateCorporation)) != requestMask(updateCorporation)) {
			errorAccessMask(updateName);
			return false;
		}
		//Check API cache time
		if (!isUpdatable(nextUpdate)) {
			addError(updateName, "Not allowed yet.\r\n(Fix: Just wait a bit)");
			LOG.info("	{} failed to update for: {} (NOT ALLOWED YET)", taskName, updateName);
			return false;
		}
		try {
			T response = getResponse(updateCorporation);
			setNextUpdate(response.getCachedUntil());
			if (!response.hasError()) { //OK
				LOG.info("	{} updated for: {}", taskName, updateName);
				this.updated = true;
				setData(response);
				notInvalid();
				return true;
			} else { //API Error
				ApiError apiError = response.getError();
				switch (apiError.getCode()) {
					case 203: //Invalid
						errorInvalid(updateName);
						break;
					case 222: //Expired
						if (account != null) {
							account.setExpires(new Date(1));
						} else if (owner != null) {
							owner.getParentAccount().setExpires(new Date(1));
						}
						errorExpired(updateName);
						break;
					default:
						addError(updateName, "ApiError: " + apiError.getError() + " (Code: " + apiError.getCode() + ")");
						LOG.info("	{} failed to update for: {} (API ERROR: code: {} :: {})", new Object[]{taskName, updateName, apiError.getCode(), apiError.getError()});
						break;
				}
			}
		} catch (ApiException ex) { //Real Error
			if (ex.getMessage().contains(INVALID_ACCOUNT) && !isExpired()) { //Invalid
				errorInvalid(updateName);
			} else if (isExpired()) { //Expired
				errorExpired(updateName);
			} else {
				addError(updateName, "ApiException: " + ex.getMessage()); //Real Error
				LOG.error(taskName + " failed to update for: " + updateName + " (ApiException: " + ex.getMessage() + ")", ex);
			}
		}
		return false;
	}

	private long getAccessMask() {
		if (account != null) {
			return account.getAccessMask();
		} else if (owner != null) {
			return owner.getParentAccount().getAccessMask();
		} else {
			return 0;
		}
	}
	private boolean isExpired() {
		if (account != null) {
			return account.isExpired();
		} else if (owner != null) {
			return owner.getParentAccount().isExpired();
		} else {
			return false; //Eve
		}
	}

	public boolean isInvalid() {
		if (account != null) {
			return account.isInvalid();
		} else if (owner != null) {
			return owner.getParentAccount().isInvalid();
		} else {
			return false; //Eve
		}
	}

	private void notInvalid() {
		if (account != null) {
			account.setInvalid(false);
		} else if (owner != null) {
			owner.getParentAccount().setInvalid(false);
		}
	}

	protected EveApiAccount getAccount() {
		return account;
	}

	protected EveApiOwner getOwner() {
		return owner;
	}

	protected boolean isForceUpdate() {
		return forceUpdate;
	}

	public boolean hasError() {
		return error != null;
	}

	public String getError() {
		return error;
	}

	private void errorInvalid(String updateName) {
		if (account != null) {
			account.setInvalid(true);
		} else if (owner != null) {
			owner.getParentAccount().setInvalid(true);
		}
		addError(updateName, "API Key invalid");
		LOG.info("	{} failed to update for: {} (API KEY INVALID)", taskName, updateName);
	}

	private void errorExpired(String updateName) {
		addError(updateName, "API Key expired");
		LOG.info("	{} failed to update for: {} (API KEY EXPIRED)", taskName, updateName);
	}


	private void errorAccessMask(String updateName) {
		addError(updateName, "Not enough access privileges.\r\n(Fix: Add " + taskName + " to the API Key)");
		LOG.info("	{} failed to update for: {} (NOT ENOUGH ACCESS PRIVILEGES)", taskName, updateName);
	}

	protected void errorWrongEntry() {
		addError("", "Wrong Entry");
	}

	protected void addError(final String owner, final String errorText) {
		error = errorText;
		if (updateTask != null) {
			updateTask.addError(owner, errorText);
		}
	}

	protected abstract T getResponse(boolean bCorp) throws ApiException;
	protected abstract Date getNextUpdate();
	protected abstract void setNextUpdate(Date nextUpdate);
	protected abstract void setData(T response);
	protected abstract void updateFailed(EveApiOwner ownerFrom, EveApiOwner ownerTo);
	protected abstract long requestMask(boolean bCorp);

	private boolean isUpdatable(final Date date) {
		return ((Settings.getNow().after(date)
				|| Settings.getNow().equals(date)
				|| forceUpdate
				|| Program.isForceUpdate()
				)
				&& !Program.isForceNoUpdate());
	}
}

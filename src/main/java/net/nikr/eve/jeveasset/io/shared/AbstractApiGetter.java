/*
 * Copyright 2009, 2010 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.shared;

import com.beimin.eveapi.core.ApiError;
import com.beimin.eveapi.core.ApiException;
import com.beimin.eveapi.core.ApiResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


abstract public class AbstractApiGetter<T extends ApiResponse> {

	private final static Logger LOG = LoggerFactory.getLogger(AbstractApiGetter.class);
	
	private String name;
	private Account account;
	private Human human;
	private boolean forceUpdate;
	private boolean updated;
	private boolean updateHuman;
	private boolean updateAccount;
	private boolean hasError;
	private boolean corporationError;
	private boolean characterError;
	private UpdateTask updateTask;
	private List<String> corporations;

	protected AbstractApiGetter(String name) {
		this(name, false, false);
	}

	protected AbstractApiGetter(String name, boolean updateHuman, boolean updateAccount) {
		this.name = name;
		this.updateHuman = updateHuman;
		this.updateAccount = updateAccount;
	}

	protected void load(UpdateTask updateTask, boolean forceUpdate, String characterName){
		init(updateTask, forceUpdate, null, null);
		load(getNextUpdate(), false, characterName);
	}

	protected void load(UpdateTask updateTask, boolean forceUpdate, Human human){
		init(updateTask, forceUpdate, human, null);
		loadHuman();
	}

	protected void load(UpdateTask updateTask, boolean forceUpdate, Account account){
		init(updateTask, forceUpdate, null, account);
		loadAccount();
	}

	protected void load(UpdateTask updateTask, boolean forceUpdate, List<Account> accounts){
		init(updateTask, forceUpdate, null, null);
		LOG.info("{} updating:", name);
		for (int a = 0; a < accounts.size(); a++){
			account = accounts.get(a);
			if (updateAccount){
				if (updateTask != null){
					if (updateTask.isCancelled()){
						addError(String.valueOf(account.getUserID()), "Cancelled");
					} else {
						loadAccount();
					}
					updateTask.setTaskProgress(accounts.size(), (a+1), 0, 100);
				} else {
					loadAccount();
				}
			}
			if (updateHuman){
				List<Human> humans = account.getHumans();
				for (int b = 0; b < humans.size(); b++){
					human = humans.get(b);
					if (updateTask != null){
						if (updateTask.isCancelled()){
							addError(human.getName(), "Cancelled");
						} else {
							loadHuman();
						}
						updateTask.setTaskProgress(accounts.size()*3, (a*3)+(b+1), 0, 100);
					} else {
						loadHuman();
					}
				}
			}
		}
		if (updated && updateTask != null && !updateTask.hasError()){
			LOG.info("	{} updated (ALL)", name);
		} else if(updated && updateTask != null && updateTask.hasError()) {
			LOG.info("	{} updated (SOME)", name);
		} else {
			LOG.info("	{} not updated (NONE)", name);
		}
	}

	private void init(UpdateTask updateTask, boolean forceUpdate, Human human, Account account){
		this.forceUpdate = forceUpdate;
		this.updateTask = updateTask;
		this.human = human;
		this.account = account;
		this.updated = false;
		this.hasError = false;
		this.corporationError = false;
		this.characterError = false;
		this.corporations = new ArrayList<String>();
	}

	private void loadHuman(){
		if(human.isShowAssets()){ //Ignore hidden characters
			Date nextUpdate = getNextUpdate();
			boolean characterUpdated = load(nextUpdate, false, human.getName());
			String corporation = human.getCorporation();
			boolean corporationLoaded = false;
			if (human.isUpdateCorporationAssets() && !corporations.contains(corporation)){
				corporationLoaded = load(nextUpdate, true, corporation+" ("+human.getName()+")");
				if (corporationLoaded){
					corporations.add(corporation);
				}
			}
			if (characterUpdated && !corporationLoaded) clearData(true);
		}
	}

	private void loadAccount(){
		load(getNextUpdate(), false, String.valueOf("Account #"+account.getUserID()));
	}

	private boolean load(Date nextUpdate, boolean updateCorporation, String updateName){
		if (isUpdatable(nextUpdate)){
			try {
				T response = getResponse(updateCorporation);
				if (response instanceof ApiResponse){
					ApiResponse apiResponse = (ApiResponse)response;
					setNextUpdate(apiResponse.getCachedUntil());
					if (!apiResponse.hasError()){
						LOG.info("	{} updated for: {}", name, updateName);
						this.updated = true;
						setData(response, updateCorporation);
						return true;
					} else {
						ApiError apiError = apiResponse.getError();
						addError(updateName, apiError.getError(), updateCorporation);
						LOG.info("	{} failed to update for: {} (API ERROR: code: {} :: {})", new Object[]{name, updateName, apiError.getCode(), apiError.getError()});
					}
				}
			} catch (ApiException ex) {
				addError(updateName, "Parser error", updateCorporation);
				LOG.info("	{} failed to update for: {} (ApiException: {})", new Object[]{name, updateName, ex.getMessage()});
			}
		} else {
			addError(updateName, "Not allowed yet", updateCorporation);
			LOG.info("	{} failed to update for: {} (NOT ALLOWED YET)", name, updateName);
		}
		return false;
	}

	protected Account getAccount(){
		return account;
	}
	protected Human getHuman(){
		return human;
	}

	protected boolean isForceUpdate() {
		return forceUpdate;
	}

	public boolean hasError(){
		return hasError;
	}

	public boolean hasCorporationError() {
		return corporationError;
	}

	public boolean hasCharacterError() {
		return characterError;
	}

	public void error(){
		hasError = true;
	}

	private void addError(String human, String error){
		if (updateTask != null) updateTask.addError(human, error);
		hasError = true;
	}

	private void addError(String human, String error, boolean updateCorporation){
		if (updateTask != null) updateTask.addError(human, error);
		hasError = true;
		if (updateCorporation){
			corporationError = true;
		} else {
			characterError = true;
		}
	}
	
	abstract protected T getResponse(boolean bCorp) throws ApiException;
	abstract protected Date getNextUpdate();
	abstract protected void setNextUpdate(Date nextUpdate);
	abstract protected void setData(T response, boolean bCorp);
	abstract protected void clearData(boolean bCorp);

	private boolean isUpdatable(Date date){
		return ( (
				Settings.getGmtNow().after(date)
				|| Settings.getGmtNow().equals(date)
				|| forceUpdate
				|| Program.isForceUpdate()
				)
				&& !Program.isForceNoUpdate());
	}
	
}

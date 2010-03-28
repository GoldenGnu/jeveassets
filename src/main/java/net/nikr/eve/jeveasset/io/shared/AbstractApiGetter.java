/*
 * Copyright 2009, 2010
 *    Niklas Kyster Rasmussen
 *    Flaming Candle*
 *
 *  (*) Eve-Online names @ http://www.eveonline.com/
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

import com.beimin.eveapi.ApiError;
import com.beimin.eveapi.ApiResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.UpdateTask;
import net.nikr.log.Log;
import org.xml.sax.SAXException;


abstract public class AbstractApiGetter<T extends ApiResponse> {
	
	private String name;
	private Account account;
	private Human human;
	private boolean forceUpdate;
	private boolean updated;
	private boolean updateHuman;
	private boolean updateAccount;
	private boolean hasError;
	private boolean hasCorpError;
	private boolean hasHumanError;
	private UpdateTask updateTask;
	private List<String> corporations;

	public AbstractApiGetter(String name) {
		this(name, false, false);
	}

	public AbstractApiGetter(String name, boolean updateHuman, boolean updateAccount) {
		this.name = name;
		this.updateHuman = updateHuman;
		this.updateAccount = updateAccount;
		
	}

	private void init(UpdateTask updateTask, boolean forceUpdate, Human human, Account account){
		this.forceUpdate = forceUpdate;
		this.updateTask = updateTask;
		this.human = human;
		this.account = account;
		this.updated = false;
		this.hasError = false;
		this.hasCorpError = false;
		this.hasHumanError = false;
		this.corporations = new ArrayList<String>();
	}

	protected void load(String characterName){
		init(null, false, null, null);
		load(getNextUpdate(), false, characterName);
	}

	protected void load(UpdateTask updateTask, String characterName){
		init(updateTask, false, null, null);
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
		Log.info(name+" updating:");
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
			Log.info("	"+name+" updated (ALL)");
		} else if(updated && updateTask != null && updateTask.hasError()) {
			Log.info("	"+name+" updated (SOME)");
		} else {
			Log.info("	"+name+" not updated (NONE)");
		}
	}

	private void loadHuman(){
		Date nextUpdate = getNextUpdate();
		load(nextUpdate, false, human.getName());
		if (human.isUpdateCorporationAssets()){
			String corporation = human.getCorporation();
			if (!corporations.contains(corporation)){
				corporations.add(corporation);
				load(nextUpdate, true, corporation);
			} else {
				human.setUpdateCorporationAssets(false);
			}
			
		}
	}

	private void loadAccount(){
		load(getNextUpdate(), false, String.valueOf(account.getUserID()));
	}

	private void load(Date nextUpdate, boolean bCorp, String characterName){
		if ((isUpdatable(nextUpdate) || forceUpdate) && !Program.FORCE_NO_UPDATE){
			try {
				T response = getResponse(bCorp);
				if (response instanceof ApiResponse){
					ApiResponse apiResponse = (ApiResponse)response;
					setNextUpdate(apiResponse.getCachedUntil());
					if (!apiResponse.hasError()){
						Log.info("	"+name+" updated for: "+characterName);
						this.updated = true;
						setData(response, bCorp);
					} else {
						ApiError apiError = apiResponse.getError();
						addError(characterName, apiError.getError(), bCorp);
						Log.info("	"+name+" failed to update for: "+characterName+" (API ERROR: code: "+apiError.getCode()+" :: "+apiError.getError()+")");
					}
				}
			} catch (IOException ex) {
				addError(characterName, "Not found", bCorp);
				Log.info("	"+name+" failed to update for: "+characterName+" (NOT FOUND)");
			} catch (SAXException ex) {
				addError(characterName, "Parser error", bCorp);
				Log.info("	"+name+" failed to update for: "+characterName+" (PARSER ERROR)");
			}
		} else {
			addError(characterName, "Not allowed yet", bCorp);
			Log.info("	"+name+" failed to update for: "+characterName+" (NOT ALLOWED YET)");
		}
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

	public boolean hasCorpError() {
		return hasCorpError;
	}

	public boolean hasHumanError() {
		return hasHumanError;
	}

	public void error(){
		hasError = true;
	}

	private void addError(String human, String error){
		if (updateTask != null) updateTask.addError(human, error);
		hasError = true;
	}

	private void addError(String human, String error, boolean corp){
		if (updateTask != null) updateTask.addError(human, error);
		hasError = true;
		if (corp){
			hasCorpError = true;
		} else {
			hasHumanError = true;
		}
	}
	
	abstract protected T getResponse(boolean bCorp) throws IOException, SAXException;
	abstract protected Date getNextUpdate();
	abstract protected void setNextUpdate(Date nextUpdate);
	abstract protected void setData(T response, boolean bCorp);

	private boolean isUpdatable(Date date){
		return ( (
				Settings.getGmtNow().after(date)
				|| Settings.getGmtNow().equals(date)
				|| Program.FORCE_UPDATE
				)
				&& !Program.FORCE_NO_UPDATE);
	}
	
}

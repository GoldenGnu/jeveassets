/*
 * Copyright 2009-2016 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.evekit;


import enterprises.orbital.evekit.client.api.AccessKeyApi;
import enterprises.orbital.evekit.client.api.CommonApi;
import enterprises.orbital.evekit.client.invoker.ApiClient;
import enterprises.orbital.evekit.client.invoker.ApiException;
import java.util.Date;
import java.util.List;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.evekit.EveKitOwner;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.gui.shared.Formater;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractEveKitGetter { 

	private static final Logger LOG = LoggerFactory.getLogger(Program.class);

	private final CommonApi commonApi = new CommonApi();
	private final AccessKeyApi accessKeyApi = new AccessKeyApi();

	private String error = null;
	private boolean invalid = false;

	protected void load(UpdateTask updateTask, List<EveKitOwner> owners) {
		error = null;
		invalid = false;
		int progress = 0;
		for (EveKitOwner owner : owners) {
			if (owner.isShowOwner()) { //Ignore not shown owners
				load(updateTask, owner);
			}
			if (updateTask != null) {
				if (updateTask.isCancelled()) {
					addError("Cancelled");
					return;
				}
				progress++;
				updateTask.setTaskProgress(owners.size(), progress, getProgressStart(), getProgressEnd());
			}
		}
	}

	protected void load(UpdateTask updateTask, EveKitOwner owner) {
		error = null;
		invalid = false;
		loadApi(updateTask, owner);
	}

	
	private boolean loadApi(UpdateTask updateTask, EveKitOwner owner) {
		try {
			//Check if the Access Mask include this API
			if ((owner.getAccessMask() & getAccessMask()) != getAccessMask()) {
				addError("	" +  getTaskName() + " failed to update for: " + owner.getOwnerName() + " (NOT ENOUGH ACCESS PRIVILEGES)");
				if (updateTask != null) {
					updateTask.addError(owner.getOwnerName(), "Not enough access privileges.\r\n(Fix: Add " + getTaskName() + " to the API Key)");
				}
				return false;
			}
			//Check if the Api Key is expired
			if (owner.isExpired()) {
				addError("	" + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (API KEY EXPIRED)");
				if (updateTask != null) {
					updateTask.addError(owner.getOwnerName(), "API Key expired");
				}
				return false;
			}
			get(owner);
			LOG.info("	EveKit " + getTaskName() + " updated for " + owner.getOwnerName());
			List<String> expiryHeaders = getApiClient().getResponseHeaders().get("Expires");
			if (expiryHeaders != null && !expiryHeaders.isEmpty()) {
				setNextUpdate(owner, Formater.parseExpireDate(expiryHeaders.get(0)));
			}
			return true;
		} catch (ApiException ex) {
			switch (ex.getCode()) {
				case 400:
					addError("	" + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (INVALID ATTRIBUTE SELECTOR)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "Invalid attribute selector");
					}
					break;
				case 401:
					addError("	" + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (INVALID CREDENTIAL)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "Access credential invalid");
					}
					invalid = true;
					break;
				case 403:
					addError("	" + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (INVALID ACCESS MASK)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "Not enough access privileges.\r\n(Fix: Add " + getTaskName() + " to the API Key)");
					}
					invalid = true;
					break;
				case 404:
					addError("	" + getTaskName() + " failed to update for: " + owner.getOwnerName() + " (INVALID ACCESS KEY ID)");
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "Access key with the given ID not found");
					}
					invalid = true;
					break;
				default:
					AbstractEveKitGetter.this.addError(ex.getMessage(), ex);
					if (updateTask != null) {
						updateTask.addError(owner.getOwnerName(), "Unknown Error Code: " + ex.getCode());
					}
					break;
			}
			return false;
		}
	}

	public final boolean hasError() {
		return error != null;
	}

	public final String getError() {
		return error;
	}

	public final boolean isInvalid() {
		return invalid;
	}

	protected int getProgressStart() {
		return 0;
	}

	protected int getProgressEnd() {
		return 100;
	}

	protected final void addError(String error, Exception ex) {
		this.error = error;
		LOG.error(error, ex);
		
	}

	protected final void addError(String error) {
		this.error = error;
		LOG.error(error);
	}

	protected final CommonApi getCommonApi() {
		return commonApi;
	}

	protected final AccessKeyApi getAccessKeyApi() {
		return accessKeyApi;
	}

	protected abstract void get(EveKitOwner owner) throws ApiException;
	protected abstract String getTaskName();
	protected abstract long getAccessMask();
	protected abstract void setNextUpdate(EveKitOwner owner, Date date);
	protected abstract ApiClient getApiClient();

  /**
   * Convenience methods for constructing EveKit attribute selectors. There are four possible selector types (as documented here:
   * https://github.com/OrbitalEnterprises/evekit-model-frontend#usage):
   * 
   * <ol>
   * <li>{any: <boolean>} - Wildcard selector. Normally, this is the default for a field, meaning you can usually omit using this selector.
   * <li>{like: <string>} - String match selector. If the associated data field is string valued, then all returned model data must satisfy the SQL expression
   * 'field LIKE selector'. Normal SQL 'LIKE' syntax is allowed (e.g. % as wildcard).
   * <li>{values: [<v1>,...,<vn>]} - Set selector. The associated data field of each returned model data item must contain one of the listed values.
   * <li>{start: <lower>, end: <upper>} - Range selector. The associated data field of each returned model data item must satisfy lower <= value <= upper.
   * </ol>
   * 
   */
  public static String ek_any() {
    return "{ any: true }";
  }

  public static String ek_like(
                               Object l) {
    return "{ like: \"" + StringEscapeUtils.escapeJavaScript(String.valueOf(l)) + "\" }";
  }

  public static String ek_values(
                                 Object... v) {
    StringBuilder builder = new StringBuilder();
    builder.append("{ values: [");
    for (Object i : v)
      builder.append("\"").append(StringEscapeUtils.escapeJavaScript(String.valueOf(i))).append("\",");
    if (v.length > 0) builder.setLength(builder.length() - 1);
    builder.append("] }");
    return builder.toString();
  }

  public static String ek_range(
                                Object start,
                                Object end) {
    return "{ start: \"" + StringEscapeUtils.escapeJavaScript(String.valueOf(start)) + "\", end: \"" + StringEscapeUtils.escapeJavaScript(String.valueOf(end))
        + "\" }";
  }
	
}

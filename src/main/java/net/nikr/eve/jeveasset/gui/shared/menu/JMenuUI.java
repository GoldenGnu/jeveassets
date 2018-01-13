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
package net.nikr.eve.jeveasset.gui.shared.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.api.my.MyContract;
import net.nikr.eve.jeveasset.data.api.my.MyContractItem;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.api.UserInterfaceApi;
import net.troja.eve.esi.auth.OAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMenuUI <T> extends MenuManager.JAutoMenu<T> {

	private static final Logger LOG = LoggerFactory.getLogger(JMenuUI.class);

	private enum MenuUIAction {
		AUTOPILOT_SYSTEM,
		AUTOPILOT_STATION,
		MARKET,
		OWNER,
		CONTRACTS
	}

	public static enum EsiOwnerRequirement {
		AUTOPILOT,
		OPEN_WINDOW,
	}
	
	private final JMenu jWaypoints;
	private final JMenuItem jSystem;
	private final JMenuItem jStation;
	private final JMenuItem jMarket;
	private final JMenuItem jOwner;
	private final JMenuItem jContracts;

	private MenuData<T> menuData;
	private static JLockWindow jLockWindow = null;

	public JMenuUI(Program program) {
		super(GuiShared.get().ui(), program);
		setIcon(Images.MISC_EVE.getIcon());

		ListenerClass listener = new ListenerClass();

		jWaypoints = new JMenu(GuiShared.get().uiWaypoint());
		jWaypoints.setIcon(Images.TOOL_ROUTING.getIcon());
		add(jWaypoints);

		jStation = new JMenuItem(GuiShared.get().uiStation());
		jStation.setIcon(Images.LOC_STATION.getIcon());
		jStation.setActionCommand(MenuUIAction.AUTOPILOT_STATION.name());
		jStation.addActionListener(listener);
		jWaypoints.add(jStation);

		jSystem = new JMenuItem(GuiShared.get().uiSystem());
		jSystem.setIcon(Images.LOC_SYSTEM.getIcon());
		jSystem.setActionCommand(MenuUIAction.AUTOPILOT_SYSTEM.name());
		jSystem.addActionListener(listener);
		jWaypoints.add(jSystem);

		jMarket = new JMenuItem(GuiShared.get().uiMarket());
		jMarket.setIcon(Images.TOOL_MARKET_ORDERS.getIcon());
		jMarket.setActionCommand(MenuUIAction.MARKET.name());
		jMarket.addActionListener(listener);
		add(jMarket);

		jOwner = new JMenuItem(GuiShared.get().uiOwner());
		jOwner.setIcon(Images.DIALOG_PROFILES.getIcon());
		jOwner.setActionCommand(MenuUIAction.OWNER.name());
		jOwner.addActionListener(listener);
		add(jOwner);

		jContracts = new JMenuItem(GuiShared.get().uiContract());
		jContracts.setIcon(Images.TOOL_CONTRACTS.getIcon());
		jContracts.setActionCommand(MenuUIAction.CONTRACTS.name());
		jContracts.addActionListener(listener);
		add(jContracts);
	}
	
	@Override
	public void setMenuData(MenuData<T> menuData) {
		this.menuData = menuData;
		jWaypoints.setEnabled(menuData.getSystemLocations().size() == 1 || menuData.getAutopilotStationLocations().size() == 1 || menuData.getContracts().size() == 1);
		jStation.setEnabled(menuData.getAutopilotStationLocations().size() == 1 || menuData.getContracts().size() == 1);
		jSystem.setEnabled(menuData.getSystemLocations().size() == 1 || menuData.getContracts().size() == 1);
		jMarket.setEnabled(menuData.getMarketTypeIDs().size() == 1);
		jOwner.setEnabled(!menuData.getOwnerIDs().isEmpty());
		jContracts.setEnabled(menuData.getContracts().size() == 1);
	}

	private JLockWindow getLockWindow() {
		return getLockWindow(program);
	}

	public static JLockWindow getLockWindow(Program program) {
		if (jLockWindow == null) {
			jLockWindow = new JLockWindow(program.getMainWindow().getFrame());
		}
		return jLockWindow;
	}

	private EsiOwner selectOwner(EsiOwnerRequirement requirement) {
		return selectOwner(program, requirement);
	}

	public static EsiOwner selectOwner(Program program, EsiOwnerRequirement requirement) {
		List<EsiOwner> owners = new ArrayList<EsiOwner>();
		for (EsiOwner owner : program.getProfileManager().getEsiOwners()) {
			if (requirement == EsiOwnerRequirement.OPEN_WINDOW && owner.isOpenWindows()) {
				owners.add(owner);
			}
			if (requirement == EsiOwnerRequirement.AUTOPILOT && owner.isAutopilot()) {
				owners.add(owner);
			}
		}
		if (owners.isEmpty()) {
			JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiCharacterInvalidMsg(), GuiShared.get().uiCharacterTitle(), JOptionPane.PLAIN_MESSAGE);
			return null;
		}
		Object object = JOptionPane.showInputDialog(program.getMainWindow().getFrame(), GuiShared.get().uiCharacterMsg(), GuiShared.get().uiCharacterTitle(), JOptionPane.PLAIN_MESSAGE, null, owners.toArray(new EsiOwner[owners.size()]), owners.get(0));
		if (object == null) {
			return null; //Cancel
		} else if (object instanceof EsiOwner) {
			return (EsiOwner) object;
		} else {
			return null;
		}
	}

	private MyLocation selectLocation(Set<MyLocation> locations) {
		return selectLocation(program, locations);
	}

	public static MyLocation selectLocation(Program program, Set<MyLocation> locations) {
		if (locations.isEmpty()) {
			return null;
		} else if (locations.size() == 1) {
			return locations.iterator().next();
		} else {
			Object object = JOptionPane.showInputDialog(program.getMainWindow().getFrame(), null, GuiShared.get().uiLocationTitle(), JOptionPane.PLAIN_MESSAGE, null, locations.toArray(new MyLocation[locations.size()]), locations.iterator().next());
			if (object == null) {
				return null; //Cancel
			} else if (object instanceof MyLocation) {
				return (MyLocation) object;
			} else {
				return null;
			}
		}
	}

	private void setAutopilot(Long locationID, EsiOwner owner) {
		boolean addToBeginning;
		boolean clearOtherWaypoints;
		int clearValue = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), GuiShared.get().uiWaypointClear(), GuiShared.get().uiWaypointTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		clearOtherWaypoints = clearValue == JOptionPane.YES_OPTION;
		if (clearOtherWaypoints) {
			addToBeginning = false;
		} else {
			int beginningValue = JOptionPane.showConfirmDialog(program.getMainWindow().getFrame(), GuiShared.get().uiWaypointBeginning(), GuiShared.get().uiWaypointTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			addToBeginning = beginningValue == JOptionPane.YES_OPTION;
		}
		getLockWindow().show(GuiShared.get().updating(), new EsiUpdate() {
			@Override
			protected void updateESI() throws Throwable {
				getApi(owner).postUiAutopilotWaypoint(addToBeginning, clearOtherWaypoints, locationID, AbstractEsiGetter.DATASOURCE, null, AbstractEsiGetter.USER_AGENT, null);
			}
			@Override
			protected void ok() {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiWaypointOk(), GuiShared.get().uiWaypointTitle(), JOptionPane.PLAIN_MESSAGE);
			}
			@Override
			protected void fail() {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiWaypointFail(), GuiShared.get().uiWaypointTitle(), JOptionPane.PLAIN_MESSAGE);
			}
		});
	}

	public static class ContractMenuData extends MenuData<MyContractItem> {

		public ContractMenuData(List<MyContractItem> items) {
			super(items);
			Set<MyContract> contracts = new HashSet<MyContract>();
			for (Object item : items) {
				if (item instanceof MyContractItem) {
					contracts.add(((MyContractItem)item).getContract());
				}
			}
			setContracts(contracts);
		}
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MenuUIAction.AUTOPILOT_STATION.name().equals(e.getActionCommand())) {
				EsiOwner owner = selectOwner(EsiOwnerRequirement.AUTOPILOT);
				if (owner == null) {
					return;
				}
				MyLocation station = selectLocation(menuData.getAutopilotStationLocations());
				if (station == null) {
					return;
				}
				setAutopilot(station.getStationID() != 0 ? station.getStationID() : station.getLocationID(), owner);
			} else if (MenuUIAction.AUTOPILOT_SYSTEM.name().equals(e.getActionCommand())) {
				EsiOwner owner = selectOwner(EsiOwnerRequirement.AUTOPILOT);
				if (owner == null) {
					return;
				}
				MyLocation system = selectLocation(menuData.getSystemLocations());
				if (system == null) {
					return;
				}
				setAutopilot(system.getSystemID(), owner);
			} else if (MenuUIAction.MARKET.name().equals(e.getActionCommand())) {
				EsiOwner owner = selectOwner(EsiOwnerRequirement.OPEN_WINDOW);
				if (owner == null) {
					return;
				}
				Integer typeID = menuData.getMarketTypeIDs().iterator().next();
				getLockWindow().show(GuiShared.get().updating(), new EsiUpdate() {
					@Override
					protected void updateESI() throws Throwable {
						getApi(owner).postUiOpenwindowMarketdetails(typeID, AbstractEsiGetter.DATASOURCE, null, AbstractEsiGetter.USER_AGENT, null);
					}
					@Override
					protected void ok() {
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiMarketOk(), GuiShared.get().uiMarketTitle(), JOptionPane.PLAIN_MESSAGE);
					}
					@Override
					protected void fail() {
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiMarketFail(), GuiShared.get().uiMarketTitle(), JOptionPane.PLAIN_MESSAGE);
					}
				});
			} else if (MenuUIAction.OWNER.name().equals(e.getActionCommand())) {
				EsiOwner esiOwner = selectOwner(EsiOwnerRequirement.OPEN_WINDOW);
				if (esiOwner == null) {
					return;
				}
				List<Owner> owners = new ArrayList<Owner>();
				for (long ownerID : menuData.getOwnerIDs()) {
					if (ownerID > 0) {
						String name = Settings.get().getOwners().get(ownerID);
						owners.add(new Owner(ownerID, name));
					}
				}
				Owner owner;
				if (owners.size() > 1) {
					Collections.sort(owners);
					Object object = JOptionPane.showInputDialog(program.getMainWindow().getFrame(), GuiShared.get().uiOwnerMsg(), GuiShared.get().uiOwnerTitle(), JOptionPane.PLAIN_MESSAGE, null, owners.toArray(new Owner[owners.size()]), owners.get(0));
					if (object == null) {
						return; //Cancel
					}
					if (object instanceof Owner) {
						owner = (Owner) object;
					} else {
						return;
					}
				} else {
					owner = owners.get(0);
				}
				getLockWindow().show(GuiShared.get().updating(), new EsiUpdate() {
					@Override
					protected void updateESI() throws Throwable {
						getApi(esiOwner).postUiOpenwindowInformation((int)owner.getId(), AbstractEsiGetter.DATASOURCE, null, AbstractEsiGetter.USER_AGENT, null);
					}
					@Override
					protected void ok() {
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiOwnerOk(), GuiShared.get().uiOwnerTitle(), JOptionPane.PLAIN_MESSAGE);
					}
					@Override
					protected void fail() {
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiOwnerFail(), GuiShared.get().uiOwnerTitle(), JOptionPane.PLAIN_MESSAGE);
					}
				});
			} else if (MenuUIAction.CONTRACTS.name().equals(e.getActionCommand())) {
				EsiOwner owner = selectOwner(EsiOwnerRequirement.OPEN_WINDOW);
				if (owner == null) {
					return;
				}
				MyContract contract = menuData.getContracts().iterator().next();
				getLockWindow().show(GuiShared.get().updating(), new EsiUpdate() {
					@Override
					protected void updateESI() throws Throwable {
						getApi(owner).postUiOpenwindowContract(contract.getContractID(), AbstractEsiGetter.DATASOURCE, null, AbstractEsiGetter.USER_AGENT, null);
					}
					@Override
					protected void ok() {
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiContractOk(), GuiShared.get().uiContractTitle(), JOptionPane.PLAIN_MESSAGE);
					}
					@Override
					protected void fail() {
						JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiContractFail(), GuiShared.get().uiContractTitle(), JOptionPane.PLAIN_MESSAGE);
					}
				});
			}
		}
	}

	private static class Owner implements Comparable<Owner> {
		private final long id;
		private final String name;

		public Owner(long id, String name) {
			this.id = id;
			this.name = name;
		}

		public long getId() {
			return id;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public int compareTo(Owner o) {
			return this.name.compareTo(o.name);
		}
	}

	public static abstract class EsiUpdate implements JLockWindow.LockWorker {

		private boolean ok;

		protected abstract void updateESI() throws Throwable;
		protected abstract void ok();
		protected abstract void fail();
		protected UserInterfaceApi getApi(EsiOwner owner) {
			final ApiClient client = new ApiClient(); //Public
			if (owner != null) { //Auth
				OAuth auth = (OAuth) client.getAuthentication("evesso");
				auth.setRefreshToken(owner.getRefreshToken());
				auth.setClientId(owner.getCallbackURL().getA());
				auth.setClientSecret(owner.getCallbackURL().getB());
			}
			return new UserInterfaceApi(client);
		}

		@Override
		public void task() {
			try {
				updateESI();
				ok = true;
			} catch (Throwable t) {
				ok = false;
				LOG.error(t.getMessage(), t);
			}
		}

		@Override
		public void gui() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (ok) {
						ok();
					} else {
						fail();
					}
				}
			});
		}
		
	}
}

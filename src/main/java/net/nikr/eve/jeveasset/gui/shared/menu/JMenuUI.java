/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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
import net.nikr.eve.jeveasset.data.api.my.MyShip;
import net.nikr.eve.jeveasset.data.sde.MyLocation;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JOptionInput;
import net.nikr.eve.jeveasset.gui.shared.NativeUtil;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow;
import net.nikr.eve.jeveasset.gui.shared.components.JLockWindow.LockWorkerAdaptor;
import net.nikr.eve.jeveasset.gui.tabs.routing.SolarSystem;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.io.esi.AbstractEsiGetter;
import net.nikr.eve.jeveasset.io.shared.SafeConverter;
import net.nikr.eve.jeveasset.io.shared.ApiIdConverter;
import net.nikr.eve.jeveasset.io.shared.DesktopUtil;
import net.troja.eve.esi.api.UserInterfaceApi;
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
	public void updateMenuData() {
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
		List<EsiOwner> owners = new ArrayList<>();
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
		Object object = JOptionInput.showInputDialog(program.getMainWindow().getFrame(), GuiShared.get().uiCharacterMsg(), GuiShared.get().uiCharacterTitle(), JOptionPane.PLAIN_MESSAGE, null, owners.toArray(new EsiOwner[owners.size()]), owners.get(0));
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
			Object object = JOptionInput.showInputDialog(program.getMainWindow().getFrame(), null, GuiShared.get().uiLocationTitle(), JOptionPane.PLAIN_MESSAGE, null, locations.toArray(new MyLocation[locations.size()]), locations.iterator().next());
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
		getLockWindow().show(GuiShared.get().updating(), new EsiUpdate(owner) {
			@Override
			protected void updateESI() throws Throwable {
				getApi().postUiAutopilotWaypoint(addToBeginning, clearOtherWaypoints, locationID, AbstractEsiGetter.COMPATIBILITY_DATE, null, null, null);
			}
			@Override
			protected void ok() {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiWaypointOk(), GuiShared.get().uiWaypointTitle(), JOptionPane.PLAIN_MESSAGE);
				EveGatecampCheck.open(program, owner, locationID);
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
			Set<MyContract> contracts = new HashSet<>();
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
				Integer typeID = menuData.getMarketTypeIDs().iterator().next();
				openMarketDetails(program, owner, typeID, true);
			} else if (MenuUIAction.OWNER.name().equals(e.getActionCommand())) {
				EsiOwner esiOwner = selectOwner(EsiOwnerRequirement.OPEN_WINDOW);
				if (esiOwner == null) {
					return;
				}
				List<Owner> owners = new ArrayList<>();
				for (Long ownerID : menuData.getOwnerIDs()) {
					if (ownerID != null && ownerID > 0) {
						String name = Settings.get().getOwners().get(ownerID);
						if (name == null) {
							name = GuiShared.get().unknownOwner();
						}
						owners.add(new Owner(ownerID, name));
					}
				}
				Owner owner;
				if (owners.size() > 1) {
					Collections.sort(owners);
					Object object = JOptionInput.showInputDialog(program.getMainWindow().getFrame(), GuiShared.get().uiOwnerMsg(), GuiShared.get().uiOwnerTitle(), JOptionPane.PLAIN_MESSAGE, null, owners.toArray(new Owner[owners.size()]), owners.get(0));
					if (object == null) {
						return; //Cancel
					}
					if (object instanceof Owner) {
						owner = (Owner) object;
					} else {
						return;
					}
				} else if (!owners.isEmpty()) {
					owner = owners.get(0);
				} else {
					return;
				}
				getLockWindow().show(GuiShared.get().updating(), new EsiUpdate(esiOwner) {
					@Override
					protected void updateESI() throws Throwable {
						getApi().postUiOpenwindowInformation(owner.getID(), AbstractEsiGetter.COMPATIBILITY_DATE, null, null, null);
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
				getLockWindow().show(GuiShared.get().updating(), new EsiUpdate(owner) {
					@Override
					protected void updateESI() throws Throwable {
						getApi().postUiOpenwindowContract(SafeConverter.toLong(contract.getContractID()), AbstractEsiGetter.COMPATIBILITY_DATE, null, null, null);
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

	public static void openMarketDetails(Program program, EsiOwner owner, Integer typeID, boolean showOkMsg) {
		if (owner == null) {
			return;
		}
		getLockWindow(program).show(GuiShared.get().updating(), new EsiUpdate(owner) {
			@Override
			protected void updateESI() throws Throwable {
				getApi().postUiOpenwindowMarketdetails(SafeConverter.toLong(typeID), AbstractEsiGetter.COMPATIBILITY_DATE, null, null, null);
			}
			@Override
			protected void ok() {
				if (showOkMsg) {
					JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiMarketOk(), GuiShared.get().uiMarketTitle(), JOptionPane.PLAIN_MESSAGE);
				}
			}
			@Override
			protected void fail() {
				JOptionPane.showMessageDialog(program.getMainWindow().getFrame(), GuiShared.get().uiMarketFail(), GuiShared.get().uiMarketTitle(), JOptionPane.PLAIN_MESSAGE);
			}
		});
	}

	private static class Owner implements Comparable<Owner> {
		private final long id;
		private final String name;

		public Owner(long id, String name) {
			this.id = id;
			this.name = name;
		}

		public long getID() {
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

	public static abstract class EsiUpdate extends LockWorkerAdaptor {

		private boolean ok;

		protected abstract void updateESI() throws Throwable;
		protected abstract void ok();
		protected abstract void fail();

		private final EsiOwner owner;

		public EsiUpdate(EsiOwner owner) {
			this.owner = owner;
		}

		protected UserInterfaceApi getApi() {
			if (owner != null) { //Auth
				return owner.getUserInterfaceApiAuth();
			} else {
				return AbstractEsiGetter.USER_INTERFACE_API;
			}
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
						if (Settings.get().isFocusEveOnlineOnEsiUiCalls()) {
							NativeUtil.focusEveOnline(owner.getOwnerName());
						}
					} else {
						fail();
					}
				}
			});
		}
	}

	public static class EveGatecampCheck {

		public static final String[] EVE_GATECAMP_CHECK_ROUTE_OPTIONS = {
					GuiShared.get().uiWaypointEveGatecampCheckSecure(),
					GuiShared.get().uiWaypointEveGatecampCheckUnsecure(),
					GuiShared.get().uiWaypointEveGatecampCheckShortest()
				};
		public static final String[] EVE_GATECAMP_CHECK_OPEN_OPTIONS = {
					GuiShared.get().uiWaypointEveGatecampCheckAsk(),
					GuiShared.get().uiWaypointEveGatecampCheckAlwaysOpen(),
					GuiShared.get().uiWaypointEveGatecampCheckNeverOpen()
				};

		public static void setOpenOption(Object object) {
			if (object == null) {
				return; //Cancel
			}
			if (GuiShared.get().uiWaypointEveGatecampCheckAlwaysOpen().equals(object)) {
				//Secure
				Settings.get().setEveGatecampCheckAlwaysOpen(true);
				Settings.get().setEveGatecampCheckNeverOpen(false);
			} else if (GuiShared.get().uiWaypointEveGatecampCheckNeverOpen().equals(object)) {
				//Unsecure
				Settings.get().setEveGatecampCheckSecure(false);
				Settings.get().setEveGatecampCheckUnsecure(true);
			} else if (GuiShared.get().uiWaypointEveGatecampCheckAsk().equals(object)) {
				//Shortest
				Settings.get().setEveGatecampCheckSecure(false);
				Settings.get().setEveGatecampCheckUnsecure(false);
			} else {
				//Default
				Settings.get().setEveGatecampCheckSecure(false);
				Settings.get().setEveGatecampCheckUnsecure(false);
			}
		}

		public static String getOpenOption() {
			if (Settings.get().isEveGatecampCheckAlwaysOpen()) {
				return GuiShared.get().uiWaypointEveGatecampCheckAlwaysOpen();
			} else if (Settings.get().isEveGatecampCheckNeverOpen()) {
				return GuiShared.get().uiWaypointEveGatecampCheckNeverOpen();
			} else {
				return GuiShared.get().uiWaypointEveGatecampCheckAsk();
			}
		}

		public static String setRouteOption(Object object) {
			if (object == null) {
				return ""; //Cancel
			}
			if (GuiShared.get().uiWaypointEveGatecampCheckSecure().equals(object)) {
				//Secure
				Settings.get().setEveGatecampCheckSecure(true);
				Settings.get().setEveGatecampCheckUnsecure(false);
				return ":secure";
			} else if (GuiShared.get().uiWaypointEveGatecampCheckUnsecure().equals(object)) {
				//Unsecure
				Settings.get().setEveGatecampCheckSecure(false);
				Settings.get().setEveGatecampCheckUnsecure(true);
				return ":insecure";
			} else if (GuiShared.get().uiWaypointEveGatecampCheckShortest().equals(object)) {
				//Shortest
				Settings.get().setEveGatecampCheckSecure(false);
				Settings.get().setEveGatecampCheckUnsecure(false);
				return ":shortest";
			}
			return "";
		}

		public static String getRouteOption() {
			if (Settings.get().isEveGatecampCheckSecure()) {
				//Secure
				return GuiShared.get().uiWaypointEveGatecampCheckSecure();
			} else if (Settings.get().isEveGatecampCheckUnsecure()) {
				//Unsecure
				return GuiShared.get().uiWaypointEveGatecampCheckUnsecure();
			} else {
				//Shortest
				return GuiShared.get().uiWaypointEveGatecampCheckShortest();
			}
		}

		public static void open(Program program, EsiOwner owner, long locationID) {
			open(program, owner, Collections.singleton(locationID));
		}

		public static void open(Program program, EsiOwner owner, Set<Long> locationIDs) {
			if (owner == null) {
				return;
			}
			MyShip activeShip = owner.getActiveShip();
			
			if (activeShip == null || activeShip.getLocation().isEmpty()) {
				return;
			}
			String from = activeShip.getLocation().getSystem();

			StringBuilder routeBuilder = new StringBuilder();
			Set<String> route = new HashSet<>();
			for (Long locationID : locationIDs) {
				MyLocation toLocation = ApiIdConverter.getLocation(locationID);
				if (toLocation.isEmpty()) {
					return;
				}
				String to = toLocation.getSystem();
				if (routeBuilder.length() > 0) {
					routeBuilder.append(",");
				} else {
					if (from.equals(to)) {
						continue;
					}
				}
				route.add(to);
				routeBuilder.append(to.replace(" ", "%20"));
			}
			if (routeBuilder.length() == 0) {
				return;
			}
			StringBuilder avoidBuilder = new StringBuilder();
			boolean avoidInRoute = false;
			for (SolarSystem system : Settings.get().getJumpsAvoidSettings().getAvoid().values()) {
				if (avoidBuilder.length() > 0) {
					avoidBuilder.append(",");
				}
				String systemName = system.getName();
				if (!avoidInRoute && route.contains(systemName)) {
					avoidInRoute = true;
				}
				avoidBuilder.append(systemName.replace(" ", "%20"));
			}
			if (!Settings.get().isEveGatecampCheckSet()) {
				//Default Options
				
				int returnValue = JOptionPane.showOptionDialog(program.getMainWindow().getFrame(), GuiShared.get().uiWaypointEveGatecampCheckOptions(), GuiShared.get().uiWaypointTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, EVE_GATECAMP_CHECK_OPEN_OPTIONS, EVE_GATECAMP_CHECK_OPEN_OPTIONS[0]);
				if (returnValue == JOptionPane.CLOSED_OPTION) {
					return; //Cancel
				}
				String openOption = EVE_GATECAMP_CHECK_OPEN_OPTIONS[returnValue];
				if (GuiShared.get().uiWaypointEveGatecampCheckAlwaysOpen().equals(openOption)) { //Always Open
					//Default Route Options
					Object routeOption = JOptionInput.showInputDialog(program.getMainWindow().getFrame(), GuiShared.get().uiWaypointEveGatecampCheckDefault(), GuiShared.get().uiWaypointTitle(), JOptionPane.PLAIN_MESSAGE, null, EVE_GATECAMP_CHECK_ROUTE_OPTIONS, EVE_GATECAMP_CHECK_ROUTE_OPTIONS[0]);
					if (routeOption == null) {
						return; //cancel
					}
					setRouteOption(routeOption);
				}
				setOpenOption(openOption);
				Settings.get().setEveGatecampCheckSet(true);
			}
			if (Settings.get().isEveGatecampCheckNeverOpen()) {
				return;
			}
			String routeOption;
			if (Settings.get().isEveGatecampCheckAlwaysOpen()) {
				if (Settings.get().isEveGatecampCheckSecure()) {
					//Secure
					routeOption = ":secure";
				} else if (Settings.get().isEveGatecampCheckUnsecure()) {
					//Unsecure
					routeOption = ":insecure";
				} else {
					//Shortest
					routeOption = ":shortest";
				}
			} else {
				String defaultOption = getRouteOption();
				Object object = JOptionInput.showInputDialog(program.getMainWindow().getFrame(), GuiShared.get().uiWaypointEveGatecampCheck(), GuiShared.get().uiWaypointTitle(), JOptionPane.PLAIN_MESSAGE, null, EVE_GATECAMP_CHECK_ROUTE_OPTIONS, defaultOption);
				if (object == null) {
					return;
				}
				routeOption = setRouteOption(object);
			}
			/*
			if (avoidInRoute) {
				JOptionPane.showMessageDialog(program.getMainWindow().get, EAST);
			}
			*/
			DesktopUtil.browse("https://eve-gatecheck.space/eve/#" + from + ":" + routeBuilder.toString() + routeOption + ":" + avoidBuilder.toString(), program);
		}
	}

}

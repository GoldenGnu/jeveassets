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

package net.nikr.eve.jeveasset.gui.tabs.values;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.i18n.TabsValues;


public class ValueRetroTab extends JMainTabSecondary {

	private enum ValueRetroAction {
		OWNER_SELECTED,
		CORP_SELECTED
	}

	//GUI
	private final JComboBox<String> jCharacters;
	private final JEditorPane jCharacter;
	private final JComboBox<String> jCorporations;
	private final JEditorPane jCorporation;
	private final JEditorPane jTotal;

	//Data
	private Map<String, Value> characters;
	private Map<String, Value> corporations;
	private Value total;
	private String backgroundHexColor;
	private String gridHexColor;

	public ValueRetroTab(final Program program) {
		super(program, TabsValues.get().oldTitle(), Images.TOOL_VALUES.getIcon(), true);

		ListenerClass listener = new ListenerClass();

		backgroundHexColor = Integer.toHexString(jPanel.getBackground().getRGB());
		backgroundHexColor = backgroundHexColor.substring(2, backgroundHexColor.length());

		gridHexColor = Integer.toHexString(jPanel.getBackground().darker().getRGB());
		gridHexColor = gridHexColor.substring(2, gridHexColor.length());

		jCharacters = new JComboBox<String>();
		jCharacters.setActionCommand(ValueRetroAction.OWNER_SELECTED.name());
		jCharacters.addActionListener(listener);

		jCharacter = new JEditorPane("text/html", "<html>");
		JCopyPopup.install(jCharacter);
		jCharacter.setEditable(false);
		jCharacter.setOpaque(false);
		jCharacter.setBorder(null);
		JScrollPane jCharacterScroll = new JScrollPane(jCharacter);
		jCharacterScroll.setBorder(null);

		jCorporations = new JComboBox<String>();
		jCorporations.setActionCommand(ValueRetroAction.CORP_SELECTED.name());
		jCorporations.addActionListener(listener);

		jCorporation = new JEditorPane("text/html", "<html>");
		JCopyPopup.install(jCorporation);
		jCorporation.setEditable(false);
		jCorporation.setOpaque(false);
		jCorporation.setBorder(null);
		JScrollPane jCorporationScroll = new JScrollPane(jCorporation);
		jCorporationScroll.setBorder(null);

		JLabel jTotalLabel = new JLabel(" " + TabsValues.get().grandTotal());
		jTotalLabel.setBackground(new Color(34, 34, 34));
		jTotalLabel.setForeground(Color.WHITE);
		Font font = jTotalLabel.getFont();
		jTotalLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 2));
		jTotalLabel.setOpaque(true);

		jTotal = new JEditorPane("text/html", "<html>");
		JCopyPopup.install(jTotal);
		jTotal.setEditable(false);
		jTotal.setOpaque(false);
		jTotal.setBorder(null);
		JScrollPane jTotalScroll = new JScrollPane(jTotal);
		jTotalScroll.setBorder(null);

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jTotalScroll, 10, 10, Short.MAX_VALUE)
							.addComponent(jTotalLabel, 10, 10, Short.MAX_VALUE)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jCharacterScroll, 10, 10, Short.MAX_VALUE)
							.addGroup(layout.createSequentialGroup()
								.addGap(3)
								.addComponent(jCharacters, 10, 10, Short.MAX_VALUE)
								.addGap(3)
							)
						)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jCorporationScroll, 10, 10, Short.MAX_VALUE)
							.addGroup(layout.createSequentialGroup()
								.addGap(3)
								.addComponent(jCorporations, 10, 10, Short.MAX_VALUE)
								.addGap(3)
							)
						)
					)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jTotalLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCharacters, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCorporations, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jTotalScroll, 0, 0, Short.MAX_VALUE)
					.addComponent(jCharacterScroll, 0, 0, Short.MAX_VALUE)
					.addComponent(jCorporationScroll, 0, 0, Short.MAX_VALUE)
				)
		);
	}

	@Override
	public void updateData() {
		calcTotal();
		jCharacters.removeAllItems();
		List<String> characterNames = new ArrayList<String>(characters.keySet());
		Collections.sort(characterNames, new CaseInsensitiveComparator());
		for (String owner : characterNames) {
			jCharacters.addItem(owner);
		}
		if (jCharacters.getModel().getSize() > 0) {
			jCharacters.setEnabled(true);
		} else {
			jCharacters.addItem(TabsValues.get().oldNoCharacter());
			jCharacters.setEnabled(false);
		}
		jCharacters.setSelectedIndex(0);

		jCorporations.removeAllItems();
		List<String> corporationNames = new ArrayList<String>(corporations.keySet());
		Collections.sort(corporationNames, new CaseInsensitiveComparator());
		for (String corp : corporationNames) {
			jCorporations.addItem(corp);
		}
		if (jCorporations.getModel().getSize() > 0) {
			jCorporations.setEnabled(true);
		} else {
			jCorporations.addItem(TabsValues.get().oldNoCorporation());
			jCorporations.setEnabled(false);
		}
		jCorporations.setSelectedIndex(0);

		setData(jTotal, total);
	}

	@Override
	public void clearData() { }

	@Override
	public void updateCache() {}

	private boolean calcTotal() {
		Date date = Settings.getNow();
		Map<String, Value> values = DataSetCreator.createDataSet(program.getProfileData(), Settings.getNow());
		characters = new HashMap<String, Value>();
		corporations = new HashMap<String, Value>();
		total = values.get(TabsValues.get().grandTotal());
		for (OwnerType owner : program.getOwnerTypes()) {
			Value value = DataSetCreator.getValue(values, owner.getOwnerName(), date);
			if (owner.isCharacter()) {
				characters.put(value.getName(), value);
			} else if (owner.isCorporation()) {
				corporations.put(value.getName(), value);
			}

		}
		return !EventListManager.isEmpty(program.getProfileData().getAssetsEventList());
	}

	private void setData(JEditorPane jEditorPane, Value value) {
		if (value == null) {
			value = new Value("", Settings.getNow()); //Create empty
		}
		Output output = new Output();

		output.addHeading(TabsValues.get().columnTotal());
		output.addValue(value.getTotal());
		output.addNone();

		output.addHeading(TabsValues.get().columnWalletBalance());
		output.addValue(value.getBalanceTotal());
		output.addNone();

		output.addHeading(TabsValues.get().columnAssets());
		output.addValue(value.getAssetsTotal());
		output.addNone();

		output.addHeading(TabsValues.get().columnSellOrders());
		output.addValue(value.getSellOrders());
		output.addNone();

		output.addHeading(TabsValues.get().columnEscrowsToCover());
		output.addValue(value.getEscrows(), value.getEscrowsToCover());
		output.addNone();

		output.addHeading(TabsValues.get().columnBestAsset());
		output.addValue(value.getBestAssetName(), value.getBestAssetValue());
		output.addNone(2);

		output.addHeading(TabsValues.get().columnBestShip());
		output.addValue(value.getBestShipName(), value.getBestShipValue());
		output.addNone(2);

		/*
		//FIXME - No room for Best Ship Fitted
		output.addHeading(TabsValues.get().columnBestShipFitted());
		output.addValue(value.getBestShipFittedName(), value.getBestShipFittedValue());
		output.addNone(2);
		*/

		output.addHeading(TabsValues.get().columnBestModule());
		output.addValue(value.getBestModuleName(), value.getBestModuleValue());
		output.addNone(2);

		jEditorPane.setText(output.getOutput());
		jEditorPane.setCaretPosition(0);
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ValueRetroAction.OWNER_SELECTED.name().equals(e.getActionCommand())) {
				String s = (String) jCharacters.getSelectedItem();
				setData(jCharacter, characters.get(s));

			}
			if (ValueRetroAction.CORP_SELECTED.name().equals(e.getActionCommand())) {
				//String output = "";
				String s = (String) jCorporations.getSelectedItem();
				setData(jCorporation, corporations.get(s));
			}
		}
	}

	private class Output {
		private String output;
		private String moduleOutput;

		public Output() {
			output = "<html>"
				+ "<div>"
				+ "<table cellspacing=\"1\" style=\"padding: 0px; background: #" + gridHexColor + "; width: 100%; font-family: Arial, Helvetica, sans-serif; font-size: 9px;\">";
			moduleOutput = "";
		}


		public void addHeading(final String heading) {
			output = output + "<tr><td style=\"background: #" + gridHexColor + "; color: #ffffff; font-size: 11px; font-weight: bold;\">" + heading + "</td></tr>";
			moduleOutput = "";
		}

		public void addValue(final double value1, final double value2) {
			if (value1 != 0 && value2 != 0) {
				addValue(Formater.iskFormat(value1), " (" + Formater.iskFormat(value2) + ")");
			} else if (value1 != 0) {
				addValue(Formater.iskFormat(value1), null);
			}
		}

		public void addValue(final double value) {
			if (value != 0) {
				addValue(Formater.iskFormat(value), null);
			}
		}

		public void addValue(final String value1, final double value2) {
			if (value1 != null && value2 != 0) {
				addValue(value1, "<br>" + Formater.iskFormat(value2));
			}
		}

		private void addValue(final String value1, final String value2) {
			if (value1 != null || value2 != null) {
				moduleOutput = moduleOutput + "<tr><td style=\"background: #ffffff; text-align: right;\">";
			}
			if (value1 != null) {
				moduleOutput = moduleOutput + value1;
			}
			if (value2 != null) {
				moduleOutput = moduleOutput + value2;
			}
			if (value1 != null || value2 != null) {
				moduleOutput = moduleOutput + "</td></tr>";
			}
		}

		public void addNone(final int count) {
			if (moduleOutput.isEmpty()) {
				String temp = "";
				for (int i = 1; i < count; i++) {
					temp = temp + "<br/>";
				}
				addValue("<i>" + TabsValues.get().none() + "</i><br/>", temp);
			}
			output = output + moduleOutput;
		}

		public void addNone() {
			addNone(1);
		}

		public String getOutput() {
			return output + "</table></div>";
		}
	}

}
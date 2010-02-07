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

package net.nikr.eve.jeveasset.gui.dialogs;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import java.awt.Image;
import net.nikr.eve.jeveasset.data.EveAsset;
import net.nikr.eve.jeveasset.gui.shared.JCustomFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.AssetFilter;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JCopyPopup;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.table.AssetFilterLogicalMatcher;
import net.nikr.eve.jeveasset.io.local.CsvWriter;
import org.supercsv.prefs.CsvPreference;


public class CsvExportDialog extends JDialogCentered implements ActionListener{

	public static final String ACTION_DISABLE_SAVED_FILTERS = "ACTION_DISABLE_SAVED_FILTERS";
	public static final String ACTION_ENABLE_SAVED_FILTERS = "ACTION_ENABLE_SAVED_FILTERS";
	public static final String ACTION_OK = "ACTION_OK";
	public static final String ACTION_CANCEL = "ACTION_CANCEL";
	public static final String ACTION_BROWSE = "ACTION_BROWSE";

	private JTextField jPath;
	private JButton jBrowse;
	private JRadioButton jAllAssets;
	private JRadioButton jSavedFilter;
	private JRadioButton jCurrentFilter;
	private JComboBox jFilters;
	private JComboBox jFieldDelimiter;
	private JComboBox jLineDelimiter;
	private JComboBox jDecimalSeparator;
	private JMultiSelectionList jColumnSelection;
	private JButton jOK;

	private static DecimalFormat DoubleEn  = new DecimalFormat("0.##", new DecimalFormatSymbols(new Locale("en")));
	private static DecimalFormat DoubleEu  = new DecimalFormat("0.##", new DecimalFormatSymbols(new Locale("da")));
	private static DecimalFormat IntegerEn  = new DecimalFormat("0", new DecimalFormatSymbols(new Locale("en")));
	private static DecimalFormat IntegerEu  = new DecimalFormat("0", new DecimalFormatSymbols(new Locale("da")));

	private JCustomFileChooser jCsvFileChooser;

	public CsvExportDialog(Program program, Image image) {
		super(program, "CSV Export", image);

		try {
			jCsvFileChooser = new JCustomFileChooser(program, "csv");
		} catch (RuntimeException e) {
			// Workaround for JRE bug 4711700. A NullPointer is thrown
			// sometimes on the first construction under XP look and feel,
			// but construction succeeds on successive attempts.
			try {
				jCsvFileChooser = new JCustomFileChooser(program, "csv");
			} catch (RuntimeException npe) {
				// ok, now we use the metal file chooser, takes a long time to load
				// but the user can still use the program
				UIManager.getDefaults().put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
				jCsvFileChooser = new JCustomFileChooser(program, "csv");
			}
		}

		jPath = new JTextField();
		JCopyPopup.install(jPath);
		jPanel.add(jPath);

		jBrowse = new JButton("Browse...");
		jBrowse.setActionCommand(ACTION_BROWSE);
		jBrowse.addActionListener(this);
		jPanel.add(jBrowse);

		JLabel jAssetsLabel = new JLabel("Assets:");
		jAllAssets = new JRadioButton("All assets");
		jAllAssets.setActionCommand(ACTION_DISABLE_SAVED_FILTERS);
		jAllAssets.addActionListener(this);
		jPanel.add(jAllAssets);

		jCurrentFilter = new JRadioButton("Current filter");
		jCurrentFilter.setActionCommand(ACTION_DISABLE_SAVED_FILTERS);
		jCurrentFilter.addActionListener(this);
		jPanel.add(jCurrentFilter);

		jSavedFilter = new JRadioButton("Saved filter");
		jSavedFilter.setActionCommand(ACTION_ENABLE_SAVED_FILTERS);
		jSavedFilter.addActionListener(this);
		jPanel.add(jSavedFilter);

		ButtonGroup jButtonGroup = new ButtonGroup();
		jButtonGroup.add(jAllAssets);
		jButtonGroup.add(jSavedFilter);
		jButtonGroup.add(jCurrentFilter);

		jFilters = new JComboBox();
		jPanel.add(jFilters);
		
		JLabel jFieldDelimiterLabel = new JLabel("Fields terminated by:");
		jFieldDelimiter = new JComboBox( new String[]{"Comma", "Semicolon"} );

		JLabel jLineDelimiterLabel = new JLabel("Lines terminated by:");
		jLineDelimiter = new JComboBox( new String[]{"\\n", "\\r\\n", "\\r"});

		JLabel jDecimalSeparatorLabel = new JLabel("Decimal Separator:");
		jDecimalSeparator = new JComboBox( new String[]{"Dot", "Comma"});

		JLabel jColumnSelectionLabel = new JLabel("Columns:");
		jColumnSelection = new JMultiSelectionList( (Vector<String>)program.getSettings().getTableColumnNames() );
		JScrollPane jColumnSelectionPanel = new JScrollPane(jColumnSelection);
		jPanel.add(jColumnSelectionPanel);

		jOK = new JButton("OK");
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);
		jPanel.add(jOK);

		JButton jCancel = new JButton("Cancel");
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);
		jPanel.add(jCancel);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jPath, 390, 390, 390)
					.addComponent(jBrowse, Program.BUTTONS_WIDTH+10, Program.BUTTONS_WIDTH+10, Program.BUTTONS_WIDTH+10)
				)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jAssetsLabel)
						.addComponent(jAllAssets)
						.addComponent(jCurrentFilter)
						.addComponent(jSavedFilter)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addGroup(layout.createSequentialGroup()
								.addGap(20)
								.addComponent(jFilters, 150, 150, 150)
							)
							
						)
					)
					.addGap(30)
					.addGroup(layout.createParallelGroup()
						.addComponent(jFieldDelimiterLabel)
						.addComponent(jFieldDelimiter)
						.addComponent(jLineDelimiterLabel)
						.addComponent(jLineDelimiter)
						.addComponent(jDecimalSeparatorLabel)
						.addComponent(jDecimalSeparator)

					)
					.addGap(30)
					.addGroup(layout.createParallelGroup()
						.addComponent(jColumnSelectionLabel)
						.addComponent(jColumnSelectionPanel, 165, 165, 165)
					)
					
				)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jPath, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jBrowse, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAssetsLabel)
						.addComponent(jAllAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jCurrentFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jSavedFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jFilters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						
						
					)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jColumnSelectionLabel)
						.addComponent(jColumnSelectionPanel, 120, 120, 120)
					)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jFieldDelimiterLabel)
						.addComponent(jFieldDelimiter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jLineDelimiterLabel)
						.addComponent(jLineDelimiter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
						.addComponent(jDecimalSeparatorLabel)
						.addComponent(jDecimalSeparator, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					)


				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private String getDefaultPath(){
		return Settings.getUserDirectory();
	}

	private String getDefaultFilePath(){
		return getDefaultPath()+getDefaultFile();
	}

	private String getDefaultFile(){
		return "assets"+Formater.simpleDate( new Date() )+".csv";
	}

	private void browse(){
		String current = jPath.getText();
		int end = current.lastIndexOf(File.separator);
		if (end > 0) current = current.substring(0, end+1);
		File currentFile = new File( current );

		File defaulFile = new File(getDefaultPath());
		if (currentFile.exists()){
			jCsvFileChooser.setCurrentDirectory( new File(current) );
			jCsvFileChooser.setSelectedFile( new File(jPath.getText()));
		} else {
			jCsvFileChooser.setCurrentDirectory( defaulFile );
			jCsvFileChooser.setSelectedFile( new File(getDefaultFilePath()));
		}
		int bFound = jCsvFileChooser.showSaveDialog(dialog); //.showDialog(this, "OK"); //.showOpenDialog(this);
		if (bFound  == JFileChooser.APPROVE_OPTION){
			File file = jCsvFileChooser.getSelectedFile();
			jPath.setText( file.getAbsolutePath() );
		}
	}

	private HashMap<String, ? super Object> getLine(String[] header, EveAsset eveAsset, String lang){
		HashMap<String, ? super Object> line = new HashMap<String, Object>();
		for (int a = 0; a < header.length; a++){
			String headerName = header[a];
			if (headerName.equals("Name")) line.put(headerName, eveAsset.getName());
			if (headerName.equals("Group")) line.put(headerName, eveAsset.getGroup());
			if (headerName.equals("Category")) line.put(headerName, eveAsset.getCategory());
			if (headerName.equals("Owner")) line.put(headerName, eveAsset.getOwner());
			if (headerName.equals("Count")) line.put(headerName, getValue(eveAsset.getCount(), lang));
			if (headerName.equals("Location")) line.put(headerName, eveAsset.getLocation());
			if (headerName.equals("Container")) line.put(headerName, eveAsset.getContainer());
			if (headerName.equals("Flag")) line.put(headerName, eveAsset.getFlag());
			if (headerName.equals("Price")) line.put(headerName, getValue(eveAsset.getPrice(), lang));
			if (headerName.equals("Sell Min")) line.put(headerName, getValue(eveAsset.getPriceSellMin(), lang));
			if (headerName.equals("Buy Max")) line.put(headerName, getValue(eveAsset.getPriceBuyMax(), lang));
			if (headerName.equals("Value")) line.put(headerName, getValue(eveAsset.getValue(), lang));
			if (headerName.equals("Meta")) line.put(headerName, eveAsset.getMeta());
			if (headerName.equals("ID")) line.put(headerName, getValue(eveAsset.getId(), lang));
			if (headerName.equals("Base Price")) line.put(headerName, getValue(eveAsset.getPriceBase(), lang));
			if (headerName.equals("Volume")) line.put(headerName, getValue(eveAsset.getVolume(), lang));
			if (headerName.equals("Type ID")) line.put(headerName, getValue(eveAsset.getTypeId(), lang));
			if (headerName.equals("Region")) line.put(headerName, eveAsset.getRegion());
			if (headerName.equals("Type Count")) line.put(headerName, getValue(eveAsset.getTypeCount(), lang));
			if (headerName.equals("Security")) line.put(headerName, eveAsset.getSecurity());
			if (headerName.equals("Reprocessed")) line.put(headerName, getValue(eveAsset.getPriceReprocessed(), lang));
		}
		return line;
	}

	private String getValue(double number, String lang){
		if (lang.equals("Dot")){
			return DoubleEn.format(number);
		}
		return DoubleEu.format(number);
	}
	private String getValue(int number, String lang){
		if (lang.equals("Dot")){
			return IntegerEn.format(number);
		}
		return IntegerEu.format(number);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jPath;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void windowActivated() {}

	@Override
	protected void save() {
		List<HashMap<String, ? super Object>> data = new Vector<HashMap<String, ? super Object>>();

		Object[] columns = jColumnSelection.getSelectedValues();

		if (columns.length == 0){
			JOptionPane.showMessageDialog(dialog, "You must select atleast one column", "CSV Export", JOptionPane.PLAIN_MESSAGE);
			return;
		}

		String[] header = new String[columns.length];
		for (int a = 0; a < columns.length; a++){
			header[a] = (String) columns[a];
		}

		char fieldDelimiter;
		switch (jFieldDelimiter.getSelectedIndex()){
			case 0:
				fieldDelimiter = ',';
				break;
			case 1:
				fieldDelimiter = ';';
				break;
			default:
				fieldDelimiter = ',';
		}

		String lineDelimiter;
		switch (jLineDelimiter.getSelectedIndex()){
			case 0:
				lineDelimiter = "\n";
				break;
			case 1:
				lineDelimiter = "\r\n";
				break;
			case 2:
				lineDelimiter = "\n";
				break;
			default:
				lineDelimiter = "\n";
		}

		String lang = (String) jDecimalSeparator.getSelectedItem();

		if (lang.equals("Comma") && fieldDelimiter == ','){
			int nReturn = JOptionPane.showConfirmDialog(program.getFrame(),
					"Both the field terminator and the decimal separator is set to comma\r\n" +
					"With those settings it could be difficult to import the CSV file in other programs\r\n" +
					"Continue anyway?", "CSV Export", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION){
				return;
			}
		}

		if (jAllAssets.isSelected()){
			EventList<EveAsset> assets = program.getEveAssetEventList();
			for (int a = 0; a < assets.size(); a++){
				EveAsset eveAsset = assets.get(a);
				data.add(getLine(header, eveAsset, lang));
			}

		}
		if (jCurrentFilter.isSelected()){
			List<AssetFilter> assetFilters = program.getToolPanel().getAssetFilters();
			FilterList<EveAsset> assets = new FilterList<EveAsset>(program.getEveAssetEventList(), new AssetFilterLogicalMatcher(assetFilters));
			for (int a = 0; a < assets.size(); a++){
				EveAsset eveAsset = assets.get(a);
				data.add(getLine(header, eveAsset, lang));
			}
		}
		if (jSavedFilter.isSelected()){
			String s = (String) jFilters.getSelectedItem();
			List<AssetFilter> assetFilters = program.getSettings().getAssetFilters().get(s);
			FilterList<EveAsset> assets = new FilterList<EveAsset>(program.getEveAssetEventList(), new AssetFilterLogicalMatcher(assetFilters));
			for (int a = 0; a < assets.size(); a++){
				EveAsset eveAsset = assets.get(a);
				data.add(getLine(header, eveAsset, lang));
			}
		}
		if (!CsvWriter.save(jPath.getText(), data, header, new CsvPreference('\"', fieldDelimiter, lineDelimiter))){
			JOptionPane.showMessageDialog(dialog, "Failed to save CSV file", "Export CSV", JOptionPane.PLAIN_MESSAGE);
		}

		this.setVisible(false);
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			Vector<String> assetFilters = new Vector<String>(program.getSettings().getAssetFilters().keySet());
			Collections.sort(assetFilters);
			jFilters.setModel( new DefaultComboBoxModel(assetFilters) );
			jFilters.setEnabled(false);

			if(assetFilters.isEmpty()){
				jSavedFilter.setEnabled(false);
			} else {
				jSavedFilter.setEnabled(true);
			}

			jLineDelimiter.setSelectedIndex(0);

			jFieldDelimiter.setSelectedIndex(0);

			jDecimalSeparator.setSelectedIndex(0);

			jColumnSelection.selectAll();

			jAllAssets.setSelected(true);

			jPath.setText( getDefaultFilePath() );
		}
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_DISABLE_SAVED_FILTERS.equals(e.getActionCommand())){
			jFilters.setEnabled(false);
		}
		if (ACTION_ENABLE_SAVED_FILTERS.equals(e.getActionCommand())){
			jFilters.setEnabled(true);
		}
		if (ACTION_OK.equals(e.getActionCommand())){
			save();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			this.setVisible(false);
		}
		if (ACTION_BROWSE.equals(e.getActionCommand())){
			browse();
		}
	}
	
}

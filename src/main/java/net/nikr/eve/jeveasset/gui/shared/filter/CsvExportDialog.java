/*
 * Copyright 2009, 2010, 2011, 2012 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.shared.filter;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.CsvSettings;
import net.nikr.eve.jeveasset.data.CsvSettings.DecimalSeperator;
import net.nikr.eve.jeveasset.data.CsvSettings.FieldDelimiter;
import net.nikr.eve.jeveasset.data.CsvSettings.LineDelimiter;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.DialoguesCsvExport;
import net.nikr.eve.jeveasset.io.local.CsvWriter;
import org.supercsv.prefs.CsvPreference;


public class CsvExportDialog<E> extends JDialogCentered implements ActionListener {

	public static final String ACTION_DISABLE_SAVED_FILTERS = "ACTION_DISABLE_SAVED_FILTERS";
	public static final String ACTION_ENABLE_SAVED_FILTERS = "ACTION_ENABLE_SAVED_FILTERS";
	public static final String ACTION_FIELD_DELIMITER = "ACTION_FIELD_DELIMITER";
	public static final String ACTION_LINE_DELIMITER = "ACTION_LINE_DELIMITER";
	public static final String ACTION_DECIMAL_SEPARATOR = "ACTION_DECIMAL_SEPARATOR";
	public static final String ACTION_OK = "ACTION_OK";
	public static final String ACTION_CANCEL = "ACTION_CANCEL";
	public static final String ACTION_DEFAULT = "ACTION_DEFAULT";

	private JRadioButton jNoFilter;
	private JRadioButton jSavedFilter;
	private JRadioButton jCurrentFilter;
	private JComboBox jFilters;
	private JComboBox jFieldDelimiter;
	private JComboBox jLineDelimiter;
	private JComboBox jDecimalSeparator;
	private JMultiSelectionList jColumnSelection;
	private JButton jOK;

	private static DecimalFormat doubleEn  = new DecimalFormat("0.##", new DecimalFormatSymbols(new Locale("en")));
	private static DecimalFormat doubleEu  = new DecimalFormat("0.##", new DecimalFormatSymbols(new Locale("da")));
	private static DecimalFormat longEn  = new DecimalFormat("0", new DecimalFormatSymbols(new Locale("en")));
	private static DecimalFormat longEu  = new DecimalFormat("0", new DecimalFormatSymbols(new Locale("da")));

	private JCustomFileChooser jCsvFileChooser;
	
	private Map<String, List<Filter>> filters;
	private List<EventList<E>> eventLists;
	private Map<String, EnumTableColumn<E>> columns = new HashMap<String, EnumTableColumn<E>>();
	private FilterControl<E> matcherControl;
	
	public CsvExportDialog(JFrame jFrame, FilterControl<E> matcherControl, Map<String, List<Filter>> filters, List<EventList<E>> eventLists, List<EnumTableColumn<E>> enumColumns) {
		super(null, DialoguesCsvExport.get().csvExport(), jFrame, Images.DIALOG_CSV_EXPORT.getImage());
		this.matcherControl = matcherControl;
		this.filters = filters;
		this.eventLists = eventLists;
		
		List<String> columnNames = new ArrayList<String>();
		for (EnumTableColumn<E> column : enumColumns){
			columns.put(column.getColumnName(), column);
			columnNames.add(column.getColumnName());
		}

		try {
			jCsvFileChooser = new JCustomFileChooser(jFrame, "csv");
		} catch (RuntimeException e) {
			// Workaround for JRE bug 4711700. A NullPointer is thrown
			// sometimes on the first construction under XP look and feel,
			// but construction succeeds on successive attempts.
			try {
				jCsvFileChooser = new JCustomFileChooser(jFrame, "csv");
			} catch (RuntimeException npe) {
				// ok, now we use the metal file chooser, takes a long time to load
				// but the user can still use the program
				UIManager.getDefaults().put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
				jCsvFileChooser = new JCustomFileChooser(jFrame, "csv");
			}
		}

		JLabel jAssetsLabel = new JLabel(DialoguesCsvExport.get().filters());
		jNoFilter = new JRadioButton(DialoguesCsvExport.get().noFilter());
		jNoFilter.setActionCommand(ACTION_DISABLE_SAVED_FILTERS);
		jNoFilter.addActionListener(this);
		jNoFilter.setSelected(true);
		
		jCurrentFilter = new JRadioButton(DialoguesCsvExport.get().currentFilter());
		jCurrentFilter.setActionCommand(ACTION_DISABLE_SAVED_FILTERS);
		jCurrentFilter.addActionListener(this);

		jSavedFilter = new JRadioButton(DialoguesCsvExport.get().savedFilter());
		jSavedFilter.setActionCommand(ACTION_ENABLE_SAVED_FILTERS);
		jSavedFilter.addActionListener(this);

		ButtonGroup jButtonGroup = new ButtonGroup();
		jButtonGroup.add(jNoFilter);
		jButtonGroup.add(jSavedFilter);
		jButtonGroup.add(jCurrentFilter);

		jFilters = new JComboBox();
		jFilters.setEnabled(false);
		
		JLabel jFieldDelimiterLabel = new JLabel(DialoguesCsvExport.get().fieldTerminated());
		jFieldDelimiter = new JComboBox( FieldDelimiter.values() );
		jFieldDelimiter.setActionCommand(ACTION_FIELD_DELIMITER);
		jFieldDelimiter.addActionListener(this);

		JLabel jLineDelimiterLabel = new JLabel(DialoguesCsvExport.get().linesTerminated());
		jLineDelimiter = new JComboBox( LineDelimiter.values() );
		jLineDelimiter.setActionCommand(ACTION_LINE_DELIMITER);
		jLineDelimiter.addActionListener(this);

		JLabel jDecimalSeparatorLabel = new JLabel(DialoguesCsvExport.get().decimalSeperator());
		jDecimalSeparator = new JComboBox( DecimalSeperator.values() );
		jDecimalSeparator.setActionCommand(ACTION_DECIMAL_SEPARATOR);
		jDecimalSeparator.addActionListener(this);

		JLabel jColumnSelectionLabel = new JLabel(DialoguesCsvExport.get().columns());
		jColumnSelection = new JMultiSelectionList(columnNames);
		jColumnSelection.selectAll();
		JScrollPane jColumnSelectionPanel = new JScrollPane(jColumnSelection);
		
		JSeparator jSeparator = new JSeparator();

		jOK = new JButton(DialoguesCsvExport.get().ok());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(this);

		JButton jDefault = new JButton(DialoguesCsvExport.get().defaultSettings());
		jDefault.setActionCommand(ACTION_DEFAULT);
		jDefault.addActionListener(this);
		
		JButton jCancel = new JButton(DialoguesCsvExport.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jAssetsLabel)
						.addComponent(jNoFilter)
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
				.addComponent(jSeparator)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jDefault, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
						.addComponent(jAssetsLabel)
						.addComponent(jNoFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
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
				.addComponent(jSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDefault, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}
	
	private boolean browse(){
		String current = Settings.getCsvSettings().getFilename();
		int end = current.lastIndexOf(File.separator);
		if (end > 0) current = current.substring(0, end+1);
		File currentPath = new File( current );

		if (currentPath.exists()){
			jCsvFileChooser.setCurrentDirectory(currentPath);
			jCsvFileChooser.setSelectedFile( new File(Settings.getCsvSettings().getFilename()));
		} else {
			jCsvFileChooser.setCurrentDirectory( new File(CsvSettings.getDefaultPath()) );
			jCsvFileChooser.setSelectedFile( new File(CsvSettings.getDefaultFilename()));
		}
		int bFound = jCsvFileChooser.showDialog(getDialog(), "OK"); //.showSaveDialog(); //; //.showOpenDialog(this);
		if (bFound  == JFileChooser.APPROVE_OPTION){
			File file = jCsvFileChooser.getSelectedFile();
			Settings.getCsvSettings().setFilename(file.getAbsolutePath());
			return true;
		} else {
			return false;
		}
	}
	
	private String getValue(Object object, DecimalSeperator decimalSeperator){
		if (object instanceof Double){
			double number = (Double) object;
			if (decimalSeperator == DecimalSeperator.DOT){
				return doubleEn.format(number);
			} else {
				return doubleEu.format(number);
			}
		} else if (object instanceof Long){
			long number = (Long) object;
			if (decimalSeperator == DecimalSeperator.DOT){
				return longEn.format(number);
			} else {
				return longEu.format(number);
			}
		}
		if (object == null){
			return "";
		} else {
			return object.toString();
		}
	}
	
	private void loadCsvSettings(){
		jFieldDelimiter.setSelectedItem(Settings.getCsvSettings().getFieldDelimiter());
		jLineDelimiter.setSelectedItem(Settings.getCsvSettings().getLineDelimiter());
		jDecimalSeparator.setSelectedItem(Settings.getCsvSettings().getDecimalSeperator());
	}
	
	private void resetCsvSettings(){
		Settings.getCsvSettings().clear();
		loadCsvSettings();
	}

	@Override
	public void setVisible(boolean b) {
		if (b){
			loadCsvSettings();
			jFilters.setEnabled(false);
			if (filters.isEmpty()){
				if (jSavedFilter.isSelected()) jNoFilter.setSelected(true);
				jSavedFilter.setEnabled(false);
				jFilters.getModel().setSelectedItem(DialoguesCsvExport.get().noSavedFilter());
			} else {
				if (jSavedFilter.isSelected()) jFilters.setEnabled(true);
				jSavedFilter.setEnabled(true);
				List<String> filterNames = new ArrayList<String>(filters.keySet());
				Collections.sort(filterNames);
				jFilters.setModel( new DefaultComboBoxModel(filterNames.toArray()) );
			}
			if (matcherControl.getCurrentFilters().isEmpty()){
				if (jCurrentFilter.isSelected()) jNoFilter.setSelected(true);
				jCurrentFilter.setEnabled(false);
			} else {
				jCurrentFilter.setEnabled(true);
			}
		}
		super.setVisible(b);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jOK;
	}

	@Override
	protected JButton getDefaultButton() {
		return jOK;
	}

	@Override
	protected void windowShown() {}

	@Override
	protected void save() {
		List<HashMap<String, ? super Object>> data = new ArrayList<HashMap<String, ? super Object>>();
		
		List<E> items = new ArrayList<E>();
	//Columns + Header
		Object[] values = jColumnSelection.getSelectedValues();
		
		if (values.length == 0){
			JOptionPane.showMessageDialog(getDialog(), DialoguesCsvExport.get().selectOne(), DialoguesCsvExport.get().csvExport(), JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		if (Settings.getCsvSettings().getDecimalSeperator() == DecimalSeperator.COMMA && Settings.getCsvSettings().getFieldDelimiter() == FieldDelimiter.COMMA){
			int nReturn = JOptionPane.showConfirmDialog(
					getDialog(),
					DialoguesCsvExport.get().confirmStupidDecision(),
					DialoguesCsvExport.get().csvExport(),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (nReturn == JOptionPane.NO_OPTION){
				return;
			}
		}
		
		boolean ok = browse();
		if (!ok) return;
		
		
		List<EnumTableColumn<E>> selectedColumns = new ArrayList<EnumTableColumn<E>>();
		List<String> header = new ArrayList<String>();
		for (Object object : values){
			if (object instanceof String){
				String columnName = (String) object;
				EnumTableColumn<E> column = columns.get(columnName);
				header.add(column.getColumnName());
				selectedColumns.add(column);
			}
		}
		
		if (jNoFilter.isSelected()){
			for (EventList<E> eventList : eventLists){
				for (E e : eventList){
					items.add(e);
				}
			}
		} else if (jCurrentFilter.isSelected()){
			List<Filter> filter = matcherControl.getCurrentFilters();
			for (EventList<E> eventList : eventLists){
				FilterList<E> filterList = new FilterList<E>(eventList, new FilterLogicalMatcher<E>(matcherControl, filter));
				for (E e : filterList){
					items.add(e);
				}
			}
		} else if (jSavedFilter.isSelected()){
			String filterName = (String)jFilters.getSelectedItem();
			List<Filter> filter = filters.get(filterName);
			for (EventList<E> eventList : eventLists){
				FilterList<E> filterList = new FilterList<E>(eventList, new FilterLogicalMatcher<E>(matcherControl, filter));
				for (E e : filterList){
					items.add(e);
				}
			}
		}
		
		for (E e: items){
			HashMap<String, ? super Object> line = new HashMap<String, Object>();
			for (EnumTableColumn<E> column : selectedColumns){
				line.put(column.getColumnName(), getValue(column.getColumnValue(e), Settings.getCsvSettings().getDecimalSeperator()) );
			}
			data.add(line);
		}
		
		if (!CsvWriter.save(Settings.getCsvSettings().getFilename(), data, header.toArray(new String[header.size()]), new CsvPreference('\"', Settings.getCsvSettings().getFieldDelimiter().getCharacter(), Settings.getCsvSettings().getLineDelimiter().toString()))){
			JOptionPane.showMessageDialog(getDialog(),
					DialoguesCsvExport.get().failedToSave(),
					DialoguesCsvExport.get().csvExport(),
					JOptionPane.PLAIN_MESSAGE);
		}
		
		setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_DISABLE_SAVED_FILTERS.equals(e.getActionCommand())){
			jFilters.setEnabled(false);
		}
		if (ACTION_ENABLE_SAVED_FILTERS.equals(e.getActionCommand())){
			jFilters.setEnabled(true);
		}
		if (ACTION_FIELD_DELIMITER.equals(e.getActionCommand())){
			Settings.getCsvSettings().setFieldDelimiter( (FieldDelimiter)jFieldDelimiter.getSelectedItem() );
		}
		if (ACTION_LINE_DELIMITER.equals(e.getActionCommand())){
			Settings.getCsvSettings().setLineDelimiter( (LineDelimiter)jLineDelimiter.getSelectedItem() );
		}
		if (ACTION_DECIMAL_SEPARATOR.equals(e.getActionCommand())){
			Settings.getCsvSettings().setDecimalSeperator( (DecimalSeperator)jDecimalSeparator.getSelectedItem() );
		}
		if (ACTION_OK.equals(e.getActionCommand())){
			save();
		}
		if (ACTION_DEFAULT.equals(e.getActionCommand())){
			resetCsvSettings();
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			setVisible(false);
		}
		
	}
}

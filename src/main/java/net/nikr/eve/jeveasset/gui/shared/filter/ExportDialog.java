/*
 * Copyright 2009-2013 Contributors (see credits.txt)
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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.ExportSettings;
import net.nikr.eve.jeveasset.data.ExportSettings.DecimalSeparator;
import net.nikr.eve.jeveasset.data.ExportSettings.FieldDelimiter;
import net.nikr.eve.jeveasset.data.ExportSettings.LineDelimiter;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDefaultField;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.i18n.DialoguesExport;
import net.nikr.eve.jeveasset.io.local.CsvWriter;
import net.nikr.eve.jeveasset.io.local.HtmlWriter;
import net.nikr.eve.jeveasset.io.local.SqlWriter;
import org.supercsv.prefs.CsvPreference;


public class ExportDialog<E> extends JDialogCentered {

	public static final String ACTION_DISABLE_SAVED_FILTERS = "ACTION_DISABLE_SAVED_FILTERS";
	public static final String ACTION_ENABLE_SAVED_FILTERS = "ACTION_ENABLE_SAVED_FILTERS";
	public static final String ACTION_OK = "ACTION_OK";
	public static final String ACTION_CANCEL = "ACTION_CANCEL";
	public static final String ACTION_DEFAULT = "ACTION_DEFAULT";
	public static final String ACTION_TOOL_COLUMNS = "ACTION_TOOL_COLUMNS";

	public static final String EXPORT_CSV = "csv";
	public static final String EXPORT_HTML = "html";
	public static final String EXPORT_SQL = "sql";

	//Filter
	private JRadioButton jNoFilter;
	private JRadioButton jSavedFilter;
	private JRadioButton jCurrentFilter;
	private JComboBox jFilters;
	//Columns
	private JCheckBox jToolColumns;
	private JMultiSelectionList jColumnSelection;
	//CSV
	private JComboBox jFieldDelimiter;
	private JComboBox jLineDelimiter;
	private JComboBox jDecimalSeparator;
	//SQL
	private JTextField jTableName;
	private JCheckBox jDropTable;
	private JCheckBox jCreateTable;
	private JCheckBox jExtendedInserts;

	private JButton jOK;

	private static final DecimalFormat EN_NUMBER_FORMAT  = new DecimalFormat("0.####", new DecimalFormatSymbols(new Locale("en")));
	private static final DecimalFormat EU_NUMBER_FORMAT  = new DecimalFormat("0.####", new DecimalFormatSymbols(new Locale("da")));

	private JCustomFileChooser jFileChooser;

	private List<EventList<E>> eventLists;
	private Map<String, EnumTableColumn<E>> columns = new HashMap<String, EnumTableColumn<E>>();
	private List<String> columnNames;
	private FilterControl<E> filterControl;
	private ExportFilterControl<E> exportFilterControl;
	private String toolName;

	public ExportDialog(final JFrame jFrame, final String toolName, final FilterControl<E> filterControl, ExportFilterControl<E> exportFilterControl, final List<EventList<E>> eventLists, final List<EnumTableColumn<E>> enumColumns) {
		super(null, DialoguesExport.get().export(), jFrame, Images.DIALOG_CSV_EXPORT.getImage());
		this.toolName = toolName;
		this.filterControl = filterControl;
		this.exportFilterControl = exportFilterControl;
		this.eventLists = eventLists;

		ListenerClass listener = new ListenerClass();
		//layout.setAutoCreateContainerGaps(false);

		columnNames = new ArrayList<String>();
		for (EnumTableColumn<E> column : enumColumns) {
			columns.put(column.getColumnName(), column);
			columnNames.add(column.getColumnName());
		}

		jToolColumns = new JCheckBox(DialoguesExport.get().toolColumns());
		jToolColumns.setActionCommand(ACTION_TOOL_COLUMNS);
		jToolColumns.addActionListener(listener);

		try {
			jFileChooser = new JCustomFileChooser(jFrame, EXPORT_CSV, EXPORT_HTML, EXPORT_SQL);
		} catch (RuntimeException e) {
			// Workaround for JRE bug 4711700. A NullPointer is thrown
			// sometimes on the first construction under XP look and feel,
			// but construction succeeds on successive attempts.
			try {
				jFileChooser = new JCustomFileChooser(jFrame, EXPORT_CSV, EXPORT_HTML, EXPORT_SQL);
			} catch (RuntimeException npe) {
				// ok, now we use the metal file chooser, takes a long time to load
				// but the user can still use the program
				UIManager.getDefaults().put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
				jFileChooser = new JCustomFileChooser(jFrame, EXPORT_CSV, EXPORT_HTML, EXPORT_SQL);
			}
		}
	//Filters
		JLabel jFiltersLabel = new JLabel(DialoguesExport.get().filters());
		jFiltersLabel.setFont(new Font(jFiltersLabel.getFont().getName(), Font.BOLD, jFiltersLabel.getFont().getSize()));

		jNoFilter = new JRadioButton(DialoguesExport.get().noFilter());
		jNoFilter.setActionCommand(ACTION_DISABLE_SAVED_FILTERS);
		jNoFilter.addActionListener(listener);
		jNoFilter.setSelected(true);

		jCurrentFilter = new JRadioButton(DialoguesExport.get().currentFilter());
		jCurrentFilter.setActionCommand(ACTION_DISABLE_SAVED_FILTERS);
		jCurrentFilter.addActionListener(listener);

		jSavedFilter = new JRadioButton(DialoguesExport.get().savedFilter());
		jSavedFilter.setActionCommand(ACTION_ENABLE_SAVED_FILTERS);
		jSavedFilter.addActionListener(listener);

		ButtonGroup jButtonGroup = new ButtonGroup();
		jButtonGroup.add(jNoFilter);
		jButtonGroup.add(jSavedFilter);
		jButtonGroup.add(jCurrentFilter);

		jFilters = new JComboBox();
		jFilters.setEnabled(false);

	//Columns
		JLabel jColumnLabel = new JLabel(DialoguesExport.get().columns());
		jColumnLabel.setFont(new Font(jColumnLabel.getFont().getName(), Font.BOLD, jColumnLabel.getFont().getSize()));

		jColumnSelection = new JMultiSelectionList(columnNames);
		jColumnSelection.selectAll();

		JScrollPane jColumnSelectionPanel = new JScrollPane(jColumnSelection);

	//Csv
		JLabel jCsvLable = new JLabel(DialoguesExport.get().csv());
		jCsvLable.setFont(new Font(jCsvLable.getFont().getName(), Font.BOLD, jCsvLable.getFont().getSize()));

		JLabel jFieldDelimiterLabel = new JLabel(DialoguesExport.get().fieldTerminated());
		jFieldDelimiter = new JComboBox(FieldDelimiter.values());

		JLabel jLineDelimiterLabel = new JLabel(DialoguesExport.get().linesTerminated());
		jLineDelimiter = new JComboBox(LineDelimiter.values());

		JLabel jDecimalSeparatorLabel = new JLabel(DialoguesExport.get().decimalSeparator());
		jDecimalSeparator = new JComboBox(DecimalSeparator.values());

	//Sql
		JLabel jSqlLable = new JLabel(DialoguesExport.get().sql());
		jSqlLable.setFont(new Font(jSqlLable.getFont().getName(), Font.BOLD, jSqlLable.getFont().getSize()));

		JLabel jTableNameLable = new JLabel(DialoguesExport.get().tableName());
		jTableName = new JDefaultField(Program.PROGRAM_NAME.toLowerCase() + "_" + toolName.toLowerCase());
		jTableName.setDocument(DocumentFactory.getWordPlainDocument());

		jDropTable = new JCheckBox(DialoguesExport.get().dropTable());

		jCreateTable = new JCheckBox(DialoguesExport.get().createTable());

		jExtendedInserts = new JCheckBox(DialoguesExport.get().extendedInserts());

	//Separatora
		JSeparator jHorizontalSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		JSeparator jVerticalSeparator = new JSeparator(SwingConstants.VERTICAL);

	//Buttons
		JSeparator jButtonSeparator = new JSeparator();

		jOK = new JButton(DialoguesExport.get().ok());
		jOK.setActionCommand(ACTION_OK);
		jOK.addActionListener(listener);

		JButton jDefault = new JButton(DialoguesExport.get().defaultSettings());
		jDefault.setActionCommand(ACTION_DEFAULT);
		jDefault.addActionListener(listener);

		JButton jCancel = new JButton(DialoguesExport.get().cancel());
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(listener);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGap(15)
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createParallelGroup()
							.addComponent(jFiltersLabel, GroupLayout.Alignment.CENTER)
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
						.addGroup(layout.createParallelGroup()
							.addComponent(jCsvLable, GroupLayout.Alignment.CENTER)
							.addComponent(jFieldDelimiterLabel)
							.addComponent(jFieldDelimiter)
							.addComponent(jLineDelimiterLabel)
							.addComponent(jLineDelimiter)
							.addComponent(jDecimalSeparatorLabel)
							.addComponent(jDecimalSeparator)
						)
					)
					.addGap(15)
					.addComponent(jVerticalSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(15)
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createParallelGroup()
							.addComponent(jColumnLabel, GroupLayout.Alignment.CENTER)
							.addComponent(jToolColumns)
							.addComponent(jColumnSelectionPanel, 165, 165, 165)
						)
						.addGroup(layout.createParallelGroup()
							.addComponent(jSqlLable, GroupLayout.Alignment.CENTER)
							.addComponent(jTableNameLable)
							.addComponent(jTableName)
							.addComponent(jDropTable)
							.addComponent(jCreateTable)
							.addComponent(jExtendedInserts)
						)
					)
					.addGap(15)
				)
				.addComponent(jHorizontalSeparator)
				.addComponent(jButtonSeparator)
				.addGroup(layout.createSequentialGroup()
					//.addGap(15)
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jDefault, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					//.addGap(15)
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jVerticalSeparator)
					.addGroup(layout.createSequentialGroup()
						.addGap(10)
						.addGroup(layout.createParallelGroup()
							.addGroup(layout.createSequentialGroup()
								.addComponent(jFiltersLabel)
								.addGap(10)
								.addComponent(jNoFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jCurrentFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jSavedFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jFilters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
							)
							.addGroup(layout.createSequentialGroup()
								.addComponent(jColumnLabel)
								.addGap(10)
								.addComponent(jToolColumns)
								.addComponent(jColumnSelectionPanel, 120, 120, 120)
							)
						)
						.addGap(15)
						.addComponent(jHorizontalSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(10)
						.addGroup(layout.createParallelGroup()
							.addGroup(layout.createSequentialGroup()
								.addComponent(jCsvLable)
								.addGap(10)
								.addComponent(jFieldDelimiterLabel)
								.addComponent(jFieldDelimiter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jLineDelimiterLabel)
								.addComponent(jLineDelimiter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jDecimalSeparatorLabel)
								.addComponent(jDecimalSeparator, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
							)
							.addGroup(layout.createSequentialGroup()
								.addComponent(jSqlLable)
								.addGap(10)
								.addComponent(jTableNameLable)
								.addComponent(jTableName, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jDropTable, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jCreateTable, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jExtendedInserts, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
							)
						)
						.addGap(20)
					)
				)
				.addGap(0)
				.addComponent(jButtonSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(10)
				.addGroup(layout.createParallelGroup()
					.addComponent(jOK, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDefault, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(10)
		);
	}

	public void setColumns(final List<EnumTableColumn<E>> enumColumns) {
		columns.clear();
		columnNames.clear();
		for (EnumTableColumn<E> column : enumColumns) {
			columns.put(column.getColumnName(), column);
			columnNames.add(column.getColumnName());
		}
		jColumnSelection.setModel(new AbstractListModel() {
			@Override
			public int getSize() {
				return columnNames.size();
			}

			@Override
			public Object getElementAt(int index) {
				return columnNames.get(index);
			}
		});
	}

	private List<String> getExportColumns() {
		List<String> selectedColumns = new ArrayList<String>();
		Object[] values = jColumnSelection.getSelectedValues();
		for (Object object : values) {
			if (object instanceof String) {
				String columnName = (String) object;
				Object column = columns.get(columnName);
				if (column instanceof Enum<?>) {
					Enum<?> e = (Enum<?>) column;
					selectedColumns.add(e.name());
				}
			}
		}
		return selectedColumns;
	}

	private boolean browse() {
		String current = Settings.get().getExportSettings().getFilename(toolName);
		int end = current.lastIndexOf(File.separator);
		if (end > 0) {
			current = current.substring(0, end + 1);
		}
		File currentPath = new File(current);

		if (currentPath.exists()) {
			jFileChooser.setCurrentDirectory(currentPath);
			jFileChooser.setSelectedFile(new File(Settings.get().getExportSettings().getFilename(toolName)));
		} else {
			jFileChooser.setCurrentDirectory(new File(ExportSettings.getDefaultPath()));
			jFileChooser.setSelectedFile(new File(ExportSettings.getDefaultFilename(toolName)));
		}
		int bFound = jFileChooser.showDialog(getDialog(), DialoguesExport.get().ok());
		if (bFound  == JFileChooser.APPROVE_OPTION) {
			File file = jFileChooser.getSelectedFile();
			Settings.get().getExportSettings().putFilename(toolName, file.getAbsolutePath());
			return true;
		} else {
			return false;
		}
	}

	private String format(final Object object, final DecimalSeparator decimalSeparator) {
		if (object == null) {
			return "";
		} else if (object instanceof Number) {
			Number number = (Number) object;
			if (decimalSeparator == DecimalSeparator.DOT) {
				return EN_NUMBER_FORMAT.format(number);
			} else {
				return EU_NUMBER_FORMAT.format(number);
			}
		} else if (object instanceof Date) {
			return Formater.columnDate(object);
		} else {
			return object.toString();
		}
	}

	private void saveSettings() {
		//CSV
		Settings.get().getExportSettings().setFieldDelimiter((FieldDelimiter) jFieldDelimiter.getSelectedItem());
		Settings.get().getExportSettings().setLineDelimiter((LineDelimiter) jLineDelimiter.getSelectedItem());
		Settings.get().getExportSettings().setDecimalSeparator((DecimalSeparator) jDecimalSeparator.getSelectedItem());
		//SQL
		Settings.get().getExportSettings().putTableName(toolName, jTableName.getText());
		Settings.get().getExportSettings().setDropTable(jDropTable.isSelected());
		Settings.get().getExportSettings().setCreateTable(jCreateTable.isSelected());
		Settings.get().getExportSettings().setExtendedInserts(jExtendedInserts.isSelected());
		//Shared
		if (jColumnSelection.getSelectedIndices().length == columnNames.size()) { //All is selected - nothing worth saving...
			Settings.get().getExportSettings().putTableExportColumns(toolName, null);
		} else {
			Settings.get().getExportSettings().putTableExportColumns(toolName, getExportColumns());
		}
	}

	private void loadSettings() {
		//CSV
		jFieldDelimiter.setSelectedItem(Settings.get().getExportSettings().getFieldDelimiter());
		jLineDelimiter.setSelectedItem(Settings.get().getExportSettings().getLineDelimiter());
		jDecimalSeparator.setSelectedItem(Settings.get().getExportSettings().getDecimalSeparator());
		//SQL
		jTableName.setText(Settings.get().getExportSettings().getTableName(toolName));
		jDropTable.setSelected(Settings.get().getExportSettings().isDropTable());
		jCreateTable.setSelected(Settings.get().getExportSettings().isCreateTable());
		jExtendedInserts.setSelected(Settings.get().getExportSettings().isExtendedInserts());
		//Shared
		jColumnSelection.clearSelection();
		List<String> list = Settings.get().getExportSettings().getTableExportColumns(toolName);
		if (list == null) {
			jColumnSelection.selectAll();
			list = new ArrayList<String>(getExportColumns());
		}
		List<Integer> selections = new ArrayList<Integer>();
		for (String column : list) {
			Enum<?> e = exportFilterControl.valueOf(column);
			if (e instanceof EnumTableColumn) {
				EnumTableColumn<?> enumColumn = (EnumTableColumn) e;
				int index = columnNames.indexOf(enumColumn.getColumnName());
				selections.add(index);
			}
		}
		int[] indices = new int[selections.size()];
		for (int i = 0; i < selections.size(); i++) {
			indices[i] = selections.get(i);
		}
		jColumnSelection.setSelectedIndices(indices);
	}

	private void resetSettings() {
		//CSV
		Settings.get().getExportSettings().setFieldDelimiter(FieldDelimiter.COMMA);
		Settings.get().getExportSettings().setLineDelimiter(LineDelimiter.DOS);
		Settings.get().getExportSettings().setDecimalSeparator(DecimalSeparator.DOT);
		//SQL
		Settings.get().getExportSettings().putTableName(toolName, "");
		Settings.get().getExportSettings().setDropTable(true);
		Settings.get().getExportSettings().setCreateTable(true);
		Settings.get().getExportSettings().setExtendedInserts(true);
		//Shared
		Settings.get().getExportSettings().putFilename(toolName, ExportSettings.getDefaultFilename(toolName));
		Settings.get().getExportSettings().putTableExportColumns(toolName, null);
		loadSettings();
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			loadSettings();
			jFilters.setEnabled(false);
			if (exportFilterControl.getAllFilters().isEmpty()) {
				if (jSavedFilter.isSelected()) {
					jNoFilter.setSelected(true);
				}
				jSavedFilter.setEnabled(false);
				jFilters.getModel().setSelectedItem(DialoguesExport.get().noSavedFilter());
			} else {
				if (jSavedFilter.isSelected()) {
					jFilters.setEnabled(true);
				}
				jSavedFilter.setEnabled(true);
				List<String> filterNames = new ArrayList<String>(exportFilterControl.getAllFilters().keySet());
				Collections.sort(filterNames, new CaseInsensitiveComparator());
				jFilters.setModel(new DefaultComboBoxModel(filterNames.toArray()));
			}
			if (exportFilterControl.getCurrentFilters().isEmpty()) {
				if (jCurrentFilter.isSelected()) {
					jNoFilter.setSelected(true);
				}
				jCurrentFilter.setEnabled(false);
			} else {
				jCurrentFilter.setEnabled(true);
			}
		} else {
			saveSettings();
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
	protected void windowShown() { }

	private String sqlHeader(Object object) {
		if (object instanceof Enum) {
			Enum headerEnum = (Enum) object;
			return headerEnum.name();
		} else {
			throw new RuntimeException("Failed to convert SQL header");
		}
	}

	@Override
	protected void save() {
		List<Map<String, String>> stringRows = new ArrayList<Map<String, String>>();
		List<Map<String, Object>> objectRows = new ArrayList<Map<String, Object>>();
		List<E> items = new ArrayList<E>();

	//Columns + Header
		List<EnumTableColumn<E>> selectedColumns = new ArrayList<EnumTableColumn<E>>();
		List<String> header = new ArrayList<String>();
		Map<String, String> sqlHeader = new HashMap<String, String>();
		if (jToolColumns.isSelected()) {
			//Use the tool current shown columns + order
			selectedColumns = exportFilterControl.getEnumShownColumns();
			for (EnumTableColumn<E> column : selectedColumns) {
				header.add(column.getColumnName());
				sqlHeader.put(column.getColumnName(), sqlHeader(column));
			}
		} else {
			//Use custom columns
			Object[] values = jColumnSelection.getSelectedValues();
			for (Object object : values) {
				if (object instanceof String) {
					String columnName = (String) object;
					EnumTableColumn<E> column = columns.get(columnName);
					header.add(column.getColumnName());
					sqlHeader.put(column.getColumnName(), sqlHeader(column));
					selectedColumns.add(column);
				}
			}
		}
	//Bad selection
		if (selectedColumns.isEmpty() || header.isEmpty()) {
			JOptionPane.showMessageDialog(getDialog(), DialoguesExport.get().selectOne(), DialoguesExport.get().export(), JOptionPane.PLAIN_MESSAGE);
			return;
		}
	//Save location
		boolean ok = browse();
		if (!ok) {
			return;
		}
	//Data source
		//FIXME - - > ExportDialog: Row order is all wrong -_-
		if (jNoFilter.isSelected()) {
			for (EventList<E> eventList : eventLists) {
				for (E e : eventList) {
					items.add(e);
				}
			}
		} else if (jCurrentFilter.isSelected()) {
			List<Filter> filter = exportFilterControl.getCurrentFilters();
			for (EventList<E> eventList : eventLists) {
				FilterList<E> filterList = new FilterList<E>(eventList, new FilterLogicalMatcher<E>(filterControl, filter));
				for (E e : filterList) {
					items.add(e);
				}
			}
		} else if (jSavedFilter.isSelected()) {
			String filterName = (String) jFilters.getSelectedItem();
			List<Filter> filter = exportFilterControl.getAllFilters().get(filterName);
			for (EventList<E> eventList : eventLists) {
				FilterList<E> filterList = new FilterList<E>(eventList, new FilterLogicalMatcher<E>(filterControl, filter));
				for (E e : filterList) {
					items.add(e);
				}
			}
		}
	//Save settings
		saveSettings();
	//Add data
		for (E e : items) {
			Map<String, String> line = new HashMap<String, String>();
			Map<String, Object> row = new HashMap<String, Object>();
			for (EnumTableColumn<E> column : selectedColumns) {
				line.put(column.getColumnName(), format(column.getColumnValue(e), Settings.get().getExportSettings().getDecimalSeparator()));
				row.put(column.getColumnName(), column.getColumnValue(e));
			}
			objectRows.add(row);
			stringRows.add(line);
		}
	//Save file
		String extension = jFileChooser.getExtension();
		boolean saved;
		if (extension.equals(EXPORT_CSV)) {
			//CSV
			//Bad selection
			if (Settings.get().getExportSettings().getDecimalSeparator() == DecimalSeparator.COMMA && Settings.get().getExportSettings().getFieldDelimiter() == FieldDelimiter.COMMA) {
				int nReturn = JOptionPane.showConfirmDialog(
						getDialog(),
						DialoguesExport.get().confirmStupidDecision(),
						DialoguesExport.get().export(),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (nReturn == JOptionPane.NO_OPTION) {
					return;
				}
			}
			saved = CsvWriter.save(Settings.get().getExportSettings().getFilename(toolName), stringRows, header.toArray(new String[header.size()]), new CsvPreference('\"', Settings.get().getExportSettings().getFieldDelimiter().getValue(), Settings.get().getExportSettings().getLineDelimiter().getValue()));
		} else if (extension.equals(EXPORT_HTML)) {
			//HTML
			saved = HtmlWriter.save(Settings.get().getExportSettings().getFilename(toolName), stringRows, header);
		} else if (extension.equals(EXPORT_SQL)) {
			//SQL
			saved = SqlWriter.save(Settings.get().getExportSettings().getFilename(toolName), objectRows, header, sqlHeader, Settings.get().getExportSettings().getTableName(toolName), Settings.get().getExportSettings().isDropTable(), Settings.get().getExportSettings().isCreateTable(), Settings.get().getExportSettings().isExtendedInserts());
		} else {
			saved = false;
		}
		if (!saved) {
			JOptionPane.showMessageDialog(getDialog(),
					DialoguesExport.get().failedToSave(),
					DialoguesExport.get().export(),
					JOptionPane.PLAIN_MESSAGE);
		}

		setVisible(false);
	}

	@Override
	public void windowClosing(final WindowEvent e) {
		super.windowClosing(e);
		saveSettings();
	}

	public class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ACTION_DISABLE_SAVED_FILTERS.equals(e.getActionCommand())) {
				jFilters.setEnabled(false);
			}
			if (ACTION_ENABLE_SAVED_FILTERS.equals(e.getActionCommand())) {
				jFilters.setEnabled(true);
			}
			if (ACTION_OK.equals(e.getActionCommand())) {
				save();
			}
			if (ACTION_DEFAULT.equals(e.getActionCommand())) {
				resetSettings();
			}
			if (ACTION_CANCEL.equals(e.getActionCommand())) {
				setVisible(false);
			}
			if (ACTION_TOOL_COLUMNS.equals(e.getActionCommand())) {
				jColumnSelection.setEnabled(!jToolColumns.isSelected());
			}
		}
	}
}

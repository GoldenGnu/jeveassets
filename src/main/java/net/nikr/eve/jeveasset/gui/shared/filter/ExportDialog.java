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
import java.awt.CardLayout;
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
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.i18n.DialoguesExport;
import net.nikr.eve.jeveasset.io.local.CsvWriter;
import net.nikr.eve.jeveasset.io.local.HtmlWriter;
import net.nikr.eve.jeveasset.io.local.SqlWriter;
import org.supercsv.prefs.CsvPreference;


public class ExportDialog<E> extends JDialogCentered {

	private static final String ACTION_DISABLE_SAVED_FILTERS = "ACTION_DISABLE_SAVED_FILTERS";
	private static final String ACTION_ENABLE_SAVED_FILTERS = "ACTION_ENABLE_SAVED_FILTERS";
	private static final String ACTION_OK = "ACTION_OK";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_DEFAULT = "ACTION_DEFAULT";
	private static final String ACTION_TOOL_COLUMNS = "ACTION_TOOL_COLUMNS";
	private static final String ACTION_FORMAT = "ACTION_FORMAT";

	private static final String CARD_CSV = "CARD_CSV";
	private static final String CARD_SQL = "CARD_SQL";
	private static final String CARD_HTML = "CARD_HTML";

	private static final String EXPORT_CSV = "csv";
	private static final String EXPORT_HTML = "html";
	private static final String EXPORT_SQL = "sql";

	//Filter
	private JRadioButton jNoFilter;
	private JRadioButton jSavedFilter;
	private JRadioButton jCurrentFilter;
	private JComboBox jFilters;
	//Columns
	private JCheckBox jToolColumns;
	private JMultiSelectionList jColumnSelection;
	//Format
	private JRadioButton jCsv;
	private JRadioButton jHtml;
	private JRadioButton jSql;
	//Options
	private CardLayout cardLayout;
	private JPanel jCardPanel;
	//CSV
	private JComboBox jFieldDelimiter;
	private JComboBox jLineDelimiter;
	private JComboBox jDecimalSeparator;
	//Html
	private JCheckBox jHtmlStyle;
	private JSlider jHtmlHeaderRepeat;
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
		layout.setAutoCreateContainerGaps(false);

		try {
			jFileChooser = new JCustomFileChooser(jFrame, EXPORT_CSV);
		} catch (RuntimeException e) {
			// Workaround for JRE bug 4711700. A NullPointer is thrown
			// sometimes on the first construction under XP look and feel,
			// but construction succeeds on successive attempts.
			try {
				jFileChooser = new JCustomFileChooser(jFrame, EXPORT_CSV);
			} catch (RuntimeException npe) {
				// ok, now we use the metal file chooser, takes a long time to load
				// but the user can still use the program
				UIManager.getDefaults().put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
				jFileChooser = new JCustomFileChooser(jFrame, EXPORT_CSV);
			}
		}
	//Format
		JLabel jFormatLabel = new JLabel(DialoguesExport.get().format());
		jFormatLabel.setFont(new Font(jFormatLabel.getFont().getName(), Font.BOLD, jFormatLabel.getFont().getSize()));

		jCsv = new JRadioButton(DialoguesExport.get().csv());
		jCsv.setActionCommand(ACTION_FORMAT);
		jCsv.addActionListener(listener);

		jHtml = new JRadioButton(DialoguesExport.get().html());
		jHtml.setActionCommand(ACTION_FORMAT);
		jHtml.addActionListener(listener);

		jSql = new JRadioButton(DialoguesExport.get().sql());
		jSql.setActionCommand(ACTION_FORMAT);
		jSql.addActionListener(listener);

		ButtonGroup jFormatButtonGroup = new ButtonGroup();
		jFormatButtonGroup.add(jCsv);
		jFormatButtonGroup.add(jHtml);
		jFormatButtonGroup.add(jSql);
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
		//FIXME - - > ExportDialog: Use saved view...
		JLabel jColumnLabel = new JLabel(DialoguesExport.get().columns());
		jColumnLabel.setFont(new Font(jColumnLabel.getFont().getName(), Font.BOLD, jColumnLabel.getFont().getSize()));

		columnNames = new ArrayList<String>();
		for (EnumTableColumn<E> column : enumColumns) {
			columns.put(column.getColumnName(), column);
			columnNames.add(column.getColumnName());
		}

		jToolColumns = new JCheckBox(DialoguesExport.get().toolColumns());
		jToolColumns.setActionCommand(ACTION_TOOL_COLUMNS);
		jToolColumns.addActionListener(listener);

		jColumnSelection = new JMultiSelectionList(columnNames);
		jColumnSelection.selectAll();

		JScrollPane jColumnSelectionPanel = new JScrollPane(jColumnSelection);

	//Options
		cardLayout = new CardLayout();
		jCardPanel = new JPanel(cardLayout);

		JLabel jOptionsLabel = new JLabel(DialoguesExport.get().options());
		jOptionsLabel.setFont(new Font(jOptionsLabel.getFont().getName(), Font.BOLD, jOptionsLabel.getFont().getSize()));
	//Csv
		JOptionPanel jCsvPanel = new JOptionPanel();
		jCardPanel.add(jCsvPanel, CARD_CSV);

		JLabel jFieldDelimiterLabel = new JLabel(DialoguesExport.get().fieldTerminated());
		jFieldDelimiter = new JComboBox(FieldDelimiter.values());
		jCsvPanel.add(jFieldDelimiterLabel);
		jCsvPanel.add(jFieldDelimiter);

		JLabel jLineDelimiterLabel = new JLabel(DialoguesExport.get().linesTerminated());
		jLineDelimiter = new JComboBox(LineDelimiter.values());
		jCsvPanel.add(jLineDelimiterLabel);
		jCsvPanel.add(jLineDelimiter);

		JLabel jDecimalSeparatorLabel = new JLabel(DialoguesExport.get().decimalSeparator());
		jDecimalSeparator = new JComboBox(DecimalSeparator.values());
		jCsvPanel.add(jDecimalSeparatorLabel);
		jCsvPanel.add(jDecimalSeparator);
	//Sql
		JOptionPanel jSqlPanel = new JOptionPanel();
		jCardPanel.add(jSqlPanel, CARD_SQL);

		JLabel jTableNameLabel = new JLabel(DialoguesExport.get().tableName());
		jTableName = new JDefaultField(Program.PROGRAM_NAME.toLowerCase() + "_" + toolName.toLowerCase());
		jTableName.setDocument(DocumentFactory.getWordPlainDocument());
		jSqlPanel.add(jTableNameLabel);
		jSqlPanel.add(jTableName);

		jDropTable = new JCheckBox(DialoguesExport.get().dropTable());
		jSqlPanel.add(jDropTable);

		jCreateTable = new JCheckBox(DialoguesExport.get().createTable());
		jSqlPanel.add(jCreateTable);

		jExtendedInserts = new JCheckBox(DialoguesExport.get().extendedInserts());
		jSqlPanel.add(jExtendedInserts);
	//Html
		JOptionPanel jHtmlPanel = new JOptionPanel();
		jCardPanel.add(jHtmlPanel, CARD_HTML);

		jHtmlStyle = new JCheckBox(DialoguesExport.get().htmlStyled());
		jHtmlPanel.add(jHtmlStyle);

		JLabel jHtmlHeaderRepeatLabel = new JLabel(DialoguesExport.get().htmlHeaderRepeat());
		jHtmlHeaderRepeat = new JSlider(JSlider.HORIZONTAL, 0, 50, 0);
		jHtmlHeaderRepeat.setMajorTickSpacing(10);
		jHtmlHeaderRepeat.setMinorTickSpacing(5);
		jHtmlHeaderRepeat.setSnapToTicks(true);
		jHtmlHeaderRepeat.setPaintTicks(true);
		jHtmlHeaderRepeat.setPaintLabels(true);
		jHtmlPanel.add(jHtmlHeaderRepeatLabel);
		jHtmlPanel.add(jHtmlHeaderRepeat);
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
					.addContainerGap()
					.addGroup(layout.createParallelGroup()
						//Format
						.addComponent(jFormatLabel, GroupLayout.Alignment.CENTER)
						.addComponent(jCsv)
						.addComponent(jHtml)
						.addComponent(jSql)
						//Filters
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
					.addGap(15)
					.addComponent(jVerticalSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(15)
					.addGroup(layout.createParallelGroup()
						//Columns
						.addComponent(jColumnLabel, GroupLayout.Alignment.CENTER)
						.addComponent(jToolColumns)
						.addComponent(jColumnSelectionPanel, 165, 165, 165)
						//Options
						.addComponent(jOptionsLabel, GroupLayout.Alignment.CENTER)
						.addComponent(jCardPanel)
					)
					.addContainerGap()
				)
				.addComponent(jHorizontalSeparator)
				.addComponent(jButtonSeparator)
				.addGroup(layout.createSequentialGroup()
					.addComponent(jOK, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jDefault, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addContainerGap()
				)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jVerticalSeparator)
					.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup()
							//Columns
							.addGroup(layout.createSequentialGroup()
								.addComponent(jFormatLabel)
								.addGap(10)
								.addComponent(jCsv, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jHtml, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jSql, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
							)
							//Options
							.addGroup(layout.createSequentialGroup()
								.addComponent(jOptionsLabel)
								.addGap(10)
								.addComponent(jCardPanel)
							)
						)
						.addGap(15)
						.addComponent(jHorizontalSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(10)
						.addGroup(layout.createParallelGroup()
							//Filters
							.addGroup(layout.createSequentialGroup()
								.addComponent(jFiltersLabel)
								.addGap(10)
								.addComponent(jNoFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jCurrentFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jSavedFilter, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jFilters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
							)
							//Columns
							.addGroup(layout.createSequentialGroup()
								.addComponent(jColumnLabel)
								.addGap(10)
								.addComponent(jToolColumns)
								.addComponent(jColumnSelectionPanel, 120, 120, 120)
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
				.addContainerGap()
		);
	}

	private void setVisible(List<JComponent> list, boolean visible) {
		for (JComponent jComponent : list) {
			jComponent.setVisible(visible);
		}
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
		String filename = Settings.get().getExportSettings().getFilename(toolName);
		int extensionEnd = filename.lastIndexOf(".");
		if (extensionEnd >= 0) {
			filename = filename.substring(0, extensionEnd + 1);
		}
		if (jCsv.isSelected()) {
			jFileChooser.setExtensions(EXPORT_CSV);
			filename = filename + EXPORT_CSV;
		} else if (jHtml.isSelected()) {
			jFileChooser.setExtensions(EXPORT_HTML);
			filename = filename + EXPORT_HTML;
		} else if (jSql.isSelected()) {
			jFileChooser.setExtensions(EXPORT_SQL);
			filename = filename + EXPORT_SQL;
		}
		File file = new File(filename);
		String pathname = Settings.get().getExportSettings().getFilename(toolName);
		int end = pathname.lastIndexOf(File.separator);
		if (end > 0) {
			pathname = pathname.substring(0, end + 1);
		}
		File pathFile = new File(pathname);

		if (pathFile.exists()) {
			jFileChooser.setCurrentDirectory(pathFile);
			jFileChooser.setSelectedFile(file);
		} else {
			jFileChooser.setCurrentDirectory(new File(ExportSettings.getDefaultPath()));
			jFileChooser.setSelectedFile(new File(ExportSettings.getDefaultFilename(toolName)));
		}
		int bFound = jFileChooser.showDialog(getDialog(), DialoguesExport.get().ok());
		if (bFound  == JFileChooser.APPROVE_OPTION) {
			file = jFileChooser.getSelectedFile();
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
		//HTML
		Settings.get().getExportSettings().setHtmlStyled(jHtmlStyle.isSelected());
		Settings.get().getExportSettings().setHtmlRepeatHeader(jHtmlHeaderRepeat.getValue());
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
		//HTML
		jHtmlStyle.setSelected(Settings.get().getExportSettings().isHtmlStyled());
		jHtmlHeaderRepeat.setValue(Settings.get().getExportSettings().getHtmlRepeatHeader());
		//Filename
		String filename = Settings.get().getExportSettings().getFilename(toolName);
		if (filename.endsWith(EXPORT_HTML)) {
			jHtml.setSelected(true);
			cardLayout.show(jCardPanel, CARD_HTML);
		} else if (filename.endsWith(EXPORT_SQL)) {
			jSql.setSelected(true);
			cardLayout.show(jCardPanel, CARD_SQL);
		} else { //EXPORT_CSV and Default
			jCsv.setSelected(true);
			cardLayout.show(jCardPanel, CARD_CSV);
		}
		//Columns (Shared)
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
		//HTML
		Settings.get().getExportSettings().setHtmlStyled(true);
		Settings.get().getExportSettings().setHtmlRepeatHeader(0);
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
			//Create data
			List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
			for (E e : items) {
				Map<String, String> row = new HashMap<String, String>();
				for (EnumTableColumn<E> column : selectedColumns) {
					row.put(column.getColumnName(), format(column.getColumnValue(e), Settings.get().getExportSettings().getDecimalSeparator()).replace("+", ""));
				}
				rows.add(row);
			}
			//Save data
			saved = CsvWriter.save(Settings.get().getExportSettings().getFilename(toolName),
					rows,
					header.toArray(new String[header.size()]),
					new CsvPreference('\"', Settings.get().getExportSettings().getFieldDelimiter().getValue(), Settings.get().getExportSettings().getLineDelimiter().getValue()));
		} else if (extension.equals(EXPORT_HTML)) {
			//HTML
			//Create data
			List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
			for (E e : items) {
				Map<String, String> row = new HashMap<String, String>();
				for (EnumTableColumn<E> column : selectedColumns) {
					row.put(column.getColumnName(), format(column.getColumnValue(e), Settings.get().getExportSettings().getDecimalSeparator()));
				}
				rows.add(row);
			}
			//Save data
			saved = HtmlWriter.save(Settings.get().getExportSettings().getFilename(toolName),
					rows,
					header,
					Settings.get().getExportSettings().isHtmlStyled(),
					Settings.get().getExportSettings().getHtmlRepeatHeader(),
					toolName.equals(TreeTab.NAME));
		} else if (extension.equals(EXPORT_SQL)) {
			//SQL
			//Create data
			List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
			for (E e : items) {
				Map<String, Object> row = new HashMap<String, Object>();
				for (EnumTableColumn<E> column : selectedColumns) {
					row.put(column.getColumnName(), column.getColumnValue(e));
				}
				rows.add(row);
			}
			//Save data
			saved = SqlWriter.save(Settings.get().getExportSettings().getFilename(toolName),
					rows,
					header,
					sqlHeader,
					Settings.get().getExportSettings().getTableName(toolName),
					Settings.get().getExportSettings().isDropTable(),
					Settings.get().getExportSettings().isCreateTable(),
					Settings.get().getExportSettings().isExtendedInserts());
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
			} else if (ACTION_ENABLE_SAVED_FILTERS.equals(e.getActionCommand())) {
				jFilters.setEnabled(true);
			} else if (ACTION_OK.equals(e.getActionCommand())) {
				save();
			} else if (ACTION_DEFAULT.equals(e.getActionCommand())) {
				resetSettings();
			} else if (ACTION_CANCEL.equals(e.getActionCommand())) {
				setVisible(false);
			} else if (ACTION_TOOL_COLUMNS.equals(e.getActionCommand())) {
				jColumnSelection.setEnabled(!jToolColumns.isSelected());
			} else if (ACTION_FORMAT.equals(e.getActionCommand())) {
				if (jCsv.isSelected()) {
					cardLayout.show(jCardPanel, CARD_CSV);
				} else if (jHtml.isSelected()) {
					cardLayout.show(jCardPanel, CARD_HTML);
				} else if (jSql.isSelected()) {
					cardLayout.show(jCardPanel, CARD_SQL);
				}
			}
		}
	}

	public static class JOptionPanel extends JPanel {
		protected final GroupLayout layout;
		private final List<JComponent> components = new ArrayList<JComponent>();

		private JOptionPanel() {
			layout = new GroupLayout(this);
			this.setLayout(layout);
			layout.setAutoCreateGaps(false);
			layout.setAutoCreateContainerGaps(false);
		}

		public void add(JComponent comp) {
			components.add(comp);
			createLayout();
		}

		protected void createLayout() {
			this.removeAll();
			GroupLayout.ParallelGroup horizontalGroup = layout.createParallelGroup();
			GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();

			for (JComponent jComponent : components) {
				horizontalGroup.addComponent(jComponent);
				if (jComponent instanceof JLabel
						|| jComponent instanceof JButton
						|| jComponent instanceof JCheckBox
						|| jComponent instanceof JTextField
						|| jComponent instanceof JComboBox) {
					verticalGroup.addComponent(jComponent, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT);
				} else {
					verticalGroup.addComponent(jComponent);
				}
			}
			layout.setHorizontalGroup(
				layout.createSequentialGroup()
					.addGroup(horizontalGroup)
				);
			layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGroup(verticalGroup)
					//.addGap(0, 0, Integer.MAX_VALUE)
				);
		}
	}
}

/*
 * Copyright 2009-2014 Contributors (see credits.txt)
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
import ca.odell.glazedlists.matchers.Matcher;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.ExportSettings.DecimalSeparator;
import net.nikr.eve.jeveasset.data.ExportSettings.ExportFormat;
import net.nikr.eve.jeveasset.data.ExportSettings.FieldDelimiter;
import net.nikr.eve.jeveasset.data.ExportSettings.LineDelimiter;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.data.tag.Tags;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.CaseInsensitiveComparator;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.components.JCustomFileChooser;
import net.nikr.eve.jeveasset.gui.shared.components.JDefaultField;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.components.JMultiSelectionList;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableColumn;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor.SimpleColumn;
import net.nikr.eve.jeveasset.gui.shared.table.View;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeAsset;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTab.AssetTreeComparator;
import net.nikr.eve.jeveasset.gui.tabs.tree.TreeTableFormat.HierarchyColumn;
import net.nikr.eve.jeveasset.i18n.DialoguesExport;
import net.nikr.eve.jeveasset.io.local.CsvWriter;
import net.nikr.eve.jeveasset.io.local.HtmlWriter;
import net.nikr.eve.jeveasset.io.local.SqlWriter;
import org.supercsv.prefs.CsvPreference;


public class ExportDialog<E> extends JDialogCentered {

	private enum ExportAction {
		OK,
		CANCEL,
		DEFAULT,
		FILTER_CHANGED,
		VIEW_CHANGED,
		FORMAT_CHANGED
	}

	//Filter
	private JRadioButton jNoFilter;
	private JRadioButton jSavedFilter;
	private JRadioButton jCurrentFilter;
	private JComboBox jFilters;
	//Columns
	private JRadioButton jViewCurrent;
	private JRadioButton jViewSelect;
	private JRadioButton jViewSaved;
	private JComboBox jViews;
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
	private JCheckBox jHtmlIGB;
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
	private List<EnumTableColumn<E>> columnIndex = new ArrayList<EnumTableColumn<E>>();
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
			jFileChooser = new JCustomFileChooser(jFrame, Settings.get().getExportSettings().getExportFormat().getExtension());
		} catch (RuntimeException e) {
			// Workaround for JRE bug 4711700. A NullPointer is thrown
			// sometimes on the first construction under XP look and feel,
			// but construction succeeds on successive attempts.
			try {
				jFileChooser = new JCustomFileChooser(jFrame, Settings.get().getExportSettings().getExportFormat().getExtension());
			} catch (RuntimeException npe) {
				// ok, now we use the metal file chooser, takes a long time to load
				// but the user can still use the program
				UIManager.getDefaults().put("FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI");
				jFileChooser = new JCustomFileChooser(jFrame, Settings.get().getExportSettings().getExportFormat().getExtension());
			}
		}
	//Format
		JLabel jFormatLabel = new JLabel(DialoguesExport.get().format());
		jFormatLabel.setFont(new Font(jFormatLabel.getFont().getName(), Font.BOLD, jFormatLabel.getFont().getSize()));

		jCsv = new JRadioButton(DialoguesExport.get().csv());
		jCsv.setActionCommand(ExportAction.FORMAT_CHANGED.name());
		jCsv.addActionListener(listener);

		jHtml = new JRadioButton(DialoguesExport.get().html());
		jHtml.setActionCommand(ExportAction.FORMAT_CHANGED.name());
		jHtml.addActionListener(listener);

		jSql = new JRadioButton(DialoguesExport.get().sql());
		jSql.setActionCommand(ExportAction.FORMAT_CHANGED.name());
		jSql.addActionListener(listener);

		ButtonGroup jFormatButtonGroup = new ButtonGroup();
		jFormatButtonGroup.add(jCsv);
		jFormatButtonGroup.add(jHtml);
		jFormatButtonGroup.add(jSql);
	//Filters
		JLabel jFiltersLabel = new JLabel(DialoguesExport.get().filters());
		jFiltersLabel.setFont(new Font(jFiltersLabel.getFont().getName(), Font.BOLD, jFiltersLabel.getFont().getSize()));

		jNoFilter = new JRadioButton(DialoguesExport.get().noFilter());
		jNoFilter.setActionCommand(ExportAction.FILTER_CHANGED.name());
		jNoFilter.addActionListener(listener);
		jNoFilter.setSelected(true);

		jCurrentFilter = new JRadioButton(DialoguesExport.get().currentFilter());
		jCurrentFilter.setActionCommand(ExportAction.FILTER_CHANGED.name());
		jCurrentFilter.addActionListener(listener);

		jSavedFilter = new JRadioButton(DialoguesExport.get().savedFilter());
		jSavedFilter.setActionCommand(ExportAction.FILTER_CHANGED.name());
		jSavedFilter.addActionListener(listener);

		ButtonGroup jFilterButtonGroup = new ButtonGroup();
		jFilterButtonGroup.add(jNoFilter);
		jFilterButtonGroup.add(jSavedFilter);
		jFilterButtonGroup.add(jCurrentFilter);

		jFilters = new JComboBox();
	//Columns
		jViewCurrent = new JRadioButton(DialoguesExport.get().viewCurrent());
		jViewCurrent.setActionCommand(ExportAction.VIEW_CHANGED.name());
		jViewCurrent.addActionListener(listener);
		jViewCurrent.setSelected(true);

		jViewSaved = new JRadioButton(DialoguesExport.get().viewSaved());
		jViewSaved.setActionCommand(ExportAction.VIEW_CHANGED.name());
		jViewSaved.addActionListener(listener);

		jViews = new JComboBox();

		jViewSelect = new JRadioButton(DialoguesExport.get().viewSelect());
		jViewSelect.setActionCommand(ExportAction.VIEW_CHANGED.name());
		jViewSelect.addActionListener(listener);
		
		JLabel jColumnLabel = new JLabel(DialoguesExport.get().columns());
		jColumnLabel.setFont(new Font(jColumnLabel.getFont().getName(), Font.BOLD, jColumnLabel.getFont().getSize()));

		columnIndex.addAll(enumColumns);
		for (EnumTableColumn<E> column : enumColumns) {
			columns.put(column.name(), column);
		}

		jColumnSelection = new JMultiSelectionList(columnIndex);
		jColumnSelection.selectAll();
		jColumnSelection.setEnabled(false);

		JScrollPane jColumnSelectionPanel = new JScrollPane(jColumnSelection);

		ButtonGroup jViewButtonGroup = new ButtonGroup();
		jViewButtonGroup.add(jViewCurrent);
		jViewButtonGroup.add(jViewSaved);
		jViewButtonGroup.add(jViewSelect);
	//Options
		cardLayout = new CardLayout();
		jCardPanel = new JPanel(cardLayout);

		JLabel jOptionsLabel = new JLabel(DialoguesExport.get().options());
		jOptionsLabel.setFont(new Font(jOptionsLabel.getFont().getName(), Font.BOLD, jOptionsLabel.getFont().getSize()));
	//Csv
		JOptionPanel jCsvPanel = new JOptionPanel();
		jCardPanel.add(jCsvPanel, ExportFormat.CSV.name());

		JLabel jFieldDelimiterLabel = new JLabel(DialoguesExport.get().fieldTerminated());
		jFieldDelimiter = new JComboBox(FieldDelimiter.values());
		jCsvPanel.add(jFieldDelimiterLabel);
		jCsvPanel.add(jFieldDelimiter);

		JLabel jLineDelimiterLabel = new JLabel(DialoguesExport.get().linesTerminated());
		jLineDelimiter = new JComboBox(LineDelimiter.values());
		jCsvPanel.add(jLineDelimiterLabel);
		jCsvPanel.add(jLineDelimiter);

		//FIXME - - > ExportDialog: DecimalSeparator also used by HTML export...
		JLabel jDecimalSeparatorLabel = new JLabel(DialoguesExport.get().decimalSeparator());
		jDecimalSeparator = new JComboBox(DecimalSeparator.values());
		jCsvPanel.add(jDecimalSeparatorLabel);
		jCsvPanel.add(jDecimalSeparator);
	//Sql
		JOptionPanel jSqlPanel = new JOptionPanel();
		jCardPanel.add(jSqlPanel, ExportFormat.SQL.name());

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
		jCardPanel.add(jHtmlPanel, ExportFormat.HTML.name());

		jHtmlStyle = new JCheckBox(DialoguesExport.get().htmlStyled());
		jHtmlPanel.add(jHtmlStyle);

		jHtmlIGB = new JCheckBox(DialoguesExport.get().htmlIGB());
		jHtmlPanel.add(jHtmlIGB);

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
		jOK.setActionCommand(ExportAction.OK.name());
		jOK.addActionListener(listener);

		JButton jDefault = new JButton(DialoguesExport.get().defaultSettings());
		jDefault.setActionCommand(ExportAction.DEFAULT.name());
		jDefault.addActionListener(listener);

		JButton jCancel = new JButton(DialoguesExport.get().cancel());
		jCancel.setActionCommand(ExportAction.CANCEL.name());
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
						.addComponent(jViewCurrent)
						.addComponent(jViewSaved)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addGroup(layout.createSequentialGroup()
								.addGap(20)
								.addComponent(jViews, 150, 150, 150)
							)
						)
						.addComponent(jViewSelect)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addGroup(layout.createSequentialGroup()
								.addGap(20)
								.addComponent(jColumnSelectionPanel, 150, 150, 150)
							)
						)
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
							//Format
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
								.addComponent(jViewCurrent, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jViewSaved, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jViews, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
								.addComponent(jViewSelect, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
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

	public void setColumns(final List<EnumTableColumn<E>> enumColumns) {
		columns.clear();
		columnIndex.clear();
		columnIndex.addAll(enumColumns);
		for (EnumTableColumn<E> column : enumColumns) {
			columns.put(column.name(), column);
		}
		jColumnSelection.setModel(new AbstractListModel() {
			@Override
			public int getSize() {
				return columnIndex.size();
			}

			@Override
			public Object getElementAt(int index) {
				return columnIndex.get(index);
			}
		});
	}

	private List<String> getExportColumns() {
		List<String> selectedColumns = new ArrayList<String>();
		Object[] values = jColumnSelection.getSelectedValues();
		for (Object object : values) {
			if (object instanceof EnumTableColumn<?>) {
				EnumTableColumn<?> column = (EnumTableColumn) object;
				selectedColumns.add(column.name());
			}
		}
		return selectedColumns;
	}

	private boolean browse() {
		File file = new File(Settings.get().getExportSettings().getFilename(toolName));
		File path = new File(Settings.get().getExportSettings().getPath(toolName));
		if (path.exists()) {
			jFileChooser.setCurrentDirectory(path);
			jFileChooser.setSelectedFile(file);
		} else {
			jFileChooser.setCurrentDirectory(new File(Settings.get().getExportSettings().getDefaultPath()));
			jFileChooser.setSelectedFile(new File(Settings.get().getExportSettings().getDefaultFilename(toolName)));
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

	private String format(final Object object, final DecimalSeparator decimalSeparator, final boolean html) {
		if (object == null) {
			return "";
		} else if (object instanceof HierarchyColumn) {
			HierarchyColumn column = (HierarchyColumn) object;
			return column.getExport();
		} else if (object instanceof Number) {
			Number number = (Number) object;
			if (decimalSeparator == DecimalSeparator.DOT) {
				return EN_NUMBER_FORMAT.format(number);
			} else {
				return EU_NUMBER_FORMAT.format(number);
			}
		} else if (object instanceof Tags && html) {
			Tags tags = (Tags) object;
			return tags.getHtml();
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
		Settings.get().getExportSettings().setHtmlIGB(jHtmlIGB.isSelected());
		Settings.get().getExportSettings().setHtmlRepeatHeader(jHtmlHeaderRepeat.getValue());
		//Shared
		if (jColumnSelection.getSelectedIndices().length == columns.size()) { //All is selected - nothing worth saving...
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
		jHtmlIGB.setSelected(Settings.get().getExportSettings().isHtmlIGB());
		jHtmlHeaderRepeat.setValue(Settings.get().getExportSettings().getHtmlRepeatHeader());
		//Filename
		ExportFormat exportFormat = Settings.get().getExportSettings().getExportFormat();
		cardLayout.show(jCardPanel, exportFormat.name());
		if (exportFormat == ExportFormat.HTML) {
			jHtml.setSelected(true);
		} else if (exportFormat == ExportFormat.SQL) {
			jSql.setSelected(true);
		} else { //CSV and Default
			jCsv.setSelected(true);
		}
		jFileChooser.setExtension(exportFormat.getExtension());
		//Columns (Shared)
		jColumnSelection.clearSelection();
		List<String> list = Settings.get().getExportSettings().getTableExportColumns(toolName);
		if (list == null) {
			jColumnSelection.selectAll();
		} else {
			List<Integer> selections = new ArrayList<Integer>();
			for (String column : list) {
				EnumTableColumn<?> enumColumn = exportFilterControl.valueOf(column);
				int index = columnIndex.indexOf(enumColumn);
				selections.add(index);
			}
			int[] indices = new int[selections.size()];
			for (int i = 0; i < selections.size(); i++) {
				indices[i] = selections.get(i);
			}
			jColumnSelection.setSelectedIndices(indices);
		}
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
		Settings.get().getExportSettings().putFilename(toolName, Settings.get().getExportSettings().getDefaultFilename(toolName));
		Settings.get().getExportSettings().putTableExportColumns(toolName, null);
		Settings.get().getExportSettings().setExportFormat(ExportFormat.CSV);
		loadSettings();
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			loadSettings();
			//Filters (Saved)
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
				Object selectedItem = jFilters.getSelectedItem(); //Save selection
				jFilters.setModel(new DefaultComboBoxModel(filterNames.toArray()));
				if (selectedItem != null) { //Restore selection
					jFilters.setSelectedItem(selectedItem);
				}
			}
			//Filters (Current)
			if (exportFilterControl.getCurrentFilters().isEmpty()) {
				if (jCurrentFilter.isSelected()) {
					jNoFilter.setSelected(true);
				}
				jCurrentFilter.setEnabled(false);
			} else {
				jCurrentFilter.setEnabled(true);
			}
			//Views
			jViews.setEnabled(false);
			Map<String, View> tableViews = Settings.get().getTableViews(toolName);
			if (tableViews.isEmpty()) {
				if (jViewSaved.isSelected()) {
					jViewCurrent.setSelected(true);
				}
				jViewSaved.setEnabled(false);
				jViews.getModel().setSelectedItem(DialoguesExport.get().viewNoSaved());
			} else {
				if (jViewSaved.isSelected()) {
					jViews.setEnabled(true);
				}
				jViewSaved.setEnabled(true);
				Object selectedItem = jViews.getSelectedItem(); //Save selection
				jViews.setModel(new DefaultComboBoxModel(tableViews.keySet().toArray()));
				if (selectedItem != null) { //Restore selection
					jViews.setSelectedItem(selectedItem);
				}
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

	@Override
	protected void save() {
		List<E> items = new ArrayList<E>();

	//Columns + Header
		List<EnumTableColumn<E>> header = new ArrayList<EnumTableColumn<E>>();
		if (jViewCurrent.isSelected()) {
			//Use the tool current shown columns + order
			header = exportFilterControl.getShownColumns();
		} else if (jViewSaved.isSelected()) {
			String viewKey = (String) jViews.getSelectedItem();
			View view = Settings.get().getTableViews(toolName).get(viewKey);
			for (SimpleColumn simpleColumn : view.getColumns()) {
				if (simpleColumn.isShown()) {
					EnumTableColumn<E> column = columns.get(simpleColumn.getEnumName());
					header.add(column);
				}
			}
		} else {
			//Use custom columns
			Object[] values = jColumnSelection.getSelectedValues();
			for (Object object : values) {
				if (object instanceof EnumTableColumn<?>) {
					String columnName = ((EnumTableColumn) object).name();
					EnumTableColumn<E> column = columns.get(columnName);
					header.add(column);
				}
			}
			
		}
	//Bad selection
		if (header.isEmpty()) {
			JOptionPane.showMessageDialog(getDialog(), DialoguesExport.get().selectOne(), DialoguesExport.get().export(), JOptionPane.PLAIN_MESSAGE);
			return;
		}
	//Save location
		boolean ok = browse();
		if (!ok) {
			return;
		}
	//Filters
		if (jNoFilter.isSelected()) {
			for (EventList<E> eventList : eventLists) {
				items.addAll(eventList);
			}
		} else if (jCurrentFilter.isSelected()) {
			List<Filter> filter = exportFilterControl.getCurrentFilters();
			for (EventList<E> eventList : eventLists) {
				FilterList<E> filterList = new FilterList<E>(eventList, new FilterLogicalMatcher<E>(filterControl, filter));
				if (!filterList.isEmpty() && filterList.get(0) instanceof TreeAsset) {
					FilterList<E> treeFilterList = new FilterList<E>(eventList, new TreeMatcher<E>(filterList));
					items.addAll(treeFilterList);
				} else {
					items.addAll(filterList);
				}
			}
		} else if (jSavedFilter.isSelected()) {
			String filterName = (String) jFilters.getSelectedItem();
			List<Filter> filter = exportFilterControl.getAllFilters().get(filterName);
			for (EventList<E> eventList : eventLists) {
				FilterList<E> filterList = new FilterList<E>(eventList, new FilterLogicalMatcher<E>(filterControl, filter));
				if (!filterList.isEmpty() && filterList.get(0) instanceof TreeAsset) {
					FilterList<E> treeFilterList = new FilterList<E>(eventList, new TreeMatcher<E>(filterList));
					items.addAll(treeFilterList);
				} else {
					items.addAll(filterList);
				}
			}
		}
	//Save settings
		saveSettings();
	//Save file
		boolean saved;
		if (jCsv.isSelected()) {
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
			List<String> headerStrings = new ArrayList<String>(header.size());
			List<String> headerKeys = new ArrayList<String>(header.size());
			for (EnumTableColumn<E> column : header) {
				headerStrings.add(column.getColumnName());
				headerKeys.add(column.name());
			}
			List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
			for (E e : items) {
				Map<String, String> row = new HashMap<String, String>();
				for (EnumTableColumn<E> column : header) {
					row.put(column.name(), format(column.getColumnValue(e), Settings.get().getExportSettings().getDecimalSeparator(), false));
				}
				rows.add(row);
			}
			//Save data
			saved = CsvWriter.save(Settings.get().getExportSettings().getFilename(toolName),
					rows,
					headerStrings.toArray(new String[headerStrings.size()]),
					headerKeys.toArray(new String[headerKeys.size()]),
					new CsvPreference('\"', Settings.get().getExportSettings().getFieldDelimiter().getValue(), Settings.get().getExportSettings().getLineDelimiter().getValue()));
		} else if (jHtml.isSelected()) {
	//HTML
			//Create data
			List<Map<EnumTableColumn<?>, String>> rows = new ArrayList<Map<EnumTableColumn<?>, String>>();
			for (E e : items) {
				Map<EnumTableColumn<?>, String> row = new HashMap<EnumTableColumn<?>, String>();
				for (EnumTableColumn<E> column : header) {
					row.put(column, format(column.getColumnValue(e), Settings.get().getExportSettings().getDecimalSeparator(), Settings.get().getExportSettings().isHtmlStyled()));
				}
				rows.add(row);
			}
			//Save data
			saved = HtmlWriter.save(Settings.get().getExportSettings().getFilename(toolName),
					rows,
					new ArrayList<EnumTableColumn<?>>(header),
					jHtmlIGB.isSelected() ? new ArrayList<Object>(items) : null,
					Settings.get().getExportSettings().isHtmlStyled(),
					Settings.get().getExportSettings().getHtmlRepeatHeader(),
					toolName.equals(TreeTab.NAME));
		} else if (jSql.isSelected()) {
	//SQL
			//Create data
			List<Map<EnumTableColumn<?>, Object>> rows = new ArrayList<Map<EnumTableColumn<?>, Object>>();
			for (E e : items) {
				Map<EnumTableColumn<?>, Object> row = new HashMap<EnumTableColumn<?>, Object>();
				for (EnumTableColumn<E> column : header) {
					row.put(column, column.getColumnValue(e));
				}
				rows.add(row);
			}
			//Save data
			saved = SqlWriter.save(Settings.get().getExportSettings().getFilename(toolName),
					rows,
					new ArrayList<EnumTableColumn<?>>(header),
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

	private class ListenerClass implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ExportAction.OK.name().equals(e.getActionCommand())) {
				save();
			} else if (ExportAction.DEFAULT.name().equals(e.getActionCommand())) {
				resetSettings();
			} else if (ExportAction.CANCEL.name().equals(e.getActionCommand())) {
				setVisible(false);
			} else if (ExportAction.FORMAT_CHANGED.name().equals(e.getActionCommand())) {
				ExportFormat exportFormat = ExportFormat.CSV;
				if (jCsv.isSelected()) {
					exportFormat = ExportFormat.CSV;
				} else if (jHtml.isSelected()) {
					exportFormat = ExportFormat.HTML;
				} else if (jSql.isSelected()) {
					exportFormat = ExportFormat.SQL;
				}
				Settings.get().getExportSettings().setExportFormat(exportFormat);
				cardLayout.show(jCardPanel, exportFormat.name());
				jFileChooser.setExtension(exportFormat.getExtension());
			} else if (ExportAction.FILTER_CHANGED.name().equals(e.getActionCommand())) {
				jFilters.setEnabled(jSavedFilter.isSelected());
			} else if (ExportAction.VIEW_CHANGED.name().equals(e.getActionCommand())) {
				jViews.setEnabled(jViewSaved.isSelected());
				jColumnSelection.setEnabled(jViewSelect.isSelected());
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
				);
		}
	}

	private class TreeMatcher<E> implements Matcher<E> {

		private final EventList<E> eventList;
		private final Set<TreeAsset> parentTree = new HashSet<TreeAsset>();

		public TreeMatcher(EventList<E> eventList) {
			this.eventList = eventList;
			Set<TreeAsset> items = new TreeSet<TreeAsset>(new AssetTreeComparator());
			for (E e : eventList) {
				if (e instanceof TreeAsset) {
					TreeAsset tree = (TreeAsset) e;
					items.add(tree);
					parentTree.addAll(tree.getTree());
				}
			}
			for (TreeAsset treeAsset : parentTree) {
				treeAsset.resetValues();
				if (treeAsset.isItem()) {
					items.add(treeAsset);
				}
			}
			for (TreeAsset treeAsset : items) {
				treeAsset.updateParents();
			}
		}

		@Override
		public boolean matches(E item) { //XXX - Expensive
			if (item instanceof TreeAsset) {
				TreeAsset treeAsset = (TreeAsset) item;
				if (treeAsset.isParent()) {
					return parentTree.contains(treeAsset);
				}
			}
			return eventList.contains(item);
		}
		
	}
}

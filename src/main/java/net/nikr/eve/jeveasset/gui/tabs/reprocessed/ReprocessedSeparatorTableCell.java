/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

package net.nikr.eve.jeveasset.gui.tabs.reprocessed;

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.SeparatorList.Separator;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ColorEntry;
import net.nikr.eve.jeveasset.data.settings.ColorSettings;
import net.nikr.eve.jeveasset.gui.shared.DocumentFactory;
import net.nikr.eve.jeveasset.gui.shared.Formatter;
import net.nikr.eve.jeveasset.gui.shared.components.JIntegerField;
import net.nikr.eve.jeveasset.gui.shared.table.SeparatorTableCell;
import net.nikr.eve.jeveasset.i18n.TabsReprocessed;

/**
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class ReprocessedSeparatorTableCell extends SeparatorTableCell<ReprocessedInterface> {

	public enum ReprocessedCellAction {
		REMOVE, UPDATE_COUNT
	}

	private final JLabel jColor;
	private final JButton jRemove;
	private final JButton jRemoveTotal;
	private final JIntegerField jCount;
	private final JLabel jName;
	private final JLabel jSellPriceLabel;
	private final JLabel jSellPrice;
	private final JLabel jBatchSizeLabel;
	private final JLabel jBatchSize;
	private final JLabel jValueLabel;
	private final JLabel jValue;
	private final Program program;

	public ReprocessedSeparatorTableCell(final Program program, final JTable jTable, final SeparatorList<ReprocessedInterface> separatorList, final ActionListener actionListener) {
		super(jTable, separatorList);
		this.program = program;

		ListenerClass listener = new ListenerClass();
		addCellEditorListener(listener);

		jColor = new JLabel();
		jColor.setOpaque(true);
		jColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		jRemove = new JButton(TabsReprocessed.get().remove());
		jRemove.setOpaque(false);
		jRemove.setActionCommand(ReprocessedCellAction.REMOVE.name());
		jRemove.addActionListener(actionListener);

		jRemoveTotal = new JButton(TabsReprocessed.get().remove());
		jRemoveTotal.setOpaque(false);
		jRemoveTotal.setEnabled(false);
		jRemoveTotal.setVisible(false);

		jCount = new JIntegerField("1", DocumentFactory.ValueFlag.POSITIVE_AND_NOT_ZERO);
		jCount.setAutoSelectAll(true);
		jCount.setHorizontalAlignment(JTextField.RIGHT);
		jCount.setActionCommand(ReprocessedCellAction.UPDATE_COUNT.name());
		jCount.addActionListener(listener);
		jCount.addKeyListener(listener);

		JLabel jCountLabel = new JLabel(TabsReprocessed.get().multiplierSign());

		jName = new JLabel();
		Font font = jName.getFont();
		jName.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 1));

		jSellPriceLabel = new JLabel(TabsReprocessed.get().price());
		jSellPriceLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		jSellPrice = new JLabel();

		jBatchSizeLabel = new JLabel(TabsReprocessed.get().batch());
		jBatchSizeLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		jBatchSize = new JLabel();

		jValueLabel = new JLabel(TabsReprocessed.get().value());
		jValueLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
		jValue = new JLabel();

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(jExpand)
				.addGap(5)
				.addComponent(jColor, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
				.addGap(10)
				.addComponent(jRemove, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				.addComponent(jRemoveTotal, Program.getButtonsWidth(), Program.getButtonsWidth(), Program.getButtonsWidth())
				.addGap(10)
				.addComponent(jCount, 50, 50, 50)
				.addComponent(jCountLabel)
				.addGap(10)
				.addComponent(jName, 220, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(10)
				.addComponent(jSellPriceLabel)
				.addGap(5)
				.addComponent(jSellPrice)
				.addGap(10)
				.addComponent(jValueLabel)
				.addGap(5)
				.addComponent(jValue)
				.addGap(10)
				.addComponent(jBatchSizeLabel)
				.addGap(5)
				.addComponent(jBatchSize)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGap(2)
				.addGroup(layout.createParallelGroup()
					.addComponent(jExpand, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addGroup(layout.createSequentialGroup()
						.addGap(3)
						.addComponent(jColor, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6, Program.getButtonsHeight() - 6)
					)
					.addComponent(jRemove, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jRemoveTotal, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jName, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCount, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jCountLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSellPriceLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jSellPrice, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jValueLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jValue, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBatchSizeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jBatchSize, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(2)
		);
	}

	@Override
	protected void configure(Separator<?> separator) {
		ReprocessedInterface material = (ReprocessedInterface) separator.first();
		if (material == null) { // handle 'late' rendering calls after this separator is invalid
			return;
		}
		jRemove.setVisible(!material.getTotal().isGrandTotal());
		jRemoveTotal.setVisible(material.getTotal().isGrandTotal());
		jName.setText(material.getTotal().getTypeName());
		//Count
		jCount.setText(String.valueOf(material.getTotal().getCount()));
		//Sell Price
		jSellPriceLabel.setVisible(!material.isGrandTotal());
		jSellPrice.setVisible(!material.isGrandTotal());
		jSellPrice.setText(Formatter.iskFormat(material.getTotal().getSellPrice()));
		//Value
		if (material.getTotal().getValue() != material.getTotal().getSellPrice()) {
			jValue.setText(Formatter.iskFormat(material.getTotal().getValue()));
			jValueLabel.setVisible(true);
			jValue.setVisible(true);
		} else {
			jValueLabel.setVisible(false);
			jValue.setVisible(false);
		}
		//Portion Size
		if (material.getPortionSize() > 1) {
			jBatchSize.setText(Formatter.longFormat(material.getPortionSize()));
			jBatchSizeLabel.setVisible(true);
			jBatchSize.setVisible(true);
		} else {
			jBatchSizeLabel.setVisible(false);
			jBatchSize.setVisible(false);
		}
		//Color
		if (material.getTotal().isSell()) {
			ColorSettings.config(jColor, ColorEntry.REPROCESSED_SELL);
		} else if (material.getTotal().isReprocess()) {
			ColorSettings.config(jColor, ColorEntry.REPROCESSED_REPROCESS);
		} else {
			ColorSettings.config(jColor, ColorEntry.REPROCESSED_EQUAL);
		}
	}

	private class ListenerClass implements ActionListener, CellEditorListener, KeyListener {

		private boolean update = true;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (ReprocessedCellAction.UPDATE_COUNT.name().equals(e.getActionCommand())) {
				stopCellEditing();
			}
		}

		@Override
		public void editingStopped(ChangeEvent e) {
			saveMultiplier();
		}

		@Override
		public void editingCanceled(ChangeEvent e) {
			saveMultiplier();
		}

		@Override
		public void keyTyped(KeyEvent e) { }

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				update = false;
				stopCellEditing();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) { }

		private void saveMultiplier() {
			if (!update) {
				update = true;
				return;
			}
			ReprocessedInterface reprocessed = (ReprocessedInterface) currentSeparator.first();
			if (reprocessed == null) { // handle 'late' rendering calls after this separator is invalid
				return;
			}
			program.getReprocessedTab().setCount(reprocessed, jCount);
		}

	}
}

/*
 * Copyright 2009, 2010, 2011 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.JNumberField;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;


public class WindowSettingsPanel extends JSettingsPanel implements ActionListener {

	private final static String ACTION_AUTO_SAVE = "ACTION_AUTO_SAVE";
	private final static String ACTION_FIXED = "ACTION_FIXED";
	private final static String ACTION_DEFAULT = "ACTION_DEFAULT";

	public final static int SAVE_ON_EXIT = 1;
	public final static int FLAG_SAVE_MAXIMIZED = 2;
	public final static int FLAG_SAVE_FIXED = 3;

	JRadioButton jAutoSave;
	JRadioButton jFixed;
	JTextField jWidth;
	JTextField jHeight;
	JTextField jX;
	JTextField jY;
	JCheckBox jMaximized;
	JButton jDefault;

	public WindowSettingsPanel(Program program, SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().windowWindow(), Images.SETTINGS_WINDOW.getIcon());

		jAutoSave = new JRadioButton(DialoguesSettings.get().windowSaveOnExit());
		jAutoSave.setActionCommand(ACTION_AUTO_SAVE);
		jAutoSave.addActionListener(this);

		jFixed = new JRadioButton(DialoguesSettings.get().windowFixed());
		jFixed.setActionCommand(ACTION_FIXED);
		jFixed.addActionListener(this);

		JLabel jWidthLabel = new JLabel(DialoguesSettings.get().windowWidth());
		jWidth = new JNumberField();

		JLabel jHeightLabel = new JLabel(DialoguesSettings.get().windowHeight());
		jHeight = new JNumberField();

		JLabel jXLabel = new JLabel(DialoguesSettings.get().windowX());
		jX = new JNumberField();

		JLabel jYLabel = new JLabel(DialoguesSettings.get().windowY());
		jY = new JNumberField();

		JLabel jMaximizedLabel = new JLabel(DialoguesSettings.get().windowMaximised());
		jMaximized = new JCheckBox();

		ButtonGroup group = new ButtonGroup();
		group.add(jAutoSave);
		group.add(jFixed);


		jDefault = new JButton("Default");
		jDefault.setActionCommand(ACTION_DEFAULT);
		jDefault.addActionListener(this);


		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(jAutoSave)
				.addComponent(jFixed)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jWidthLabel)
						.addComponent(jHeightLabel)
						.addComponent(jMaximizedLabel)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jWidth)
						.addComponent(jHeight)
						.addComponent(jMaximized)
					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jXLabel)
						.addComponent(jYLabel)


					)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jX)
						.addComponent(jY)
						.addComponent(jDefault)
					)
				)
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jAutoSave, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addComponent(jFixed, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jWidthLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jWidth, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jXLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jX, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jHeightLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jHeight, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jYLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jY, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(jMaximizedLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jMaximized, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jDefault, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	private void setValuesFromSettings(){
		jWidth.setText( String.valueOf(program.getMainWindow().getFrame().getSize().width));
		jHeight.setText( String.valueOf(program.getMainWindow().getFrame().getSize().height));
		jX.setText( String.valueOf(program.getMainWindow().getFrame().getLocation().x));
		jY.setText( String.valueOf(program.getMainWindow().getFrame().getLocation().y));
		jMaximized.setSelected( program.getMainWindow().getFrame().getState() == JFrame.MAXIMIZED_BOTH );
	}

	private void setValuesFromWindow(){
		jWidth.setText( String.valueOf(program.getSettings().getWindowSize().width));
		jHeight.setText( String.valueOf(program.getSettings().getWindowSize().height));
		jX.setText( String.valueOf(program.getSettings().getWindowLocation().x));
		jY.setText( String.valueOf(program.getSettings().getWindowLocation().y));
		jMaximized.setSelected( program.getSettings().isWindowMaximized() );
	}

	private int validate(String s){
		int n;
		try {
			n = Integer.valueOf(s);
		} catch(NumberFormatException ex) {
			n = 0;
		}
		if (n < 0) n = 0;
		return n;
	}

	private void setInputEnabled(boolean b){
		jWidth.setEnabled(b);
		jHeight.setEnabled(b);
		jX.setEnabled(b);
		jY.setEnabled(b);
		jMaximized.setEnabled(b);
		jDefault.setEnabled(b);
	}

	@Override
	public boolean save() {
		if (jAutoSave.isSelected()){
			program.getSettings().setWindowAutoSave(true);
		} else {
			int width = validate(jWidth.getText());
			int height = validate(jHeight.getText());
			int x = validate(jX.getText());
			int y = validate(jY.getText());
			boolean maximized = jMaximized.isSelected();
			Dimension d = new Dimension(width, height);
			Point p = new Point(x, y);
			boolean first = program.getSettings().isWindowAutoSave();
			program.getSettings().setWindowAutoSave(false);

			//if changed...
			if ((program.getSettings().getWindowSize().height != d.height) 
					|| (program.getSettings().getWindowSize().width != d.width)
					|| (program.getSettings().getWindowLocation().x != p.x)
					|| (program.getSettings().getWindowLocation().y != p.y)
					|| (program.getSettings().isWindowMaximized() != maximized)
					|| first){
				program.getSettings().setWindowSize( d );
				program.getSettings().setWindowLocation( p );
				program.getSettings().setWindowMaximized(maximized);
				program.getMainWindow().setSizeAndLocation(d, p, maximized);
			}
		}
		return false;
	}

	@Override
	public void load() {
		if (program.getSettings().isWindowAutoSave()){
			jAutoSave.setSelected(true);
			setValuesFromSettings();
			setInputEnabled(false);
		} else {
			jFixed.setSelected(true);
			setValuesFromWindow();
			setInputEnabled(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_AUTO_SAVE.equals(e.getActionCommand())){
			setInputEnabled(false);
		}
		if (ACTION_FIXED.equals(e.getActionCommand())){
			setInputEnabled(true);
		}
		if (ACTION_DEFAULT.equals(e.getActionCommand())){
			jWidth.setText("800");
			jHeight.setText("600");
			jX.setText("0");
			jY.setText("0");
		}
	}

}

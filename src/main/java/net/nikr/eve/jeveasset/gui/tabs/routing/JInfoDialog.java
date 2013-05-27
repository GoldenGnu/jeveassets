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
package net.nikr.eve.jeveasset.gui.tabs.routing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JDialogCentered;
import net.nikr.eve.jeveasset.i18n.TabsRouting;


public class JInfoDialog extends JDialogCentered {

	private JTextArea jDescription;
	private JButton jOK;

	public JInfoDialog(Program program, String text) {
		super(program, TabsRouting.get().algorithm(), Images.TOOL_ROUTING.getImage());
		jOK = new JButton(TabsRouting.get().ok());
		jOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		jDescription = new JTextArea();
		jDescription.setEditable(false);
		jDescription.setWrapStyleWord(true);
		jDescription.setLineWrap(true);
		jDescription.setFont(jPanel.getFont());
		jDescription.setText(text);
		jDescription.setCaretPosition(0);

		JScrollPane jDescriptionScroll = new JScrollPane(jDescription);

		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(jDescriptionScroll, 400, 400, 400)
				.addComponent(jOK)
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(jDescriptionScroll, 200, 200, 200)
				.addComponent(jOK)
		);
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
	protected void windowShown() {
		
	}

	@Override
	protected void save() {
		this.setVisible(false);
	}
	
}

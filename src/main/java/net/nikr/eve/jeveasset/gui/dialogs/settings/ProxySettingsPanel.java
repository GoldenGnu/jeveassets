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

// <editor-fold defaultstate="collapsed" desc="imports">
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.net.Proxy;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;

// </editor-fold>
/**
 *
 * @author Flaming Candle
 */
public class ProxySettingsPanel extends JSettingsPanel {

	public final static String ACTION_CANCEL = "ACTION_CANCEL";
	public final static String ACTION_SAVE = "ACTION_SAVE";
	private JComboBox proxyTypeField;
	private JTextField proxyAddressField;
	private JSpinner proxyPortField;
	private JCheckBox enableApiProxy;
	private JTextField apiProxyField;

	public ProxySettingsPanel(Program program, SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().proxy(), Images.SETTINGS_PROXY.getIcon());

		JLabel proxyTypeLabel = new JLabel(DialoguesSettings.get().type());
		JLabel proxyAddressLabel = new JLabel(DialoguesSettings.get().address());
		JLabel proxyPortLabel = new JLabel(DialoguesSettings.get().port());

		JLabel enableApiLabel = new JLabel(DialoguesSettings.get().enable());
		JLabel apiProxyLabel = new JLabel(DialoguesSettings.get().apiProxy());

		proxyTypeField = new JComboBox(new DefaultComboBoxModel(Proxy.Type.values()));
		proxyTypeField.setEnabled(true);
		proxyTypeField.setPreferredSize(new Dimension(200, (int) proxyTypeField.getPreferredSize().getHeight()));
		proxyTypeField.setSelectedItem(Proxy.Type.DIRECT);
		proxyTypeField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean enabled = !proxyTypeField.getSelectedItem().equals(Proxy.Type.DIRECT);
				proxyAddressField.setEnabled(enabled);
				proxyPortField.setEnabled(enabled);
			}
		});

		proxyAddressField = new JTextField();
		proxyAddressField.setEnabled(false);

		proxyPortField = new JSpinner(new SpinnerNumberModel(0, 0, 65536, 1));
		proxyPortField.setEnabled(false);

		enableApiProxy = new JCheckBox();
		enableApiProxy.setSelected(false);
		enableApiProxy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				apiProxyField.setEnabled(enableApiProxy.isSelected());
			}
		});

		apiProxyField = new JTextField();

		// note: the layout is defined in the super class.
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(proxyTypeLabel)
						.addComponent(proxyAddressLabel)
						.addComponent(proxyPortLabel)
						.addComponent(enableApiLabel)
						.addComponent(apiProxyLabel)
					)
					.addGap(10)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(proxyTypeField)
						.addComponent(proxyAddressField)
						.addComponent(proxyPortField)
						.addComponent(enableApiProxy)
						.addComponent(apiProxyField)
					)
				)
			);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(proxyTypeLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(proxyTypeField, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(proxyAddressLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(proxyAddressField, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(proxyPortLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(proxyPortField, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(enableApiLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(enableApiProxy, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(apiProxyLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(apiProxyField, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
			);
	}

	private void enableProxy(boolean b) {
		proxyAddressField.setEnabled(b);
		proxyPortField.setEnabled(b);
	}

	@Override
	public boolean save() {
		if (proxyTypeField.getSelectedItem() != Proxy.Type.DIRECT) {
			try {
				program.getSettings().setProxy(
								proxyAddressField.getText(), (Integer) proxyPortField.getValue(), (Proxy.Type) proxyTypeField.getSelectedItem());
			} catch (IllegalArgumentException iae) {
				JOptionPane.showMessageDialog(parent, "There was an error with the proxy:\n" + iae.getMessage(), "Proxy Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			program.getSettings().setProxy(null);
		}

		// save the API proxy.
		if (enableApiProxy.isSelected()) {
			program.getSettings().setApiProxy(apiProxyField.getText());
		} else {
			program.getSettings().setApiProxy(null);
		}
		return false;
	}

	@Override
	public void load() {
		Proxy proxy = program.getSettings().getProxy();
		if (proxy.type().equals(Proxy.Type.DIRECT)) {
			proxyTypeField.setSelectedItem(Proxy.Type.DIRECT);
		} else {
			proxyTypeField.setSelectedItem(proxy.type());
			if (proxy.address() instanceof InetSocketAddress) {
				InetSocketAddress addr = (InetSocketAddress) proxy.address();
				proxyAddressField.setText(String.valueOf(addr.getHostName()));
				proxyPortField.setValue(addr.getPort());
			}
			enableProxy(true);
		}

		// set the API Proxy fields
		String apiProxy = program.getSettings().getApiProxy();
		if (apiProxy == null) {
			enableApiProxy.setSelected(false);
		} else {
			enableApiProxy.setSelected(true);
			apiProxyField.setText(apiProxy);
		}
		apiProxyField.setEnabled(enableApiProxy.isSelected());
	}
}

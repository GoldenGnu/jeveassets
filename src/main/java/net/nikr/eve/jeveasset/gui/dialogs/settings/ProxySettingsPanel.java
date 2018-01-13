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
package net.nikr.eve.jeveasset.gui.dialogs.settings;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Proxy;
import javax.swing.*;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.ProxyData;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.i18n.DialoguesSettings;

/**
 *
 * @author Flaming Candle
 */
public class ProxySettingsPanel extends JSettingsPanel {

	private final JComboBox<Proxy.Type> jProxyType;
	private final JTextField jProxyAddress;
	private final JSpinner jProxyPort;
	private final JCheckBox jEnableAuth;
	private final JTextField jProxyUsername;
	private final JPasswordField jProxyPassword;
	private final JCheckBox jEnableApiProxy;
	private final JTextField jApiProxy;

	public ProxySettingsPanel(final Program program, final SettingsDialog optionsDialog) {
		super(program, optionsDialog, DialoguesSettings.get().proxy(), Images.SETTINGS_PROXY.getIcon());

		JLabel jProxyTypeLabel = new JLabel(DialoguesSettings.get().type());
		jProxyType = new JComboBox<Proxy.Type>(new ListComboBoxModel<Proxy.Type>(Proxy.Type.values()));
		jProxyType.setEnabled(true);
		jProxyType.setPreferredSize(new Dimension(200, (int) jProxyType.getPreferredSize().getHeight()));
		jProxyType.setSelectedItem(Proxy.Type.DIRECT);
		jProxyType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				enableProxy(jProxyType.getSelectedItem() != Proxy.Type.DIRECT);
			}
		});

		JLabel jProxyAddressLabel = new JLabel(DialoguesSettings.get().address());
		jProxyAddress = new JTextField();
		jProxyAddress.setEnabled(false);

		JLabel jProxyPortLabel = new JLabel(DialoguesSettings.get().port());
		jProxyPort = new JSpinner(new SpinnerNumberModel(0, 0, 65536, 1));
		jProxyPort.setEnabled(false);

		jEnableAuth = new JCheckBox(DialoguesSettings.get().auth());
		jEnableAuth.setSelected(false);
		jEnableAuth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jProxyUsername.setEnabled(jEnableAuth.isSelected());
				jProxyPassword.setEnabled(jEnableAuth.isSelected());
			}
		});

		JLabel jProxyUsernameLabel = new JLabel(DialoguesSettings.get().username());
		jProxyUsername = new JTextField();
		jProxyUsername.setEnabled(false);

		JLabel jProxyPasswordLabel = new JLabel(DialoguesSettings.get().password());
		jProxyPassword = new JPasswordField();
		jProxyPassword.setEnabled(false);

		jEnableApiProxy = new JCheckBox(DialoguesSettings.get().enable());
		jEnableApiProxy.setSelected(false);
		jEnableApiProxy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				jApiProxy.setEnabled(jEnableApiProxy.isSelected());
			}
		});

		JLabel jApiProxyLabel = new JLabel(DialoguesSettings.get().apiProxy());
		jApiProxy = new JTextField();

		// note: the layout is defined in the super class.
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(jProxyTypeLabel)
						.addComponent(jProxyAddressLabel)
						.addComponent(jProxyPortLabel)
						.addComponent(jProxyUsernameLabel)
						.addComponent(jProxyPasswordLabel)
						.addComponent(jApiProxyLabel)
					)
					.addGap(10)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jProxyType)
						.addComponent(jProxyAddress)
						.addComponent(jProxyPort)
						.addComponent(jEnableAuth)
						.addComponent(jProxyUsername)
						.addComponent(jProxyPassword)
						.addComponent(jEnableApiProxy)
						.addComponent(jApiProxy)
					)
				)
			);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jProxyTypeLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jProxyType, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jProxyAddressLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jProxyAddress, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jProxyPortLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jProxyPort, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jEnableAuth, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jProxyUsernameLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jProxyUsername, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jProxyPasswordLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jProxyPassword, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jEnableApiProxy, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jApiProxyLabel, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
					.addComponent(jApiProxy, Program.getButtonsHeight(), Program.getButtonsHeight(), Program.getButtonsHeight())
				)
			);
	}

	private void enableProxy(final boolean b) {
		jProxyAddress.setEnabled(b);
		jProxyPort.setEnabled(b);
		if (b) {
			jEnableAuth.setEnabled(true);
			jEnableAuth.setSelected(false);
			jProxyUsername.setEnabled(false);
			jProxyPassword.setEnabled(false);
		} else {
			jEnableAuth.setEnabled(false);
			jEnableAuth.setSelected(false);
			jProxyUsername.setEnabled(false);
			jProxyPassword.setEnabled(false);
		}
	}

	@Override
	public boolean save() {
		//Save proxy data
		if (jProxyType.getSelectedItem() != Proxy.Type.DIRECT) {
			String address = jProxyAddress.getText();
			int port = (int) jProxyPort.getValue();
			Proxy.Type type = (Proxy.Type) jProxyType.getSelectedItem();
			String username;
			String password;
			if (jEnableAuth.isSelected()) {
				username = jProxyUsername.getText();
				char[] passwordChars = jProxyPassword.getPassword();
				password = String.valueOf(passwordChars);
			} else {
				username = null;
				password = null;
			}
			Settings.get().setProxyData(new ProxyData(address, type, port, username, password));
		} else {
			Settings.get().setProxyData(new ProxyData());
		}

		// save the API proxy.
		if (jEnableApiProxy.isSelected()) {
			Settings.get().setApiProxy(jApiProxy.getText());
		} else {
			Settings.get().setApiProxy(null);
		}
		return false;
	}

	@Override
	public void load() {
		ProxyData proxyData = Settings.get().getProxyData();
		if (proxyData.getType() == Proxy.Type.DIRECT) {
			jProxyType.setSelectedItem(Proxy.Type.DIRECT);
			jProxyAddress.setText("");
			jProxyPort.setValue(0);
			jProxyUsername.setText("");
			jProxyPassword.setText("");
			enableProxy(false);
		} else {
			enableProxy(true);
			jProxyType.setSelectedItem(proxyData.getType());
			jProxyAddress.setText(proxyData.getAddress());
			jProxyPort.setValue(proxyData.getPort());
			if (proxyData.isAuth()) {
				jEnableAuth.setSelected(true);
				jProxyUsername.setEnabled(true);
				jProxyUsername.setText(proxyData.getUsername());
				jProxyPassword.setEnabled(true);
				jProxyPassword.setText(proxyData.getPassword());
			} else {
				jEnableAuth.setSelected(false);
				jProxyUsername.setEnabled(false);
				jProxyUsername.setText("");
				jProxyPassword.setEnabled(false);
				jProxyPassword.setText("");
			}
		}

		// set the API Proxy fields
		String apiProxy = Settings.get().getApiProxy();
		if (apiProxy == null) {
			jEnableApiProxy.setSelected(false);
		} else {
			jEnableApiProxy.setSelected(true);
			jApiProxy.setText(apiProxy);
		}
		jApiProxy.setEnabled(jEnableApiProxy.isSelected());
	}
}

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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.gui.shared.Formater;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.shared.UpdateTask;
import net.nikr.eve.jeveasset.io.eveapi.AccountBalanceGetter;
import net.nikr.eve.jeveasset.io.eveapi.AssetsGetter;
import net.nikr.eve.jeveasset.io.eveapi.ConquerableStationsGetter;
import net.nikr.eve.jeveasset.io.eveapi.HumansGetter;
import net.nikr.eve.jeveasset.io.eveapi.IndustryJobsGetter;
import net.nikr.eve.jeveasset.io.eveapi.MarketOrdersGetter;


public class UpdateDialog extends JDialogCentered implements ActionListener {


	public final static String ACTION_CANCEL = "ACTION_CANCEL";
	public final static String ACTION_UPDATE = "ACTION_UPDATE";

	private JCheckBox jMarketOrders;
	private JLabel jMarketOrdersUpdate;
	private JCheckBox jIndustryJobs;
	private JLabel jIndustryJobsUpdate;
	private JCheckBox jAccounts;
	private JLabel jAccountsUpdate;
	private JCheckBox jAccountBalance;
	private JLabel jAccountBalanceUpdate;
	private JCheckBox jAssets;
	private JLabel jAssetsUpdate;
	private JCheckBox jPriceData;
	private JLabel jPriceDataUpdate;
	private JButton jUpdate;
	private JButton jCancel;


	private UpdateSelectedDialog updateSelectedDialog;


	public UpdateDialog(Program program, Image image) {
		super(program, "Update", image);

		JLabel jUpdateLabel = new JLabel("Update:");
		jMarketOrders = new JCheckBox("Market Orders");
		jIndustryJobs = new JCheckBox("Industry Jobs");
		jAccounts = new JCheckBox("Accounts");
		jAccountBalance = new JCheckBox("Account Balance");
		jAssets = new JCheckBox("Assets");
		jPriceData = new JCheckBox("Price Data");

		JLabel jNextUpdateLabel = new JLabel("Next Update:");
		jMarketOrdersUpdate = new JLabel();
		jIndustryJobsUpdate = new JLabel();
		jAccountsUpdate = new JLabel();
		jAccountBalanceUpdate = new JLabel();
		jAssetsUpdate = new JLabel();
		jPriceDataUpdate = new JLabel();

		jUpdate = new JButton("Update");
		jUpdate.setActionCommand(ACTION_UPDATE);
		jUpdate.addActionListener(this);

		jCancel = new JButton("Cancel");
		jCancel.setActionCommand(ACTION_CANCEL);
		jCancel.addActionListener(this);
		
		

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(jUpdateLabel, 110, 110, 110)
						.addComponent(jMarketOrders)
						.addComponent(jIndustryJobs)
						.addComponent(jAccounts)
						.addComponent(jAccountBalance)
						.addComponent(jAssets)
						.addComponent(jPriceData)
					)
					.addGroup(layout.createParallelGroup()
						.addComponent(jNextUpdateLabel, 110, 110, 110)
						.addComponent(jMarketOrdersUpdate)
						.addComponent(jIndustryJobsUpdate)
						.addComponent(jAccountsUpdate)
						.addComponent(jAccountBalanceUpdate)
						.addComponent(jAssetsUpdate)
						.addComponent(jPriceDataUpdate)

					)
				)
				.addGroup(Alignment.TRAILING ,layout.createSequentialGroup()
					.addComponent(jUpdate, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
					.addComponent(jCancel, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
				)

		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jUpdateLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jNextUpdateLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jMarketOrders, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jMarketOrdersUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jIndustryJobs, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jIndustryJobsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAccounts, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAccountsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAccountBalance, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAccountBalanceUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jAssets, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jAssetsUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGroup(layout.createParallelGroup()
					.addComponent(jPriceData, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jPriceDataUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addGap(30)
				.addGroup(layout.createParallelGroup()
					.addComponent(jUpdate, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCancel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
		);
	}

	public void update(){
		List<Account> accounts = program.getSettings().getAccounts();
		Date accountsNextUpdate = null;
		Date industryJobsNextUpdate = null;
		Date marketOrdersNextUpdate = null;
		Date assetsNextUpdate = null;
		Date accountBalanceNextUpdate = null;
		Date priceDataNextUpdate = program.getSettings().getPriceDataNextUpdate();
		for (int a = 0; a < accounts.size(); a++){
			Account account = accounts.get(a);
			//Account
			accountsNextUpdate = nextUpdate(accountsNextUpdate, account.getCharactersNextUpdate());
			List<Human> humans = account.getHumans();
			for (int b = 0; b < humans.size(); b++){
				Human human = humans.get(b);
				if (human.isShowAssets()){
					industryJobsNextUpdate = nextUpdate(industryJobsNextUpdate, human.getIndustryJobsNextUpdate());
					marketOrdersNextUpdate = nextUpdate(marketOrdersNextUpdate, human.getMarketOrdersNextUpdate());
					assetsNextUpdate = nextUpdate(assetsNextUpdate, human.getAssetNextUpdate());
					accountBalanceNextUpdate = nextUpdate(accountBalanceNextUpdate, human.getBalanceNextUpdate());
				}
			}
		}
		setUpdateLabel(jMarketOrdersUpdate, jMarketOrders, marketOrdersNextUpdate);
		setUpdateLabel(jIndustryJobsUpdate, jIndustryJobs, industryJobsNextUpdate);
		setUpdateLabel(jAccountsUpdate, jAccounts, accountsNextUpdate);
		setUpdateLabel(jAccountBalanceUpdate, jAccountBalance, accountBalanceNextUpdate);
		setUpdateLabel(jAssetsUpdate, jAssets, assetsNextUpdate);
		setUpdateLabel(jPriceDataUpdate, jPriceData, priceDataNextUpdate, false);
		jUpdate.setEnabled(false);
		setUpdatableButton(marketOrdersNextUpdate);
		setUpdatableButton(industryJobsNextUpdate);
		setUpdatableButton(accountsNextUpdate);
		setUpdatableButton(accountBalanceNextUpdate);
		setUpdatableButton(assetsNextUpdate);
		setUpdatableButton(priceDataNextUpdate, false);
	}

	private void setUpdateLabel(JLabel jLabel, JCheckBox jCheckBox, Date nextUpdate){
		this.setUpdateLabel(jLabel, jCheckBox, nextUpdate, true);
	}

	private void setUpdateLabel(JLabel jLabel, JCheckBox jCheckBox, Date nextUpdate, boolean ignoreOnProxy){
		if (nextUpdate == null) nextUpdate = Settings.getGmtNow();
		if (program.getSettings().isUpdatable(nextUpdate, ignoreOnProxy)){
			jLabel.setText("-");
			jLabel.setEnabled(false);
			jCheckBox.setSelected(true);
			jCheckBox.setEnabled(true);
		} else {
			jLabel.setText(Formater.weekdayAndTime(nextUpdate)+" GMT");
			jLabel.setEnabled(true);
			jCheckBox.setSelected(false);
			jCheckBox.setEnabled(false);
			
		}
	}
	
	private void setUpdatableButton(Date nextUpdate){
		setUpdatableButton(nextUpdate, true);
	}

	private void setUpdatableButton(Date nextUpdate, boolean ignoreOnProxy){
		if (nextUpdate == null) nextUpdate = Settings.getGmtNow();
		if (program.getSettings().isUpdatable(nextUpdate, ignoreOnProxy)){
			jUpdate.setEnabled(true);
		}
	}

	private Date nextUpdate(Date nextUpdate, Date thisUpdate){
		if (nextUpdate == null){
				nextUpdate = thisUpdate;
		}
		if (thisUpdate.before(nextUpdate)){
			nextUpdate = thisUpdate;
		}
		return nextUpdate;
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jUpdate;
	}

	@Override
	protected JButton getDefaultButton() {
		return jUpdate;
	}

	@Override
	protected void windowShown() {
		update();
	}

	@Override
	protected void windowActivated() {

	}

	@Override
	protected void save() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_UPDATE.equals(e.getActionCommand())){
			this.setVisible(false);
			List<UpdateTask> updateTasks = new ArrayList<UpdateTask>();
			if (jMarketOrders.isSelected()){
				updateTasks.add( new MarketOrdersTask() );
			}
			if (jIndustryJobs.isSelected()){
				updateTasks.add( new IndustryJobsTask() );
			}
			if (jAccounts.isSelected()){
				updateTasks.add( new AccountsTask() );
			}
			if (jAccountBalance.isSelected()){
				updateTasks.add( new BalanceTask() );
			}
			if (jAssets.isSelected()){
				updateTasks.add( new AssetsTask() );
			}
			if (jPriceData.isSelected()
					|| jMarketOrders.isSelected()
					|| jIndustryJobs.isSelected()
					|| jAssets.isSelected()
					){
				updateTasks.add( new PriceDataTask(jAssets.isSelected(), program) );
			}
			if (!updateTasks.isEmpty())
				updateSelectedDialog = new UpdateSelectedDialog(program, updateTasks);
		}
		if (ACTION_CANCEL.equals(e.getActionCommand())){
			setVisible(false);
		}
	}

	public class AccountsTask extends UpdateTask {

		public AccountsTask() {
			super("Accounts");
		}

		@Override
		public void update() throws Throwable {
			HumansGetter humansGetter = new HumansGetter();
			humansGetter.load(this, program.getSettings().isForceUpdate(), program.getSettings().getAccounts());
		}
	}

	public class AssetsTask extends UpdateTask {

		public AssetsTask() {
			super("Assets");
		}

		@Override
		public void update() throws Throwable {
			ConquerableStationsGetter conquerableStationsGetter = new ConquerableStationsGetter();
			conquerableStationsGetter.load(program.getSettings());

			AssetsGetter assetsGetter = new AssetsGetter();
			assetsGetter.load(this, program.getSettings());
		}
	}

	public class BalanceTask extends UpdateTask {

		public BalanceTask() {
			super("Balance");
		}

		@Override
		public void update() throws Throwable {
			AccountBalanceGetter accountBalanceGetter = new AccountBalanceGetter();
			accountBalanceGetter.load(this, program.getSettings().isForceUpdate(), program.getSettings().getAccounts());
		}
	}

	public class IndustryJobsTask extends UpdateTask {

		public IndustryJobsTask() {
			super("Industry Jobs");
		}

		@Override
		public void update() throws Throwable {
			IndustryJobsGetter industryJobsGetter = new IndustryJobsGetter();
			industryJobsGetter.load(this, program.getSettings().isForceUpdate(), program.getSettings().getAccounts());
		}
	}

	public class MarketOrdersTask extends UpdateTask {

		public MarketOrdersTask() {
			super("Market Orders");
		}

		@Override
		public void update() throws Throwable {
			MarketOrdersGetter marketOrdersGetter = new MarketOrdersGetter();
			marketOrdersGetter.load(this, program.getSettings().isForceUpdate(), program.getSettings().getAccounts());
		}
	}

	public static class PriceDataTask extends UpdateTask {
		private boolean forceUpdate;
		private Program program;

		public PriceDataTask(boolean forceUpdate, Program program) {
			super("Price Data");
			this.program = program;
			this.forceUpdate = forceUpdate;
		}

		@Override
		public void update() throws Throwable {
			program.getSettings().clearEveAssetList();
			program.getSettings().updatePriceData(this, forceUpdate);
		}
	}
}

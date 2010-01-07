/*
 * Copyright 2009
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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.Account;
import net.nikr.eve.jeveasset.data.Human;
import net.nikr.eve.jeveasset.data.IndustryJob;
import net.nikr.eve.jeveasset.gui.shared.JDialogCentered;
import net.nikr.eve.jeveasset.gui.table.IndustryJobTableFormat;
import net.nikr.eve.jeveasset.gui.table.JAutoColumnTable;
import net.nikr.eve.jeveasset.io.shared.AssetConverter;


public class IndustryJobsDialog extends JDialogCentered implements ActionListener {

	private final static String ACTION_CLOSE = "ACTION_CLOSE";
	private final static String ACTION_SELECTED = "ACTION_SELECTED";

	private JButton jClose;
	private JComboBox jCharacters;
	private JComboBox jState;
	private JComboBox jActivity;
	private JTable jJobs;

	private EventList<IndustryJob> jobsEventList;

	private List<IndustryJob> all;
	private Map<String, List<IndustryJob>> jobs;
	private Vector<String> characters;

	public IndustryJobsDialog(Program program, Image image) {
		super(program, "Industry Jobs", image);

		jClose = new JButton("Close");
		jClose.setActionCommand(ACTION_CLOSE);
		jClose.addActionListener(this);

		jCharacters = new JComboBox();
		jCharacters.setActionCommand(ACTION_SELECTED);
		jCharacters.addActionListener(this);

		jState = new JComboBox();
		jState.setActionCommand(ACTION_SELECTED);
		jState.addActionListener(this);

		jActivity = new JComboBox();
		jActivity.setActionCommand(ACTION_SELECTED);
		jActivity.addActionListener(this);

		JLabel jCharactersLabel = new JLabel("Character");
		JLabel jStateLabel = new JLabel("State");
		JLabel jActivityLabel = new JLabel("Activity");

		//Table format
		IndustryJobTableFormat industryJobsTableFormat = new IndustryJobTableFormat();
		//Backend
		jobsEventList = new BasicEventList<IndustryJob>();
		//For soring the table
		SortedList<IndustryJob> jobsSortedList = new SortedList<IndustryJob>(jobsEventList);
		//Table Model
		EventTableModel jobsTableModel = new EventTableModel<IndustryJob>(jobsSortedList, industryJobsTableFormat);
		//Tables
		jJobs = new JAutoColumnTable(jobsTableModel, industryJobsTableFormat.getColumnNames());
		//Sorters
		TableComparatorChooser.install(jJobs, jobsSortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE, industryJobsTableFormat);
		//Scroll Panels
		JScrollPane jJobsScrollPanel = new JScrollPane(jJobs);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jCharactersLabel)
					.addComponent(jCharacters, 190, 190, 190)
					.addComponent(jActivityLabel)
					.addComponent(jActivity, 190, 190, 190)
					.addComponent(jStateLabel)
					.addComponent(jState, 190, 190, 190)
				)
				.addComponent(jJobsScrollPanel, 700, 700, 700)
				.addComponent(jClose, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH, Program.BUTTONS_WIDTH)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jCharactersLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jCharacters, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jStateLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jState, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jActivityLabel, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
					.addComponent(jActivity, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
				)
				.addComponent(jJobsScrollPanel, 400, 400, 400)
				.addComponent(jClose, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT, Program.BUTTONS_HEIGHT)
		);
	}

	@Override
	protected JComponent getDefaultFocus() {
		return jClose;
	}

	@Override
	protected JButton getDefaultButton() {
		return jClose;
	}

	@Override
	protected void windowShown() {
		characters = new Vector<String>();
		//characters.add("All");
		jobs = new HashMap<String, List<IndustryJob>>();
		all = new Vector<IndustryJob>();
		List<Account> accounts = program.getSettings().getAccounts();
		for (int a = 0; a < accounts.size(); a++){
			List<Human> tempHumans = accounts.get(a).getHumans();
			for (int b = 0; b < tempHumans.size(); b++){
				Human human = tempHumans.get(b);
				if (human.isShowAssets()){
					characters.add(human.getName());
					List<IndustryJob> characterIndustryJobs = AssetConverter.apiIndustryJobsToIndustryJobs(human.getIndustryJobs(), program.getSettings(), human.getName());
					jobs.put(human.getName(), characterIndustryJobs);
					all.addAll(characterIndustryJobs);
					if (human.isUpdateCorporationAssets()){
						String corpKey = "["+human.getCorporation()+"]";
						characters.add(corpKey);
						List<IndustryJob> corporationIndustryJobs = AssetConverter.apiIndustryJobsToIndustryJobs(human.getIndustryJobsCorporation(), program.getSettings(), human.getCorporation());
						jobs.put(corpKey, corporationIndustryJobs);
						all.addAll(corporationIndustryJobs);
					}
				}
			}
		}
		if (!characters.isEmpty()){
			jCharacters.setEnabled(true);
			jJobs.setEnabled(true);
			jActivity.setEnabled(true);
			jState.setEnabled(true);
			Collections.sort(characters);
			characters.add(0, "All");
			jCharacters.setModel( new DefaultComboBoxModel(characters));
			jActivity.setModel( new DefaultComboBoxModel(IndustryJob.ACTIVITIES));
			jState.setModel( new DefaultComboBoxModel(IndustryJob.STATES));
			jCharacters.setSelectedIndex(0);
			jActivity.setSelectedIndex(0);
			jState.setSelectedIndex(0);
		} else {
			jCharacters.setEnabled(false);
			jJobs.setEnabled(false);
			jActivity.setEnabled(false);
			jState.setEnabled(false);
			jCharacters.setModel( new DefaultComboBoxModel());
			jCharacters.getModel().setSelectedItem("No character found");
			jActivity.setModel( new DefaultComboBoxModel());
			jActivity.getModel().setSelectedItem("No character found");
			jState.setModel( new DefaultComboBoxModel());
			jState.getModel().setSelectedItem("No character found");
			jobsEventList.clear();
		}
	}

	@Override
	protected void windowActivated() {
		
	}

	@Override
	protected void save() {}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ACTION_CLOSE.equals(e.getActionCommand())) {
			dialog.setVisible(false);
		}
		if (ACTION_SELECTED.equals(e.getActionCommand())) {
			String selected = (String) jCharacters.getSelectedItem();
			if (characters.size() > 1){
				List<IndustryJob> industryJobsInput;
				List<IndustryJob> industryJobsOutput = new Vector<IndustryJob>();
				//Characters
				if (selected.equals("All")){
					industryJobsInput = all;
				} else {
					industryJobsInput = jobs.get(selected);
				}
				//State
				String sState = (String) jState.getSelectedItem();
				//Activity
				String sActivity = (String) jActivity.getSelectedItem();
				for (int a = 0; a < industryJobsInput.size(); a++){
					IndustryJob industryJob = industryJobsInput.get(a);
					boolean bState = (industryJob.getState().equals(sState) || sState.equals(IndustryJob.ALL));
					boolean bActivity = (industryJob.getActivity().equals(sActivity) || sActivity.equals(IndustryJob.ALL));
					if (bState && bActivity){
						industryJobsOutput.add(industryJob);
					}
				}
				jobsEventList.clear();
				jobsEventList.addAll( industryJobsOutput );
			}
		}
	}


	
}

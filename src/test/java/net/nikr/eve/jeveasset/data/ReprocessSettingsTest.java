/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nikr.eve.jeveasset.data;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Niklas
 */
public class ReprocessSettingsTest {
	
	public ReprocessSettingsTest() {
	}

	@Test
	public void testSomeMethod() {
		ReprocessSettings reprocessSettings;
		//Level 4 Material Skill At 50% Facilities
		reprocessSettings = new ReprocessSettings(50, 5, 5, 4);
		assertEquals(68.31, reprocessSettings.getPercent(), 0);
		//Level 4 Material Skill 52% Reprocessing Array
		reprocessSettings = new ReprocessSettings(52, 5, 5, 4);
		assertEquals(71.0424, reprocessSettings.getPercent(), 0);
		//Max Skill At 50% Facilities
		reprocessSettings = new ReprocessSettings(50, 5, 5, 5);
		assertEquals(69.575, reprocessSettings.getPercent(), 0);
		//Max Skill At 52% Reprocessing Array
		reprocessSettings = new ReprocessSettings(52, 5, 5, 5);
		assertEquals(72.358, reprocessSettings.getPercent(), 0);
	}
	
}

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

package net.nikr.eve.jeveasset.io.eveapi;

import com.beimin.eveapi.utils.stationlist.Parser;
import com.beimin.eveapi.utils.stationlist.Response;
import java.io.IOException;
import java.util.Date;
import net.nikr.eve.jeveasset.data.Settings;
import net.nikr.eve.jeveasset.io.shared.AbstractApiGetter;
import org.xml.sax.SAXException;


public class ConquerableStationsGetter extends AbstractApiGetter<Response> {

	private Settings settings;

	public ConquerableStationsGetter() {
		super("Conquerable Stations");
	}

	public void load(Settings settings){
		this.settings = settings;
		load("jEveAssets");
	}

	@Override
	protected Response getResponse(boolean bCorp) throws IOException, SAXException {
		Parser parser = new Parser();
		return parser.getStationList();
	}

	@Override
	protected Date getNextUpdate() {
		return settings.getConquerableStationsNextUpdate();
	}

	@Override
	protected void setNextUpdate(Date nextUpdate) {
		settings.setConquerableStationsNextUpdate(nextUpdate);
	}

	@Override
	protected void setData(Response response, boolean bCorp) {
		settings.setConquerableStations( response.getStations() );
	}
}

/*
 * Copyright 2009-2025 Contributors (see credits.txt)
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

package net.nikr.eve.jeveasset.io.local;

import java.util.Map;
import net.nikr.eve.jeveasset.data.sde.Agent;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class AgentsReader extends AbstractXmlReader<Boolean> {

	private final Map<Integer, Agent> agents;

	public AgentsReader(Map<Integer, Agent> agents) {
		this.agents = agents;
	}

	public static void load(Map<Integer, Agent> agents) {
		AgentsReader reader = new AgentsReader(agents);
		reader.read("Agents", FileUtil.getPathAgents(), AbstractXmlReader.XmlType.STATIC);
	}

	@Override
	protected Boolean parse(Element element) throws XmlException {
		parseAgents(element);
		return true;
	}

	@Override
	protected Boolean failValue() {
		return false;
	}

	@Override
	protected Boolean doNotExistValue() {
		return false;
	}

	private void parseAgents(final Element element) throws XmlException {
		NodeList nodes = element.getElementsByTagName("row");
		for (int i = 0; i < nodes.getLength(); i++) {
			Element itemElement = (Element) nodes.item(i);
			Agent agent = parseAgent(itemElement);
			agents.put(agent.getAgentID(), agent);
		}
	}

	private Agent parseAgent(final Node node) throws XmlException {
		int agentID = getInt(node, "agentid");
		int corporationID = getInt(node, "corporationid");
		return new Agent(agentID, corporationID);
	}
}

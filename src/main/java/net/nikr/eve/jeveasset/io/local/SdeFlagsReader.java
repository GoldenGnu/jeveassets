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
import net.nikr.eve.jeveasset.io.shared.RawConverter;
import net.nikr.eve.jeveasset.data.sde.ItemFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SdeFlagsReader {
  private static final Logger LOG = LoggerFactory.getLogger(SdeFlagsReader.class);

  public static boolean load(Map<Integer, ItemFlag> flags) {
    // invFlags.jsonl was removed from the new SDE; synthesize from ESI LocationFlag
    // definitions
    int count = 0;
    for (RawConverter.LocationFlag lf : RawConverter.LocationFlag.values()) {
      int id = lf.getID();
      String name = lf.getValue();
      flags.put(id, new ItemFlag(id, name, name));
      count++;
    }
    LOG.info("Synthesized " + count + " flags from ESI definitions");
    return true;
  }
}

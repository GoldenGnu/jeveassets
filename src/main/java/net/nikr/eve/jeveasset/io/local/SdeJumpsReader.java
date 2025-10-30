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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.nikr.eve.jeveasset.data.sde.Jump;
import net.nikr.eve.jeveasset.data.sde.StaticData;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class SdeJumpsReader {
	private static final Logger LOG = LoggerFactory.getLogger(SdeJumpsReader.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static void load(List<Jump> jumps) {
		String sdeDir = FileUtil.getPathSdeDirectory();

        File stargatesFile = resolveFile(new File(sdeDir), "mapStargates.jsonl");

        if (!stargatesFile.exists()) {
            LOG.error("SDE mapStargates.jsonl not found at: " + stargatesFile.getAbsolutePath());
            return;
        }

        // Derive jumps from stargates (new SDE format)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stargatesFile), StandardCharsets.UTF_8))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    JsonNode node = objectMapper.readTree(line);
                    long from = node.path("solarSystemID").asLong();
                    long to = node.path("destination").path("solarSystemID").asLong();
                    if (from != 0 && to != 0) {
                        Jump jump = new Jump(StaticData.get().getLocation(from), StaticData.get().getLocation(to));
                        jumps.add(jump);
                        count++;
                    }
                } catch (Exception ex) {
                    LOG.warn("Failed to parse stargate link: " + ex.getMessage());
                }
            }
            LOG.info("Loaded " + count + " jumps from stargates");
        } catch (Exception ex) {
            LOG.error("Failed to load jumps from stargates: " + ex.getMessage(), ex);
        }
	}
    private static File resolveFile(File dir, String filename) {
        File direct = new File(dir, filename);
        if (direct.exists()) return direct;
        try {
            Path found = Files.walk(dir.toPath())
                .filter(p -> p.getFileName().toString().equals(filename))
                .findFirst()
                .orElse(null);
            return found != null ? found.toFile() : direct;
        } catch (Exception ex) {
            return direct;
        }
    }
}

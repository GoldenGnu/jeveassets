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

package net.nikr.eve.jeveasset.io.online;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdeDownloader {
    private static final Logger LOG = LoggerFactory.getLogger(SdeDownloader.class);

    private static final String CHANGELOG_URL = "https://developers.eveonline.com/static-data/tranquility/schema-changelog.yaml";
    private static final String JSONL_ZIP_URL_TEMPLATE = "https://developers.eveonline.com/static-data/tranquility/eve-online-static-data-%s-jsonl.zip";

    public static File getSdeFile(String filename, String sdeDirectory) {
        File sdeDir = new File(sdeDirectory);
        if (!sdeDir.exists()) {
            sdeDir.mkdirs();
        }
        return new File(sdeDir, filename);
    }

    public static boolean ensureRequiredSdeFiles(String sdeDirectory) {
        File sdeDir = new File(sdeDirectory);
        if (!sdeDir.exists()) {
            sdeDir.mkdirs();
        }

        // If key file exists, assume SDE present
        File typesFile = new File(sdeDir, "types.jsonl");
        if (typesFile.exists()) {
            return true;
        }

        // Otherwise, download the latest JSONL archive and extract
        LOG.info("Downloading latest SDE JSONL archive (types.jsonl missing)...");
        return downloadAndExtractLatestJsonlArchive(sdeDir);
    }

    public static boolean downloadAndExtractLatestJsonlArchive(File sdeDir) {
        String build = getLatestBuildFromChangelog();
        if (build == null || build.isEmpty()) {
            LOG.warn("Could not determine latest SDE build from changelog");
            return false;
        }
        String zipUrl = String.format(JSONL_ZIP_URL_TEMPLATE, build);
        LOG.info("Latest SDE build: " + build + ", downloading: " + zipUrl);

        File tempZip = null;
        try {
            tempZip = File.createTempFile("sde-", ".zip");
            if (!downloadToFile(zipUrl, tempZip)) {
                LOG.warn("Failed to download SDE JSONL zip");
                return false;
            }
            extractZip(tempZip, sdeDir);
            return new File(sdeDir, "types.jsonl").exists();
        } catch (IOException ex) {
            LOG.warn("Failed handling SDE JSONL zip: " + ex.getMessage());
            return false;
        } finally {
            if (tempZip != null && tempZip.exists()) {
                try {
                    Files.delete(tempZip.toPath());
                } catch (IOException ignore) {
                }
            }
        }
    }

    private static boolean downloadToFile(String urlString, File outputFile) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(600000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "jEveAssets/8 (Java)");
            conn.setRequestProperty("Accept", "application/zip, */*");
            try (InputStream is = conn.getInputStream()) {
                Files.copy(is, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException ex) {
            LOG.warn("Download failed: " + ex.getMessage());
            return false;
        }
    }

    private static void extractZip(File zipFile, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        try (InputStream fis = Files.newInputStream(zipFile.toPath());
                ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File dir = new File(destDir, entry.getName());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } else {
                    File outFile = new File(destDir, entry.getName());
                    File parent = outFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    Files.copy(zis, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    public static String getLatestBuildFromChangelog() {
        try {
            URL url = new URL(CHANGELOG_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "jEveAssets/8 (Java)");
            conn.setRequestProperty("Accept", "application/yaml, text/yaml, */*");
            try (InputStream is = conn.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                Object root = mapper.readValue(is, Object.class);
                String best = findBestBuild(root);
                if (best != null) {
                    return best;
                }
            }
        } catch (IOException ex) {
            LOG.warn("Failed to parse changelog.yaml: " + ex.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static String findBestBuild(Object node) {
        String best = null;
        if (node instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) node;
            for (Map.Entry<Object, Object> e : map.entrySet()) {
                String key = String.valueOf(e.getKey());
                Object val = e.getValue();
                if ("build".equalsIgnoreCase(key)) {
                    String s = String.valueOf(val).trim();
                    if (isPlausibleBuild(s)) {
                        best = pickMax(best, s);
                    }
                }
                String child = findBestBuild(val);
                if (child != null) {
                    best = pickMax(best, child);
                }
            }
        } else if (node instanceof List) {
            for (Object item : (List<?>) node) {
                String child = findBestBuild(item);
                if (child != null) {
                    best = pickMax(best, child);
                }
            }
        } else if (node != null) {
            String s = String.valueOf(node).trim();
            if (isPlausibleBuild(s)) {
                best = pickMax(best, s);
            }
        }
        return best;
    }

    private static boolean isPlausibleBuild(String s) {
        if (!s.matches("\\d{6,9}")) {
            return false;
        }
        try {
            long v = Long.parseLong(s);
            return v > 100000; // simple sanity
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static String pickMax(String a, String b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return (Long.parseLong(b) > Long.parseLong(a)) ? b : a;
    }
}

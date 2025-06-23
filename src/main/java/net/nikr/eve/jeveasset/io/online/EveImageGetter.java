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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import net.nikr.eve.jeveasset.data.api.accounts.OwnerType;
import net.nikr.eve.jeveasset.data.api.my.MyLoyaltyPoints;
import net.nikr.eve.jeveasset.data.api.my.MyNpcStanding;
import net.nikr.eve.jeveasset.data.api.raw.RawNpcStanding.FromType;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.nikr.eve.jeveasset.io.shared.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EveImageGetter implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(EveImageGetter.class);

	private static final ExecutorService RETURN_THREAD_POOL = Executors.newFixedThreadPool(10);
	
	public static enum ImageSize {
		SIZE_32(32),
		SIZE_64(64),
		SIZE_128(128),
		SIZE_256(256),
		SIZE_512(512),
		SIZE_1024(1024);

		private final int size;

		private ImageSize(int size) {
			this.size = size;
		}

		public int getSize() {
			return size;
		}

		
	}

	public static enum ImageTypeVariation {
		BPO("bpo"),
		BPC("bpc"),
		RELIC("relic"),
		ICON("icon"),
		RENDER("render");

		private final String variation;

		private ImageTypeVariation(String variation) {
			this.variation = variation;
		}	

		public String getVariation() {
			return variation;
		}
	}

	public static enum ImageCategory {
		ALLIANCES("alliances", "logo"),
		CORPORATIONS("corporations", "logo"),
		CHARACTERS("characters", "portrait"),
		TYPE("type", "icon");

		private final String category;
		private final String variation;

		private ImageCategory(String category, String variation) {
			this.category = category;
			this.variation = variation;
		}

		public String getURL(int id, ImageSize imageSize) {
			return getURL(id, imageSize, null);
		}

		public String getURL(int id, ImageSize size, ImageTypeVariation typeVariation) {
			if (typeVariation != null) {
				return "https://images.evetech.net/" + category + "/" + id + "/" + typeVariation.getVariation() + "?size=" + size.getSize();
			} else {
				return "https://images.evetech.net/" + category + "/" + id + "/" + variation + "?size=" + size.getSize();
			}
		}
	}

	private final UpdateTask updateTask;
	private final List<OwnerType> ownerTypes;

	public EveImageGetter(UpdateTask updateTask, List<OwnerType> ownerTypes) {
		this.updateTask = updateTask;
		this.ownerTypes = ownerTypes;
	}

	public static BufferedImage getBufferedImage(int corporationID) {
		ImageDownload imageDownload = new ImageDownload(corporationID);
		try {
			return imageDownload.get();
		} catch (Exception ex) {
			return null;
		}
	}

	public static BufferedImage getBufferedImage(MyNpcStanding npcStanding) {
		ImageDownload imageDownload = new ImageDownload(npcStanding);
		try {
			return imageDownload.get();
		} catch (Exception ex) {
			return null;
		}
	}

	public static BufferedImage getBufferedImage(MyLoyaltyPoints loyaltyPoints) {
		ImageDownload imageDownload = new ImageDownload(loyaltyPoints);
		try {
			return imageDownload.get();
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public void run() {
		try {
			List<Future<?>> futures = new ArrayList<>();
			for (Runnable runnable : getImageDownloads(ownerTypes)) {
				futures.add(RETURN_THREAD_POOL.submit(runnable));
			}
			RETURN_THREAD_POOL.shutdown();
			while (!RETURN_THREAD_POOL.awaitTermination(500, TimeUnit.MICROSECONDS)) {
				if (updateTask != null) {
					if (updateTask.isCancelled()) {
						RETURN_THREAD_POOL.shutdownNow();
					}
				}
			}
			//Get errors (if any)
			for (Future<?> future : futures) {
				future.get();
			}
		} catch (InterruptedException ex) {
			LOG.error(ex.getMessage(), ex);
		} catch (ExecutionException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	private Set<ImageDownload> getImageDownloads(List<OwnerType> ownerTypes) {
		Set<ImageDownload> set = new HashSet<>();
		for (OwnerType ownerType : ownerTypes) {
			for (MyNpcStanding npcStanding : ownerType.getNpcStanding()) {
				set.add(new ImageDownload(npcStanding));
				set.add(new ImageDownload(npcStanding.getCorporationID()));
			}
			for (MyLoyaltyPoints loyaltyPoints : ownerType.getLoyaltyPoints()) {
				set.add(new ImageDownload(loyaltyPoints));
			}
		}
		return set;
	}

	private static class ImageDownload implements Runnable {
		private final int id;
		private final ImageCategory category;
		private final ImageSize size;
		private final ImageTypeVariation typeVariation;

		private ImageDownload(MyNpcStanding npcStanding) {
			this.id = npcStanding.getFromID();
			category = getImageCategory(npcStanding.getFromType());
			size = MyNpcStanding.IMAGE_SIZE;
			typeVariation = null;
		}

		private ImageDownload(int corporationID) {
			this.id = corporationID;
			this.category = ImageCategory.CORPORATIONS;
			size = MyNpcStanding.IMAGE_SIZE;
			typeVariation = null;
		}

		private ImageDownload(MyLoyaltyPoints loyaltyPoints) {
			id = loyaltyPoints.getCorporationID();
			category = ImageCategory.CORPORATIONS;
			size = MyLoyaltyPoints.IMAGE_SIZE;
			typeVariation = null;
		}

		private ImageCategory getImageCategory(FromType fromType) {
			if (fromType == FromType.FACTION) {
				return ImageCategory.CORPORATIONS;
			} else if (fromType == FromType.NPC_CORP) {
				return ImageCategory.CORPORATIONS;
			} else if (fromType == FromType.AGENT) {
				return ImageCategory.CHARACTERS;
			} else {
				throw new IllegalArgumentException();
			}
		}

		@Override
		public void run() {
			try {
				download(id, category, size, typeVariation);
			} catch (IOException ex) {
				LOG.error(ex.getMessage(), ex);
			}
		}

		public BufferedImage get() throws Exception {
			String filename = download(id, category, size, typeVariation);
			return getBufferedImage(filename);
		}

		private String download(int id, ImageCategory category, ImageSize size, ImageTypeVariation typeVariation) throws IOException {
			String url = category.getURL(id, size, typeVariation);
			String filename = FileUtil.getPathImages(getFilename(id, category, size, typeVariation));
			Path path = Paths.get(filename);
			if (Files.exists(path)) {
				return filename;
			}
			try (InputStream in = new URL(url).openStream()) {
				Files.copy(in, path);
			}
			return filename;
		}

		private String getFilename(int id, ImageCategory category, ImageSize size, ImageTypeVariation typeVariation) {
			if (typeVariation != null) {
				return category.name().toLowerCase() + "_" + typeVariation.name().toLowerCase() + "_" + id + "_" + size.getSize() + ".png";
			} else {
				return category.name().toLowerCase() + "_" + id + "_" + size.getSize() + ".png";
			}
		}

		private static BufferedImage getBufferedImage(final String filename) {
			try {
				if (filename != null) {
					return ImageIO.read(new File(filename));
				} else {
					LOG.warn("image: " + filename + " not found (URL == null)");
				}
			} catch (IOException ex) {
				LOG.warn("image: " + filename + " not found (IOException)");
			}
			return null;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 97 * hash + this.id;
			hash = 97 * hash + Objects.hashCode(this.category);
			hash = 97 * hash + Objects.hashCode(this.typeVariation);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ImageDownload other = (ImageDownload) obj;
			if (this.id != other.id) {
				return false;
			}
			if (this.category != other.category) {
				return false;
			}
			return this.typeVariation == other.typeVariation;
		}
	}
}

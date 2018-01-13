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
package net.nikr.eve.jeveasset.io.online;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;


public class UpdateTaskInputStream extends FilterInputStream {
	private final long maxNumBytes;
	private final UpdateTask updateTask;
	private final int start;
	private final int end;

	private volatile long totalNumBytesRead;

	public UpdateTaskInputStream(final InputStream in, final long maxNumBytes, final UpdateTask updateTask) {
		this(in, maxNumBytes, updateTask, 0, 100);
	}

	public UpdateTaskInputStream(final InputStream in, final long maxNumBytes, final UpdateTask updateTask, int start, int end) {
		super(in);
		this.maxNumBytes = maxNumBytes;
		this.updateTask = updateTask;
		this.start = start;
		this.end = end;
	}

	public long getMaxNumBytes() {
		return maxNumBytes;
	}

	public long getTotalNumBytesRead() {
		return totalNumBytesRead;
	}

	@Override
	public int read() throws IOException {
		return updateProgressInteger(super.read());
	}

	@Override
	public int read(byte[] b) throws IOException {
		return updateProgressInteger(super.read(b));
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return updateProgressInteger(super.read(b, off, len));
	}

	@Override
	public long skip(long n) throws IOException {
		return updateProgressLong(super.skip(n));
	}

	@Override
	public void mark(int readlimit) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	private int updateProgressInteger(final int numBytesRead) {
		updateProgressLong(numBytesRead);
		return numBytesRead;
	}

	private long updateProgressLong(final long numBytesRead) {
		if (numBytesRead > 0) {
			this.totalNumBytesRead += numBytesRead;
			if (updateTask != null) {
				updateTask.setTaskProgress(maxNumBytes, totalNumBytesRead, start, end);
			}
		}
		return numBytesRead;
	}

}
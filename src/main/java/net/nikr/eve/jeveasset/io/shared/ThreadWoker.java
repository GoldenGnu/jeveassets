/*
 * Copyright 2009-2019 Contributors (see credits.txt)
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
package net.nikr.eve.jeveasset.io.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThreadWoker {

	private static final int MAIN_THREADS = 100;
	private static final int SUB_THREADS = 100;
	private static final ExecutorService RETURN_THREAD_POOL = Executors.newFixedThreadPool(SUB_THREADS);

	private static final Logger LOG = LoggerFactory.getLogger(ThreadWoker.class);

	public static void start(UpdateTask updateTask, Collection<? extends Runnable> updaters) {
		start(updateTask, updaters, true);
	}

	public static void start(UpdateTask updateTask, Collection<? extends Runnable> updaters, int start, int end) {
		start(updateTask, updaters, true, start, end);
	}

	public static void start(UpdateTask updateTask, Collection<? extends Runnable> updaters, boolean updateProgress) {
		start(updateTask, updaters, updateProgress, 0, 100);
	}

	public static void start(UpdateTask updateTask, Collection<? extends Runnable> updaters, boolean updateProgress, int start, int end) {
		ExecutorService threadPool = Executors.newFixedThreadPool(MAIN_THREADS);
		try {
			LOG.info("Starting " + updaters.size() + " main threads");
			List<Future<?>> futures = new ArrayList<Future<?>>();
			for (Runnable runnable : updaters) {
				futures.add(threadPool.submit(runnable));
			}
			threadPool.shutdown();
			while (!threadPool.awaitTermination(500, TimeUnit.MICROSECONDS)) {
				if (updateTask != null) {
					if (updateTask.isCancelled()) {
						threadPool.shutdownNow();
					} else if (updateProgress) {
						int progress = 0;
						for (Future<?> future : futures) {
							if (future.isDone()) {
								progress++;
							}
						}
						updateTask.setTaskProgress(updaters.size(), progress, start, end);
					}
				}
			}
			//Get errors (if any)
			for (Future<?> future : futures) {
				future.get();
			}
		} catch (InterruptedException ex) {
			//No problem
		} catch (ExecutionException ex) {
			throwExecutionException(ex);
		}
	}

	public static <K> List<Future<K>> startReturn(UpdateTask updateTask, Collection<? extends Callable<K>> updaters) throws InterruptedException {
		return startReturn(updateTask, updaters, false);
	}
	
	public static <K> List<Future<K>> startReturn(UpdateTask updateTask, Collection<? extends Callable<K>> updaters, boolean updateProgress) throws InterruptedException {
		return startReturn(updateTask, updaters, updateProgress, 0, 100);
	}

	public static <K> List<Future<K>> startReturn(UpdateTask updateTask, Collection<? extends Callable<K>> updaters, boolean updateProgress, int start, int end) throws InterruptedException {
		if (updateTask != null && updateTask.isCancelled()) {
			throw new TaskCancelledException();
		}
		LOG.info("Starting " + updaters.size() + " sub threads");
		List<Future<K>> futures = new ArrayList<Future<K>>();
		for (Callable<K> callable : updaters) {
			futures.add(RETURN_THREAD_POOL.submit(callable));
		}
		int done = 0;
		while (done < futures.size()) {
			done = 0;
			for (Future<?> future : futures) {
				if (future.isDone()) {
					done++;
				}
			}
			if (updateTask != null) {
				if (updateTask.isCancelled()) { //If task is cancelled
					for (Future<?> future : futures) { //cancel all threads
						future.cancel(true);
					}
					throw new TaskCancelledException(); //Stop parent Task
				} else if (updateProgress) {
					updateTask.setTaskProgress(updaters.size(), done, start, end);
				}
			}
			Thread.sleep(500);
		}
		return futures;
	}

	public static class TaskCancelledException extends RuntimeException {
		
	}

	private static <E extends Exception> void throwExecutionException(ExecutionException ex) throws E {
		throwExecutionException(null, ex);
	}

	public static <E extends Exception> void throwExecutionException(Class<E> clazz, ExecutionException ex) throws E {
		Throwable cause = ex.getCause();
		if (clazz != null && cause.getClass().equals(clazz) ) {
			throw clazz.cast(cause);
		} else if (cause instanceof Error) {
			throw (Error) cause;
		} else if (cause instanceof RuntimeException) {
			throw (RuntimeException) cause;
		} else {
			throw new RuntimeException(cause);
		}
	}
}

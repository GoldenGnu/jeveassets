/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.nikr.eve.jeveasset.io.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
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

	public static void start(UpdateTask updateTask, Collection<? extends Callable<Void>> updaters) {
		ExecutorService threadPool = Executors.newFixedThreadPool(MAIN_THREADS);
		try {
			LOG.info("Starting " + updaters.size() + " main threads");
			List<Future<Void>> futures = new ArrayList<Future<Void>>();
			for (Callable<Void> callable : updaters) {
				futures.add(threadPool.submit(callable));
			}
			threadPool.shutdown();
			while (!threadPool.awaitTermination(500, TimeUnit.MICROSECONDS)) {
				int progress = 0;
				for (Future<Void> future : futures) {
					if (future.isDone()) {
						progress++;
					}
				}
				if (updateTask != null) {
					updateTask.setTaskProgress(updaters.size(), progress, 0, 100);
					if (updateTask.isCancelled()) {
						threadPool.shutdownNow();
						updateTask.addError("", "Cancelled");
					}
				}
			}
		} catch (InterruptedException ex) {
			
		}
	}

	public static <K> List<Future<K>> startReturn(Collection<? extends Callable<K>> updaters) {
		try {
			return RETURN_THREAD_POOL.invokeAll(updaters);
		} catch (InterruptedException ex) {
			return null;
		}
	}
}

/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package org.helios.hiex.agent.tracer.contention;

import gnu.trove.map.hash.TIntLongHashMap;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicInteger;

import org.helios.hiex.agent.tracer.base.BaseSingleMetricTracerFactory;

import com.wily.introscope.agent.IAgent;
import com.wily.introscope.agent.stat.IIntegerAverageDataAccumulator;
import com.wily.introscope.agent.stat.ILongAverageDataAccumulator;
import com.wily.introscope.agent.trace.InvocationData;
import com.wily.introscope.agent.trace.ProbeIdentification;
import com.wily.util.properties.AttributeListing;

/**
 * <p>Title: ThreadContentionTracer</p>
 * <p>Description: Tracer to capture wait and block counts and times experienced by threads when executing the instrumented methods.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.agent.tracer.base.ThreadContentionTracer</code></p>
 */

public class ThreadContentionTracer extends BaseSingleMetricTracerFactory {
	
	/** A thread local stack of captured wait count baselines during the startTrace callback */
	protected final ThreadLocal<TIntLongHashMap> waitCountBaselines = new ThreadLocal<TIntLongHashMap>(){ protected TIntLongHashMap initialValue(){ return new TIntLongHashMap(MAPS_SIZE, MAPS_LF, NO_KEY, NO_ENTRY);} };
	/** A thread local stack of captured wait time baselines during the startTrace callback */
	protected final ThreadLocal<TIntLongHashMap> waitTimeBaselines;  
	/** A thread local stack of captured block count baselines during the startTrace callback */
	protected final ThreadLocal<TIntLongHashMap> blockCountBaselines  = new ThreadLocal<TIntLongHashMap>(){ protected TIntLongHashMap initialValue(){ return new TIntLongHashMap(MAPS_SIZE, MAPS_LF, NO_KEY, NO_ENTRY);} };  
	/** A thread local stack of captured block time baselines during the startTrace callback */
	protected final ThreadLocal<TIntLongHashMap> blockTimeBaselines;
	
	/** Wait count accumulator */
	protected final ILongAverageDataAccumulator waitCountAcc;
	/** Block count accumulator */
	protected final ILongAverageDataAccumulator blockCountAcc;
	/** Wait time accumulator */
	protected final ILongAverageDataAccumulator waitTimeAcc;
	/** Block time accumulator */
	protected final ILongAverageDataAccumulator blockTimeAcc;
	
	
	/** Flag indicating if contention times are enabled AND contention times are supported AND contention times were successfully enabled */
	protected final boolean timesEnabled;
	
	/** A basic reentrancy tracker */
	public static final ThreadLocal<AtomicInteger> reentrancyIndex = new ThreadLocal<AtomicInteger>() {
		protected AtomicInteger initialValue() {
			return new AtomicInteger(0);
		}
	};
	
	/** Reference to the JVM's ThreadMXBean */
	public static final ThreadMXBean TMX = ManagementFactory.getThreadMXBean();
	
	
	
	/** The map size for the base lines */
	public static final int MAPS_SIZE = 16;
	/** The map load factor for the base lines */
	public static final float MAPS_LF = 0.75f;
	/** The logical int for a no key in map */
	public static final int NO_KEY = -2;
	/** The logical long for a no entry in map */
	public static final long NO_ENTRY = -2;
	
	/** Configuration param name to indicate that the tracer should attempt to collect times */
	public static final String ENABLE_TIMES = "enableTimes";
	
	/**
	 * Creates a new ThreadContentionTracer
	 * @param agent A reference to the agent
	 * @param parameters The tracer configuration parameters
	 * @param probe THe crosscut details
	 * @param sampleTracedObject A sample instance of the crosscut method's class
	 */
	public ThreadContentionTracer(IAgent agent, AttributeListing parameters, ProbeIdentification probe, Object sampleTracedObject) {
		super(agent, parameters, probe, sampleTracedObject);
		timesEnabled = getParameter(ENABLE_TIMES, false) ? enableContentionTimes() : false;		
		waitCountAcc = this.dataAccumulatorFactory.safeGetLongAverageDataAccumulator(getFormattedName() + ":Wait Count");
		blockCountAcc = this.dataAccumulatorFactory.safeGetLongAverageDataAccumulator(getFormattedName() + ":Block Count");
		log.info("\n\tWAIT:[", getFormattedResource() , ":Wait Count]");
		log.info("\n\tBLOCK:[", getFormattedResource() , ":Block Count]");
		if(timesEnabled) {
			waitTimeBaselines =  new ThreadLocal<TIntLongHashMap>(){ protected TIntLongHashMap initialValue(){ return new TIntLongHashMap(MAPS_SIZE, MAPS_LF, NO_KEY, NO_ENTRY);} };
			blockTimeBaselines =  new ThreadLocal<TIntLongHashMap>(){ protected TIntLongHashMap initialValue(){ return new TIntLongHashMap(MAPS_SIZE, MAPS_LF, NO_KEY, NO_ENTRY);} };
			waitTimeAcc = this.dataAccumulatorFactory.safeGetLongAverageDataAccumulator(getFormattedName() + ":Wait Time (ms)");
			blockTimeAcc = this.dataAccumulatorFactory.safeGetLongAverageDataAccumulator(getFormattedName() + ":Block Time (ms)");
		} else {
			waitTimeBaselines =  null;
			blockTimeBaselines =  null;
			waitTimeAcc = null;
			blockTimeAcc = null;
		}
 	}
	
	/**
	 * Attempts to enable thread contention monitoring.
	 * @return true if thread contention monitoring is enabled, false otherwise 
	 */
	protected static boolean enableContentionTimes() {
		if(!TMX.isThreadContentionMonitoringSupported()) return false;
		if(TMX.isThreadContentionMonitoringEnabled()) return true;
		try {
			TMX.setThreadContentionMonitoringEnabled(true);			
		} catch (Throwable t) {}
		return TMX.isThreadContentionMonitoringEnabled();
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.wily.introscope.agent.trace.ITracer#ITracer_finishTrace(int, com.wily.introscope.agent.trace.InvocationData)
	 */
	@Override
	public void ITracer_finishTrace(int tracerIndex, InvocationData data) {
		final int currentIndex = reentrancyIndex.get().decrementAndGet();
		try {			
			final int priorIndex = currentIndex+1;
			final ThreadInfo ti = TMX.getThreadInfo(Thread.currentThread().getId());
			long newWaitCount = ti.getWaitedCount();
			long newBlockCount = ti.getBlockedCount();
			long priorWaitCount = waitCountBaselines.get().remove(priorIndex);
			long priorBlockCount = blockCountBaselines.get().remove(priorIndex);
			if(priorWaitCount!=NO_ENTRY && !waitCountAcc.IDataAccumulator_isShutOff()) {				
				waitCountAcc.ILongAggregatingDataAccumulator_recordDataPoint(newWaitCount-priorWaitCount);
			}
			if(priorBlockCount!=NO_ENTRY && !blockCountAcc.IDataAccumulator_isShutOff()) {
				blockCountAcc.ILongAggregatingDataAccumulator_recordDataPoint(newBlockCount-priorBlockCount);
			}
			if(timesEnabled) {
				long newWaitTime = ti.getWaitedTime();
				long newBlockTime = ti.getBlockedTime();
				long priorWaitTime = waitTimeBaselines.get().remove(priorIndex);
				long priorBlockTime = blockTimeBaselines.get().remove(priorIndex);
				if(priorWaitTime!=NO_ENTRY && !waitTimeAcc.IDataAccumulator_isShutOff()) {
					waitTimeAcc.ILongAggregatingDataAccumulator_recordDataPoint(newWaitTime-priorWaitTime);
				}
				if(priorBlockTime!=NO_ENTRY && !blockTimeAcc.IDataAccumulator_isShutOff()) {
					blockTimeAcc.ILongAggregatingDataAccumulator_recordDataPoint(newBlockTime-priorBlockTime);
				}				
			}
		} finally {
			if(currentIndex<1) {				
				waitCountBaselines.remove();
				blockCountBaselines.remove();
				if(timesEnabled) {
					waitTimeBaselines.remove();
					blockTimeBaselines.remove();				
				}
			}
			if(currentIndex<0) {
				log.warn("Reentrancy Index Underrun:", currentIndex);
				reentrancyIndex.get().set(0);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.wily.introscope.agent.trace.ITracer#ITracer_startTrace(int, com.wily.introscope.agent.trace.InvocationData)
	 */
	@Override
	public void ITracer_startTrace(int tracerIndex, InvocationData data) {
		final int currentIndex = reentrancyIndex.get().incrementAndGet();
		final ThreadInfo ti = TMX.getThreadInfo(Thread.currentThread().getId());
		waitCountBaselines.get().put(currentIndex, ti.getWaitedCount());
		blockCountBaselines.get().put(currentIndex, ti.getBlockedCount());
		if(timesEnabled) {
			waitTimeBaselines.get().put(currentIndex, ti.getWaitedTime());
			blockTimeBaselines.get().put(currentIndex, ti.getBlockedTime());
		}
	}	

}

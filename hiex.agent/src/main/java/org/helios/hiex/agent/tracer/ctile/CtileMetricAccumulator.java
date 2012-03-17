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
package org.helios.hiex.agent.tracer.ctile;

import gnu.trove.list.array.TIntArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.helios.hiex.util.FlexiLogger;
import org.helios.hiex.util.collections.StatsIntArray;
import org.helios.hiex.util.collections.SynchronizedTIntArrayList;
import org.helios.hiex.util.math.SimpleMath;

import com.wily.introscope.agent.stat.DataAccumulatorFactory;
import com.wily.introscope.agent.stat.IDataAccumulator;
import com.wily.introscope.agent.stat.IIntegerFluctuatingCounterDataAccumulator;
import com.wily.introscope.agent.stat.ILongAverageDataAccumulator;

/**
 * <p>Title: CtileMetricAccumulator</p>
 * <p>Description: Indexed container for a tracers compound names, accumulators and interval accumulators.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.agent.tracer.ctile.CtileMetricAccumulator</code></p>
 */
public class CtileMetricAccumulator  {
	/** The interval accumulator pair used to accumulate elapsed times during an interval window. */
	protected final SynchronizedTIntArrayList[] intervalAccumulators = new SynchronizedTIntArrayList[] {
			new SynchronizedTIntArrayList(), new SynchronizedTIntArrayList() };
	/** The boolean alternator used to switch between interval accumulators on alternating intervals */
	protected final AtomicBoolean alternator;
	/** Indicates if the percentile calc. elapsed time should be traced */
	protected final boolean tracePerformance;
	/** Indicates if debug output should be traced */
	protected final boolean debug;
	/** The percentile to calculate */
	protected final int percentile;
	/** The agent logging channel */
	protected final FlexiLogger log;
	
	
	

	/**
	 * Creates a new CtileMetricAccumulator
	 * @param alternator The interval alternator.
	 * @param percentile The percentile to calculate on.
	 * @param tracePerformance  Indicates if the percentile calc. elapsed time should be traced
	 * @param debug Indicates if debug output should be traced
	 * @param log The agent logger.
	 */
	public CtileMetricAccumulator(int percentile, AtomicBoolean alternator, boolean tracePerformance, boolean debug, FlexiLogger log) {
		this.percentile = percentile;
		this.alternator = alternator;
		this.tracePerformance = tracePerformance;
		this.debug = debug;
		this.log = log;
	}

	/**
	 * The current interval's interval Accumulator.
	 * @return the current SynchronizedTIntArrayList
	 */
	public SynchronizedTIntArrayList getIntervalAccumulator() {
		if (alternator.get()) {
			return intervalAccumulators[1];
		} else {

			return intervalAccumulators[0];
		}
	}

	/**
	 * The alternate interval's interval Accumulator.
	 * @return the alternate SynchronizedTIntArrayList
	 */
	public SynchronizedTIntArrayList getAltIntervalAccumulator() {
		if (alternator.get()) {
			return intervalAccumulators[0];
		} else {
			return intervalAccumulators[1];
		}
	}


	

	/**
	 * Adds an elapsed time to the current interval accumulator.
	 * @param time The elapsed time of the method invocation.
	 */
	public void addElapsedTime(int time) {
		getIntervalAccumulator().add(time);
	}


	/**
	 * 
	 */
	public Map<String, Number> calcAndPublishInterval(Map<String, String> metricNameMap) throws Exception {				
		if(metricNameMap.size()<1) return Collections.emptyMap();
		Map<String, Number> results = new HashMap<String, Number>(metricNameMap.size());
		long start = System.currentTimeMillis();		
		SynchronizedTIntArrayList lastInterval = getAltIntervalAccumulator();
		if (lastInterval.size() < 2) {
			lastInterval.reset();
			// Need to publish zero out metrics here.
			return results;
		}
		try {
			TIntArrayList lastIntervalValues = new TIntArrayList(lastInterval.toArray());
			lastInterval.reset();
			StatsIntArray statsArray = new StatsIntArray(lastIntervalValues.toArray());
			
			
			// The nth percentile elapsed time threshold
			int ctile = statsArray.percentileRankValue(percentile);
			int total = statsArray.size();
			int below = statsArray.grep(new PercentileDiscriminator(ctile)).size();
			int above = total - below;

			if(metricNameMap.containsKey(MethodTimerCtile.PERCENTILE_ELAPSED)) {
				results.put(MethodTimerCtile.PERCENTILE_ELAPSED, ctile);
			}
			if(metricNameMap.containsKey(MethodTimerCtile.COUNT_LTOE_PERCENTILE)) {
				results.put(MethodTimerCtile.COUNT_LTOE_PERCENTILE, below);				
			}
			if(metricNameMap.containsKey(MethodTimerCtile.COUNT_GT_PERCENTILE)) {
				results.put(MethodTimerCtile.COUNT_GT_PERCENTILE, above);				
			}
			if(metricNameMap.containsKey(MethodTimerCtile.PERCENT_LTOE_PERCENTILE)) {
				results.put(MethodTimerCtile.PERCENT_LTOE_PERCENTILE, SimpleMath.ipercent(below, total));				
			}
			if(metricNameMap.containsKey(MethodTimerCtile.PERCENT_GT_PERCENTILE)) {
				results.put(MethodTimerCtile.PERCENT_GT_PERCENTILE, SimpleMath.ipercent(above, total));				
			}
			if(metricNameMap.containsKey(MethodTimerCtile.MEAN_ELAPSED)) {
				int mean = statsArray.mean();
				results.put(MethodTimerCtile.MEAN_ELAPSED, mean);
				if(metricNameMap.containsKey(MethodTimerCtile.STDDEV_ELAPSED)) {
					results.put(MethodTimerCtile.STDDEV_ELAPSED, statsArray.stddev());
				}
			}
			if(metricNameMap.containsKey(MethodTimerCtile.COUNT_ELAPSED)) {
				results.put(MethodTimerCtile.COUNT_ELAPSED, total);
			}
			if(tracePerformance && metricNameMap.containsKey(MethodTimerCtile.PERCENTILE_PERF)) {
				results.put(MethodTimerCtile.PERCENTILE_PERF, System.currentTimeMillis()-start);
			}
			return results;
		} catch (Exception e) {
			log.warn("Failed to calculate interval percentiles", e);
			return Collections.emptyMap();
		}

	}

	/**
	 * Calculates the value of the defined percentile-th for the passed values
	 * @param percentile The percentile.
	 * @param values The values to calculate against.
	 * @return the percentile.
	 */
	public static int calcPercentile(float percentile, int... values) {

		TIntArrayList arr = new TIntArrayList(values);
		arr.sort();
		double rank = (percentile / 100f) * (values.length + 1);
		double d = rank;
		int ir = (int) Math.floor(d);
		double fr = rank - ir;
		int ir1 = 0, ir2 = 0;
		if (arr.size() >= ir) {
			ir1 = arr.get(ir - 1);
			if (arr.size() > ir) {
				ir2 = arr.get(ir);
			} else {
				ir2 = ir1;
			}
		}
		double interpolated = fr * (ir2 - ir1) + ir1;

		return (int) Math.round(interpolated);

	}

	/**
	 * @param total
	 * @param part
	 * @return
	 */
	private int calcPercent(float total, float part) {
		if (total == 0 || part == 0) {
			return 0;
		}
		float p = part / total * 100;
		return (int) p;
	}

}

//if (debug) {
//if (!isShutOff(MethodTimerCtile.DEBUG)) {
//	ctile = statsArray.percentileRankValue(percentile);
//	int ctile2 = statsArray.percentileRankValue(percentile);
//	total = statsArray.size();
//	below = statsArray.grep(new PercentileDiscriminator(ctile))
//			.size();
//	above = total - below;
//	mean = statsArray.mean();
//	stddev = statsArray.stddev();
//	//=======
//	StringBuilder debugStr = new StringBuilder(statsArray
//			.printValues(true));
//	debugStr.append(StatsIntArray.CR);
//	debugStr
//			.append("COUNT, MEAN, PERCENTILE, PERCENTILE VALUE, PERCENTILE VALUE2, BELOW, ABOVE, STDDEV");
//	debugStr.append(StatsIntArray.CR);
//	debugStr.append(total).append(",");
//	debugStr.append(mean).append(",");
//	debugStr.append(percentile).append(",");
//	debugStr.append(ctile).append(",");
//	debugStr.append(ctile2).append(",");
//	debugStr.append(below).append(",");
//	debugStr.append(above).append(",");
//	debugStr.append(stddev).append(StatsIntArray.CR);
//	((IStringEveryEventDataAccumulator) getDataAccumulator(MethodTimerCtile.DEBUG))
//			.IStringEveryEventDataAccumulator_addString(debugStr
//					.toString());
//}
//}

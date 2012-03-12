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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.helios.hiex.util.collections.StatsIntArray;
import org.helios.hiex.util.collections.SynchronizedTIntArrayList;

import com.wily.introscope.agent.stat.DataAccumulatorFactory;
import com.wily.introscope.agent.stat.IDataAccumulator;
import com.wily.introscope.agent.stat.IIntegerFluctuatingCounterDataAccumulator;
import com.wily.introscope.agent.stat.ILongAverageDataAccumulator;
import com.wily.introscope.agent.stat.IStringEveryEventDataAccumulator;
import com.wily.util.feedback.IModuleFeedbackChannel;

/**
 * <p>Title: CtileMetricAccumulator</p>
 * <p>Description: Indexed container for a tracers compound names, accumulators and interval accumulators.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.agent.tracer.ctile.CtileMetricAccumulator</code></p>
 */
public class CtileMetricAccumulator {
	/** The interval accumulator pair used to accumulate elapsed times during an interval window. */
	protected SynchronizedTIntArrayList[] intervalAccumulators = new SynchronizedTIntArrayList[] {
			new SynchronizedTIntArrayList(), new SynchronizedTIntArrayList() };
	/** A map of data accumulators indexed by sub-name */
	protected Map<String, IDataAccumulator> accumulators = new ConcurrentHashMap<String, IDataAccumulator>();
	/** The full metric name minus the sub type name */
	private String metricResource = null;
	/** The boolean alternator used to switch between interval accumulators on alternating intervals */
	private AtomicBoolean alternator = null;
	/** The accumulator factory used to create data accumulators */
	private DataAccumulatorFactory tracerFactory = null;
	/** The map of constants to configured sub metric names */
	private Map<String, String> metricNameMap = null;
	/** Indicates if the percentile calc. elapsed time should be traced */
	private boolean tracePerformance = false;
	/** Indicates if debug output should be traced */
	private boolean debug = false;
	/** The percentile to calculate */
	private int percentile = 0;
	/** The agent logging channel */
	private IModuleFeedbackChannel log = null;
	

	/**
	 * Creates a new CtileMetricAccumulator
	 * @param alternator The interval alternator.
	 * @param percentile The percentile to calculate on.
	 * @param tracePerformance  Indicates if the percentile calc. elapsed time should be traced
	 * @param debug Indicates if debug output should be traced
	 * @param tracerFactory The agent's tracer factory.
	 * @param metricResource The full metric name minus the sub type name 
	 * @param  metricNameMap The map of constants to configured metric names decode.
	 * @param log The agent logger.
	 */
	public CtileMetricAccumulator(int percentile, AtomicBoolean alternator,
			boolean tracePerformance, boolean debug,
			DataAccumulatorFactory tracerFactory, String metricResource,
			Map<String, String> metricNameMap, IModuleFeedbackChannel log) {

		this.percentile = percentile;
		this.alternator = alternator;
		this.tracePerformance = tracePerformance;
		this.debug = debug;
		this.tracerFactory = tracerFactory;
		this.metricResource = metricResource;
		this.metricNameMap = metricNameMap;
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
	 * Returns the data accumulator for the passed sub type code.
	 * @param typeCode The sub type code.
	 * @return A data accumumator.
	 */
	public IDataAccumulator getDataAccumulator(String typeCode) {

		IDataAccumulator ida = accumulators.get(typeCode);
		if (ida == null) {
			ida = createIDataAccumulator(typeCode);
			accumulators.put(typeCode, ida);
		}

		return ida;

	}

	/**
	 * Creates a DataAccumulator for the given subName.
	 * Constants map:<ul>
	 * <li>PERCENTILE_ELAPSED : IIntegerFluctuatingCounterDataAccumulator</li>
	 * <li>COUNT_LTOE_PERCENTILE : IIntegerFluctuatingCounterDataAccumulator</li>
	 * <li>COUNT_GT_PERCENTILE : IIntegerFluctuatingCounterDataAccumulator</li>
	 * <li>MEAN_ELAPSED : IIntegerFluctuatingCounterDataAccumulator</li>
	 * <li>STDDEV_ELAPSED : IIntegerFluctuatingCounterDataAccumulator</li>
	 * <li>COUNT_ELAPSED : IIntegerFluctuatingCounterDataAccumulator</li>
	 * <li>DEBUG : IStringEveryEventDataAccumulator</li>
	 * <li>PERCENTILE_PERF : ILongAverageDataAccumulator</li>
	 * </ul>
	 * @param subName The subName to create an accumulator for.
	 * @return An IDataAccumulator
	 */
	private IDataAccumulator createIDataAccumulator(String subName) {
		if (
				MethodTimerCtile.PERCENTILE_ELAPSED.equals(subName) || MethodTimerCtile.COUNT_LTOE_PERCENTILE.equals(subName) ||  
				MethodTimerCtile.COUNT_GT_PERCENTILE.equals(subName) || MethodTimerCtile.MEAN_ELAPSED.equals(subName) ||
				MethodTimerCtile.STDDEV_ELAPSED.equals(subName) || MethodTimerCtile.STDDEV_ELAPSED.equals(subName) ||
				MethodTimerCtile.COUNT_ELAPSED.equals(subName)) {			
			return tracerFactory.safeGetIntegerFluctuatingCounterDataAccumulator(metricResource + "|" 	+ metricNameMap.get(subName));
			
		} else if(MethodTimerCtile.PERCENTILE_PERF.equals(subName)) {
			return tracerFactory.safeGetLongAverageDataAccumulator(metricResource + "|" + metricNameMap.get(subName));
		} else if(MethodTimerCtile.DEBUG.equals(subName)) {
			return tracerFactory.safeGetStringEveryEventDataAccumulator(metricResource + "|" + metricNameMap.get(subName));
		} else {
			throw new RuntimeException("SubName Not Indexed [" + subName + "]");
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
	 * Determines if the dataAccumulator associated with the passed subName is shut off.
	 * @param name The subName
	 * @return true if the accumulator is shut off, false if it is on.
	 */
	protected boolean isShutOff(String name) {

		IDataAccumulator ida = getDataAccumulator(name);
		if (ida == null) {

			return true;
		} else {

			return ida.IDataAccumulator_isShutOff();
		}

	}

	/**
	 * 
	 */
	public void calcAndPublishInterval() throws Exception {		
		if(log.isDebugEnabled()) {
			log.debug("Starting  calcAndPublishInterval for [" + metricResource + "]");
		}
		long start = System.currentTimeMillis();		
		SynchronizedTIntArrayList lastInterval = getAltIntervalAccumulator();
		if (lastInterval.size() < 2) {
			lastInterval.reset();
			// Need to publish zero out metrics here.
			return;
		}
		try {
			TIntArrayList lastIntervalValues = new TIntArrayList(lastInterval.toArray());
			lastInterval.reset();
			StatsIntArray statsArray = new StatsIntArray(lastIntervalValues.toArray());
			int ctile = 0;
			int total = -1;
			int below = 0;
			int above = 0;
			int mean = 0;
			int stddev = 0;
		
			if (!isShutOff(MethodTimerCtile.PERCENTILE_ELAPSED)) {
				ctile = statsArray.percentileRankValue(percentile);
				total = statsArray.size();
				((IIntegerFluctuatingCounterDataAccumulator) getDataAccumulator(MethodTimerCtile.PERCENTILE_ELAPSED))
						.IIntegerCounterDataAccumulator_setValue(ctile);
				if (!isShutOff(MethodTimerCtile.COUNT_LTOE_PERCENTILE)) {
					below = statsArray.grep(new PercentileDiscriminator(ctile))
							.size();
					((IIntegerFluctuatingCounterDataAccumulator) getDataAccumulator(MethodTimerCtile.COUNT_LTOE_PERCENTILE))
							.IIntegerCounterDataAccumulator_setValue(below);
					if (!isShutOff(MethodTimerCtile.COUNT_GT_PERCENTILE)) {
						above = total - below;
						((IIntegerFluctuatingCounterDataAccumulator) getDataAccumulator(MethodTimerCtile.COUNT_GT_PERCENTILE))
								.IIntegerCounterDataAccumulator_setValue(above);
					}
				}
			}
			if (!isShutOff(MethodTimerCtile.MEAN_ELAPSED)) {
				mean = statsArray.mean();
				((IIntegerFluctuatingCounterDataAccumulator) getDataAccumulator(MethodTimerCtile.MEAN_ELAPSED))
						.IIntegerCounterDataAccumulator_setValue(mean);
			}
			if (!isShutOff(MethodTimerCtile.STDDEV_ELAPSED)) {
				stddev = statsArray.stddev();
				((IIntegerFluctuatingCounterDataAccumulator) getDataAccumulator(MethodTimerCtile.STDDEV_ELAPSED))
						.IIntegerCounterDataAccumulator_setValue(stddev);
			}
			if (!isShutOff(MethodTimerCtile.COUNT_ELAPSED)) {
				if (total != -1)
					total = statsArray.size();
				((IIntegerFluctuatingCounterDataAccumulator) getDataAccumulator(MethodTimerCtile.COUNT_ELAPSED))
						.IIntegerCounterDataAccumulator_setValue(total);
			}

			if (debug) {
				if (!isShutOff(MethodTimerCtile.DEBUG)) {
					ctile = statsArray.percentileRankValue(percentile);
					int ctile2 = statsArray.percentileRankValue(percentile);
					total = statsArray.size();
					below = statsArray.grep(new PercentileDiscriminator(ctile))
							.size();
					above = total - below;
					mean = statsArray.mean();
					stddev = statsArray.stddev();
					//=======
					StringBuilder debugStr = new StringBuilder(statsArray
							.printValues(true));
					debugStr.append(StatsIntArray.CR);
					debugStr
							.append("COUNT, MEAN, PERCENTILE, PERCENTILE VALUE, PERCENTILE VALUE2, BELOW, ABOVE, STDDEV");
					debugStr.append(StatsIntArray.CR);
					debugStr.append(total).append(",");
					debugStr.append(mean).append(",");
					debugStr.append(percentile).append(",");
					debugStr.append(ctile).append(",");
					debugStr.append(ctile2).append(",");
					debugStr.append(below).append(",");
					debugStr.append(above).append(",");
					debugStr.append(stddev).append(StatsIntArray.CR);
					((IStringEveryEventDataAccumulator) getDataAccumulator(MethodTimerCtile.DEBUG))
							.IStringEveryEventDataAccumulator_addString(debugStr
									.toString());
				}
			}

			if (tracePerformance) {
				if (!isShutOff(MethodTimerCtile.PERCENTILE_PERF)) {
					long elapsed = System.currentTimeMillis() - start;
					((ILongAverageDataAccumulator) getDataAccumulator(MethodTimerCtile.PERCENTILE_PERF))
							.ILongAggregatingDataAccumulator_recordDataPoint(elapsed);
				}
			}

		} catch (Exception e) {
			//  log and throw
			throw e;
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


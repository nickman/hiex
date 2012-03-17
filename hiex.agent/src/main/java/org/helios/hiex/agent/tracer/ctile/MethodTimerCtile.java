/**
 * 
 */
package org.helios.hiex.agent.tracer.ctile;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.helios.hiex.agent.tracer.base.BaseSingleMetricTracerFactory;

import com.wily.introscope.agent.IAgent;
import com.wily.introscope.agent.stat.DataAccumulatorFactory;
import com.wily.introscope.agent.stat.IDataAccumulator;
import com.wily.introscope.agent.stat.IIntegerAverageDataAccumulator;
import com.wily.introscope.agent.trace.ASingleMetricTracerFactory;
import com.wily.introscope.agent.trace.INameFormatter;
import com.wily.introscope.agent.trace.InvocationData;
import com.wily.introscope.agent.trace.ProbeIdentification;
import com.wily.util.StringUtils;
import com.wily.util.feedback.IModuleFeedbackChannel;
import com.wily.util.feedback.Module;
import com.wily.util.heartbeat.ITimestampedRunnable;
import com.wily.util.properties.AttributeListing;


/**
 * <p>Title: MethodTimerCtile</p>
 * <p>Description: Introscope Agent Tracer to trace the nth percentile value during
 * an interval.
 * <ul>
 * <li>The threshold elapsed time value of the nth percentile.</li>
 * <li>The number of transactions with elapsed times at or below the nth
 * percentile.</li>
 * <li>The number of transactions with elapsed times above the nth percentile.</li>
 * <li>The percentage of transactions with elapsed times at or below the nth
 * percentile.</li>
 * <li>The percentage of transactions with elapsed times above the nth
 * percentile.</li>
 * </ul></p> 
 * @todo: Reentrancy:if kNone or kMethodName, a single instance of the tracer needs to be able to handle multiple crosscuts.
 * Otherwise, it is kInstance in which case one instance of this tracer will be created per crosscut, so no multiplexing is necessary.
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.agent.tracer.ctile.MethodTimerCtile</code></p>
 */
public class MethodTimerCtile extends BaseSingleMetricTracerFactory {
	/** The percentile to calculate */
	protected int percentile = 0;
	/** Metric name --> interval and data accumulators map */
	protected final Map<String, CtileMetricAccumulator> accumulators = new ConcurrentHashMap<String, CtileMetricAccumulator>(100);
	/** Alternator */
	protected AtomicBoolean alternator = new AtomicBoolean(false);
	/** Trace performance flag */
	protected boolean tracePerformance = false;
	/** The map of constants to configured sub metric names */
	protected final Map<String, String> metricNameMap = new HashMap<String, String>();
	/** The pbd defined resource pattern */
	protected String formattedResource = null;
	/** The name of the percentile based resource segment */
	protected String percentileResourceName = null;
	/** The name of the summary percentile based resource segment */
	protected String summaryPercentileResourceName = null;
	
	/** The key of the percentile parameter */
	public static final String PERCENTILE_PARAM = "percentile";
	/** The key of the performance parameter */
	public static final String PERCENTILE_PERF = "performance";

	/** The counter key of the percentile elapsed time */
	public static final String PERCENTILE_ELAPSED = "percentileelapsed";
	/** The counter key of the count at or below percentile */
	public static final String COUNT_LTOE_PERCENTILE = "countltoe";
	/** The counter key of the count above percentile */
	public static final String COUNT_GT_PERCENTILE = "countgt";

	/** The counter key of the percentage at or below percentile */
	public static final String PERCENT_LTOE_PERCENTILE = "percentltoe";
	/** The counter key of the percentage above percentile */
	public static final String PERCENT_GT_PERCENTILE = "percentlgt";

	/** The counter key of the mean */
	public static final String MEAN_ELAPSED = "mean";
	/** The counter key of the count */
	public static final String COUNT_ELAPSED = "count";
	/** The counter key of the standard deviation */
	public static final String STDDEV_ELAPSED = "stddev";
	
	/** Valid sub names that we can generate an accumulator for */
	public static final Set<String> SUB_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[]{
			MethodTimerCtile.PERCENTILE_ELAPSED , MethodTimerCtile.COUNT_LTOE_PERCENTILE,
			MethodTimerCtile.COUNT_GT_PERCENTILE, MethodTimerCtile.MEAN_ELAPSED,
			MethodTimerCtile.PERCENT_GT_PERCENTILE, MethodTimerCtile.PERCENT_LTOE_PERCENTILE,
			MethodTimerCtile.STDDEV_ELAPSED, MethodTimerCtile.STDDEV_ELAPSED,
			MethodTimerCtile.COUNT_ELAPSED			
			
	})));
	

	/**
	 * Creates a new MethodTimeCtile tracer instance.
	 * @param agent
	 * @param parameters
	 * @param probe
	 * @param sampleTracedObject
	 */
	public MethodTimerCtile(IAgent agent, AttributeListing parameters, ProbeIdentification probe, Object sampleTracedObject) {
		super(agent, parameters, probe, sampleTracedObject);		
		formattedResource = this.getFormattedName();				
		percentile = getParameter(PERCENTILE_PARAM, 90);
		tracePerformance = getParameter(PERCENTILE_PERF, false);
		percentileResourceName = "Percentile " + percentile;
		summaryPercentileResourceName = getCtileResourceName(formattedResource, percentileResourceName);
		initPatterns();
		if (DEBUG) {			
			dataAccumulatorFactory.safeGetLongConstantDataAccumulator(summaryPercentileResourceName + "|Debug:Percentile Period (ms)", schedulePeriod);
		}
	}

	/**
	 * Initializes the parameterized metric names and creates a map of data accumulators.
	 */
	private void initPatterns() {
		// ======
		// Standard Metrics
		// ======
		metricNameMap.put(PERCENTILE_ELAPSED, summaryPercentileResourceName + getParameter(PERCENTILE_ELAPSED, "Average Elapsed Time (ms)"));

		
		// ======
		// Optional Metrics
		// ======
		metricMapIfNotNull(COUNT_LTOE_PERCENTILE, COUNT_LTOE_PERCENTILE, summaryPercentileResourceName);
		metricMapIfNotNull(COUNT_GT_PERCENTILE, COUNT_GT_PERCENTILE, summaryPercentileResourceName);
		metricMapIfNotNull(PERCENT_LTOE_PERCENTILE, PERCENT_LTOE_PERCENTILE, summaryPercentileResourceName);
		metricMapIfNotNull(PERCENT_GT_PERCENTILE, PERCENT_GT_PERCENTILE, summaryPercentileResourceName);
		metricMapIfNotNull(MEAN_ELAPSED, MEAN_ELAPSED, summaryPercentileResourceName);
		metricMapIfNotNull(COUNT_ELAPSED, COUNT_ELAPSED, summaryPercentileResourceName);
		metricMapIfNotNull(STDDEV_ELAPSED, STDDEV_ELAPSED, summaryPercentileResourceName);
		
		// ======
		// Performance Metrics
		// ======
		if (tracePerformance) {
			metricMapIfNotNull(PERCENTILE_PERF, PERCENTILE_PERF, summaryPercentileResourceName);
		}
		
		// ======
		// Debug Metrics    TBD
		// ======
//		if (DEBUG) {
//			metricNameMap.put(DEBUG, percentileResourceName + "|Debug:Debug Output");
//		}
	}
	
	/**
	 * Interrogates the parameter map for optional metric keys and adds them to the metric map if they are defined.
	 * @param key The metric key
	 * @param paramName The parameter key
	 * @param resourcePrefix The resource prefix for the full metric name
	 */
	protected void metricMapIfNotNull(String key, String paramName, String resourcePrefix) {
		String value = getParameter(key, null);
		if(value!=null) {
			metricNameMap.put(key, resourcePrefix + value);
		}
	}

	/**
	 * Callback from the agent scheduler on every percentile window period.
	 * 
	 * @param t
	 * @see com.wily.util.heartbeat.ITimestampedRunnable#ITimestampedRunnable_execute(long)
	 */
	public void ITimestampedRunnable_execute(long t) {
		long start = System.currentTimeMillis();
		int count = 0;
		alternator.set(!alternator.get());
		for (Entry<String, CtileMetricAccumulator> acc : accumulators.entrySet()) {
			try {
				log.debug("Interval Calc Percentile Issuing for [" , acc.getKey() , "]");
				acc.getValue().calcAndPublishInterval(metricNameMap);
				count++;
			} catch (Exception e) {
				log.error("Interval Calc Percentile Failure for [" , acc.getKey(), "]", e);				
			}
		}
//		long elapsed = System.currentTimeMillis() - start;
//		if (debug) {
//			getDataAccumulatorFactory().safeGetLongAverageDataAccumulator(
//					this.summaryPercentileResourceName
//							+ "|Debug:Total Percentile Calc Elapsed Time (ms)")
//					.ILongAggregatingDataAccumulator_recordDataPoint(elapsed);
//			getDataAccumulatorFactory()
//					.safeGetIntegerFluctuatingCounterDataAccumulator(
//							this.summaryPercentileResourceName
//									+ "|Debug:Total Percentile Calc Items Count")
//					.IIntegerCounterDataAccumulator_setValue(count);
//		}
	}

	/**
	 * Acquires the current interval accumulator.
	 * @param counterName
	 * @return
	 */
	protected CtileMetricAccumulator getCtileMetricAccumulator(String counterName) {
		CtileMetricAccumulator cma = accumulators.get(counterName);
		if (cma == null) {
			log.debug("Creating CtileMetricAccumulator for [", counterName, "]" );
			try {
				cma = new CtileMetricAccumulator(percentile, alternator, tracePerformance, DEBUG, log);
				accumulators.put(counterName, cma);
			} catch (Throwable e) {
				e.printStackTrace(System.err);
				throw new RuntimeException("Failed to create CtileMetricAccumulator for [" + counterName + "]", e);
			}
		}
		return cma;
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
//	private IDataAccumulator createIDataAccumulator(String subName) {
//		if (SUB_NAMES.contains(subName)) {
//			return tracerFactory.safeGetIntegerFluctuatingCounterDataAccumulator(metricResource + "|" 	+ metricNameMap.get(subName));
//		} else if(MethodTimerCtile.PERCENTILE_PERF.equals(subName)) {
//			return tracerFactory.safeGetLongAverageDataAccumulator(metricResource + "|" + metricNameMap.get(subName));
//		} else {
//			throw new RuntimeException("SubName Not Indexed [" + subName + "]");
//		}
//	}
	

	/**
	 * @param formattedMetricName
	 * @return
	 * @see com.wily.introscope.agent.trace.ASingleMetricTracerFactory#createDataAccumulator(java.lang.String)
	 */
	protected final IDataAccumulator createDataAccumulator(String formattedMetricName) {
		return getDataAccumulatorFactory()
				.safeGetIntegerAverageDataAccumulator(formattedMetricName);
	}

	/**
	 * Determines the ctile calculation resource name
	 * @param fmtResource the base resource
	 * @param pctResource the percent resource
	 * @return the full resource
	 */
	protected String getCtileResourceName(String fmtResource, String pctResource) {
		String ctileResource = fmtResource;
		Pattern p = Pattern.compile("(\\{.*?})");
		Matcher m = p.matcher(fmtResource);
		if(m.find()) {
			ctileResource = m.replaceAll("");			
		}
		ctileResource = ctileResource.replaceAll("\\|{2,}", "|");
		if(ctileResource.endsWith("|")) ctileResource = ctileResource.substring(0, ctileResource.length()-1);
		return new StringBuilder(ctileResource).append("|").append(pctResource).append(":").toString();

	}

	/**
	 * @param tracerIndex
	 * @param data
	 * @see com.wily.introscope.agent.trace.ITracer#ITracer_startTrace(int,
	 *      com.wily.introscope.agent.trace.InvocationData)
	 */
	public void ITracer_startTrace(int tracerIndex, InvocationData data) {
		data.storeWallClockStartTime();		
	}

	/**
	 * Callback from the agent on instrumented method completion.
	 * 
	 * @param tracerIndex
	 * @param data
	 * @see com.wily.introscope.agent.trace.ITracer#ITracer_finishTrace(int,
	 *      com.wily.introscope.agent.trace.InvocationData)
	 */
	public void ITracer_finishTrace(int tracerIndex, InvocationData data) {
		int time = data.getWallClockElapsedTimeAsInt();
		String metricName = nameFormatter.INameFormatter_format(formattedResource, data);
		IIntegerAverageDataAccumulator average = (IIntegerAverageDataAccumulator) dataAccumulatorFactory.safeGetIntegerAverageDataAccumulator(metricName + ":Average Elapsed Time (ms)");
		if (!average.IDataAccumulator_isShutOff()) {
			average.IIntegerAggregatingDataAccumulator_recordDataPoint(time);
		}		
		getCtileMetricAccumulator(metricName).addElapsedTime(time);
	}


}

/*
 * public static int calcPercentile(boolean weighted, int percentile,
 * int...values) { if(weighted) return calcWeightedPercentile(percentile,
 * values); else return calcPercentile(percentile, values); } private static int
 * calcPercentile(int percentile, int...values) { if(values==null ||
 * values.length < 1 || (percentile <1 || percentile >99)) throw new
 * IllegalArgumentException("Invalid values or percentile"); TIntArrayList
 * valuesArray = new TIntArrayList(values); int max = valuesArray.max(); return
 * calcPercentile(max, percentile); } private static int
 * calcWeightedPercentile(int percentile, int...values) { if(values==null ||
 * values.length < 1 || (percentile <1 || percentile >99)) throw new
 * IllegalArgumentException("Invalid values or percentile"); TIntArrayList
 * valuesArray = new TIntArrayList(values); int max = valuesArray.max(); int min =
 * valuesArray.min(); int range = max-min; return calcPercentile(range,
 * percentile); }
 */

/*
 * private void initCounters() { String formattedName = getFormattedName();
 * DataAccumulatorFactory af = getDataAccumulatorFactory();
 * 
 * if(patterns.get(PERCENTILE_ELAPSED)!=null) { counters.put(PERCENTILE_ELAPSED,
 * af.safeGetIntegerFluctuatingCounterDataAccumulator(
 * formattedName.split(":")[0] + "|" + percentile + "th Percentile:" +
 * patterns.get(PERCENTILE_ELAPSED)));
 * if(log.isDebugEnabled())log.debug("Created Counter [" +
 * counters.get(PERCENTILE_ELAPSED)); }
 * if(patterns.get(COUNT_LTOE_PERCENTILE)!=null) {
 * counters.put(COUNT_LTOE_PERCENTILE,
 * af.safeGetIntegerFluctuatingCounterDataAccumulator(
 * formattedName.split(":")[0] + "|" + percentile + "th Percentile:" +
 * patterns.get(COUNT_LTOE_PERCENTILE)));
 * if(log.isDebugEnabled())log.debug("Created Counter [" +
 * counters.get(COUNT_LTOE_PERCENTILE)); }
 * if(patterns.get(COUNT_GT_PERCENTILE)!=null) {
 * counters.put(COUNT_GT_PERCENTILE,
 * af.safeGetIntegerFluctuatingCounterDataAccumulator(
 * formattedName.split(":")[0] + "|" + percentile + "th Percentile:" +
 * patterns.get(COUNT_GT_PERCENTILE)));
 * if(log.isDebugEnabled())log.debug("Created Counter [" +
 * counters.get(COUNT_GT_PERCENTILE)); } if(patterns.get(MEAN_ELAPSED)!=null) {
 * counters.put(MEAN_ELAPSED,
 * af.safeGetIntegerFluctuatingCounterDataAccumulator(
 * formattedName.split(":")[0] + "|" + percentile + "th Percentile:" +
 * patterns.get(MEAN_ELAPSED))); if(log.isDebugEnabled())log.debug("Created
 * Counter [" + counters.get(MEAN_ELAPSED)); }
 * if(patterns.get(COUNT_ELAPSED)!=null) { counters.put(COUNT_ELAPSED,
 * af.safeGetIntegerFluctuatingCounterDataAccumulator(
 * formattedName.split(":")[0] + "|" + percentile + "th Percentile:" +
 * patterns.get(COUNT_ELAPSED))); if(log.isDebugEnabled())log.debug("Created
 * Counter [" + counters.get(COUNT_ELAPSED)); }
 * if(patterns.get(STDDEV_ELAPSED)!=null) { counters.put(STDDEV_ELAPSED,
 * af.safeGetIntegerFluctuatingCounterDataAccumulator(
 * formattedName.split(":")[0] + "|" + percentile + "th Percentile:" +
 * patterns.get(STDDEV_ELAPSED))); if(log.isDebugEnabled())log.debug("Created
 * Counter [" + counters.get(STDDEV_ELAPSED)); } if(tracePerformance) {
 * counters.put(PERCENTILE_PERF, af.safeGetLongAverageDataAccumulator(
 * formattedName.split(":")[0] + "|" + percentile + "th Percentile:" +
 * patterns.get(PERCENTILE_PERF))); if(log.isDebugEnabled())log.debug("Created
 * Counter [" + counters.get(PERCENTILE_PERF)); } if(debug) {
 * counters.put(DEBUG, af.safeGetStringEveryEventDataAccumulator(
 * formattedName.split(":")[0] + "|" + percentile + "th Percentile|Debug:" +
 * patterns.get(DEBUG))); if(log.isDebugEnabled())log.debug("Created Counter [" +
 * counters.get(DEBUG)); counters.put(DEBUG_MEMORY,
 * af.safeGetLongFluctuatingCounterDataAccumulator( formattedName.split(":")[0] +
 * "|" + percentile + "th Percentile|Debug:" + patterns.get(DEBUG_MEMORY)));
 * if(log.isDebugEnabled())log.debug("Created Counter [" +
 * counters.get(DEBUG_MEMORY)); counters.put(DEBUG_DEEP_MEMORY,
 * af.safeGetLongFluctuatingCounterDataAccumulator( formattedName.split(":")[0] +
 * "|" + percentile + "th Percentile|Debug:" +
 * patterns.get(DEBUG_DEEP_MEMORY))); if(log.isDebugEnabled())log.debug("Created
 * Counter [" + counters.get(DEBUG_DEEP_MEMORY)); counters.put(DEBUG_ACC_MEMORY,
 * af.safeGetLongFluctuatingCounterDataAccumulator( formattedName.split(":")[0] +
 * "|" + percentile + "th Percentile|Debug:" + patterns.get(DEBUG_ACC_MEMORY)));
 * if(log.isDebugEnabled())log.debug("Created Counter [" +
 * counters.get(DEBUG_ACC_MEMORY)); }
 * 
 * 
 * if(log.isDebugEnabled()) { StringBuilder b = new
 * StringBuilder("\nMethodTimerCtile Metric CounterTypes:"); for(Entry<String,
 * IDataAccumulator> entry: counters.entrySet()) {
 * entry.getValue().IDataAccumulator_getMetric();
 * b.append("\n\t").append(entry.getKey()).append(":").append(entry.getValue().getClass().getName());
 * b.append("\n\t\t").append(entry.getValue().IDataAccumulator_getMetric().getSuffixingAttributeURL()); }
 * b.append("\n"); log.debug(b.toString()); } }
 */

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
package org.helios.hiex.agent.tracer.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.helios.hiex.util.FlexiLogger;

import com.wily.introscope.agent.IAgent;
import com.wily.introscope.agent.recording.MetricRecordingAdministrator;
import com.wily.introscope.agent.stat.DataAccumulatorFactory;
import com.wily.introscope.agent.trace.ASingleInstanceTracerFactory;
import com.wily.introscope.agent.trace.INameFormatter;
import com.wily.introscope.agent.trace.InvocationData;
import com.wily.introscope.agent.trace.ProbeIdentification;
import com.wily.introscope.agent.trace.ReentrancyLevel;
import com.wily.util.feedback.IModuleFeedbackChannel;
import com.wily.util.feedback.Module;
import com.wily.util.feedback.SimpleModuleFeedbackChannel;
import com.wily.util.heartbeat.ITimestampedRunnable;
import com.wily.util.properties.AttributeListing;

/**
 * <p>Title: BaseSingleMetricTracerFactory</p>
 * <p>Description: Base class for single metric tracer factories</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.agent.tracer.base.BaseSingleMetricTracerFactory</code></p>
 */
public class BaseSingleMetricTracerFactory extends ASingleInstanceTracerFactory implements ITimestampedRunnable {
	/** The tracer module */
	protected final Module module;
	/** The tracer logging feedback channel */
	protected final IModuleFeedbackChannel channel;
	/** The flexilogger */
	protected final FlexiLogger log;
	/** A reference to the java agent */
	protected final IAgent agent;
	/** The probe identification */
	protected final ProbeIdentification probe;
	/** A map of the tracer parameters */
	protected final Map<String, String> parameterMap;
	/** The reentrancy level for this tracer */
	protected ReentrancyLevel rLevel = null;
	/** The configured scheduled callback period */
	protected long schedulePeriod = -1;
	/** Flag indicating DEBUG setting */
	public final boolean DEBUG;
	/** The reentrancy of this tracer */
	protected final ReentrancyLevel reentrancy;
	/** The reentrancy name of this tracer */
	protected final String reentrancyName;
	
	/** The name formatter */
	protected final INameFormatter nameFormatter;
	
	// ==========================================================================
	// 			Administrators
	// ==========================================================================
	/** Administrator used to listen on metric shutoff */
	protected final MetricRecordingAdministrator metricRecordingAdministrator;
	/** Administrator for creating accumulators */
	protected final DataAccumulatorFactory dataAccumulatorFactory;
	// ==========================================================================
	/** A set of lower case strings indicating a true boolean */
	public static final Set<String> BOOLEANS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("yes", "true")));
	/** The parameter constant for the scheduled heartbeat callback in ms */
	public static final String PARAM_HEART_BEAT = "schedule";
	/** The parameter constant for the DEBUG setting for this instance */
	public static final String PARAM_DEBUG = "debug";
	
	/**
	 * Creates a new BaseSingleMetricTracerFactory
	 * @param agent A reference to the agent
	 * @param parameters The tracer instance parameters
	 * @param probe The probe ientification
	 * @param sampleTracedObject A sample of the instrumented object
	 */
	public BaseSingleMetricTracerFactory(IAgent agent, AttributeListing parameters, ProbeIdentification probe, Object sampleTracedObject) {
		super(agent, parameters, probe, sampleTracedObject);
		this.agent = agent;
		this.probe = probe;
		module = new Module(getClass().getSimpleName());
		channel = new SimpleModuleFeedbackChannel(agent.IAgent_getModuleFeedback(), module.getName());
		log = new FlexiLogger(module, channel);
		parameterMap = new HashMap<String, String>(parameters.size());
		for(Iterator<String> iter = parameters.getKeys(); iter.hasNext();) {
			String key = iter.next().trim();
			parameterMap.put(key.trim().toLowerCase(), parameters.get(key));
		}
		if (getCustomNameFormatter() == null) {
			final BaseSingleMetricTracerFactory finalBase = this;
			nameFormatter = new INameFormatter() {
				public String INameFormatter_format(String paramString, InvocationData paramInvocationData) {
					return new StringBuilder(finalBase.formatParameterizedResource(paramInvocationData)).append(":").append(paramString.trim()).toString();
				}
			};
		} else {
			nameFormatter = getCustomNameFormatter();
		}
		
		DEBUG = getParameter(PARAM_DEBUG, false);
		metricRecordingAdministrator = agent.IAgent_getMetricRecordingAdministrator();
		dataAccumulatorFactory = agent.IAgent_getDataAccumulatorFactory();
		//metricRecordingAdministrator.addMetricGroup(metricKey, metrics)
		reentrancy = calculateReentrancyLevel(ReentrancyLevel.kNone);
		reentrancyName = getParameter("reentrancy", "kNone");
		initializeScheduler();
		log.info("Created [", this.getClass().getName(), "] instance.");
		if(DEBUG) {
			log.info(probeToString());
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.wily.introscope.agent.trace.ITracer#ITracer_finishTrace(int, com.wily.introscope.agent.trace.InvocationData)
	 */
	@Override
	public void ITracer_finishTrace(int tracerIndex, InvocationData data) {

	}

	/**
	 * {@inheritDoc}
	 * @see com.wily.introscope.agent.trace.ITracer#ITracer_startTrace(int, com.wily.introscope.agent.trace.InvocationData)
	 */
	@Override
	public void ITracer_startTrace(int tracerIndex, InvocationData data) {

	}



	/**
	 * {@inheritDoc}
	 * @see com.wily.util.heartbeat.ITimestampedRunnable#ITimestampedRunnable_execute(long)
	 */
	@Override
	public void ITimestampedRunnable_execute(long timestamp) {		
		
	}
	
	/**
	 * 	 * <p>{@link com.wily.introscope.agent.trace.ATracerFactory#calculateReentrancyLevel} determines the reentrancy by 
	 * inspecting a paramter called <b><code>reentrancy</code></b>. The reentrancy returned for the value of this parameter:<ul>
	 * <li><b>instance</b>:ReentrancyLevel.kInstance</li>
	 * <li><b>methodname</b>:ReentrancyLevel.kMethodName</li>
	 * <li><b>null</b>:ReentrancyLevel.defaultReentrancyLevel</li>
	 * </ul>
	 * {@inheritDoc}
	 * @see com.wily.introscope.agent.trace.ITracerFactory#ITracerFactory_getReentrancyLevel()
	 */
	@Override
	public ReentrancyLevel ITracerFactory_getReentrancyLevel() {		
		return reentrancy;
	}

	/**
	 * {@inheritDoc}
	 * @see com.wily.introscope.agent.trace.ITracerFactory#ITracerFactory_isShutoff()
	 */
	@Override
	public boolean ITracerFactory_isShutoff() {		
		return false;
	}
	
	/**
	 * Checks the parameters for a schedule and registers if found.
	 */
	protected void initializeScheduler() {
		schedulePeriod = getParameter(PARAM_HEART_BEAT, -1L);
		if(schedulePeriod>0) {
			agent.IAgent_getCommonHeartbeat().addBehavior(this, module.getName(), true, schedulePeriod, true);
			log.info("[", module.getName() , "] Scheduled Callback [", schedulePeriod, "] ms.");
		}
	}
	
	/**
	 * Returns a formatted parameter value
	 * @param name The name of the parameter
	 * @param defaultValue The default value to return if the passed value is null
	 * @return a formatted value
	 */
	protected String getParameter(String name, String defaultValue) {
		if(name==null) throw new IllegalArgumentException("The passed parameter name was null", new Throwable());
		String value = parameterMap.get(name.trim().toLowerCase());
		if(value==null) return defaultValue;
		return value.trim();
	}
	
	/**
	 * Returns a formatted parameter value
	 * @param name The name of the parameter
	 * @param defaultValue The default value to return if the passed value is null
	 * @return a formatted value
	 */
	protected int getParameter(String name, int defaultValue) {
		if(name==null) throw new IllegalArgumentException("The passed parameter name was null", new Throwable());
		String value = parameterMap.get(name.trim().toLowerCase());
		if(value==null) return defaultValue;
		return Integer.parseInt(value.trim());
	}
	
	/**
	 * Returns a formatted parameter value
	 * @param name The name of the parameter
	 * @param defaultValue The default value to return if the passed value is null
	 * @return a formatted value
	 */
	protected long getParameter(String name, long defaultValue) {
		if(name==null) throw new IllegalArgumentException("The passed parameter name was null", new Throwable());
		String value = parameterMap.get(name.trim().toLowerCase());
		if(value==null) return defaultValue;
		return Long.parseLong(value.trim());
	}
	
	/**
	 * Returns a formatted parameter value
	 * @param name The name of the parameter
	 * @param defaultValue The default value to return if the passed value is null
	 * @return a formatted value
	 */
	protected boolean getParameter(String name, boolean defaultValue) {
		if(name==null) throw new IllegalArgumentException("The passed parameter name was null", new Throwable());
		String value = parameterMap.get(name.trim().toLowerCase());
		if(value==null) return defaultValue;
		return BOOLEANS.contains(value.trim().toLowerCase());
	}


	/**
	 * Generates a formated string representing the probe details
	 * @return a formated string representing the probe details
	 */
	public String probeToString() {
		StringBuilder b = new StringBuilder("Probe [");
		b.append("\n\tClass Name:").append(probe.getProbeClassName())
		.append("\n\tMethod Descriptor:").append(probe.getProbeMethodDescriptor())
		.append("\n\tMethod Name:").append(probe.getProbeMethodName())
		.append("\n\tContainer Name:").append(probe.getRuntimeContainerName())
		.append("\n\tRuntime Full Class Name:").append(probe.getRuntimeFullClassName())
		.append("\n]");
		return b.toString();
	}

	
	

}

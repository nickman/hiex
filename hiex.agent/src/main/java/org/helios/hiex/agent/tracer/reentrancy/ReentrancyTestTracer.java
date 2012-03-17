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
package org.helios.hiex.agent.tracer.reentrancy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.helios.hiex.agent.tracer.base.BaseSingleMetricTracerFactory;

import com.wily.introscope.agent.IAgent;
import com.wily.introscope.agent.trace.InvocationData;
import com.wily.introscope.agent.trace.ProbeIdentification;
import com.wily.util.properties.AttributeListing;

/**
 * <p>Title: ReentrancyTestTracer</p>
 * <p>Description: Test for reentrancy settings.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.agent.tracer.reentrancy.ReentrancyTestTracer</code></p>
 */

public class ReentrancyTestTracer extends BaseSingleMetricTracerFactory {
	private static final AtomicLong instances = new AtomicLong(0);
	private static final ConcurrentHashMap<String, AtomicLong> methodInstances = new ConcurrentHashMap<String, AtomicLong>();
	public static final String TEMPLATE = 
			"\nInstance Count:%s" +
		    "\nMethod Count:%s" +
		    "\nCrosscut:%s" +
		    "\nClassloader:%s" +
		    "\nReentrancy:%s" +		    
		    "\n======================================";
	/**
	 * Creates a new ReentrancyTestTracer
	 * @param agent
	 * @param parameters
	 * @param probe
	 * @param sampleTracedObject
	 */
	public ReentrancyTestTracer(IAgent agent, AttributeListing parameters,
			ProbeIdentification probe, Object sampleTracedObject) {
		super(agent, parameters, probe, sampleTracedObject);
		long instanceCount = instances.incrementAndGet();
		String crossCut = probe.getRuntimeSimpleClassName() + "-" +  probe.getProbeMethodName() + probe.getProbeMethodDescriptor();
		methodInstances.putIfAbsent(crossCut, new AtomicLong(0));
		long mi = methodInstances.get(crossCut).incrementAndGet();
		dataAccumulatorFactory.safeGetLongMonotonicallyIncreasingCounterDataAccumulator("Reentrancy:" + crossCut).ILongCounterDataAccumulator_setValue(mi);
		System.out.println(String.format(
				TEMPLATE, 
				instanceCount, 
				mi, 
				crossCut, 
				getClass().getClassLoader()==null ? "Bootstrap" : getClass().getClassLoader().toString(),
				this.reentrancyName				
			)
		);
		dataAccumulatorFactory.safeGetLongMonotonicallyIncreasingCounterDataAccumulator("Reentrancy:Instances").ILongCounterDataAccumulator_setValue(instanceCount);		
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.wily.introscope.agent.trace.ITracer#ITracer_finishTrace(int, com.wily.introscope.agent.trace.InvocationData)
	 */
	@Override
	public void ITracer_finishTrace(int tracerIndex, InvocationData data) {
		log.info("\n\tfinishTrace [", data.getProbeInformation().getProbeIdentification().getProbeMethodName(), "] tI:", tracerIndex);
	}

	/**
	 * {@inheritDoc}
	 * @see com.wily.introscope.agent.trace.ITracer#ITracer_startTrace(int, com.wily.introscope.agent.trace.InvocationData)
	 */
	@Override
	public void ITracer_startTrace(int tracerIndex, InvocationData data) {
		log.info("\n\tstartTrace [", data.getProbeInformation().getProbeIdentification().getProbeMethodName(), "] tI:", tracerIndex);
	}
	

}

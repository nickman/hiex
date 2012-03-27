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
package org.helios.hiex.jsagent;

import static com.wily.introscope.spec.metric.Frequency.kDefaultSystemFrequencyInSeconds;

import java.lang.reflect.Field;
import java.util.List;

import com.wily.introscope.server.beans.javascriptcalculator.CalculatorScript.JavascriptResultSetHelper;
import com.wily.introscope.spec.metric.AgentMetric;
import com.wily.introscope.spec.metric.Frequency;
import com.wily.introscope.spec.metric.Metric;
import com.wily.introscope.spec.metric.MetricData;
import com.wily.introscope.spec.server.beans.metricdata.IMetricDataValue;
import com.wily.util.feedback.SimpleModuleFeedbackChannel;
/**
 * <p>Title: CalculatorHelper</p>
 * <p>Description: A set of utility functions to simplify metric rewriting</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.jsagent.CalculatorHelper</code></p>
 */
public class CalculatorHelper {
	/** The wily logger */
	protected final SimpleModuleFeedbackChannel log = new SimpleModuleFeedbackChannel(getClass().getName());
	/** The default frequency */
	protected final Frequency DEFAULT_FREQUENCY = Frequency.getFrequencyInSeconds(kDefaultSystemFrequencyInSeconds);
	/** The JavascriptResultSetHelper <code>fResultValues</code> field reference */
	protected volatile Field fResultValues = null;
	
	public void process(MetricData[] metricsIn, JavascriptResultSetHelper metricOut) {
		for(MetricData md: metricsIn) {
			AgentMetric am = md.getAgentMetric();
			
		}
	}
	
	public static CalculatorHelper getInstance() {
		return new CalculatorHelper();
	}
	
	public void addMetric(String fullMetricName, int type, IMetricDataValue value, JavascriptResultSetHelper metricOut) {
		try {
			Metric metric = Metric.getMetric(fullMetricName, type);			 
			MetricData binding = new MetricData(metric, DEFAULT_FREQUENCY, value);
			addMetricOut(binding, metricOut);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Failed to create Metric for [" + fullMetricName + "] and type [" + type + "]", e);
		}
	}
	
	protected void addMetricOut(MetricData binding, JavascriptResultSetHelper metricOut) {
		try {
			if(fResultValues==null) {
				initFResultValues();
			}
			List<MetricData> dataList = (List<MetricData>) fResultValues.get(metricOut);
			dataList.add(binding);
		} catch (Exception e) {
			log.error("Failed to add MetricData for [" + binding + "]", e);
		}
	}
	
	protected void initFResultValues() {
		try {
			fResultValues = JavascriptResultSetHelper.class.getDeclaredField("fResultValues");
		} catch (Exception e) {			
			throw new RuntimeException("Failed to get fResultValues field from  JavascriptResultSetHelper", e);
		}
	}
}

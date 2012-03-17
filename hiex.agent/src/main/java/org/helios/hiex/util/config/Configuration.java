/**
 * Helios, OpenSource  Monitoring
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
package org.helios.hiex.util.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * <p>Title: Configuration</p>
 * <p>Description: Static helper methods for configuration</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.util.config.Configuration</code></p>
 */
public class Configuration {
	
	/** A set of lower case strings indicating a true boolean */
	public static final Set<String> BOOLEANS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("yes", "true")));
	
	/**
	 * Returns a formatted parameter value
	 * @param value The value retrieved from the tracer factory
	 * @param defaultValue The default value to return if the passed value is null
	 * @return a formatted value
	 */
	public static String getParameter(String value, String defaultValue) {
		if(value==null) return defaultValue;
		return value.trim();
	}
	
	/**
	 * Returns a formatted parameter value
	 * @param value The value retrieved from the tracer factory
	 * @param defaultValue The default value to return if the passed value is null
	 * @return a formatted value
	 */
	public static int getParameter(String value, int defaultValue) {
		if(value==null) return defaultValue;
		return Integer.parseInt(value.trim());
	}
	
	/**
	 * Returns a formatted parameter value
	 * @param value The value retrieved from the tracer factory
	 * @param defaultValue The default value to return if the passed value is null
	 * @return a formatted value
	 */
	public static long getParameter(String value, long defaultValue) {
		if(value==null) return defaultValue;
		return Long.parseLong(value.trim());
	}
	
	/**
	 * Returns a formatted parameter value
	 * @param value The value retrieved from the tracer factory
	 * @param defaultValue The default value to return if the passed value is null
	 * @return a formatted value
	 */
	public static boolean getParameter(String value, boolean defaultValue) {
		if(value==null) return defaultValue;
		return BOOLEANS.contains(value.trim().toLowerCase());
	}
	
	
	
}

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
package org.helios.hiex.util.math;

/**
 * <p>Title: SimpleMath</p>
 * <p>Description: Some basic static convenience math methods </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.util.math.SimpleMath</code></p>
 */

public class SimpleMath {
	/**
	 * Calculates a percentage and returns it as a long
	 * @param part The part
	 * @param whole The whole
	 * @return the percentage as a long
	 */
	public static long lpercent(double part, double whole) {
		if(part<=0 || whole<=0) return 0;
		double p = part/whole*100;
		return (long)p;
	}
	
	/**
	 * Calculates a percentage and returns it as an int
	 * @param part The part
	 * @param whole The whole
	 * @return the percentage as an int
	 */
	public static int ipercent(double part, double whole) {
		if(part<=0 || whole<=0) return 0;
		double p = part/whole*100;
		return (int)p;
	}
	
}

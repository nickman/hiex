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

import gnu.trove.procedure.TIntProcedure;



/**
 * <p>Title: PercentileDiscriminator</p>
 * <p>Description: Impl. of a Trove TIntProcedure to divide the elapsed times across the percentile line.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.agent.tracer.ctile.PercentileDiscriminator</code></p>
 */
class PercentileDiscriminator implements TIntProcedure {

	int percentile = 0;

	/**
	 * Creates a new PercentileDiscriminator
	 * @param percentile The percentile on which to split the counts.
	 */
	public PercentileDiscriminator(int percentile) {
		this.percentile = percentile;
	}

	public boolean execute(int i) {
		return i <= percentile;
	}

	public void setPercentile(int percentile) {
		this.percentile = percentile;
	}
}

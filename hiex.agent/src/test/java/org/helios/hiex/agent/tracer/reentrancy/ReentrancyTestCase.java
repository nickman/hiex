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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: ReentrancyTestCase</p>
 * <p>Description: Reentrancy tracer test case</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.agent.tracer.reentrancy.ReentrancyTestCase</code></p>
 */

public class ReentrancyTestCase {
	public static final ThreadLocal<AtomicInteger> reentrancy = new ThreadLocal<AtomicInteger>() {
		protected AtomicInteger initialValue() {
			return new AtomicInteger(0);
		}
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log("ReentrancyTestCase Test Case");
		ReentrancyTestCase tc = new ReentrancyTestCase();
		ReentrancyTestCase tc1 = new ReentrancyTestCase();
		ReentrancyTestCase tc2 = new RET2();
		log("Sleeping");
		try { Thread.sleep(10000); } catch (Exception e) {}
		log("Starting test case");
		for(int i = 0; i < 3; i++) {
			tc.level1();
			tc1.level1();
			tc2.level1();
			log("Completed Loop:" + i);
		}
		log("Test Complete");
		try { Thread.sleep(Long.MAX_VALUE); } catch (Exception e){}
	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}
	
	
	public void level1() {
		try { Thread.sleep(1000); } catch (Exception e) {}
		level1_level2();
	}

	public void level1_level2() {
		try { Thread.sleep(1000); } catch (Exception e) {}
		level1_level2_level3();
	}
	
	
	public void level1_level2_level3() {
		try { Thread.sleep(1000); } catch (Exception e) {}
		if(reentrancy.get().incrementAndGet()<1) {
			level1_level2_level3();
		}
		reentrancy.get().decrementAndGet();
	}
	

}

package org.helios.hiex.agent.tracer.reentrancy;

/**
 * <p>Title: RET2</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.agent.tracer.reentrancy.RET2</code></p>
 */
public class RET2 extends ReentrancyTestCase {

	public RET2() {
		log("Created RET2==========================");
	}
	
	public void level1_level2() {
		try { Thread.sleep(1000); } catch (Exception e) {}
		level1_level2_level3();
	}	

}

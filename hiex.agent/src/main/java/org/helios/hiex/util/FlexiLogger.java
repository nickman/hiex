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
package org.helios.hiex.util;

import com.wily.util.feedback.IModuleFeedbackChannel;
import com.wily.util.feedback.Module;
import com.wily.util.feedback.SeverityLevel;

/**
 * <p>Title: FlexiLogger</p>
 * <p>Description: A wrapper for the module feedback channel that performs some optimizations.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.util.FlexiLogger</code></p>
 */
public class FlexiLogger {
	/** The tracer's feedback channel */
	protected final IModuleFeedbackChannel channel;
	/** The tracer's module */
	protected final Module module;

	/**
	 * Creates a new FlexiLogger
	 * @param module The tracer's module
	 * @param channel The tracer's feedback channel
	 */
	public FlexiLogger(Module module, IModuleFeedbackChannel channel) {
		this.channel = channel;
		this.module = module;
	}
	
    /**
     * Level parameterized optimized logger. The message will only render and append if:<ul>
     * <li>Either<ol>
     * 	 <li>The throwable is not null</li> 
     *   <li>The args array is not null and has a non-zero length</li>
     *   </ol></li>
     * <li>The class logger's level is at or below the passed level</li>
     * </ul>. 
     * @param level The level to log at
     * @param t An optional throwable to log which may be null
     * @param args An array of objects that will be rendered and concatenated into the log message
     */
    protected void log(SeverityLevel level, Throwable t, Object...args) {
    	if(((args!=null && args.length > 0) || t !=null) && channel.isLogEnabled(level, module)) {
    		StringBuilder b = new StringBuilder();
    		for(Object obj: args) {
    			if(obj==null) continue;
    			b.append(obj.toString());
    		}
    		if(t!=null) {
    			channel.log(level, module, b.toString(), t);
    		} else {
    			channel.log(level, module, b.toString());
    		}    		
    	}
    }
	
	/**
	 * Logs a rendered message at FATAL level
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void fatal(Object...args) {
		log(SeverityLevel.FATAL, null, args);
	}
	
	/**
	 * Logs a rendered message at FATAL level
	 * @param t An optional throwable to render. Ignored if null.
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void fatal(Throwable t, Object...args) {
		log(SeverityLevel.FATAL, t, args);
	}	

	/**
	 * Logs a rendered message at ERROR level
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void error(Object...args) {
		log(SeverityLevel.ERROR, null, args);
	}
	
	/**
	 * Logs a rendered message at ERROR level
	 * @param t An optional throwable to render. Ignored if null.
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void error(Throwable t, Object...args) {
		log(SeverityLevel.ERROR, t, args);
	}	

    
	/**
	 * Logs a rendered message at WARN level
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void warn(Object...args) {
		log(SeverityLevel.WARN, null, args);
	}
	
	/**
	 * Logs a rendered message at WARN level
	 * @param t An optional throwable to render. Ignored if null.
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void warn(Throwable t, Object...args) {
		log(SeverityLevel.WARN, t, args);
	}
	
	
	/**
	 * Logs a rendered message at INFO level
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void info(Object...args) {
		log(SeverityLevel.INFO, null, args);
	}
	
	/**
	 * Logs a rendered message at INFO level
	 * @param t An optional throwable to render. Ignored if null.
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void info(Throwable t, Object...args) {
		log(SeverityLevel.INFO, t, args);
	}
	
	/**
	 * Logs a rendered message at DEBUG level
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void debug(Object...args) {
		log(SeverityLevel.DEBUG, null, args);
	}
	
	/**
	 * Logs a rendered message at DEBUG level
	 * @param t An optional throwable to render. Ignored if null.
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void debug(Throwable t, Object...args) {
		log(SeverityLevel.DEBUG, t, args);
	}
	
	/**
	 * Logs a rendered message at TRACE level
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void trace(Object...args) {
		log(SeverityLevel.TRACE, null, args);
	}
	
	/**
	 * Logs a rendered message at TRACE level
	 * @param t An optional throwable to render. Ignored if null.
	 * @param args An array of objects that will be rendered and concatenated into the log message
	 */
	public void trace(Throwable t, Object...args) {
		log(SeverityLevel.TRACE, t, args);
	}
	

}

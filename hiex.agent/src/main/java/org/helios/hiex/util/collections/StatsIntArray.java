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
package org.helios.hiex.util.collections;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.procedure.TIntProcedure;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Random;



/**
 * <p>Title: StatsIntArray</p>
 * <p>Description: Trove int array extended to provide some statstical methods on the values in the array.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.util.collections.StatsIntArray</code></p>
 */
public class StatsIntArray  {
	public static final String CR = "\n";
	protected TIntArrayList inner = null;
	/**
	 * 
	 */
	public StatsIntArray() {
		inner = new TIntArrayList();
	}

	/**
	 * @param capacity
	 */
	public StatsIntArray(int capacity) {
		inner = new TIntArrayList(capacity);
	}

	/**
	 * @param values
	 */
	public StatsIntArray(int[] values) {
		inner = new TIntArrayList(values);
	}
	
	/**
	 * Calculates the arithmetical mean of all the int values.
	 * @return the arithmetical mean 
	 */
	public int mean() {
		int mean = 0;
		int total = 0;
		int[] values = inner.toArray();
		for(int i: values) {
			total += i;
		}
		mean = total/values.length;
		return mean;
	}
	
	/**
	 * Returns an array of deviations from the mean.
	 * @return The deviations from the mean.
	 */
	public int[] deviations() {
		int[] values = inner.toArray();
		int mean = mean();
		int[] deviations = new int[values.length];
		for(int i = 0; i < values.length; i++) {
			deviations[i] = values[i] - mean;
		}
		return deviations;		
	}
	
	/**
	 * Calculates the standard deviation of the ints.
	 * @return the standard deviation of the ints.
	 */
	public int stddev() {
		int stddev = 0;
		int[] deviations = deviations();
		for(int i = 0; i < deviations.length; i++) {
			deviations[i] = deviations[i]*deviations[i]; 
		}
		StatsIntArray arr = new StatsIntArray(deviations);
		int meanOfSquaresOfDeviations = arr.mean();
		stddev = (int)Math.round(Math.sqrt(meanOfSquaresOfDeviations));
		return stddev;
	}
	
	/**
	 * Returns the members that are above the standard deviation.
	 * @return An array of high outliers.
	 */
	public int[] stddevHighOutliers() {
		return stddevHighOutliers(1F);
	}
	
	/**
	 * Returns the members that are above the adjusted standard deviation.
	 * @param stdDevMultipler The multiplier against the standard deviation.
	 * @return An array of high outliers.
	 */
	public int[] stddevHighOutliers(float stdDevMultipler) {
		float tolerance = mean() + (stdDevMultipler * stddev());
		return inner.grep(new GTFilter(tolerance)).toArray();		
	}
	
	/**
	 * Returns the members that are below the standard deviation.
	 * @return An array of low outliers.
	 */
	public int[] stddevLowOutliers() {
		return stddevLowOutliers(1F);
	}
	
	/**
	 * Returns the members that are below the adjusted standard deviation.
	 * @param stdDevMultipler The multiplier against the standard deviation.
	 * @return An array of low outliers.
	 */
	public int[] stddevLowOutliers(float stdDevMultipler) {
		float tolerance = mean() - (stdDevMultipler * stddev());
		return inner.grep(new LTFilter(tolerance)).toArray();		
	}	
	

	
	/**
	 * Calculates the rank for the given percentile.
	 * @param percentile The percentile to get the rank for.
	 * @return the percentileRank
	 */
	public double percentileRank(float percentile) {
		TIntArrayList arr = new TIntArrayList(toArray());
		arr.sort();
		double rank = (percentile/100f)*(arr.size()+1);
		return rank;		
	}
	
	/**
	 * Generates a delimeted string of the values.
	 * @param horizontal If true, the delimeter will be <code>,</code>, otherwise, it will be a carriage return.
	 * @return A delimeted string of the array's values. 
	 */
	public String printValues(boolean horizontal) {		
		int[] arr = toArray();
		if(arr==null || arr.length <1) return "";
		String delim = (horizontal ? "," : CR);
		StringBuilder b = new StringBuilder(arr.length*4);
		for(int i: arr) {
			b.append(i).append(delim);
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}
	
//	public int percentileRankValue2(double percentile) {
//		int[] values = toArray();
//		double[] dvalues = new double[values.length];
//		for(int i = 0; i < values.length; i++) {
//			dvalues[i] = (double)values[i];
//		}
//		return (int)StatUtils.percentile(dvalues, percentile);
//	}
	
	/**
	 * Calculates the threshold value for the given percentile.
	 * @param percentile the percentile to calc the threshold for.
	 * @return the threshold value for the percentile.
	 */
	public int percentileRankValue(float percentile) {		
		TIntArrayList arr = new TIntArrayList(toArray());
		arr.sort();
		double rank = (percentile/100f)*(arr.size()+1);
		int ir = (int)Math.floor(rank);
		double fr = rank-ir;
		int ir1=0, ir2=0;
		if(arr.size() >= ir) {
			ir1 = arr.get(ir-1 >= 0 ? ir-1 : ir);
			if(arr.size() > ir) {
				ir2 = arr.get(ir);
			} else {
				ir2 = ir1;
			}
		}
		double interpolated = fr*(ir2-ir1)+ir1;
		return (int)Math.round(interpolated);		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log("StatsIntArray Test");
		StatsIntArray arr = new StatsIntArray(varg(3,7,7,19));
		log("Mean:" + arr.mean());
		log("Standard Deviation:" + arr.stddev());
		log("High Outliers (1):" + formatIntArr(arr.stddevHighOutliers()));
		log("Low Outliers (1):" + formatIntArr(arr.stddevLowOutliers()));
		log("High Outliers (.5):" + formatIntArr(arr.stddevHighOutliers(.5F)));
		log("Low Outliers (.5):" + formatIntArr(arr.stddevLowOutliers(.5F)));
//		log("Percentile Value (50):" + arr.percentileRankValue(50F));
//		log("Percentile Value (90):" + arr.percentileRankValue(90F));
//		log("Percentile Value (10):" + arr.percentileRankValue(10F));
//		log("Percentile Value (25):" + arr.percentileRankValue(25F));
		for(int i = 1; i < 100; i++) {
			log("Percentile Value (", i, "):", arr.percentileRankValue(i));
		}
		
	}
	
	public static int[] varg(int...values) {
		return values;
	}
	
	public static String formatIntArr(int...values) {
		StringBuilder b = new StringBuilder();
		for(int i:values) {
			b.append(i).append(",");
		}
		if(b.length() > 1) b.deleteCharAt(b.length()-1);
		return b.toString();
	}
	
	public static int[] randomSampling(int limit, int sampleCount) {
		int[] values = new int[sampleCount];
		Random random = new Random(System.nanoTime());
		for(int i = 0; i < sampleCount; i++) {
			values[i] = random.nextInt(limit);
		}
		return values;
	}
	
	
	public static void log(Object...message) {
		log(System.out, message);
	}
	/**
	 * Simple logger.
	 * @param message
	 */
	public static void log(PrintStream ps, Object...message) {
		if(message != null && message.length > 0) {
			StringBuilder b = new StringBuilder("[StatsIntArray]");
			for(Object obj: message) {
				if(obj!=null) {
					b.append(obj.toString());
				}
			}
			ps.println(b.toString());
		}		
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#getNoEntryValue()
	 */
	public int getNoEntryValue() {
		return inner.getNoEntryValue();
	}

	/**
	 * @param capacity
	 * @see gnu.trove.list.array.TIntArrayList#ensureCapacity(int)
	 */
	public void ensureCapacity(int capacity) {
		inner.ensureCapacity(capacity);
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#size()
	 */
	public int size() {
		return inner.size();
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#isEmpty()
	 */
	public boolean isEmpty() {
		return inner.isEmpty();
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#trimToSize()
	 */
	public void trimToSize() {
		inner.trimToSize();
	}

	/**
	 * @param val
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#add(int)
	 */
	public boolean add(int val) {
		return inner.add(val);
	}

	/**
	 * @param vals
	 * @see gnu.trove.list.array.TIntArrayList#add(int[])
	 */
	public void add(int[] vals) {
		inner.add(vals);
	}

	/**
	 * @param vals
	 * @param offset
	 * @param length
	 * @see gnu.trove.list.array.TIntArrayList#add(int[], int, int)
	 */
	public void add(int[] vals, int offset, int length) {
		inner.add(vals, offset, length);
	}

	/**
	 * @param offset
	 * @param value
	 * @see gnu.trove.list.array.TIntArrayList#insert(int, int)
	 */
	public void insert(int offset, int value) {
		inner.insert(offset, value);
	}

	/**
	 * @param offset
	 * @param values
	 * @see gnu.trove.list.array.TIntArrayList#insert(int, int[])
	 */
	public void insert(int offset, int[] values) {
		inner.insert(offset, values);
	}

	/**
	 * @param offset
	 * @param values
	 * @param valOffset
	 * @param len
	 * @see gnu.trove.list.array.TIntArrayList#insert(int, int[], int, int)
	 */
	public void insert(int offset, int[] values, int valOffset, int len) {
		inner.insert(offset, values, valOffset, len);
	}

	/**
	 * @param offset
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#get(int)
	 */
	public int get(int offset) {
		return inner.get(offset);
	}

	/**
	 * @param offset
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#getQuick(int)
	 */
	public int getQuick(int offset) {
		return inner.getQuick(offset);
	}

	/**
	 * @param offset
	 * @param val
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#set(int, int)
	 */
	public int set(int offset, int val) {
		return inner.set(offset, val);
	}

	/**
	 * @param offset
	 * @param val
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#replace(int, int)
	 */
	public int replace(int offset, int val) {
		return inner.replace(offset, val);
	}

	/**
	 * @param offset
	 * @param values
	 * @see gnu.trove.list.array.TIntArrayList#set(int, int[])
	 */
	public void set(int offset, int[] values) {
		inner.set(offset, values);
	}

	/**
	 * @param offset
	 * @param values
	 * @param valOffset
	 * @param length
	 * @see gnu.trove.list.array.TIntArrayList#set(int, int[], int, int)
	 */
	public void set(int offset, int[] values, int valOffset, int length) {
		inner.set(offset, values, valOffset, length);
	}

	/**
	 * @param offset
	 * @param val
	 * @see gnu.trove.list.array.TIntArrayList#setQuick(int, int)
	 */
	public void setQuick(int offset, int val) {
		inner.setQuick(offset, val);
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#clear()
	 */
	public void clear() {
		inner.clear();
	}

	/**
	 * @param capacity
	 * @see gnu.trove.list.array.TIntArrayList#clear(int)
	 */
	public void clear(int capacity) {
		inner.clear(capacity);
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#reset()
	 */
	public void reset() {
		inner.reset();
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#resetQuick()
	 */
	public void resetQuick() {
		inner.resetQuick();
	}

	/**
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#remove(int)
	 */
	public boolean remove(int value) {
		return inner.remove(value);
	}

	/**
	 * @param offset
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#removeAt(int)
	 */
	public int removeAt(int offset) {
		return inner.removeAt(offset);
	}

	/**
	 * @param offset
	 * @param length
	 * @see gnu.trove.list.array.TIntArrayList#remove(int, int)
	 */
	public void remove(int offset, int length) {
		inner.remove(offset, length);
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#iterator()
	 */
	public TIntIterator iterator() {
		return inner.iterator();
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> collection) {
		return inner.containsAll(collection);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#containsAll(gnu.trove.TIntCollection)
	 */
	public boolean containsAll(TIntCollection collection) {
		return inner.containsAll(collection);
	}

	/**
	 * @param array
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#containsAll(int[])
	 */
	public boolean containsAll(int[] array) {
		return inner.containsAll(array);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends Integer> collection) {
		return inner.addAll(collection);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#addAll(gnu.trove.TIntCollection)
	 */
	public boolean addAll(TIntCollection collection) {
		return inner.addAll(collection);
	}

	/**
	 * @param array
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#addAll(int[])
	 */
	public boolean addAll(int[] array) {
		return inner.addAll(array);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> collection) {
		return inner.retainAll(collection);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#retainAll(gnu.trove.TIntCollection)
	 */
	public boolean retainAll(TIntCollection collection) {
		return inner.retainAll(collection);
	}

	/**
	 * @param array
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#retainAll(int[])
	 */
	public boolean retainAll(int[] array) {
		return inner.retainAll(array);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> collection) {
		return inner.removeAll(collection);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#removeAll(gnu.trove.TIntCollection)
	 */
	public boolean removeAll(TIntCollection collection) {
		return inner.removeAll(collection);
	}

	/**
	 * @param array
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#removeAll(int[])
	 */
	public boolean removeAll(int[] array) {
		return inner.removeAll(array);
	}

	/**
	 * @param function
	 * @see gnu.trove.list.array.TIntArrayList#transformValues(gnu.trove.function.TIntFunction)
	 */
	public void transformValues(TIntFunction function) {
		inner.transformValues(function);
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#reverse()
	 */
	public void reverse() {
		inner.reverse();
	}

	/**
	 * @param from
	 * @param to
	 * @see gnu.trove.list.array.TIntArrayList#reverse(int, int)
	 */
	public void reverse(int from, int to) {
		inner.reverse(from, to);
	}

	/**
	 * @param rand
	 * @see gnu.trove.list.array.TIntArrayList#shuffle(java.util.Random)
	 */
	public void shuffle(Random rand) {
		inner.shuffle(rand);
	}

	/**
	 * @param begin
	 * @param end
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#subList(int, int)
	 */
	public TIntList subList(int begin, int end) {
		return inner.subList(begin, end);
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toArray()
	 */
	public int[] toArray() {
		return inner.toArray();
	}

	/**
	 * @param offset
	 * @param len
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toArray(int, int)
	 */
	public int[] toArray(int offset, int len) {
		return inner.toArray(offset, len);
	}

	/**
	 * @param dest
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toArray(int[])
	 */
	public int[] toArray(int[] dest) {
		return inner.toArray(dest);
	}

	/**
	 * @param dest
	 * @param offset
	 * @param len
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toArray(int[], int, int)
	 */
	public int[] toArray(int[] dest, int offset, int len) {
		return inner.toArray(dest, offset, len);
	}

	/**
	 * @param dest
	 * @param source_pos
	 * @param dest_pos
	 * @param len
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toArray(int[], int, int, int)
	 */
	public int[] toArray(int[] dest, int source_pos, int dest_pos, int len) {
		return inner.toArray(dest, source_pos, dest_pos, len);
	}

	/**
	 * @param other
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return inner.equals(other);
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#hashCode()
	 */
	public int hashCode() {
		return inner.hashCode();
	}

	/**
	 * @param procedure
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#forEach(gnu.trove.procedure.TIntProcedure)
	 */
	public boolean forEach(TIntProcedure procedure) {
		return inner.forEach(procedure);
	}

	/**
	 * @param procedure
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#forEachDescending(gnu.trove.procedure.TIntProcedure)
	 */
	public boolean forEachDescending(TIntProcedure procedure) {
		return inner.forEachDescending(procedure);
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#sort()
	 */
	public void sort() {
		inner.sort();
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @see gnu.trove.list.array.TIntArrayList#sort(int, int)
	 */
	public void sort(int fromIndex, int toIndex) {
		inner.sort(fromIndex, toIndex);
	}

	/**
	 * @param val
	 * @see gnu.trove.list.array.TIntArrayList#fill(int)
	 */
	public void fill(int val) {
		inner.fill(val);
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @param val
	 * @see gnu.trove.list.array.TIntArrayList#fill(int, int, int)
	 */
	public void fill(int fromIndex, int toIndex, int val) {
		inner.fill(fromIndex, toIndex, val);
	}

	/**
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#binarySearch(int)
	 */
	public int binarySearch(int value) {
		return inner.binarySearch(value);
	}

	/**
	 * @param value
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#binarySearch(int, int, int)
	 */
	public int binarySearch(int value, int fromIndex, int toIndex) {
		return inner.binarySearch(value, fromIndex, toIndex);
	}

	/**
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#indexOf(int)
	 */
	public int indexOf(int value) {
		return inner.indexOf(value);
	}

	/**
	 * @param offset
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#indexOf(int, int)
	 */
	public int indexOf(int offset, int value) {
		return inner.indexOf(offset, value);
	}

	/**
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#lastIndexOf(int)
	 */
	public int lastIndexOf(int value) {
		return inner.lastIndexOf(value);
	}

	/**
	 * @param offset
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#lastIndexOf(int, int)
	 */
	public int lastIndexOf(int offset, int value) {
		return inner.lastIndexOf(offset, value);
	}

	/**
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#contains(int)
	 */
	public boolean contains(int value) {
		return inner.contains(value);
	}

	/**
	 * @param condition
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#grep(gnu.trove.procedure.TIntProcedure)
	 */
	public TIntList grep(TIntProcedure condition) {
		return inner.grep(condition);
	}

	/**
	 * @param condition
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#inverseGrep(gnu.trove.procedure.TIntProcedure)
	 */
	public TIntList inverseGrep(TIntProcedure condition) {
		return inner.inverseGrep(condition);
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#max()
	 */
	public int max() {
		return inner.max();
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#min()
	 */
	public int min() {
		return inner.min();
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toString()
	 */
	public String toString() {
		return inner.toString();
	}

	/**
	 * @param out
	 * @throws IOException
	 * @see gnu.trove.list.array.TIntArrayList#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		inner.writeExternal(out);
	}

	/**
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @see gnu.trove.list.array.TIntArrayList#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		inner.readExternal(in);
	}

}

abstract class Filter implements TIntProcedure {
	float comp = 0;
	
	/**
	 * @param comp
	 */
	public Filter(float comp) {
		this.comp = comp;
	}

	/**
	 * @param comp the comp to set
	 */
	public void setComp(float comp) {
		this.comp = comp;
	}	
}

class GTFilter extends Filter {
	public GTFilter(float comp) {
		super(comp);
	}

	public boolean execute(int value) {
		return value > comp;
	}	
}

class LTFilter extends Filter {
	public LTFilter(float comp) {
		super(comp);
	}

	public boolean execute(int value) {
		return value < comp;
	}	
}

class EQFilter extends Filter {
	public EQFilter(float comp) {
		super(comp);
	}

	public boolean execute(int value) {
		return value == comp;
	}	
}



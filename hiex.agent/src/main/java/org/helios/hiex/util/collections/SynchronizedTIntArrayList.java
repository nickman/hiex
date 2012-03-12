/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General public synchronized synchronized License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General public synchronized synchronized License for more details.
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
import java.util.Collection;
import java.util.Random;


/**
 * <p>Title: SynchronizedTIntArrayList</p>
 * <p>Description: A synchronized and thread safe implementation of the GNU Trove {@link gnu.trove.list.array.TIntArrayList}.</p> 
 * <p>Company: Helios Development Group</p>
 * @author Whitehead (whitehead.nicholas@gmail.com)
 * @version $LastChangedRevision$
 * $HeadURL$
 * $Id$
 */
/**
 * <p>Title: SynchronizedTIntArrayList</p>
 * <p>Description: A synchronized and thread safe implementation of the GNU Trove <code>gnu.trove.TIntArrayList</code>.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.hiex.util.collections.SynchronizedTIntArrayList</code></p>
 */
public class SynchronizedTIntArrayList implements Cloneable {
	/** The inner protected int array list */
	protected final TIntArrayList intArray;

	/**
	 * Creates a new SynchronizedTIntArrayList instance with the default capacity.
	 */
	public SynchronizedTIntArrayList() {
		intArray = new TIntArrayList();
	}
	
	/**
	 * Creates a new SynchronizedTIntArrayList instance based on the passed <code>TIntArrayList</code>.
	 * @param intArray The inner <code>TIntArrayList</code>
	 */
	public SynchronizedTIntArrayList(TIntArrayList intArray) {
		this.intArray = intArray;
	}
	
	
	/**
	 * Creates a new SynchronizedTIntArrayList instance with the specified capacity.
	 * @param capacity the initial capacity of the array.
	 */
	public SynchronizedTIntArrayList(int capacity) {
		intArray = new TIntArrayList(capacity);
	}
	
	/**
	 * Creates a new SynchronizedTIntArrayList instance whose capacity is the greater of the length of values and the default capacity and whose initial contents are the specified values.
	 * @param values the initial values
	 */
	public SynchronizedTIntArrayList(int[] values) {
		intArray = new TIntArrayList(values);
	}
	
	
	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#getNoEntryValue()
	 */
	public synchronized int getNoEntryValue() {
		return intArray.getNoEntryValue();
	}

	/**
	 * @param capacity
	 * @see gnu.trove.list.array.TIntArrayList#ensureCapacity(int)
	 */
	public synchronized void ensureCapacity(int capacity) {
		intArray.ensureCapacity(capacity);
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#size()
	 */
	public synchronized int size() {
		return intArray.size();
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#isEmpty()
	 */
	public synchronized boolean isEmpty() {
		return intArray.isEmpty();
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#trimToSize()
	 */
	public synchronized void trimToSize() {
		intArray.trimToSize();
	}

	/**
	 * @param val
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#add(int)
	 */
	public synchronized boolean add(int val) {
		return intArray.add(val);
	}

	/**
	 * @param vals
	 * @see gnu.trove.list.array.TIntArrayList#add(int[])
	 */
	public synchronized void add(int[] vals) {
		intArray.add(vals);
	}

	/**
	 * @param vals
	 * @param offset
	 * @param length
	 * @see gnu.trove.list.array.TIntArrayList#add(int[], int, int)
	 */
	public synchronized void add(int[] vals, int offset, int length) {
		intArray.add(vals, offset, length);
	}

	/**
	 * @param offset
	 * @param value
	 * @see gnu.trove.list.array.TIntArrayList#insert(int, int)
	 */
	public synchronized void insert(int offset, int value) {
		intArray.insert(offset, value);
	}

	/**
	 * @param offset
	 * @param values
	 * @see gnu.trove.list.array.TIntArrayList#insert(int, int[])
	 */
	public synchronized void insert(int offset, int[] values) {
		intArray.insert(offset, values);
	}

	/**
	 * @param offset
	 * @param values
	 * @param valOffset
	 * @param len
	 * @see gnu.trove.list.array.TIntArrayList#insert(int, int[], int, int)
	 */
	public synchronized void insert(int offset, int[] values, int valOffset, int len) {
		intArray.insert(offset, values, valOffset, len);
	}

	/**
	 * @param offset
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#get(int)
	 */
	public synchronized int get(int offset) {
		return intArray.get(offset);
	}

	/**
	 * @param offset
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#getQuick(int)
	 */
	public synchronized int getQuick(int offset) {
		return intArray.getQuick(offset);
	}

	/**
	 * @param offset
	 * @param val
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#set(int, int)
	 */
	public synchronized int set(int offset, int val) {
		return intArray.set(offset, val);
	}

	/**
	 * @param offset
	 * @param val
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#replace(int, int)
	 */
	public synchronized int replace(int offset, int val) {
		return intArray.replace(offset, val);
	}

	/**
	 * @param offset
	 * @param values
	 * @see gnu.trove.list.array.TIntArrayList#set(int, int[])
	 */
	public synchronized void set(int offset, int[] values) {
		intArray.set(offset, values);
	}

	/**
	 * @param offset
	 * @param values
	 * @param valOffset
	 * @param length
	 * @see gnu.trove.list.array.TIntArrayList#set(int, int[], int, int)
	 */
	public synchronized void set(int offset, int[] values, int valOffset, int length) {
		intArray.set(offset, values, valOffset, length);
	}

	/**
	 * @param offset
	 * @param val
	 * @see gnu.trove.list.array.TIntArrayList#setQuick(int, int)
	 */
	public synchronized void setQuick(int offset, int val) {
		intArray.setQuick(offset, val);
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#clear()
	 */
	public synchronized void clear() {
		intArray.clear();
	}

	/**
	 * @param capacity
	 * @see gnu.trove.list.array.TIntArrayList#clear(int)
	 */
	public synchronized void clear(int capacity) {
		intArray.clear(capacity);
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#reset()
	 */
	public synchronized void reset() {
		intArray.reset();
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#resetQuick()
	 */
	public synchronized void resetQuick() {
		intArray.resetQuick();
	}

	/**
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#remove(int)
	 */
	public synchronized boolean remove(int value) {
		return intArray.remove(value);
	}

	/**
	 * @param offset
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#removeAt(int)
	 */
	public synchronized int removeAt(int offset) {
		return intArray.removeAt(offset);
	}

	/**
	 * @param offset
	 * @param length
	 * @see gnu.trove.list.array.TIntArrayList#remove(int, int)
	 */
	public synchronized void remove(int offset, int length) {
		intArray.remove(offset, length);
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#iterator()
	 */
	public synchronized TIntIterator iterator() {
		return intArray.iterator();
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#containsAll(java.util.Collection)
	 */
	public synchronized boolean containsAll(Collection<?> collection) {
		return intArray.containsAll(collection);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#containsAll(gnu.trove.TIntCollection)
	 */
	public synchronized boolean containsAll(TIntCollection collection) {
		return intArray.containsAll(collection);
	}

	/**
	 * @param array
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#containsAll(int[])
	 */
	public synchronized boolean containsAll(int[] array) {
		return intArray.containsAll(array);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#addAll(java.util.Collection)
	 */
	public synchronized boolean addAll(Collection<? extends Integer> collection) {
		return intArray.addAll(collection);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#addAll(gnu.trove.TIntCollection)
	 */
	public synchronized boolean addAll(TIntCollection collection) {
		return intArray.addAll(collection);
	}

	/**
	 * @param array
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#addAll(int[])
	 */
	public synchronized boolean addAll(int[] array) {
		return intArray.addAll(array);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#retainAll(java.util.Collection)
	 */
	public synchronized boolean retainAll(Collection<?> collection) {
		return intArray.retainAll(collection);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#retainAll(gnu.trove.TIntCollection)
	 */
	public synchronized boolean retainAll(TIntCollection collection) {
		return intArray.retainAll(collection);
	}

	/**
	 * @param array
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#retainAll(int[])
	 */
	public synchronized boolean retainAll(int[] array) {
		return intArray.retainAll(array);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#removeAll(java.util.Collection)
	 */
	public synchronized boolean removeAll(Collection<?> collection) {
		return intArray.removeAll(collection);
	}

	/**
	 * @param collection
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#removeAll(gnu.trove.TIntCollection)
	 */
	public synchronized boolean removeAll(TIntCollection collection) {
		return intArray.removeAll(collection);
	}

	/**
	 * @param array
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#removeAll(int[])
	 */
	public synchronized boolean removeAll(int[] array) {
		return intArray.removeAll(array);
	}

	/**
	 * @param function
	 * @see gnu.trove.list.array.TIntArrayList#transformValues(gnu.trove.function.TIntFunction)
	 */
	public synchronized void transformValues(TIntFunction function) {
		intArray.transformValues(function);
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#reverse()
	 */
	public synchronized void reverse() {
		intArray.reverse();
	}

	/**
	 * @param from
	 * @param to
	 * @see gnu.trove.list.array.TIntArrayList#reverse(int, int)
	 */
	public synchronized void reverse(int from, int to) {
		intArray.reverse(from, to);
	}

	/**
	 * @param rand
	 * @see gnu.trove.list.array.TIntArrayList#shuffle(java.util.Random)
	 */
	public synchronized void shuffle(Random rand) {
		intArray.shuffle(rand);
	}

	/**
	 * @param begin
	 * @param end
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#subList(int, int)
	 */
	public synchronized TIntList subList(int begin, int end) {
		return intArray.subList(begin, end);
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toArray()
	 */
	public synchronized int[] toArray() {
		return intArray.toArray();
	}

	/**
	 * @param offset
	 * @param len
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toArray(int, int)
	 */
	public synchronized int[] toArray(int offset, int len) {
		return intArray.toArray(offset, len);
	}

	/**
	 * @param dest
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toArray(int[])
	 */
	public synchronized int[] toArray(int[] dest) {
		return intArray.toArray(dest);
	}

	/**
	 * @param dest
	 * @param offset
	 * @param len
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toArray(int[], int, int)
	 */
	public synchronized int[] toArray(int[] dest, int offset, int len) {
		return intArray.toArray(dest, offset, len);
	}

	/**
	 * @param dest
	 * @param source_pos
	 * @param dest_pos
	 * @param len
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toArray(int[], int, int, int)
	 */
	public synchronized int[] toArray(int[] dest, int source_pos, int dest_pos, int len) {
		return intArray.toArray(dest, source_pos, dest_pos, len);
	}

	/**
	 * @param other
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#equals(java.lang.Object)
	 */
	public synchronized boolean equals(Object other) {
		return intArray.equals(other);
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#hashCode()
	 */
	public synchronized int hashCode() {
		return intArray.hashCode();
	}

	/**
	 * @param procedure
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#forEach(gnu.trove.procedure.TIntProcedure)
	 */
	public synchronized boolean forEach(TIntProcedure procedure) {
		return intArray.forEach(procedure);
	}

	/**
	 * @param procedure
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#forEachDescending(gnu.trove.procedure.TIntProcedure)
	 */
	public synchronized boolean forEachDescending(TIntProcedure procedure) {
		return intArray.forEachDescending(procedure);
	}

	/**
	 * 
	 * @see gnu.trove.list.array.TIntArrayList#sort()
	 */
	public synchronized void sort() {
		intArray.sort();
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @see gnu.trove.list.array.TIntArrayList#sort(int, int)
	 */
	public synchronized void sort(int fromIndex, int toIndex) {
		intArray.sort(fromIndex, toIndex);
	}

	/**
	 * @param val
	 * @see gnu.trove.list.array.TIntArrayList#fill(int)
	 */
	public synchronized void fill(int val) {
		intArray.fill(val);
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @param val
	 * @see gnu.trove.list.array.TIntArrayList#fill(int, int, int)
	 */
	public synchronized void fill(int fromIndex, int toIndex, int val) {
		intArray.fill(fromIndex, toIndex, val);
	}

	/**
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#binarySearch(int)
	 */
	public synchronized int binarySearch(int value) {
		return intArray.binarySearch(value);
	}

	/**
	 * @param value
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#binarySearch(int, int, int)
	 */
	public synchronized int binarySearch(int value, int fromIndex, int toIndex) {
		return intArray.binarySearch(value, fromIndex, toIndex);
	}

	/**
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#indexOf(int)
	 */
	public synchronized int indexOf(int value) {
		return intArray.indexOf(value);
	}

	/**
	 * @param offset
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#indexOf(int, int)
	 */
	public synchronized int indexOf(int offset, int value) {
		return intArray.indexOf(offset, value);
	}

	/**
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#lastIndexOf(int)
	 */
	public synchronized int lastIndexOf(int value) {
		return intArray.lastIndexOf(value);
	}

	/**
	 * @param offset
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#lastIndexOf(int, int)
	 */
	public synchronized int lastIndexOf(int offset, int value) {
		return intArray.lastIndexOf(offset, value);
	}

	/**
	 * @param value
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#contains(int)
	 */
	public synchronized boolean contains(int value) {
		return intArray.contains(value);
	}

	/**
	 * @param condition
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#grep(gnu.trove.procedure.TIntProcedure)
	 */
	public synchronized TIntList grep(TIntProcedure condition) {
		return intArray.grep(condition);
	}

	/**
	 * @param condition
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#inverseGrep(gnu.trove.procedure.TIntProcedure)
	 */
	public synchronized TIntList inverseGrep(TIntProcedure condition) {
		return intArray.inverseGrep(condition);
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#max()
	 */
	public synchronized int max() {
		return intArray.max();
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#min()
	 */
	public synchronized int min() {
		return intArray.min();
	}

	/**
	 * @return
	 * @see gnu.trove.list.array.TIntArrayList#toString()
	 */
	public synchronized String toString() {
		return intArray.toString();
	}

	/**
	 * @param out
	 * @throws IOException
	 * @see gnu.trove.list.array.TIntArrayList#writeExternal(java.io.ObjectOutput)
	 */
	public synchronized void writeExternal(ObjectOutput out) throws IOException {
		intArray.writeExternal(out);
	}

	/**
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @see gnu.trove.list.array.TIntArrayList#readExternal(java.io.ObjectInput)
	 */
	public synchronized void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		intArray.readExternal(in);
	}

	
	

}

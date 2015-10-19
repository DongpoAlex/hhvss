package com.royalstone.util.aw;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public   class CMDetailComparatorBySeqno implements Comparator{
	public int compare(Object arg0, Object arg1) {
		ColModelDetail cmd0 = (ColModelDetail) arg0;
		ColModelDetail cmd1 = (ColModelDetail) arg1;
		
		//比较seqno
		return cmd0.getSeqno() - cmd1.getSeqno();
	}

	
	public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
			Function<? super T, ? extends U> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public static <T, U> Comparator<T> comparing(
			Function<? super T, ? extends U> arg0, Comparator<? super U> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public static <T> Comparator<T> comparingDouble(
			ToDoubleFunction<? super T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <T> Comparator<T> comparingInt(ToIntFunction<? super T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public static <T> Comparator<T> comparingLong(ToLongFunction<? super T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public static <T> Comparator<T> nullsFirst(Comparator<? super T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	public static <T> Comparator<T> nullsLast(Comparator<? super T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Comparator<?> reversed() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator<?> thenComparing(Comparator arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator thenComparing(Function arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator thenComparing(Function arg0, Comparator arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator thenComparingDouble(ToDoubleFunction arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator thenComparingInt(ToIntFunction arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comparator thenComparingLong(ToLongFunction arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.lodborg.intervaltree;

import org.junit.Test;
import com.lodborg.intervaltree.Interval.*;
import java.util.*;
import static org.junit.Assert.*;

public class DoubleIntervalTest {

	@Test
	public void test_isEmptyDegenerate(){
		DoubleInterval[] arr = new DoubleInterval[]{
				new DoubleInterval(Math.sqrt(-2), 5.0, Bounded.CLOSED),
				new DoubleInterval(8.0, Math.sqrt(-100), Bounded.OPEN),
				new DoubleInterval(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Bounded.CLOSED),
				new DoubleInterval(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Bounded.CLOSED),
				new DoubleInterval(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Bounded.CLOSED)
		};

		for (DoubleInterval next: arr)
			assertTrue(next.isEmpty());
	}

	@Test
	public void test_zeros(){
		DoubleInterval[] zeros = new DoubleInterval[]{
				new DoubleInterval(-0.0, -0.0, Bounded.CLOSED),
				new DoubleInterval(-0.0, 0.0, Bounded.CLOSED),
				new DoubleInterval(0.0, -0.0, Bounded.CLOSED),
				new DoubleInterval(0.0, 0.0, Bounded.CLOSED),
		};
		for (DoubleInterval zero: zeros)
			assertFalse(zero.isEmpty());
	}

	@Test
	public void test_notEmpty(){
		DoubleInterval[] notEmpty = new DoubleInterval[]{
				new DoubleInterval(4.0, 5.0, Bounded.OPEN),
				new DoubleInterval(4.0, 5.0, Bounded.CLOSED_LEFT),
				new DoubleInterval(4.0, 5.0, Bounded.CLOSED_RIGHT),
				new DoubleInterval(4.0, 5.0, Bounded.CLOSED),
				new DoubleInterval(4.0, 4.0, Bounded.CLOSED),
				new DoubleInterval(9.0, Unbounded.CLOSED_RIGHT),
				new DoubleInterval(9.0, Unbounded.CLOSED_LEFT),
				new DoubleInterval(9.0, Unbounded.OPEN_RIGHT),
				new DoubleInterval(9.0, Unbounded.OPEN_LEFT),
				new DoubleInterval(),
				new DoubleInterval(Double.NEGATIVE_INFINITY, -20.0, Bounded.CLOSED),
				new DoubleInterval(-104.0, Double.POSITIVE_INFINITY, Bounded.CLOSED)
		};

		for (DoubleInterval next: notEmpty)
			assertFalse(next.isEmpty());
	}

	@Test
	public void test_intervalsOffByAtMostOne(){
		DoubleInterval[] notEmpty = new DoubleInterval[]{
				new DoubleInterval(4.0, 5.0, Bounded.CLOSED_LEFT),
				new DoubleInterval(4.0, 5.0, Bounded.CLOSED_RIGHT),
				new DoubleInterval(4.0, 5.0, Bounded.CLOSED),
		};

		for (DoubleInterval next: notEmpty)
			assertFalse(next.isEmpty());

		assertTrue(new DoubleInterval(4.0, 4.0, Bounded.OPEN).isEmpty());
	}

	@Test
	public void test_zeroValuesAreSame(){
		assertEquals(new DoubleInterval(0.0, 20.0, Bounded.OPEN), new DoubleInterval(-0.0, 20.0, Bounded.OPEN));
		assertEquals(new DoubleInterval(-20.0, 0.0, Bounded.OPEN), new DoubleInterval(-20.0, -0.0, Bounded.OPEN));

		assertEquals(new DoubleInterval(-0.0, Unbounded.CLOSED_LEFT), new DoubleInterval(0.0, Unbounded.CLOSED_LEFT));
		assertEquals(new DoubleInterval(-0.0, Unbounded.CLOSED_RIGHT), new DoubleInterval(0.0, Unbounded.CLOSED_RIGHT));
		assertEquals(new DoubleInterval(-0.0, Unbounded.OPEN_RIGHT), new DoubleInterval(0.0, Unbounded.OPEN_RIGHT));
		assertEquals(new DoubleInterval(-0.0, Unbounded.OPEN_LEFT), new DoubleInterval(0.0, Unbounded.OPEN_LEFT));
	}

	@Test
	public void test_middlepointDegenerate(){
		DoubleInterval[] arr = new DoubleInterval[]{
				new DoubleInterval(Math.sqrt(-2), 5.0, Bounded.CLOSED),
				new DoubleInterval(8.0, Math.sqrt(-100), Bounded.OPEN),
				new DoubleInterval(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Bounded.CLOSED),
				new DoubleInterval(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Bounded.CLOSED),
				new DoubleInterval(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Bounded.CLOSED),
				new DoubleInterval(12.0, -93.0, Bounded.CLOSED_LEFT)
		};

		for (DoubleInterval next: arr)
			assertNull(next.getMidpoint());
	}

	@Test
	public void test_middlepointZeros(){
		DoubleInterval[] zeros = new DoubleInterval[]{
				new DoubleInterval(-0.0, -0.0, Bounded.CLOSED),
				new DoubleInterval(-0.0, 0.0, Bounded.CLOSED),
				new DoubleInterval(0.0, -0.0, Bounded.CLOSED),
				new DoubleInterval(0.0, 0.0, Bounded.CLOSED),
				new DoubleInterval(),
				new DoubleInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Bounded.CLOSED_RIGHT),
				new DoubleInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Bounded.CLOSED_LEFT),
				new DoubleInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Bounded.OPEN),
				new DoubleInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Bounded.CLOSED)
		};
		for (DoubleInterval zero: zeros)
			assertEquals(new Double(0.0), zero.getMidpoint());
	}

	@Test
	public void test_middlepointUnbounded(){
		DoubleInterval[] arr = new DoubleInterval[]{
				new DoubleInterval(-674539.674, Unbounded.CLOSED_LEFT),
				new DoubleInterval(-917063.9, Unbounded.CLOSED_RIGHT),
				new DoubleInterval(109481287932.909, Unbounded.OPEN_LEFT),
				new DoubleInterval(712837123.0, Unbounded.OPEN_RIGHT),

				new DoubleInterval(-674539.674, Double.POSITIVE_INFINITY, Bounded.CLOSED_LEFT),
				new DoubleInterval(-917063.9, Double.POSITIVE_INFINITY, Bounded.CLOSED_RIGHT),
				new DoubleInterval(Double.NEGATIVE_INFINITY, 109481287932.909, Bounded.CLOSED),
				new DoubleInterval(Double.NEGATIVE_INFINITY, 712837123.0, Bounded.OPEN)
		};

		for (DoubleInterval next: arr){
			assertNotNull(next.getMidpoint());
			if (next.getStart() != null)
				assertTrue(next.getMidpoint().compareTo(next.getStart()) > 0);
			if (next.getEnd() != null)
				assertTrue(next.getMidpoint().compareTo(next.getEnd()) < 0);
		}
	}

	@Test
	public void test_tree(){
		IntervalTree<Double> tree = new IntervalTree<>();
		DoubleInterval[] arr = new DoubleInterval[]{
				new DoubleInterval(9.0, 22.0, Bounded.CLOSED),
				new DoubleInterval(12.0, 25.0, Bounded.OPEN),
				new DoubleInterval(0.0, 5.0, Bounded.CLOSED),
				new DoubleInterval(102.0, 200.0, Bounded.CLOSED_RIGHT),
				new DoubleInterval(-20.0, -15.0, Bounded.CLOSED_LEFT)
		};

		for (DoubleInterval next: arr)
			tree.add(next);

		Set<Interval<Double>> res = tree.query(new DoubleInterval(-17.5, -0.0, Bounded.CLOSED_RIGHT));
		assertEquals(2, res.size());
		assertTrue(res.contains(arr[2]));
		assertTrue(res.contains(arr[4]));

		res = tree.query(new DoubleInterval(Double.NEGATIVE_INFINITY, 12.0, Bounded.CLOSED));
		assertEquals(3, res.size());
		assertTrue(res.contains(arr[0]));
		assertTrue(res.contains(arr[2]));
		assertTrue(res.contains(arr[4]));

		res = tree.query(new DoubleInterval(-16.0, 0.0001, Bounded.OPEN));
		assertEquals(2, res.size());
		assertTrue(res.contains(arr[2]));
		assertTrue(res.contains(arr[4]));
	}
}

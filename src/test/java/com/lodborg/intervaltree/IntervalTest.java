package com.lodborg.intervaltree;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import com.lodborg.intervaltree.Interval.*;

import static org.junit.Assert.*;

public class IntervalTest {
	@Test
	public void test_isNotPoint(){
		IntMock[] intervals = new IntMock[]{
				new IntMock(2, 1, Bounded.CLOSED),
				new IntMock(2, 1, Bounded.CLOSED_LEFT),
				new IntMock(2, 1, Bounded.CLOSED_RIGHT),
				new IntMock(2, 1, Bounded.OPEN),
				new IntMock(2, 2, Bounded.CLOSED_LEFT),
				new IntMock(2, 2, Bounded.CLOSED_RIGHT),
				new IntMock(2, 2, Bounded.OPEN),
				new IntMock(2, Unbounded.CLOSED_LEFT),
				new IntMock(2, Unbounded.CLOSED_RIGHT),
				new IntMock(2, Unbounded.OPEN_RIGHT),
				new IntMock(2, Unbounded.OPEN_LEFT),
				new IntMock(9, 20, Bounded.CLOSED),
				new IntMock(-1, 1, Bounded.CLOSED),
				new IntMock(-123123, -4, Bounded.CLOSED),
				new IntMock()
		};
		for (Interval interval: intervals)
			assertFalse(interval.isPoint());
	}

	@Test
	public void test_isPoint(){
		IntMock[] intervals = new IntMock[]{
				new IntMock(22, 22, Bounded.CLOSED),
				new IntMock(-11, -11, Bounded.CLOSED),
				new IntMock(0, 0, Bounded.CLOSED)
		};
		for (Interval interval: intervals)
			assertTrue(interval.isPoint());
	}

	@Test
	public void test_isEmpty(){
		IntMock[] intervals = new IntMock[]{
				new IntMock(2, 1, Bounded.CLOSED),
				new IntMock(2, 1, Bounded.CLOSED_LEFT),
				new IntMock(2, 1, Bounded.CLOSED_RIGHT),
				new IntMock(2, 1, Bounded.OPEN),
				new IntMock(2, 2, Bounded.CLOSED_LEFT),
				new IntMock(2, 2, Bounded.CLOSED_RIGHT),
				new IntMock(2, 2, Bounded.OPEN)
		};
		for (Interval interval: intervals)
			assertTrue(interval.isEmpty());
	}

	@Test
	public void test_isNotEmpty(){
		IntMock[] intervals = new IntMock[]{
				new IntMock(2, 2, Bounded.CLOSED),
				new IntMock(2, Unbounded.CLOSED_LEFT),
				new IntMock(2, Unbounded.CLOSED_RIGHT),
				new IntMock(2, Unbounded.OPEN_RIGHT),
				new IntMock(2, Unbounded.OPEN_LEFT),
				new IntMock(9, 20, Bounded.CLOSED),
				new IntMock(-1, 1, Bounded.CLOSED),
				new IntMock(-123123, -4, Bounded.CLOSED),
				new IntMock()
		};
		for (Interval interval: intervals)
			assertFalse(interval.isEmpty());
	}

	@Test
	public void test_hashSet(){
		HashSet<IntMock> set = new HashSet<>();
		set.add(new IntMock(1, 10, Bounded.CLOSED));
		set.add(new IntMock(1, 10, Bounded.OPEN));
		set.add(new IntMock(1, 10, Bounded.CLOSED_LEFT));
		set.add(new IntMock(1, 10, Bounded.CLOSED_RIGHT));
		set.add(new IntMock(1, Unbounded.CLOSED_LEFT));
		set.add(new IntMock(1, Unbounded.CLOSED_RIGHT));
		set.add(new IntMock(1, Unbounded.OPEN_LEFT));
		set.add(new IntMock(1, Unbounded.OPEN_RIGHT));
		assertEquals(8, set.size());
	}

	@Test
	public void test_equalSameEndPointsDifferentTypes(){
		IntMock a = new IntMock(1, 10, Bounded.CLOSED);
		IntMock b = new IntMock(1, 10, Bounded.OPEN);
		IntMock c = new IntMock(1, 10, Bounded.CLOSED_LEFT);
		IntMock d = new IntMock(1, 10, Bounded.CLOSED_RIGHT);

		assertNotEquals(a, b);
		assertNotEquals(a, c);
		assertNotEquals(a, d);
		assertNotEquals(b, c);
		assertNotEquals(b, d);
		assertNotEquals(c, d);

		assertNotEquals(b, a);
		assertNotEquals(c, a);
		assertNotEquals(d, a);
		assertNotEquals(c, b);
		assertNotEquals(d, b);
		assertNotEquals(d, c);

		assertFalse(a.equals(null));
		assertFalse(a.equals(new IntMock()));

		assertFalse(a.equals(new IntMock(99, 129, Bounded.OPEN)));
	}

	@Test
	public void test_equalSame(){
		IntMock a = new IntMock(1, 10, Bounded.CLOSED);
		IntMock b = new IntMock(1, 10, Bounded.OPEN);
		IntMock c = new IntMock(1, 10, Bounded.CLOSED_LEFT);
		IntMock d = new IntMock(1, 10, Bounded.CLOSED_RIGHT);

		IntMock aa = new IntMock(1, 10, Bounded.CLOSED);
		IntMock bb = new IntMock(1, 10, Bounded.OPEN);
		IntMock cc = new IntMock(1, 10, Bounded.CLOSED_LEFT);
		IntMock dd = new IntMock(1, 10, Bounded.CLOSED_RIGHT);

		IntMock e = new IntMock(20, Unbounded.CLOSED_LEFT);
		IntMock f = new IntMock(20, Unbounded.CLOSED_RIGHT);
		IntMock g = new IntMock(20, Unbounded.OPEN_LEFT);
		IntMock h = new IntMock(20, Unbounded.OPEN_RIGHT);

		IntMock ee = new IntMock(20, Unbounded.CLOSED_LEFT);
		IntMock ff = new IntMock(20, Unbounded.CLOSED_RIGHT);
		IntMock gg = new IntMock(20, Unbounded.OPEN_LEFT);
		IntMock hh = new IntMock(20, Unbounded.OPEN_RIGHT);

		assertTrue(a.equals(aa));
		assertEquals(b, bb);
		assertEquals(c, cc);
		assertEquals(d, dd);
		assertEquals(e, ee);
		assertEquals(f, ff);
		assertEquals(g, gg);
		assertEquals(h, hh);
	}

	@Test
	public void nothingContainsDegenerate(){
		IntMock[] arr = new IntMock[]{
				new IntMock(0, 5, Bounded.OPEN),
				new IntMock(0, 5, Bounded.CLOSED),
				new IntMock(0, 5, Bounded.CLOSED_RIGHT),
				new IntMock(0, 5, Bounded.CLOSED_LEFT),
				new IntMock(0, Unbounded.CLOSED_LEFT),
				new IntMock(0, Unbounded.CLOSED_RIGHT),
				new IntMock(0, Unbounded.OPEN_LEFT),
				new IntMock(0, Unbounded.OPEN_RIGHT),
				new IntMock(11, 10, Bounded.OPEN)
		};

		for (IntMock a: arr) {
			assertFalse(a.contains(new IntMock()));
			assertFalse(a.contains((Integer) null));
			assertFalse(a.contains((Interval<Integer>) null));
			assertFalse(a.contains(new IntMock(30, 20, Bounded.OPEN)));
		}
	}

	@Test
	public void test_emptyContainsNothing(){
		IntMock a = new IntMock(11, 10, Bounded.CLOSED);

		assertFalse(a.contains(new IntMock(11, 10, Bounded.OPEN)));
		assertFalse(a.contains(new IntMock(11, 10, Bounded.CLOSED_RIGHT)));
		assertFalse(a.contains(new IntMock(11, 10, Bounded.CLOSED_LEFT)));
		assertFalse(a.contains(new IntMock(11, 10, Bounded.CLOSED)));

		assertFalse(a.contains(new IntMock(0, 5, Bounded.OPEN)));
		assertFalse(a.contains(new IntMock(0, 20, Bounded.CLOSED_RIGHT)));
		assertFalse(a.contains(new IntMock(15, 30, Bounded.CLOSED_LEFT)));

		assertFalse(a.contains(new IntMock(0, Unbounded.CLOSED_LEFT)));
		assertFalse(a.contains(new IntMock(0, Unbounded.CLOSED_RIGHT)));
		assertFalse(a.contains(new IntMock(0, Unbounded.OPEN_LEFT)));
		assertFalse(a.contains(new IntMock(0, Unbounded.OPEN_RIGHT)));

		assertFalse(a.contains(new IntMock(100, Unbounded.CLOSED_LEFT)));
		assertFalse(a.contains(new IntMock(100, Unbounded.CLOSED_RIGHT)));
		assertFalse(a.contains(new IntMock(100, Unbounded.OPEN_LEFT)));
		assertFalse(a.contains(new IntMock(100, Unbounded.OPEN_RIGHT)));

		assertFalse(a.contains(new IntMock()));
		assertFalse(a.contains((Interval<Integer>)null));
	}

	@Test
	public void test_emptyIntersectsNothing(){
		IntMock a = new IntMock(11, 10, Bounded.CLOSED);

		assertFalse(a.intersects(new IntMock(11, 10, Bounded.OPEN)));
		assertFalse(a.intersects(new IntMock(11, 10, Bounded.CLOSED_RIGHT)));
		assertFalse(a.intersects(new IntMock(11, 10, Bounded.CLOSED_LEFT)));
		assertFalse(a.intersects(new IntMock(11, 10, Bounded.CLOSED)));

		assertFalse(a.intersects(new IntMock(0, 5, Bounded.OPEN)));
		assertFalse(a.intersects(new IntMock(0, 20, Bounded.CLOSED_RIGHT)));
		assertFalse(a.intersects(new IntMock(15, 30, Bounded.CLOSED_LEFT)));

		assertFalse(a.intersects(new IntMock(0, Unbounded.CLOSED_LEFT)));
		assertFalse(a.intersects(new IntMock(0, Unbounded.CLOSED_RIGHT)));
		assertFalse(a.intersects(new IntMock(0, Unbounded.OPEN_LEFT)));
		assertFalse(a.intersects(new IntMock(0, Unbounded.OPEN_RIGHT)));

		assertFalse(a.intersects(new IntMock(100, Unbounded.CLOSED_LEFT)));
		assertFalse(a.intersects(new IntMock(100, Unbounded.CLOSED_RIGHT)));
		assertFalse(a.intersects(new IntMock(100, Unbounded.OPEN_LEFT)));
		assertFalse(a.intersects(new IntMock(100, Unbounded.OPEN_RIGHT)));

		assertFalse(a.intersects(new IntMock()));
		assertFalse(a.intersects((Interval<Integer>)null));
	}

	@Test
	public void getIntersectionCommonLeft(){
		IntMock a = new IntMock(0, 10, Bounded.OPEN);
		assertEquals(new IntMock(0, 3, Bounded.OPEN), a.getIntersection(new IntMock(0, 3, Bounded.OPEN)));
		assertEquals(new IntMock(0, 10, Bounded.OPEN), a.getIntersection(new IntMock(0, 20, Bounded.CLOSED)));
		assertEquals(new IntMock(0, 4, Bounded.CLOSED_RIGHT), a.getIntersection(new IntMock(0, 4, Bounded.CLOSED_RIGHT)));

		a = new IntMock(0, 10, Bounded.CLOSED);
		assertEquals(new IntMock(0, 3, Bounded.OPEN), a.getIntersection(new IntMock(0, 3, Bounded.OPEN)));
		assertEquals(new IntMock(0, 10, Bounded.CLOSED), a.getIntersection(new IntMock(0, 20, Bounded.CLOSED_LEFT)));
		assertEquals(new IntMock(0, 4, Bounded.CLOSED_RIGHT), a.getIntersection(new IntMock(0, 4, Bounded.CLOSED_RIGHT)));
	}

	@Test
	public void getIntersectionCommonRight(){
		IntMock a = new IntMock(0, 10, Bounded.OPEN);
		assertEquals(new IntMock(4, 10, Bounded.OPEN), a.getIntersection(new IntMock(4, 10, Bounded.OPEN)));
		assertEquals(new IntMock(0, 10, Bounded.OPEN), a.getIntersection(new IntMock(-10, 10, Bounded.CLOSED)));
		assertEquals(new IntMock(2, 10, Bounded.CLOSED_LEFT), a.getIntersection(new IntMock(2, 10, Bounded.CLOSED_LEFT)));

		a = new IntMock(0, 10, Bounded.CLOSED);
		assertEquals(new IntMock(4, 10, Bounded.OPEN), a.getIntersection(new IntMock(4, 10, Bounded.OPEN)));
		assertEquals(new IntMock(0, 10, Bounded.CLOSED), a.getIntersection(new IntMock(-10, 10, Bounded.CLOSED)));
		assertEquals(new IntMock(2, 10, Bounded.CLOSED_LEFT), a.getIntersection(new IntMock(2, 10, Bounded.CLOSED_LEFT)));
	}

	@Test
	public void getIntersectionInfinityWithPoint(){
		IntMock a = new IntMock(15, 15, Bounded.CLOSED);
		IntMock b = new IntMock();

		assertEquals(a, a.getIntersection(b));
		assertEquals(a, b.getIntersection(a));
	}

	@Test
	public void getIntersectionWithNull(){
		assertNull(new IntMock().getIntersection(null));
		assertNull(new IntMock(1, 2, Bounded.OPEN).getIntersection(null));
		assertNull(new IntMock(1, 2, Bounded.CLOSED).getIntersection(null));
		assertNull(new IntMock(1, Unbounded.CLOSED_LEFT).getIntersection(null));
		assertNull(new IntMock(1, Unbounded.CLOSED_RIGHT).getIntersection(null));
	}

	@Test
	public void getIntersectionWithPoint(){
		IntMock a = new IntMock(2, 20, Bounded.OPEN);
		assertNull(a.getIntersection(new IntMock(2, 2, Bounded.CLOSED)));
		assertNull(a.getIntersection(new IntMock(20, 20, Bounded.CLOSED)));
		assertEquals(new IntMock(5, 5, Bounded.CLOSED), a.getIntersection(new IntMock(5, 5, Bounded.CLOSED)));

		a = new IntMock(2, 20, Bounded.CLOSED);
		assertEquals(new IntMock(2, 2, Bounded.CLOSED), a.getIntersection(new IntMock(2, 2, Bounded.CLOSED)));
		assertEquals(new IntMock(20, 20, Bounded.CLOSED), a.getIntersection(new IntMock(20, 20, Bounded.CLOSED)));

		a = new IntMock(2, 20, Bounded.CLOSED_LEFT);
		assertEquals(new IntMock(2, 2, Bounded.CLOSED), a.getIntersection(new IntMock(2, 2, Bounded.CLOSED)));
		assertNull(a.getIntersection(new IntMock(20, 20, Bounded.CLOSED)));

		a = new IntMock(2, 20, Bounded.CLOSED_RIGHT);
		assertNull(a.getIntersection(new IntMock(2, 2, Bounded.CLOSED)));
		assertEquals(new IntMock(20, 20, Bounded.CLOSED), a.getIntersection(new IntMock(20, 20, Bounded.CLOSED)));
	}

	@Test
	public void boundedContainsGenericPoint(){
		IntMock a = new IntMock(2, 20, Bounded.OPEN);
		assertTrue(a.contains(5));
		assertFalse(a.contains(2));
		assertFalse(a.contains(20));
		assertFalse(a.contains(21));
		assertTrue(a.contains(3));
		assertTrue(a.contains(19));
		assertFalse(a.contains(1));

		a = new IntMock(2, 20, Bounded.CLOSED);
		assertTrue(a.contains(6));
		assertTrue(a.contains(2));
		assertTrue(a.contains(20));
		assertFalse(a.contains(21));
		assertTrue(a.contains(3));
		assertTrue(a.contains(19));
		assertFalse(a.contains(1));

		a = new IntMock(2, 20, Bounded.CLOSED_LEFT);
		assertTrue(a.contains(6));
		assertTrue(a.contains(2));
		assertFalse(a.contains(20));
		assertFalse(a.contains(21));
		assertTrue(a.contains(3));
		assertTrue(a.contains(19));
		assertFalse(a.contains(1));

		a = new IntMock(2, 20, Bounded.CLOSED_RIGHT);
		assertTrue(a.contains(6));
		assertFalse(a.contains(2));
		assertTrue(a.contains(20));
		assertFalse(a.contains(21));
		assertTrue(a.contains(3));
		assertTrue(a.contains(19));
		assertFalse(a.contains(1));
	}

	@Test
	public void unBoundedContainsGenericPoint(){
		IntMock a = new IntMock(2, Unbounded.CLOSED_LEFT);
		assertTrue(a.contains(2));
		assertTrue(a.contains(3));
		assertFalse(a.contains(1));

		a = new IntMock(2, Unbounded.OPEN_LEFT);
		assertFalse(a.contains(2));
		assertTrue(a.contains(3));
		assertFalse(a.contains(1));

		a = new IntMock(2, Unbounded.CLOSED_RIGHT);
		assertTrue(a.contains(2));
		assertFalse(a.contains(3));
		assertTrue(a.contains(1));

		a = new IntMock(2, Unbounded.OPEN_RIGHT);
		assertFalse(a.contains(2));
		assertFalse(a.contains(3));
		assertTrue(a.contains(1));
	}

	@Test
	public void emptyContainsGenericPoint(){
		assertFalse(new IntMock(10, 0, Bounded.OPEN).contains(5));
		assertFalse(new IntMock(10, 0, Bounded.OPEN).contains(10));
		assertFalse(new IntMock(10, 0, Bounded.OPEN).contains(0));
		assertFalse(new IntMock(10, 0, Bounded.OPEN).contains(20));
		assertFalse(new IntMock(10, 0, Bounded.OPEN).contains(-5));
	}

	@Test
	public void sortByStarts(){
		IntMock[] arr = new IntMock[]{
				new IntMock(0, 5, Bounded.OPEN),
				new IntMock(0, 5, Bounded.CLOSED_RIGHT),
				new IntMock(0, 5, Bounded.CLOSED_LEFT),
				new IntMock(0, 5, Bounded.CLOSED),
				new IntMock(0, Unbounded.CLOSED_LEFT),
				new IntMock(0, Unbounded.CLOSED_RIGHT),
				new IntMock(0, Unbounded.OPEN_RIGHT),
				new IntMock(0, Unbounded.OPEN_LEFT),
				new IntMock(5, Unbounded.CLOSED_LEFT),
				new IntMock(5, Unbounded.CLOSED_RIGHT),
				new IntMock(5, Unbounded.OPEN_RIGHT),
				new IntMock(5, Unbounded.OPEN_LEFT)
		};

		IntMock[] expected = new IntMock[]{
				new IntMock(0, Unbounded.OPEN_RIGHT),
				new IntMock(0, Unbounded.CLOSED_RIGHT),
				new IntMock(5, Unbounded.OPEN_RIGHT),
				new IntMock(5, Unbounded.CLOSED_RIGHT),
				new IntMock(0, 5, Bounded.CLOSED_LEFT),
				new IntMock(0, 5, Bounded.CLOSED),
				new IntMock(0, Unbounded.CLOSED_LEFT),
				new IntMock(0, 5, Bounded.OPEN),
				new IntMock(0, 5, Bounded.CLOSED_RIGHT),
				new IntMock(0, Unbounded.OPEN_LEFT),
				new IntMock(5, Unbounded.CLOSED_LEFT),
				new IntMock(5, Unbounded.OPEN_LEFT)
		};

		Arrays.sort(arr, Interval.startComparator);
		assertArrayEquals(expected, arr);
	}

	@Test
	public void sortByEnds(){
		IntMock[] arr = new IntMock[]{
				new IntMock(0, 5, Bounded.OPEN),
				new IntMock(0, 5, Bounded.CLOSED_RIGHT),
				new IntMock(0, 5, Bounded.CLOSED_LEFT),
				new IntMock(0, 5, Bounded.CLOSED),
				new IntMock(0, Unbounded.CLOSED_LEFT),
				new IntMock(0, Unbounded.CLOSED_RIGHT),
				new IntMock(0, Unbounded.OPEN_RIGHT),
				new IntMock(0, Unbounded.OPEN_LEFT),
				new IntMock(5, Unbounded.CLOSED_LEFT),
				new IntMock(5, Unbounded.CLOSED_RIGHT),
				new IntMock(5, Unbounded.OPEN_RIGHT),
				new IntMock(5, Unbounded.OPEN_LEFT)
		};

		IntMock[] expected = new IntMock[]{
				new IntMock(5, Unbounded.OPEN_LEFT),
				new IntMock(5, Unbounded.CLOSED_LEFT),
				new IntMock(0, Unbounded.OPEN_LEFT),
				new IntMock(0, Unbounded.CLOSED_LEFT),
				new IntMock(0, 5, Bounded.CLOSED_RIGHT),
				new IntMock(0, 5, Bounded.CLOSED),
				new IntMock(5, Unbounded.CLOSED_RIGHT),
				new IntMock(0, 5, Bounded.OPEN),
				new IntMock(0, 5, Bounded.CLOSED_LEFT),
				new IntMock(5, Unbounded.OPEN_RIGHT),
				new IntMock(0, Unbounded.CLOSED_RIGHT),
				new IntMock(0, Unbounded.OPEN_RIGHT)
		};

		Arrays.sort(arr, Interval.endComparator);
		for (int i=0; i<arr.length; i++)
			assertEquals(arr[i], expected[i]);
		assertArrayEquals(expected, arr);
	}

	@Test
	public void equalsOtherClasses(){
		IntMock a = new IntMock(5, 10, Bounded.OPEN);
		assertNotEquals(a, new Integer(5));
		assertEquals(a, new IntegerInterval(5, 10, Bounded.OPEN));
		assertNotEquals(a, new IntegerInterval(5, 11, Bounded.CLOSED));
		assertNotEquals(a, new Date());
	}

	@Test
	public void builderWithOnlyOneMethod() {
		IntMock a = new IntMock(5, 10, Bounded.OPEN);
		assertEquals(new IntMock(2, Unbounded.OPEN_LEFT), a.builder().greater(2).build());
		assertEquals(new IntMock(2, Unbounded.CLOSED_LEFT), a.builder().greaterEqual(2).build());
		assertEquals(new IntMock(2, Unbounded.OPEN_RIGHT), a.builder().less(2).build());
		assertEquals(new IntMock(2, Unbounded.CLOSED_RIGHT), a.builder().lessEqual(2).build());
	}

	@Test
	public void builderWithTwoMethods() {
		IntMock a = new IntMock(5, 10, Bounded.OPEN);
		assertEquals(new IntMock(2, 10, Bounded.OPEN), a.builder().greater(2).less(10).build());
		assertEquals(new IntMock(2, 10, Bounded.CLOSED_RIGHT), a.builder().greater(2).lessEqual(10).build());

		assertEquals(new IntMock(2, 10, Bounded.CLOSED_LEFT), a.builder().greaterEqual(2).less(10).build());
		assertEquals(new IntMock(2, 10, Bounded.CLOSED), a.builder().greaterEqual(2).lessEqual(10).build());

		assertEquals(new IntMock(0, 2, Bounded.OPEN), a.builder().less(2).greater(0).build());
		assertEquals(new IntMock(0, 2, Bounded.CLOSED_LEFT), a.builder().less(2).greaterEqual(0).build());

		assertEquals(new IntMock(0, 2, Bounded.CLOSED_RIGHT), a.builder().lessEqual(2).greater(0).build());
		assertEquals(new IntMock(0, 2, Bounded.CLOSED), a.builder().lessEqual(2).greaterEqual(0).build());
	}

	@Test
	public void builderWithSameMethodTwice() {
		IntMock a = new IntMock(5, 10, Bounded.OPEN);

		assertEquals(new IntMock(3, Unbounded.OPEN_LEFT), a.builder().greater(2).greater(3).build());
		assertEquals(new IntMock(1, Unbounded.OPEN_LEFT), a.builder().greater(2).greater(1).build());
		assertEquals(new IntMock(2, Unbounded.OPEN_LEFT), a.builder().greater(2).greater(2).build());

		assertEquals(new IntMock(3, Unbounded.CLOSED_LEFT), a.builder().greaterEqual(2).greaterEqual(3).build());
		assertEquals(new IntMock(1, Unbounded.CLOSED_LEFT), a.builder().greaterEqual(2).greaterEqual(1).build());
		assertEquals(new IntMock(2, Unbounded.CLOSED_LEFT), a.builder().greaterEqual(2).greaterEqual(2).build());

		assertEquals(new IntMock(3, Unbounded.OPEN_RIGHT), a.builder().less(2).less(3).build());
		assertEquals(new IntMock(1, Unbounded.OPEN_RIGHT), a.builder().less(2).less(1).build());
		assertEquals(new IntMock(2, Unbounded.OPEN_RIGHT), a.builder().less(2).less(2).build());

		assertEquals(new IntMock(3, Unbounded.CLOSED_RIGHT), a.builder().lessEqual(2).lessEqual(3).build());
		assertEquals(new IntMock(1, Unbounded.CLOSED_RIGHT), a.builder().lessEqual(2).lessEqual(1).build());
		assertEquals(new IntMock(2, Unbounded.CLOSED_RIGHT), a.builder().lessEqual(2).lessEqual(2).build());
	}

	@Test
	public void builderWithTwoDifferentMethodsOfSameType() {
		IntMock a = new IntMock(5, 10, Bounded.OPEN);

		assertEquals(new IntMock(2, Unbounded.OPEN_RIGHT), a.builder().lessEqual(2).less(2).build());
		assertEquals(new IntMock(2, Unbounded.CLOSED_RIGHT), a.builder().less(2).lessEqual(2).build());
		assertEquals(new IntMock(2, Unbounded.OPEN_LEFT), a.builder().greaterEqual(2).greater(2).build());
		assertEquals(new IntMock(2, Unbounded.CLOSED_LEFT), a.builder().greater(2).greaterEqual(2).build());
	}

	@Test
	public void isLeftOfBoundedInterval(){
		IntMock a = new IntMock(5, 10, Bounded.OPEN);
		assertTrue(a.isLeftOf(new IntMock(10, 11, Bounded.CLOSED)));
		assertTrue(a.isLeftOf(new IntMock(10, 11, Bounded.OPEN)));
		assertFalse(a.isLeftOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(6, 12, Bounded.CLOSED_RIGHT)));
		assertFalse(a.isLeftOf(new IntMock(1, 7, Bounded.CLOSED_LEFT)));
		assertFalse(a.isLeftOf(new IntMock(1, 4, Bounded.CLOSED)));

		a = new IntMock(5, 10, Bounded.CLOSED);
		assertFalse(a.isLeftOf(new IntMock(10, 11, Bounded.CLOSED)));
		assertTrue(a.isLeftOf(new IntMock(10, 11, Bounded.OPEN)));
		assertFalse(a.isLeftOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(6, 12, Bounded.CLOSED_RIGHT)));
		assertFalse(a.isLeftOf(new IntMock(1, 7, Bounded.CLOSED_LEFT)));
		assertFalse(a.isLeftOf(new IntMock(1, 4, Bounded.CLOSED)));
	}

	@Test
	public void isLeftOfUnboundedInterval(){
		IntMock a = new IntMock(5, Unbounded.CLOSED_LEFT);
		assertFalse(a.isLeftOf(new IntMock(0, 5, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(0, 5, Bounded.OPEN)));
		assertFalse(a.isLeftOf(new IntMock(5, 11, Bounded.OPEN)));
		assertFalse(a.isLeftOf(new IntMock(5, 11, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(4, 12, Bounded.CLOSED_RIGHT)));
		assertFalse(a.isLeftOf(new IntMock(1, 7, Bounded.CLOSED_LEFT)));

		a = new IntMock(5, Unbounded.OPEN_LEFT);
		assertFalse(a.isLeftOf(new IntMock(0, 5, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(0, 5, Bounded.OPEN)));
		assertFalse(a.isLeftOf(new IntMock(5, 11, Bounded.OPEN)));
		assertFalse(a.isLeftOf(new IntMock(5, 11, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(4, 12, Bounded.CLOSED_RIGHT)));
		assertFalse(a.isLeftOf(new IntMock(1, 7, Bounded.CLOSED_LEFT)));

		a = new IntMock(5, Unbounded.CLOSED_RIGHT);
		assertFalse(a.isLeftOf(new IntMock(0, 5, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(0, 5, Bounded.OPEN)));
		assertTrue(a.isLeftOf(new IntMock(5, 11, Bounded.OPEN)));
		assertFalse(a.isLeftOf(new IntMock(5, 11, Bounded.CLOSED)));
		assertTrue(a.isLeftOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(4, 12, Bounded.CLOSED_RIGHT)));

		a = new IntMock(5, Unbounded.OPEN_RIGHT);
		assertFalse(a.isLeftOf(new IntMock(0, 5, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(0, 5, Bounded.OPEN)));
		assertTrue(a.isLeftOf(new IntMock(5, 11, Bounded.OPEN)));
		assertTrue(a.isLeftOf(new IntMock(5, 11, Bounded.CLOSED)));
		assertTrue(a.isLeftOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isLeftOf(new IntMock(4, 12, Bounded.CLOSED_RIGHT)));
	}

	@Test
	public void isRightOfInterval(){
		IntMock a = new IntMock(5, 10, Bounded.OPEN);
		assertTrue(a.isRightOf(new IntMock(4, 5, Bounded.CLOSED)));
		assertTrue(a.isRightOf(new IntMock(4, 5, Bounded.OPEN)));
		assertFalse(a.isRightOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(6, 12, Bounded.CLOSED_RIGHT)));
		assertFalse(a.isRightOf(new IntMock(1, 7, Bounded.CLOSED_LEFT)));
		assertFalse(a.isRightOf(new IntMock(11, 14, Bounded.CLOSED)));

		a = new IntMock(5, 10, Bounded.CLOSED);
		assertFalse(a.isRightOf(new IntMock(4, 5, Bounded.CLOSED)));
		assertTrue(a.isRightOf(new IntMock(4, 5, Bounded.OPEN)));
		assertFalse(a.isRightOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(6, 12, Bounded.CLOSED_RIGHT)));
		assertFalse(a.isRightOf(new IntMock(1, 7, Bounded.CLOSED_LEFT)));
		assertFalse(a.isRightOf(new IntMock(11, 14, Bounded.CLOSED)));
	}

	@Test
	public void isRightOfUnboundedInterval(){
		IntMock a = new IntMock(5, Unbounded.CLOSED_LEFT);
		assertFalse(a.isRightOf(new IntMock(0, 5, Bounded.CLOSED)));
		assertTrue(a.isRightOf(new IntMock(0, 5, Bounded.OPEN)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.CLOSED_LEFT)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.CLOSED_RIGHT)));
		assertTrue(a.isRightOf(new IntMock(5, Unbounded.OPEN_RIGHT)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.OPEN_LEFT)));
		assertFalse(a.isRightOf(new IntMock(5, 11, Bounded.OPEN)));
		assertFalse(a.isRightOf(new IntMock(5, 11, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(4, 12, Bounded.CLOSED_RIGHT)));
		assertTrue(a.isRightOf(new IntMock(1, 3, Bounded.CLOSED_LEFT)));

		a = new IntMock(5, Unbounded.OPEN_LEFT);
		assertTrue(a.isRightOf(new IntMock(0, 5, Bounded.CLOSED)));
		assertTrue(a.isRightOf(new IntMock(0, 5, Bounded.OPEN)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.CLOSED_LEFT)));
		assertTrue(a.isRightOf(new IntMock(5, Unbounded.CLOSED_RIGHT)));
		assertTrue(a.isRightOf(new IntMock(5, Unbounded.OPEN_RIGHT)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.OPEN_LEFT)));
		assertFalse(a.isRightOf(new IntMock(5, 11, Bounded.OPEN)));
		assertFalse(a.isRightOf(new IntMock(5, 11, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(4, 12, Bounded.CLOSED_RIGHT)));
		assertTrue(a.isRightOf(new IntMock(1, 3, Bounded.CLOSED_LEFT)));

		a = new IntMock(5, Unbounded.CLOSED_RIGHT);
		assertFalse(a.isRightOf(new IntMock(5, 8, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(5, 8, Bounded.OPEN)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.CLOSED_LEFT)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.CLOSED_RIGHT)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.OPEN_RIGHT)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.OPEN_LEFT)));
		assertFalse(a.isRightOf(new IntMock(0, 5, Bounded.OPEN)));
		assertFalse(a.isRightOf(new IntMock(0, 5, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(4, 12, Bounded.CLOSED_RIGHT)));
		assertFalse(a.isRightOf(new IntMock(1, 3, Bounded.CLOSED_LEFT)));

		a = new IntMock(5, Unbounded.OPEN_RIGHT);
		assertFalse(a.isRightOf(new IntMock(5, 8, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(5, 8, Bounded.OPEN)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.CLOSED_LEFT)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.CLOSED_RIGHT)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.OPEN_RIGHT)));
		assertFalse(a.isRightOf(new IntMock(5, Unbounded.OPEN_LEFT)));
		assertFalse(a.isRightOf(new IntMock(0, 5, Bounded.OPEN)));
		assertFalse(a.isRightOf(new IntMock(0, 5, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(6, 8, Bounded.CLOSED)));
		assertFalse(a.isRightOf(new IntMock(4, 12, Bounded.CLOSED_RIGHT)));
		assertFalse(a.isRightOf(new IntMock(1, 3, Bounded.CLOSED_LEFT)));
	}

	@Test
	public void isRightOfDegenerate(){
		IntMock a = new IntMock(5, Unbounded.CLOSED_LEFT);
		assertFalse(a.isRightOf((Interval<Integer>)null));
		assertFalse(a.isRightOf((Integer)null));
		assertFalse(a.isRightOf(new IntMock()));
		assertFalse(a.isRightOf(new IntMock(20, 10, Bounded.OPEN)));

		a = new IntMock(5, Unbounded.CLOSED_RIGHT);
		assertFalse(a.isRightOf((Interval<Integer>)null));
		assertFalse(a.isRightOf((Integer)null));
		assertFalse(a.isRightOf(new IntMock()));
		assertFalse(a.isRightOf(new IntMock(20, 10, Bounded.OPEN)));

		a = new IntMock(5, Unbounded.OPEN_LEFT);
		assertFalse(a.isRightOf((Interval<Integer>)null));
		assertFalse(a.isRightOf((Integer)null));
		assertFalse(a.isRightOf(new IntMock()));
		assertFalse(a.isRightOf(new IntMock(20, 10, Bounded.OPEN)));

		a = new IntMock(5, Unbounded.OPEN_RIGHT);
		assertFalse(a.isRightOf((Interval<Integer>)null));
		assertFalse(a.isRightOf((Integer)null));
		assertFalse(a.isRightOf(new IntMock()));
		assertFalse(a.isRightOf(new IntMock(20, 10, Bounded.OPEN)));
	}

	@Test
	public void isLeftOfDegenerate(){
		IntMock a = new IntMock(5, Unbounded.CLOSED_LEFT);
		assertFalse(a.isLeftOf((Interval<Integer>)null));
		assertFalse(a.isLeftOf((Integer)null));
		assertFalse(a.isLeftOf(new IntMock()));
		assertFalse(a.isLeftOf(new IntMock(20, 10, Bounded.OPEN)));

		a = new IntMock(5, Unbounded.CLOSED_RIGHT);
		assertFalse(a.isLeftOf((Interval<Integer>)null));
		assertFalse(a.isLeftOf((Integer)null));
		assertFalse(a.isLeftOf(new IntMock()));
		assertFalse(a.isLeftOf(new IntMock(20, 10, Bounded.OPEN)));

		a = new IntMock(5, Unbounded.OPEN_LEFT);
		assertFalse(a.isLeftOf((Interval<Integer>)null));
		assertFalse(a.isLeftOf((Integer)null));
		assertFalse(a.isLeftOf(new IntMock()));
		assertFalse(a.isLeftOf(new IntMock(20, 10, Bounded.OPEN)));

		a = new IntMock(5, Unbounded.OPEN_RIGHT);
		assertFalse(a.isLeftOf((Interval<Integer>)null));
		assertFalse(a.isLeftOf((Integer)null));
		assertFalse(a.isLeftOf(new IntMock()));
		assertFalse(a.isLeftOf(new IntMock(20, 10, Bounded.OPEN)));
	}

	@Test
	public void isLeftOfPoint(){
		IntMock a = new IntMock(5, 10, Bounded.OPEN);
		assertTrue(a.isLeftOf(10));
		assertTrue(a.isLeftOf(11));
		assertFalse(a.isLeftOf(9));
		assertFalse(a.isLeftOf(5));
		assertFalse(a.isLeftOf(3));

		a = new IntMock(5, 10, Bounded.CLOSED);
		assertFalse(a.isLeftOf(10));
		assertTrue(a.isLeftOf(11));
		assertFalse(a.isLeftOf(9));
		assertFalse(a.isLeftOf(5));
		assertFalse(a.isLeftOf(3));
	}

	@Test
	public void isRightOfPoint(){
		IntMock a = new IntMock(5, 10, Bounded.OPEN);
		assertTrue(a.isRightOf(5));
		assertTrue(a.isRightOf(4));
		assertFalse(a.isRightOf(6));
		assertFalse(a.isRightOf(10));
		assertFalse(a.isRightOf(12));

		a = new IntMock(5, 10, Bounded.CLOSED);
		assertFalse(a.isRightOf(5));
		assertTrue(a.isRightOf(4));
		assertFalse(a.isRightOf(6));
		assertFalse(a.isRightOf(10));
		assertFalse(a.isRightOf(12));
	}

	@Test
	public void test_enum(){
		assertEquals(Unbounded.CLOSED_LEFT, Unbounded.valueOf("CLOSED_LEFT"));
		assertEquals(Unbounded.CLOSED_RIGHT, Unbounded.valueOf("CLOSED_RIGHT"));
		assertEquals(Unbounded.OPEN_LEFT, Unbounded.valueOf("OPEN_LEFT"));
		assertEquals(Unbounded.OPEN_RIGHT, Unbounded.valueOf("OPEN_RIGHT"));

		assertEquals(Bounded.CLOSED, Bounded.valueOf("CLOSED"));
		assertEquals(Bounded.OPEN, Bounded.valueOf("OPEN"));
		assertEquals(Bounded.CLOSED_LEFT, Bounded.valueOf("CLOSED_LEFT"));
		assertEquals(Bounded.CLOSED_RIGHT, Bounded.valueOf("CLOSED_RIGHT"));

		assertEquals(new IntMock(1, 10, Bounded.CLOSED), new IntMock(1, 10, null));
		assertEquals(new IntMock(1, Unbounded.CLOSED_RIGHT), new IntMock(1, null));
	}

	private static class IntMock extends Interval<Integer>{

		private IntMock(int start, int end, Bounded type) {
			super(start, end, type);
		}

		private IntMock(int start, Unbounded type) {
			super(start, type);
		}

		private IntMock() {
			super();
		}

		@Override
		protected Interval<Integer> create() {
			return new IntMock();
		}

		@Override
		public Integer getMidpoint() {
			return null;
		}
	}
}

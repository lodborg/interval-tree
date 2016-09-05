package com.lodborg.intervaltree;

import org.junit.Test;

import com.lodborg.intervaltree.Interval.*;

import static org.junit.Assert.*;

public class IntegerIntervalTest {
	@Test
	public void test_emptyIntervalMidPoint(){
		IntegerInterval[] intervals = new IntegerInterval[]{
				new IntegerInterval(1, 2, Bounded.OPEN),
				new IntegerInterval(2, 1, Bounded.CLOSED),
				new IntegerInterval(2, 1, Bounded.CLOSED_LEFT),
				new IntegerInterval(2, 1, Bounded.CLOSED_RIGHT),
				new IntegerInterval(2, 1, Bounded.CLOSED),
				new IntegerInterval(2, 2, Bounded.CLOSED_LEFT),
				new IntegerInterval(2, 2, Bounded.CLOSED_RIGHT),
				new IntegerInterval(2, 2, Bounded.OPEN)
		};
		for (Interval interval: intervals)
			assertNull(interval.getMidpoint());
	}

	@Test
	public void test_isNotPoint(){
		IntegerInterval[] intervals = new IntegerInterval[]{
				new IntegerInterval(1, 2, Bounded.OPEN),
				new IntegerInterval(2, 1, Bounded.CLOSED),
				new IntegerInterval(2, 1, Bounded.CLOSED_LEFT),
				new IntegerInterval(2, 1, Bounded.CLOSED_RIGHT),
				new IntegerInterval(2, 1, Bounded.OPEN),
				new IntegerInterval(2, 2, Bounded.CLOSED_LEFT),
				new IntegerInterval(2, 2, Bounded.CLOSED_RIGHT),
				new IntegerInterval(2, 2, Bounded.OPEN),
				new IntegerInterval(2, Unbounded.CLOSED_LEFT),
				new IntegerInterval(2, Unbounded.CLOSED_RIGHT),
				new IntegerInterval(2, Unbounded.OPEN_RIGHT),
				new IntegerInterval(2, Unbounded.OPEN_LEFT),
				new IntegerInterval(9, 20, Bounded.CLOSED)
		};
		for (Interval interval: intervals)
			assertFalse(interval.isPoint());
	}

	@Test
	public void test_intersectsEndpointClosedRight(){
		IntegerInterval main = new IntegerInterval(-2897, 19, Bounded.CLOSED_RIGHT);
		assertTrue(main.intersects(new IntegerInterval(19, 222, Bounded.CLOSED_LEFT)));
		assertTrue(main.intersects(new IntegerInterval(19, 19, Bounded.CLOSED)));
		assertTrue(main.intersects(new IntegerInterval(18, 19, Bounded.CLOSED_RIGHT)));

		assertTrue(main.intersects(new IntegerInterval(19, Unbounded.CLOSED_LEFT)));
		assertTrue(main.intersects(new IntegerInterval(19, Unbounded.CLOSED_RIGHT)));
		assertTrue(main.intersects(new IntegerInterval(19, Unbounded.OPEN_RIGHT)));
	}

	@Test
	public void test_notIntersectsEndpointClosedRight(){
		IntegerInterval main = new IntegerInterval(-2897, 19, Bounded.CLOSED_RIGHT);
		assertFalse(main.intersects(new IntegerInterval(19, 3874, Bounded.CLOSED_RIGHT)));
		assertFalse(main.intersects(new IntegerInterval(19, 20, Bounded.OPEN)));

		assertFalse(main.intersects(new IntegerInterval(19, Unbounded.OPEN_LEFT)));
	}

	@Test
	public void test_notIntersectsEndpointOpenRight(){
		IntegerInterval main = new IntegerInterval(-2897, 19, Bounded.OPEN);
		assertFalse(main.intersects(new IntegerInterval(19, 3874, Bounded.CLOSED_LEFT)));
		assertFalse(main.intersects(new IntegerInterval(19, 20, Bounded.CLOSED)));

		assertFalse(main.intersects(new IntegerInterval(19, Unbounded.OPEN_LEFT)));
		assertFalse(main.intersects(new IntegerInterval(19, Unbounded.CLOSED_LEFT)));
	}

	@Test
	public void test_intersectsEndpointClosedLeft(){
		IntegerInterval main = new IntegerInterval(-2897, 19, Bounded.CLOSED_LEFT);
		assertTrue(main.intersects(new IntegerInterval(-2899, -2897, Bounded.CLOSED_RIGHT)));
		assertTrue(main.intersects(new IntegerInterval(-2897, -2897, Bounded.CLOSED)));
		assertTrue(main.intersects(new IntegerInterval(-2897, -2890, Bounded.CLOSED_RIGHT)));
		assertTrue(main.intersects(new IntegerInterval(-47589, -2896, Bounded.CLOSED_RIGHT)));

		assertTrue(main.intersects(new IntegerInterval(-2897, Unbounded.CLOSED_RIGHT)));
		assertTrue(main.intersects(new IntegerInterval(-2897, Unbounded.OPEN_LEFT)));
		assertTrue(main.intersects(new IntegerInterval(-2897, Unbounded.CLOSED_LEFT)));
	}

	@Test
	public void test_notIntersectsEndpointClosedLeft(){
		IntegerInterval main = new IntegerInterval(-2897, 19, Bounded.CLOSED_LEFT);
		assertFalse(main.intersects(new IntegerInterval(-47589, -2897, Bounded.CLOSED_LEFT)));
		assertFalse(main.intersects(new IntegerInterval(-47589, -2897, Bounded.OPEN)));

		assertFalse(main.intersects(new IntegerInterval(-2897, Unbounded.OPEN_RIGHT)));
	}

	@Test
	public void test_notIntersectsEndpointOpenLeft(){
		IntegerInterval main = new IntegerInterval(-2897, 19, Bounded.CLOSED_RIGHT);
		assertFalse(main.intersects(new IntegerInterval(-47589, -2897, Bounded.CLOSED_RIGHT)));
		assertFalse(main.intersects(new IntegerInterval(-47589, -2897, Bounded.CLOSED)));
		assertFalse(main.intersects(new IntegerInterval(-47589, -2897, Bounded.OPEN)));
		assertFalse(main.intersects(new IntegerInterval(-47589, -2897, Bounded.CLOSED_LEFT)));

		assertFalse(main.intersects(new IntegerInterval(-2897, Unbounded.OPEN_RIGHT)));
		assertFalse(main.intersects(new IntegerInterval(-2897, Unbounded.CLOSED_RIGHT)));
	}

	@Test
	public void test_intersectsOffByOne(){
		IntegerInterval main = new IntegerInterval(-2897, 19, Bounded.CLOSED);
		assertTrue(main.intersects(new IntegerInterval(-47589, -2896, Bounded.CLOSED_LEFT)));
		assertTrue(main.intersects(new IntegerInterval(18, 20, Bounded.CLOSED)));

		assertFalse(main.intersects(new IntegerInterval(20, 21, Bounded.CLOSED)));
		assertFalse(main.intersects(new IntegerInterval(20, 21, Bounded.OPEN)));
		assertFalse(main.intersects(new IntegerInterval(20, 21, Bounded.CLOSED_RIGHT)));
		assertFalse(main.intersects(new IntegerInterval(20, 21, Bounded.CLOSED_LEFT)));
		assertFalse(main.intersects(new IntegerInterval(-3000, -2898, Bounded.CLOSED)));
		assertFalse(main.intersects(new IntegerInterval(-3000, -2898, Bounded.OPEN)));
		assertFalse(main.intersects(new IntegerInterval(-3000, -2898, Bounded.CLOSED_RIGHT)));
		assertFalse(main.intersects(new IntegerInterval(-3000, -2898, Bounded.CLOSED_LEFT)));
	}

	@Test
	public void test_intersectionExistsButEmpty(){
		IntegerInterval main = new IntegerInterval(-2897, 19, Bounded.CLOSED_RIGHT);
		IntegerInterval other = new IntegerInterval(-47589, -2896, Bounded.OPEN);
		assertFalse(main.intersects(other));
		assertNull(main.getIntersection(other));
		assertNull(other.getIntersection(main));
	}

	@Test
	public void test_intersectsFullyContained(){
		IntegerInterval main = new IntegerInterval(-2897, 19, Bounded.CLOSED_RIGHT);
		assertTrue(main.intersects(new IntegerInterval(-15, 18, Bounded.OPEN)));
		assertTrue(main.intersects(new IntegerInterval(-100, -100, Bounded.CLOSED)));
	}

	@Test
	public void test_intersectsPoint(){
		IntegerInterval main = new IntegerInterval(105, 105, Bounded.CLOSED);

		assertFalse(main.intersects(new IntegerInterval(-90, 0, Bounded.CLOSED_RIGHT)));
		assertEquals(main.intersects(new IntegerInterval(33, Integer.MAX_VALUE, Bounded.CLOSED_RIGHT)), true);
		assertEquals(main.intersects(new IntegerInterval(33, 105, Bounded.CLOSED_RIGHT)), true);
		assertEquals(main.intersects(new IntegerInterval(33, 104, Bounded.CLOSED_RIGHT)), false);
		assertEquals(main.intersects(new IntegerInterval(100, 200, Bounded.CLOSED_RIGHT)), true);
		assertEquals(main.intersects(new IntegerInterval(105, 180, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.intersects(new IntegerInterval(105, 180, Bounded.CLOSED_RIGHT)), false);

		assertEquals(main.intersects(new IntegerInterval(105, 105, Bounded.CLOSED)), true);
		assertEquals(main.intersects(new IntegerInterval(106, 106, Bounded.CLOSED)), false);
		assertEquals(main.intersects(new IntegerInterval(104, 104, Bounded.CLOSED)), false);
	}

	@Test
	public void openLeftContainsOtherWithCommonLeftEndpoint(){
		IntegerInterval main = new IntegerInterval(-7392, -42, Bounded.CLOSED_RIGHT);

		assertEquals(main.contains(new IntegerInterval(-7392, 15, Bounded.OPEN)), false);
		assertEquals(main.contains(new IntegerInterval(-7392, 15, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(-7392, 15, Bounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(-7392, 15, Bounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(-7392, -100, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(-7392, -100, Bounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(-7392, -100, Bounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(-7392, -100, Bounded.CLOSED)), false);
	}

	@Test
	public void closedLeftContainsOtherWithCommonLeftEndpoint(){
		IntegerInterval main = new IntegerInterval(-7392, -42, Bounded.CLOSED_LEFT);

		assertEquals(main.contains(new IntegerInterval(-7392, 15, Bounded.OPEN)), false);
		assertEquals(main.contains(new IntegerInterval(-7392, 15, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(-7392, 15, Bounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(-7392, -100, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(-7392, -100, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(-7392, -100, Bounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(-7392, -100, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(-7391, -100, Bounded.OPEN)), true);
	}

	@Test
	public void openRightContainsOtherWithCommonRightEndpoint(){
		IntegerInterval main = new IntegerInterval(-7392, 42, Bounded.CLOSED_LEFT);

		assertEquals(main.contains(new IntegerInterval(-10000, 42, Bounded.OPEN)), false);
		assertEquals(main.contains(new IntegerInterval(-10000, 42, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(-10000, 42, Bounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(-10000, 42, Bounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(0, 42, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(0, 42, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(0, 42, Bounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(0, 42, Bounded.CLOSED)), false);
	}

	@Test
	public void closedRightContainsOtherWithCommonRightEndpoint(){
		IntegerInterval main = new IntegerInterval(-7392, 42, Bounded.CLOSED_RIGHT);

		assertEquals(main.contains(new IntegerInterval(-10000, 42, Bounded.OPEN)), false);
		assertEquals(main.contains(new IntegerInterval(-10000, 42, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(-10000, 42, Bounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(-10000, 42, Bounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(0, 42, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(0, 42, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(0, 42, Bounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(0, 42, Bounded.CLOSED)), true);
	}

	@Test
	public void openLeftContainsOffByOne(){
		IntegerInterval main = new IntegerInterval(-7392, -42, Bounded.CLOSED_RIGHT);

		assertEquals(main.contains(new IntegerInterval(-7393, -100, Bounded.OPEN)), false);
		assertEquals(main.contains(new IntegerInterval(-7391, -100, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(-7391, -100, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(-7391, -100, Bounded.OPEN)), true);
	}

	@Test
	public void openRightContainsOffByOne(){
		IntegerInterval main = new IntegerInterval(-7392, 42, Bounded.CLOSED_LEFT);

		assertEquals(main.contains(new IntegerInterval(0, 43, Bounded.OPEN)), false);
		assertEquals(main.contains(new IntegerInterval(0, 43, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(0, 43, Bounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(0, 43, Bounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(0, 41, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(0, 41, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(0, 41, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(0, 41, Bounded.CLOSED_RIGHT)), true);
	}

	@Test
	public void closedLeftContainsOffByOne(){
		IntegerInterval main = new IntegerInterval(-7392, -42, Bounded.CLOSED_LEFT);

		assertEquals(main.contains(new IntegerInterval(-7393, -100, Bounded.OPEN)), false);
		assertEquals(main.contains(new IntegerInterval(-7391, -100, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(-7391, -100, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(-7391, -100, Bounded.OPEN)), true);
	}

	@Test
	public void test_containsSameEndpoints(){
		IntegerInterval main = new IntegerInterval(10, 20, Bounded.CLOSED_LEFT);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED_RIGHT)), false);

		main = new IntegerInterval(10, 20, Bounded.CLOSED_RIGHT);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED_RIGHT)), true);

		main = new IntegerInterval(10, 20, Bounded.CLOSED);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED_RIGHT)), true);

		main = new IntegerInterval(10, 20, Bounded.OPEN);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(10, 20, Bounded.CLOSED_RIGHT)), false);
	}

	@Test
	public void test_containsUnboundedClosedLeft(){
		IntegerInterval main = new IntegerInterval(200, Unbounded.CLOSED_LEFT); // [200, +inf)
		assertEquals(main.contains(new IntegerInterval(200, 250, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(200, 250, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(201, Unbounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(200, Unbounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(199, Unbounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(199, 200, Bounded.CLOSED)), false);
	}

	@Test
	public void test_containsUnboundedOpenLeft(){
		IntegerInterval main = new IntegerInterval(200, Unbounded.OPEN_LEFT); // (200, +inf)
		assertEquals(main.contains(new IntegerInterval(200, 250, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(200, 250, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(201, Unbounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(200, Unbounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(199, Unbounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(199, 200, Bounded.CLOSED)), false);
	}

	@Test
	public void test_containsUnboundedOpenRight(){
		IntegerInterval main = new IntegerInterval(81, Unbounded.OPEN_RIGHT); // (-inf, 81)
		assertEquals(main.contains(new IntegerInterval(10, 81, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(15, 81, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(81, Unbounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(80, Unbounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(82, Unbounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(80, 81, Bounded.CLOSED)), false);
	}

	@Test
	public void test_containsUnboundedClosedRight(){
		IntegerInterval main = new IntegerInterval(81, Unbounded.CLOSED_RIGHT); // (-inf, 81]
		assertEquals(main.contains(new IntegerInterval(10, 81, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(15, 81, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(81, Unbounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(80, Unbounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(82, Unbounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(80, 81, Bounded.CLOSED)), true);
	}

	@Test
	public void test_containsUnboundedEverything(){
		IntegerInterval main = new IntegerInterval(); // (-inf, +inf)
		assertEquals(main.contains(new IntegerInterval()), true);
		assertEquals(main.contains(new IntegerInterval(10, 81, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(10, 81, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.OPEN_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.OPEN_RIGHT)), true);
	}
}
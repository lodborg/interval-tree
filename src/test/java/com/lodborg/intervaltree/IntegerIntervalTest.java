package com.lodborg.intervaltree;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.lodborg.intervaltree.Interval.*;

public class IntegerIntervalTest {
	@Test
	public void intersectsInterval() {
		IntegerInterval main = new IntegerInterval(-2897, 19, Bounded.CLOSED_RIGHT);

		assertEquals(main.intersects(new IntegerInterval(19, 222, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.intersects(new IntegerInterval(19, 19, Bounded.CLOSED)), true);
		assertEquals(main.intersects(new IntegerInterval(19, 3874, Bounded.CLOSED_RIGHT)), false);
		assertEquals(main.intersects(new IntegerInterval(19, 20, Bounded.OPEN)), false);

		assertEquals(main.intersects(new IntegerInterval(-47589, -2897, Bounded.CLOSED_RIGHT)), false);
		assertEquals(main.intersects(new IntegerInterval(-47589, -2897, Bounded.OPEN)), false);
		assertEquals(main.intersects(new IntegerInterval(-47589, -2896, Bounded.CLOSED_RIGHT)), true);

		assertEquals(main.intersects(new IntegerInterval(-47589, -2896, Bounded.OPEN)), false); // No integers in (-2897, -2896)
		assertEquals(main.intersects(new IntegerInterval(-47589, -2898, Bounded.OPEN)), false);
		assertEquals(main.intersects(new IntegerInterval(-47589, -2898, Bounded.CLOSED_RIGHT)), false);

		assertEquals(main.intersects(new IntegerInterval(-15, 18, Bounded.OPEN)), true);
		assertEquals(main.intersects(new IntegerInterval(-100, -100, Bounded.CLOSED)), true);

		main = new IntegerInterval(105, 105, Bounded.CLOSED);

		assertEquals(main.intersects(new IntegerInterval(-90, 0, Bounded.CLOSED_RIGHT)), false);
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
	public void containsInterval(){
		IntegerInterval main = new IntegerInterval(-7392, -42, Bounded.CLOSED_RIGHT);

		assertEquals(main.contains(new IntegerInterval(-7392, 15, Bounded.OPEN)), false);
		assertEquals(main.contains(new IntegerInterval(-7392, -100, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(-7392, -100, Bounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(-7391, -100, Bounded.OPEN)), true);

		main = new IntegerInterval(128, 201, Bounded.CLOSED_LEFT);
		assertEquals(main.contains(new IntegerInterval(128, 150, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(-243, 128, Bounded.OPEN)), false);
		assertEquals(main.contains(new IntegerInterval(-243, 1000, Bounded.OPEN)), false);
		assertEquals(main.contains(new IntegerInterval(128, 201, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(128, 201, Bounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(128, 201, Bounded.CLOSED_RIGHT)), false);

		main = new IntegerInterval(8647, 11112, Bounded.CLOSED_LEFT);
		assertEquals(main.contains(new IntegerInterval(9197, 9199, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(9197, 11112, Bounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(9197, 11111, Bounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(9197, 11111, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(9197, 11112, Bounded.OPEN)), true);

		main = new IntegerInterval(200, Unbounded.CLOSED_LEFT); // [200, +inf)
		assertEquals(main.contains(new IntegerInterval(200, 250, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(200, 250, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(201, Unbounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(200, Unbounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(199, Unbounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(199, 200, Bounded.CLOSED)), false);

		main = new IntegerInterval(200, Unbounded.OPEN_LEFT); // (200, +inf)
		assertEquals(main.contains(new IntegerInterval(200, 250, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(200, 250, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(201, Unbounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(200, Unbounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(199, Unbounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(199, 200, Bounded.CLOSED)), false);

		main = new IntegerInterval(81, Unbounded.OPEN_RIGHT); // (-inf, 81)
		assertEquals(main.contains(new IntegerInterval(10, 81, Bounded.CLOSED)), false);
		assertEquals(main.contains(new IntegerInterval(15, 81, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(81, Unbounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(80, Unbounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(82, Unbounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(80, 81, Bounded.CLOSED)), false);

		main = new IntegerInterval(81, Unbounded.CLOSED_RIGHT); // (-inf, 81]
		assertEquals(main.contains(new IntegerInterval(10, 81, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(15, 81, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.CLOSED_LEFT)), false);
		assertEquals(main.contains(new IntegerInterval(81, Unbounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(80, Unbounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(82, Unbounded.CLOSED_RIGHT)), false);
		assertEquals(main.contains(new IntegerInterval(80, 81, Bounded.CLOSED)), true);

		main = new IntegerInterval(); // (-inf, +inf)
		assertEquals(main.contains(new IntegerInterval()), true);
		assertEquals(main.contains(new IntegerInterval(10, 81, Bounded.CLOSED)), true);
		assertEquals(main.contains(new IntegerInterval(10, 81, Bounded.OPEN)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.CLOSED_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.OPEN_LEFT)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.CLOSED_RIGHT)), true);
		assertEquals(main.contains(new IntegerInterval(10, Unbounded.OPEN_RIGHT)), true);
	}
}
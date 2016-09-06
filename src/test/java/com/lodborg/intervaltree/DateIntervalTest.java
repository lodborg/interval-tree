package com.lodborg.intervaltree;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import com.lodborg.intervaltree.Interval.*;
import static org.junit.Assert.*;

public class DateIntervalTest {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	@Test
	public void test_middlepointBounded() {
		assertEquals(parse("12.11.2005 22:20:15"), new DateInterval(
				parse("31.10.2005 10:20:15"),
				parse("25.11.2005 10:20:15"),
				Bounded.CLOSED).getMidpoint()
		);
		assertEquals(parse("12.11.2005 10:20:15"), new DateInterval(
				parse("30.10.2005 10:20:15"),
				parse("25.11.2005 10:20:15"),
				Bounded.CLOSED).getMidpoint()
		);
	}

	@Test
	public void test_middlepointUnbounded() {
		assertEquals(new Date(0), new DateInterval().getMidpoint());
		assertEquals(new Date(4611677853814364403L), new DateInterval(parse("10.07.1452 08:52:33"), Unbounded.CLOSED_LEFT).getMidpoint());
		assertEquals(new Date(-4611694183040411404L), new DateInterval(parse("10.07.1452 08:52:33"), Unbounded.CLOSED_RIGHT).getMidpoint());
		assertEquals(new Date(4611677853814364403L), new DateInterval(parse("10.07.1452 08:52:33"), Unbounded.OPEN_LEFT).getMidpoint());
		assertEquals(new Date(-4611694183040411404L), new DateInterval(parse("10.07.1452 08:52:33"), Unbounded.OPEN_RIGHT).getMidpoint());
	}

	@Test
	public void test_middlepointStartAndEndOffByOne() {
		long tmp = 128643893;
		assertEquals(new Date(tmp + 1), new DateInterval(new Date(tmp), new Date(tmp + 1), Bounded.CLOSED_RIGHT).getMidpoint());
		assertEquals(new Date(tmp), new DateInterval(new Date(tmp), new Date(tmp + 1), Bounded.CLOSED).getMidpoint());
		assertEquals(new Date(tmp), new DateInterval(new Date(tmp), new Date(tmp), Bounded.CLOSED).getMidpoint());
		assertNull(new DateInterval(new Date(tmp), new Date(tmp), Bounded.OPEN).getMidpoint());
	}

	@Test
	public void test_dateIntervalTree(){
		IntervalTree<Date> tree = new IntervalTree<>();
		Date g = parse("30.09.2016 08:05:42");
		Date f = parse("18.09.2016 23:22:00");
		Date e = parse("12.09.2016 12:15:00");
		Date d = parse("12.09.2016 12:14:59");
		Date c = parse("01.03.2016 17:39:08");
		Date b = parse("27.12.2015 08:05:42");
		Date a = parse("08.04.2010 08:02:27");

		DateInterval aa = new DateInterval(e, f, Bounded.CLOSED);
		Interval<Date> bb = aa.builder().greaterEqual(b).less(d).build();
		DateInterval cc = new DateInterval(c, d, Bounded.CLOSED_RIGHT);
		DateInterval dd = new DateInterval(a, d, Bounded.OPEN);
		DateInterval ee = new DateInterval(e, g, Bounded.CLOSED_LEFT);

		tree.addInterval(aa);
		tree.addInterval(bb);
		tree.addInterval(cc);
		tree.addInterval(dd);
		tree.addInterval(ee);

		Set<Interval<Date>> res = tree.query(new DateInterval(c, d, Bounded.OPEN));
		assertEquals(3, res.size());
		assertTrue(res.contains(bb));
		assertTrue(res.contains(cc));
		assertTrue(res.contains(dd));
	}

	private static Date parse(String str){
		try{
			return sdf.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}
}

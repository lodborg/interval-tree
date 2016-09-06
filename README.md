# Interval Tree

[![Build Status](https://travis-ci.org/lodborg/interval-tree.svg?branch=master)](https://travis-ci.org/lodborg/interval-tree) &nbsp; [![codecov.io](https://codecov.io/github/lodborg/interval-tree/coverage.svg?branch=master)](https://codecov.io/gh/lodborg/interval-tree)

Implementation of dynamic interval trees.
* Supports lookup for all intervals intersecting a query point in O(logn + k) worst-case time, where n is the amount of intervals stored in the tree and k is the amount of intervals returned by the lookup.
* Insertions and deletions are in *average* O(logn) time and O(n) worst-case time.
* Supports bounded and unbounded, as well as open and closed intervals.

**_This README is still under construction. It will be updated soon._**

## Usage

### Interval classes
The interval tree is built around a generic interval class. You can easily extend it and build your own classes to fit your needs. To get you started, the library provides implementations for the most common interval classes that you might need:
* `IntegerInterval`
* `DoubleInterval`
* `DateInterval`

Both bounded and unbounded intervals are supported, meaning that you can have intervals extending from negative infinity and/or to positive infinity. The end points of the intervals can be inclusive and exclusive, so both open and closed intervals are supported.

#### Instantiation of intervals
There are several possible ways to initialize new intervals. You can use the provided Builder, for example like this:

```java
new IntegerInterval().builder()
    .greater(12)
    .lessEqual(15)
    .build();                         // returns the interval (12; 15].

new IntegerInterval().builder()
    .greaterEqual(-5)
    .lessEqual(0)
    .build();                         // returns the interval [-5, 0].

new DoubleInterval().builder()
    .less(0.2)
    .build();                         // returns (negative infinity, 0.2)

Date from = new GregorianCalendar(2014, Calendar.JULY, 6).getTime();
Date to = new Date();
new DateInterval().builder()
    .greaterEqual(from)
    .lessEqual(to).build();          // from 06 July 2014 until today,
                                     // both ends inclusive

IntegerInterval.builder().build();   // from negative infinity, to positive infinity
```

Or you can use one of the constructors
```java
public <T> Interval(T start, T end, Bounded type)
public <T> Interval(T value, Unbounded type)
```
for example like this
```java
new IntegerInterval(0, 2, Bounded.CLOSED_LEFT);  // the interval [0, 2). The start point is inclusive, end point is not
new DoubleInterval(1d/3, 10.1, Bounded.OPEN)     // the interval (0.333(3), 10.1)
new IntegerInterval(5, Unbounded.OPEN_RIGHT)     // the interval (negative infinity, 5)
new IntegerInterval(3, Unbounded.CLOSED_LEFT)    // the interval [3, positive infinity)
```

#### Extending the abstract interval class
If you need a special interval class, you can create your own subclass of the generic class `Interval<T>`. The majority of the methods from the interval arithmetic will be transferable and you don't have to overwrite them. The only abstract methods, that you do need to implement are:
```java
public T getMidpoint()      // returns the middle of the interval. This is needed for the tree.
protected Interval create() // returns a newly created instance of the class. Needed to avoid reflexion.
```
The `Interval` class requires its generic type variable to implement the `Comparable` interface. Because of that, there are many interval methods, which will be available to your subclass out of the box. Please refer to the documentation of the `Interval` class for the full API. To illustrate some examples, here we will use the subclass `IntegerInterval`, since it is probably the most intuitive one:
```java
IntegerInterval main = new IntergerInterval(0, 100, Bounded.CLOSED);    // the interval [0, 100]
IntegerInterval next = new IntegerInterval(20, Unbounded.OPEN_RIGHT);   // the interval (negative infinity, 20)
IntegerInterval small = new IntegerInterval(20, 80, Bounded.OPEN);      // the interval (20, 80)

main.contains(50);          // true
main.contains(-3);          // false
main.contains(small)        // true
small.contains(20);         // false, end is open
main.intersects(next);      // true
main.isLeftOf(200)          // true
main.getIntersection(next); // returns the interval [0, 20)
```

#### Interval class internals
All intervals are represented by four class properties. `start` and `end` are instances of the generic type and represent the end points of the interval. There are also two boolean properties - `isStartInclusive` and `isEndInclusive`, which indicate if the corresponding end point is inclusive or exclusive. The interval classes are designed to be immutable, so you can't change the values of the four internal variables once you instantiate an object. You have access to them via getter methods, though.

Please note, that `null` values for the `start` and `end` properties are completely valid and represent negative and positive infinity, respectively. Please, consider that fact, if you extend the `Interval` class yourself and keep in mind that `null` is an acceptable value for these properties.

The `Interval` class provides its own implementation of the methods `hashCode()` and `equals()`, which are based on the four internal properties. If you introduce new properties into your subclasses, please make sure to overwrite the `hashCode()` and `equals()` methods and reuse the implementation in the superclass, especially if you are going to be using your subclasses in data structures such as `HashMap` or `HashSet`.







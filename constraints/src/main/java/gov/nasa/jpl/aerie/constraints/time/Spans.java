package gov.nasa.jpl.aerie.constraints.time;

import gov.nasa.jpl.aerie.merlin.protocol.types.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import gov.nasa.jpl.aerie.constraints.time.Interval.Inclusivity;

/**
 * A collection of intervals that can overlap.
 */
public class Spans implements IntervalContainer<Spans>, Iterable<Interval> {
  private final List<Interval> intervals;

  public Spans() {
    this.intervals = new ArrayList<>();
  }

  public Spans(final ArrayList<Interval> intervals) {
    intervals.removeIf(Interval::isEmpty);
    this.intervals = intervals;
  }

  public Spans(final Iterable<Interval> iter) {
    this.intervals = StreamSupport.stream(iter.spliterator(), false).filter($ -> !$.isEmpty()).toList();
  }

  public Spans(final Interval... intervals) {
    this.intervals = Arrays.stream(intervals).filter($ -> !$.isEmpty()).toList();
  }

  public Windows intoWindows() {
    return new Windows(false).set(this.intervals, true);
  }

  public void add(final Interval window) {
    if (!window.isEmpty()) {
      this.intervals.add(window);
    }
  }

  public void addAll(final Iterable<Interval> iter) {
    this.intervals.addAll(
        StreamSupport
            .stream(iter.spliterator(), false)
            .filter($ -> !$.isEmpty())
            .toList()
    );
  }

  public Spans map(final Function<Interval, Interval> mapper) {
    return new Spans(this.intervals.stream().map(mapper).filter($ -> !$.isEmpty()).toList());
  }

  public Spans flatMap(final Function<Interval, ? extends Stream<Interval>> mapper) {
    return new Spans(this.intervals.stream().flatMap(mapper).filter($ -> !$.isEmpty()).toList());
  }

  public Spans filter(final Predicate<Interval> filter) {
    return new Spans(this.intervals.stream().filter(filter).toList());
  }

  /**
   * Splits each span into sub-spans.
   *
   * @param numberOfSubSpans number of sub-spans for each span.
   * @param internalStartInclusivity Inclusivity for any newly generated span start points (default Inclusive in eDSL).
   * @param internalEndInclusivity Inclusivity for any newly generated span end points (default Exclusive in eDSL).
   * @return a new Spans
   * @throws UnsplittableSpanException if any span contains only one point or contains fewer microseconds than `numberOfSubSpans`.
   * @throws UnsplittableSpanException if any span contains {@link Duration#MIN_VALUE} or {@link Duration#MAX_VALUE} (representing unbounded intervals)
   */
  @Override
  public Spans split(final int numberOfSubSpans, final Inclusivity internalStartInclusivity, final Inclusivity internalEndInclusivity) {
    if (numberOfSubSpans == 1) {
      return new Spans(this);
    }
    return this.flatMap(x -> {
      // Width of each sub-window, rounded down to the microsecond
      final var width = Duration.divide(Duration.subtract(x.end, x.start), numberOfSubSpans);

      final var numberOfMicroSeconds = Duration.subtract(x.end, x.start).in(Duration.MICROSECOND);

      // We throw an exception if the interval contains fewer microseconds than the requested number of sub-spans.
      if (x.isSingleton()) {
        throw new UnsplittableSpanException("Cannot split an instantaneous span into " + numberOfSubSpans + " pieces.");
      } else if (numberOfMicroSeconds < numberOfSubSpans) {
        throw new UnsplittableSpanException("Cannot split a span only " + numberOfMicroSeconds + " microseconds long into " + numberOfSubSpans + " pieces.");
      }

      // Throw an exception if trying to split an "unbounded" interval.
      // It is unlikely that a user will ever need to split a Windows or Spans that includes +/- infinity.
      //
      // If they do, and it is a legitimate use case that we should support, this block should be replaced
      // with a check that returns the unbounded interval unchanged, because the split points on an unbounded
      // interval will be outside the finite range of `Duration`.
      if (x.contains(Duration.MIN_VALUE)) {
        throw new UnsplittableSpanException("Cannot split an unbounded span. (interval contains MIN_VALUE, which is a stand-in for -infinity.");
      } else if (x.contains(Duration.MAX_VALUE)) {
        throw new UnsplittableSpanException("Cannot split an unbounded span. (interval contains MAX_VALUE, which is a stand-in for +infinity.");
      }

      var cursor = Duration.add(x.start, width);
      final List<Interval> ret = new ArrayList<>();
      ret.add(Interval.between(x.start, x.startInclusivity, cursor, internalEndInclusivity));
      for (int i = 1; i < numberOfSubSpans - 1; i++) {
        final var nextCursor = Duration.add(cursor, width);
        ret.add(Interval.between(cursor, internalStartInclusivity, nextCursor, internalEndInclusivity));
        cursor = nextCursor;
      }
      ret.add(Interval.between(cursor, internalStartInclusivity, x.end, x.endInclusivity));
      return ret.stream();
    });
  }

  @Override
  public Spans starts() {
    return this.map($ -> Interval.at($.start));
  }

  @Override
  public Spans ends() {
    return this.map($ -> Interval.at($.end));
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof final Spans spans)) return false;

    return Objects.equals(this.intervals, spans.intervals);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.intervals);
  }

  @Override
  public String toString() {
    return this.intervals.toString();
  }

  @Override
  public Iterator<Interval> iterator() {
    return this.intervals.iterator();
  }
}

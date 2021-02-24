package gov.nasa.jpl.aerie.merlin.driver.engine;

import gov.nasa.jpl.aerie.merlin.timeline.History;
import gov.nasa.jpl.aerie.time.Duration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;

public final class TaskQueue<Task> {
  // A time-ordered queue of all scheduled tasks.
  private final PriorityQueue<Pair<Duration, Task>> queue = new PriorityQueue<>(Comparator.comparing(Pair::getLeft));

  // The elapsed simulation time.
  private Duration elapsedTime = Duration.ZERO;

  public void deferTo(final Duration resumptionTime, final Task task) {
    if (resumptionTime.shorterThan(this.elapsedTime)) {
      throw new RuntimeException("Cannot schedule a task in the past");
    }

    this.queue.add(Pair.of(resumptionTime, task));
  }

  public <$Timeline> Optional<TaskFrame<$Timeline, Task>>
  popNextFrame(final History<$Timeline> tip, final Duration maximum) {
    if (this.queue.isEmpty() && this.elapsedTime.noShorterThan(maximum)) {
      return Optional.empty();
    }

    // Step up to either the given maximum or to the next task.
    final var nextJobTime = (this.queue.isEmpty()) ? maximum : this.queue.peek().getLeft();
    final var resumptionTime = tip.wait(nextJobTime.minus(this.elapsedTime));
    this.elapsedTime = nextJobTime;

    // Extract any tasks at this time.
    return Optional.of(TaskFrame.of(resumptionTime, builder -> {
      final var iter = this.queue.iterator();

      if (!iter.hasNext()) return;
      var entry = iter.next();

      while (entry.getLeft().noLongerThan(nextJobTime)) {
        iter.remove();

        builder.signal(entry.getRight());

        if (!iter.hasNext()) break;
        entry = iter.next();
      }
    }));
  }

  public String getDebugTrace() {
    final var builder = new StringBuilder();

    @SuppressWarnings("unchecked")
    final var x = (Pair<Duration, Task>[]) this.queue.toArray(new Pair[0]);
    Arrays.sort(x);

    for (final var entry : x) {
      builder.append(String.format("%10s: %s\n", entry.getLeft(), entry.getRight()));
    }

    return builder.toString();
  }

  public Duration getElapsedTime() {
    return this.elapsedTime;
  }
}
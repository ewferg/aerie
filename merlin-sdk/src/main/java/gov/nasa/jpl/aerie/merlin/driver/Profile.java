package gov.nasa.jpl.aerie.merlin.driver;

import gov.nasa.jpl.aerie.merlin.protocol.DelimitedDynamics;
import gov.nasa.jpl.aerie.merlin.protocol.ResourceSolver;
import gov.nasa.jpl.aerie.time.Duration;
import gov.nasa.jpl.aerie.time.Window;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Profile<Dynamics, Condition>
    implements Iterable<Pair<Window, Dynamics>>
{
  private final ResourceSolver<?, ?, Dynamics, Condition> solver;
  private final List<Pair<Window, Dynamics>> pieces;

  private Profile(
      final ResourceSolver<?, ?, Dynamics, Condition> solver,
      final List<Pair<Window, Dynamics>> pieces)
  {
    this.solver = solver;
    this.pieces = pieces;
  }

  public Profile(final ResourceSolver<?, ?, Dynamics, Condition> solver) {
    this(Objects.requireNonNull(solver), Collections.emptyList());
  }

  public Profile<Dynamics, Condition> append(final Duration duration, Dynamics dynamics) {
    final Duration startTime;
    if (this.pieces.isEmpty()) {
      startTime = Duration.ZERO;
    } else {
      startTime = this.pieces
          .get(this.pieces.size() - 1)
          .getLeft()
          .end
          .plus(Duration.EPSILON);
    }

    final var window = Window.between(startTime, startTime.plus(duration));

    final var newPieces = new ArrayList<>(this.pieces);
    newPieces.add(Pair.of(window,  dynamics));
    return new Profile<>(this.solver, newPieces);
  }

  public ResourceSolver<?, ?, Dynamics, Condition> getSolver() {
    return this.solver;
  }

  @Override
  public Iterator<Pair<Window, Dynamics>> iterator() {
    return Collections.unmodifiableList(this.pieces).iterator();
  }
}
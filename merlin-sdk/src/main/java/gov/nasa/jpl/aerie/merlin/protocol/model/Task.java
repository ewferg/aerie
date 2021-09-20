package gov.nasa.jpl.aerie.merlin.protocol.model;

import gov.nasa.jpl.aerie.merlin.protocol.driver.Scheduler;
import gov.nasa.jpl.aerie.merlin.protocol.types.TaskStatus;

public interface Task<$Timeline> {
  /** Perform one step of the task, returning the conditions under which to progress to the next step. */
  TaskStatus<$Timeline> step(Scheduler<$Timeline> scheduler);

  /** Reset this task to its state before {@link #step(Scheduler)} was ever called. */
  void reset();
}
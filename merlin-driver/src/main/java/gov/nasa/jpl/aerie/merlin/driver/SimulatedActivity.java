package gov.nasa.jpl.aerie.merlin.driver;

import gov.nasa.jpl.aerie.merlin.protocol.types.Duration;
import gov.nasa.jpl.aerie.merlin.protocol.types.SerializedValue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SimulatedActivity {
  public final String type;
  public final Map<String, SerializedValue> parameters;
  public final Instant start;
  public final Duration duration;
  public final ActivityInstanceId parentId;
  public final List<ActivityInstanceId> childIds;
  public final Optional<ActivityInstanceId> directiveId;

  public SimulatedActivity(
      final String type,
      final Map<String, SerializedValue> parameters,
      final Instant start,
      final Duration duration,
      final ActivityInstanceId parentId,
      final List<ActivityInstanceId> childIds,
      final Optional<ActivityInstanceId> directiveId
  ) {
    this.type = type;
    this.parameters = parameters;
    this.start = start;
    this.duration = duration;
    this.parentId = parentId;
    this.childIds = childIds;
    this.directiveId = directiveId;
  }

  @Override
  public String toString() {
    return
        "SimulatedActivity "
        + "{ type=" + this.type
        + ", parameters=" + this.parameters
        + ", start=" + this.start
        + ", duration=" + this.duration
        + ", parentId=" + this.parentId
        + ", directiveId=" + this.directiveId
        + ", childIds=" + this.childIds
        + " }";
  }
}

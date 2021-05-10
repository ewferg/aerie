package gov.nasa.jpl.aerie.merlin.driver;

import gov.nasa.jpl.aerie.merlin.protocol.Duration;
import gov.nasa.jpl.aerie.merlin.protocol.SerializedValue;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class SimulatedActivity {
  public final String type;
  public final Map<String, SerializedValue> parameters;
  public final Instant start;
  public final Duration duration;
  public final String parentId;
  public final List<String> childIds;

  public SimulatedActivity(
      final String type,
      final Map<String, SerializedValue> parameters,
      final Instant start,
      final Duration duration,
      final String parentId,
      final List<String> childIds
  ) {
    this.type = type;
    this.parameters = parameters;
    this.start = start;
    this.duration = duration;
    this.parentId = parentId;
    this.childIds = childIds;
  }
}
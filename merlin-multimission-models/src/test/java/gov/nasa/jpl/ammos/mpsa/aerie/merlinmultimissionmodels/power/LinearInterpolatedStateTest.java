package gov.nasa.jpl.ammos.mpsa.aerie.merlinmultimissionmodels.power;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;
import static org.assertj.core.api.Assertions.catchThrowable;

import gov.nasa.jpl.ammos.mpsa.aerie.merlinmultimissionmodels.jpltime.Time;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.activities.Activity;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.engine.ActivityJob;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.engine.SimulationEffects;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.engine.SimulationEngine;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.engine.SimulationInstant;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.states.StateContainer;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.time.Instant;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.time.TimeUnit;
import org.junit.Test;

import java.util.List;


/**
 * exercises basic functionality and mathematics of the simple linear interpolation state
 */
public class LinearInterpolatedStateTest {
    private final Instant simStart = SimulationInstant.fromQuantity(0, TimeUnit.SECONDS);
    private final Time t2020 = new Time("2020-001T00:00:00.000");
    private final Time t2020_10s = new Time("2020-001T00:00:10.000");
    private final Time t2020_20s = new Time("2020-001T00:00:20.000");

    @Test
    public void twoPointCtorWorks() {
        new LinearInterpolatedState(t2020, 0, t2020_10s, 10);
    }

    @Test
    public void twoPointCtorFailsOnSameTime() {
        final var thrown = catchThrowable(() -> new LinearInterpolatedState(t2020, 0, t2020, 10));

        //make sure right exception thrown
        assertThat(thrown)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("zero");
    }

    @Test
    public void getAtLeftWorks() {
        final var state = new LinearInterpolatedState(t2020, 100, t2020_10s, 110);
        final var activity = new Activity<>() {
            @Override
            public void modelEffects(final StateContainer _states) {
                double result = state.get(SimulationEffects.now());
                assertThat(result).isCloseTo(100, withinPercentage(0.01));
            }
        };

        final var engine = new SimulationEngine(
            simStart,
            List.of(new ActivityJob<>(activity, simStart)),
            () -> List.of(state));
        engine.run();
    }

    @Test
    public void getAtRightWorks() {
        final var state = new LinearInterpolatedState(t2020, 100, t2020_10s, 110);
        final var activity = new Activity<>() {
            @Override
            public void modelEffects(StateContainer states) {
                double result = state.get(SimulationEffects.now().plus(10, TimeUnit.SECONDS));
                assertThat(result).isCloseTo(110, withinPercentage(0.01));
            }
        };

        final var engine = new SimulationEngine(
            simStart,
            List.of(new ActivityJob<>(activity, simStart)),
            () -> List.of(state));
        engine.run();
    }

    @Test
    public void getAtMiddleWorks() {
        final var state = new LinearInterpolatedState(t2020, 100, t2020_20s, 120);
        final var activity = new Activity<>() {
            @Override
            public void modelEffects(StateContainer states) {
                double result = state.get(SimulationEffects.now().plus(10, TimeUnit.SECONDS));
                assertThat(result).isCloseTo(110, withinPercentage(0.01));
            }
        };

        final var engine = new SimulationEngine(
            simStart,
            List.of(new ActivityJob<>(activity, simStart)),
            () -> List.of(state));
        engine.run();
    }

    @Test
    public void getPastLeftWorks() {
        final var state = new LinearInterpolatedState(t2020_10s, 100, t2020_20s, 110);
        final var activity = new Activity<>() {
            @Override
            public void modelEffects(StateContainer states) {
                double result = state.get(SimulationEffects.now().minus(10, TimeUnit.SECONDS));
                assertThat(result).isCloseTo(90, withinPercentage(0.01));
            }
        };

        final var engine = new SimulationEngine(
            simStart.plus(10, TimeUnit.SECONDS),
            List.of(new ActivityJob<>(activity, simStart.plus(10, TimeUnit.SECONDS))),
            () -> List.of(state));
        engine.run();
    }

    @Test
    public void getPastRightWorks() {
        final var state = new LinearInterpolatedState(t2020, 100, t2020_10s, 110);
        final var activity = new Activity<>() {
            @Override
            public void modelEffects(StateContainer states) {
                double result = state.get(SimulationEffects.now().plus(20, TimeUnit.SECONDS));
                assertThat(result).isCloseTo(120, withinPercentage(0.01));
            }
        };

        final var engine = new SimulationEngine(
            simStart,
            List.of(new ActivityJob<>(activity, simStart)),
            () -> List.of(state));
        engine.run();
    }
}

package gov.nasa.jpl.aerie.scheduler.server.services;

import gov.nasa.jpl.aerie.constraints.tree.GreaterThan;
import gov.nasa.jpl.aerie.constraints.tree.LessThan;
import gov.nasa.jpl.aerie.constraints.tree.LongerThan;
import gov.nasa.jpl.aerie.constraints.tree.RealResource;
import gov.nasa.jpl.aerie.constraints.tree.RealValue;
import gov.nasa.jpl.aerie.merlin.protocol.types.Duration;
import gov.nasa.jpl.aerie.merlin.protocol.types.SerializedValue;
import gov.nasa.jpl.aerie.scheduler.TimeUtility;
import gov.nasa.jpl.aerie.scheduler.constraints.timeexpressions.TimeAnchor;
import gov.nasa.jpl.aerie.scheduler.server.models.MissionModelId;
import gov.nasa.jpl.aerie.scheduler.server.models.PlanId;
import gov.nasa.jpl.aerie.scheduler.server.models.SchedulingDSL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static gov.nasa.jpl.aerie.scheduler.server.services.TypescriptCodeGenerationServiceTest.MISSION_MODEL_TYPES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SchedulingDSLCompilationServiceTests {
  private static final PlanId PLAN_ID = new PlanId(1L);
  private static final MissionModelService missionModelService = new MissionModelService() {
    @Override
    public MissionModelTypes getMissionModelTypes(final PlanId missionModelId)
    {
      return MISSION_MODEL_TYPES;
    }

    @Override
    public MissionModelTypes getMissionModelTypes(final MissionModelId missionModelId)
    {
      return MISSION_MODEL_TYPES;
    }
  };
  SchedulingDSLCompilationService schedulingDSLCompilationService;

  @BeforeAll
  void setUp() throws IOException {
    schedulingDSLCompilationService = new SchedulingDSLCompilationService();
  }

  @AfterAll
  void tearDown() {
    schedulingDSLCompilationService.close();
  }

  @Test
  void  testSchedulingDSL_basic()
  {
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID, """
                export default function myGoal() {
                  return Goal.ActivityRecurrenceGoal({
                    activityTemplate: ActivityTemplates.SampleActivity1({
                      variant: 'option2',
                      fancy: { subfield1: 'value1', subfield2: [{subsubfield1: 2}]},
                      duration: Temporal.Duration.from({ milliseconds: 60 * 60 * 1000 })
                    }),
                    interval: Temporal.Duration.from({ milliseconds: 60 * 60 * 1000 })
                  })
                }
            """);
    final var expectedGoalDefinition = new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
        new SchedulingDSL.ActivityTemplate(
            "SampleActivity1",
            Map.ofEntries(
                Map.entry("variant", SerializedValue.of("option2")),
                Map.entry("fancy", SerializedValue.of(Map.ofEntries(
                    Map.entry("subfield1", SerializedValue.of("value1")),
                    Map.entry("subfield2", SerializedValue.of(List.of(SerializedValue.of(Map.of("subsubfield1", SerializedValue.of(2)))))
                )))),
                Map.entry("duration", SerializedValue.of("PT3600S"))
            )
        ),
        Duration.HOUR
    );
    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success<SchedulingDSL.GoalSpecifier> r) {
      assertEquals(expectedGoalDefinition, r.value());
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> r) {
      fail(r.toString());
    }
  }

  @Test
  void testSchedulingDSL_helper_function()
  {
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID, """
                export default function myGoal() {
                  return myHelper(ActivityTemplates.SampleActivity1({
                    variant: 'option2',
                    fancy: { subfield1: 'value1', subfield2: [{subsubfield1: 2}]},
                    duration: Temporal.Duration.from({ milliseconds: 60 * 60 * 1000 })
                  }))
                }
                function myHelper(activityTemplate) {
                  return Goal.ActivityRecurrenceGoal({
                    activityTemplate,
                    interval: Temporal.Duration.from({ milliseconds: 60 * 60 * 1000 })
                  })
                }
            """);
    final var expectedGoalDefinition = new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
        new SchedulingDSL.ActivityTemplate(
            "SampleActivity1",
            Map.ofEntries(
                Map.entry("variant", SerializedValue.of("option2")),
                Map.entry("fancy", SerializedValue.of(Map.ofEntries(
                    Map.entry("subfield1", SerializedValue.of("value1")),
                    Map.entry("subfield2", SerializedValue.of(List.of(SerializedValue.of(Map.of("subsubfield1", SerializedValue.of(2)))))
                    )))),
                Map.entry("duration", SerializedValue.of("PT3600S"))
            )
        ),
        Duration.HOUR);
    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success r) {
      assertEquals(expectedGoalDefinition, r.value());
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> r) {
      fail(r.toString());
    }
  }

  @Test
  void testSchedulingDSL_variable_not_defined() {
    final SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> actualErrors;
    actualErrors = (SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier>) schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
          PLAN_ID, """
                export default function myGoal() {
                  const x = 4 - 2
                  return myHelper(ActivityTemplates.SampleActivity1({
                    variant: 'option2',
                    fancy: { subfield1: 'value1', subfield2: [{subsubfield1: 2}]},
                    duration: Temporal.Duration.from({ milliseconds: 60 * 60 * 1000 })
                  }))
                }
                function myHelper(activityTemplate) {
                  return Goal.ActivityRecurrenceGoal({
                    activityTemplate,
                    interval: x
                  })
                }
              """);
    assertTrue(
        actualErrors.errors()
                    .stream()
                    .anyMatch(e -> e.message().contains("TypeError: TS2304 Cannot find name 'x'."))
    );
  }

  @Test
  void testSchedulingDSL_applyWhen()
  {
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID, """
        export default function myGoal() {
          return Goal.ActivityRecurrenceGoal({
                    activityTemplate: ActivityTemplates.SampleActivity1({
                        variant: 'option2',
                        fancy: { subfield1: 'value1', subfield2: [{subsubfield1: 2}]},
                        duration: Temporal.Duration.from({ hours : 1 })
                    }),
                    interval: Temporal.Duration.from({ hours : 1 })
                  }).applyWhen(Real.Resource(Resources["/sample/resource/1"]).greaterThan(2.0))
        }
        """);
    final var expectedGoalDefinition = new SchedulingDSL.GoalSpecifier.GoalApplyWhen(
        new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
            new SchedulingDSL.ActivityTemplate(
                "SampleActivity1",
                Map.ofEntries(
                    Map.entry("variant", SerializedValue.of("option2")),
                    Map.entry("fancy", SerializedValue.of(Map.ofEntries(
                        Map.entry("subfield1", SerializedValue.of("value1")),
                        Map.entry(
                            "subfield2",
                            SerializedValue.of(List.of(SerializedValue.of(Map.of(
                                "subsubfield1",
                                SerializedValue.of(2)))))
                        )))),
                    Map.entry("duration", SerializedValue.of("PT1H"))
                )
            ),
            Duration.HOUR
        ),
        new GreaterThan(
            new RealResource("/sample/resource/1"),
            new RealValue(2.0)
        )
    );
    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success r) {
      assertEquals(expectedGoalDefinition, r.value());
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> r) {
      fail(r.toString());
    }
  }

  @Test
  void testSchedulingDSL_wrong_return_type() {
    final SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> actualErrors;
    actualErrors = (SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier>) schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
          PLAN_ID, """
                export default function myGoal() {
                  return 5
                }
              """);
    assertTrue(
        actualErrors.errors()
                    .stream()
                    .anyMatch(e -> e.message().contains("TypeError: TS2322 Incorrect return type. Expected: 'Goal', Actual: 'number'."))
    );
  }

  @Test
  void testSchedulingDSL_temporal() {
    final SchedulingDSLCompilationService.SchedulingDSLCompilationResult result;
    result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID,
        """
                    export default () => Goal.ActivityRecurrenceGoal({
                      activityTemplate: ActivityTemplates.SampleActivity1({
                        variant: 'option2',
                        fancy: { subfield1: 'value1', subfield2: [{subsubfield1: 2}]},
                        duration: Temporal.Duration.from({ milliseconds: 60 * 60 * 1000 })
                      }),
                      interval:  Temporal.Duration.from({days: 1})
                    })
                    """);
    final var expectedGoalDefinition = new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
        new SchedulingDSL.ActivityTemplate(
            "SampleActivity1",
            Map.ofEntries(
                Map.entry("variant", SerializedValue.of("option2")),
                Map.entry("fancy", SerializedValue.of(Map.ofEntries(
                    Map.entry("subfield1", SerializedValue.of("value1")),
                    Map.entry("subfield2", SerializedValue.of(List.of(SerializedValue.of(Map.of("subsubfield1", SerializedValue.of(2)))))
                    )))),
                Map.entry("duration", SerializedValue.of("PT3600S"))
            )
        ),
        Duration.HOURS.times(24)
    );
    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success r) {
      assertEquals(expectedGoalDefinition, r.value());
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error r) {
      fail(r.toString());
    }
  }


  @Test
  void testHugeGoal() {
    // This test is intended to create a Goal that is bigger than the node subprocess's standard input buffer
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID, """
                export default function myGoal() {
                  return Goal.ActivityRecurrenceGoal({
                    activityTemplate: ActivityTemplates.SampleActivity1({
                      variant: 'option2',
                      fancy: { subfield1: 'value1', subfield2: [{subsubfield1: 2}]},
                      duration: Temporal.Duration.from({ milliseconds: 60 * 60 * 1000 })
                    }),
                    interval: Temporal.Duration.from({ milliseconds: 60 * 60 * 1000 })
                  })
                }
            """ + " ".repeat(9001));
    final var expectedGoalDefinition = new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
        new SchedulingDSL.ActivityTemplate(
            "SampleActivity1",
            Map.ofEntries(
                Map.entry("variant", SerializedValue.of("option2")),
                Map.entry("fancy", SerializedValue.of(Map.ofEntries(
                    Map.entry("subfield1", SerializedValue.of("value1")),
                    Map.entry("subfield2", SerializedValue.of(List.of(SerializedValue.of(Map.of("subsubfield1", SerializedValue.of(2)))))
                    )))),
                Map.entry("duration", SerializedValue.of("PT3600S"))
            )
        ),
        Duration.HOUR
    );
    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success r) {
      assertEquals(expectedGoalDefinition, r.value());
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> r) {
      fail(r.toString());
    }
  }

  @Test
  void testCoexistenceGoalActivityExpression() {
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID, """
          export default function() {
            return Goal.CoexistenceGoal({
              activityTemplate: ActivityTemplates.SampleActivity1({
                variant: 'option2',
                fancy: { subfield1: 'value1', subfield2: [{subsubfield1: 2}]},
                duration: Temporal.Duration.from({ hours : 1 })
              }),
              forEach: ActivityExpression.ofType(ActivityTypes.SampleActivity2),
              startsAt: TimingConstraint.singleton(WindowProperty.START).plus(Temporal.Duration.from({ seconds : 1 }))
            })
          }
        """);

    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success r) {
      assertEquals(
          new SchedulingDSL.GoalSpecifier.CoexistenceGoalDefinition(
              new SchedulingDSL.ActivityTemplate("SampleActivity1",
                                                 Map.ofEntries(
                                                     Map.entry("variant", SerializedValue.of("option2")),
                                                     Map.entry("fancy", SerializedValue.of(Map.ofEntries(
                                                         Map.entry("subfield1", SerializedValue.of("value1")),
                                                         Map.entry("subfield2", SerializedValue.of(List.of(SerializedValue.of(Map.of("subsubfield1", SerializedValue.of(2)))))
                                                         )))),
                                                     Map.entry("duration", SerializedValue.of("PT1H"))
                                                 )
              ),
              new SchedulingDSL.ConstraintExpression.ActivityExpression("SampleActivity2"),
              Optional.of(new SchedulingDSL.ActivityTimingConstraint(TimeAnchor.START, TimeUtility.Operator.PLUS, Duration.of(1, Duration.SECONDS), true)),
              Optional.empty()
          ),
          r.value()
      );
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> r) {
      fail(r.toString());
    }
  }

  @Test
  void strictTypeCheckingTest_astNode() {
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID,
        """
          interface FakeGoal {
            and(...others: FakeGoal[]): FakeGoal;
            or(...others: FakeGoal[]): FakeGoal;
            applyWhen(window: Windows): FakeGoal;
          }
          export default function() {
            const myFakeGoal: FakeGoal = {
              and: (...others: FakeGoal[]) => {
                return myFakeGoal;
              },
              or: (...others: FakeGoal[]) => {
                return myFakeGoal;
              },
              applyWhen: (window: Windows) => {
                return myFakeGoal;
              },
            };
            return myFakeGoal;
          }
        """);

    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> r) {
      assertEquals(r.errors().size(), 1);
      assertEquals(
          "TypeError: TS2741 Incorrect return type. Expected: 'Goal', Actual: 'FakeGoal'.",
          r.errors().get(0).message()
      );
    }
  }

  @Test
  void strictTypeCheckingTest_transition() {
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID,
        """
          export default function() {
            return Goal.CoexistenceGoal({
              activityTemplate: ActivityTemplates.SampleActivity1({
                variant: 'option2',
                fancy: { subfield1: 'value1', subfield2: [{subsubfield1: 2}]},
                duration: Temporal.Duration.from({ milliseconds: 60 * 60 * 1000 })
              }),
              forEach: Discrete.Resource(Resources["/sample/resource/1"]).transition("Chiquita", "Dole"),
              startsAt: TimingConstraint.singleton(WindowProperty.END)
            })
          }
        """);

    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> r) {
      assertEquals(1, r.errors().size());
      assertEquals(
          "TypeError: TS2345 Argument of type 'string' is not assignable to parameter of type 'number'.",
          r.errors().get(0).message()
      );
    }
  }

  @Test
  void testSchedulingDSL_emptyActivityCorrect()
  {
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID, """
                export default function myGoal() {
                  return Goal.ActivityRecurrenceGoal({
                    activityTemplate: ActivityTemplates.SampleActivityEmpty(),
                    interval: Temporal.Duration.from({ hours : 1 }) // 1 hour in microseconds
                  })
                }
            """);
    final var expectedGoalDefinition = new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
        new SchedulingDSL.ActivityTemplate(
            "SampleActivityEmpty",
            Map.of()
        ),
        Duration.HOUR
    );
    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success r) {
      assertEquals(expectedGoalDefinition, r.value());
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> r) {
      fail(r.toString());
    }
  }

  @Test
  void testSchedulingDSL_emptyActivityBogus()
  {
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID, """
                export default function myGoal() {
                  return Goal.ActivityRecurrenceGoal({
                    activityTemplate: ActivityTemplates.SampleActivityEmpty({ fake: "bogus" }),
                    interval: Temporal.Duration.from({ hours : 1 }) // 1 hour in microseconds
                  })
                }
            """);
    final var expectedGoalDefinition = new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
        new SchedulingDSL.ActivityTemplate(
            "SampleActivityEmpty",
            Map.of()
        ),
        Duration.HOUR
    );
    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> r) {
      assertEquals(1, r.errors().size());
      assertEquals(
          "TypeError: TS2554 Expected 0 arguments, but got 1.",
          r.errors().get(0).message()
      );
    }
    else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success r) {
      fail(r.value().toString());
    }
  }

  @Test
  void testCoexistenceGoalStateConstraint() {
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID,
        """
          export default function() {
            return Goal.CoexistenceGoal({
              activityTemplate: ActivityTemplates.SampleActivity1({
                variant: 'option2',
                fancy: { subfield1: 'value1', subfield2: [{subsubfield1: 2}]},
                duration: Temporal.Duration.from({ milliseconds: 60 * 60 * 1000 })
              }),
              forEach: Real.Resource(Resources["/sample/resource/1"]).greaterThan(50.0).longerThan(10),
              startsAt: TimingConstraint.singleton(WindowProperty.END)
            })
          }
        """);

    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success r) {
      assertEquals(
          new SchedulingDSL.GoalSpecifier.CoexistenceGoalDefinition(
              new SchedulingDSL.ActivityTemplate("SampleActivity1",
                                                 Map.ofEntries(
                                                     Map.entry("variant", SerializedValue.of("option2")),
                                                     Map.entry("fancy", SerializedValue.of(Map.ofEntries(
                                                         Map.entry("subfield1", SerializedValue.of("value1")),
                                                         Map.entry("subfield2", SerializedValue.of(List.of(SerializedValue.of(Map.of("subsubfield1", SerializedValue.of(2)))))
                                                         )))),
                                                     Map.entry("duration", SerializedValue.of("PT3600S"))
                                                 )
              ),
              new SchedulingDSL.ConstraintExpression.WindowsExpression(new LongerThan(new GreaterThan(new RealResource("/sample/resource/1"), new RealValue(50.0)), Duration.of(10, Duration.MICROSECOND))),
              Optional.of(new SchedulingDSL.ActivityTimingConstraint(TimeAnchor.END, TimeUtility.Operator.PLUS, Duration.ZERO, true)),
              Optional.empty()
          ),
          r.value()
      );
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error<SchedulingDSL.GoalSpecifier> r) {
      fail(r.toString());
    }
  }

  @Test
  void testWindowsExpression() {
    final var result = schedulingDSLCompilationService.compileGlobalSchedulingCondition(
        missionModelService,
        PLAN_ID,
        """
          export default function() {
            return Real.Resource("/sample/resource/1").lessThan(5.0);
          }
        """);
    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success r) {
      assertEquals(new LessThan(new RealResource("/sample/resource/1"), new RealValue(5.0)), r.value());
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error r) {
      fail(r.toString());
    }
  }

  @Test
  void testAndGoal(){
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID, """
                export default function myGoal() {
                  return Goal.ActivityRecurrenceGoal({
                    activityTemplate: ActivityTemplates.SampleActivityEmpty(),
                    interval: Temporal.Duration.from({ hours : 1 }) // 1 hour in microseconds
                  }).and(
                    Goal.ActivityRecurrenceGoal({
                      activityTemplate: ActivityTemplates.SampleActivityEmpty(),
                      interval: Temporal.Duration.from({ hours : 2 }) // 2 hour in microseconds
                    })
                  )
                }
            """);
    final var expectedGoalDefinition = new SchedulingDSL.GoalSpecifier.GoalAnd(List.of(
        new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
          new SchedulingDSL.ActivityTemplate(
              "SampleActivityEmpty",
              Map.of()
          ),
          Duration.HOUR
    ), new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
            new SchedulingDSL.ActivityTemplate(
                "SampleActivityEmpty",
                Map.of()
            ),
            Duration.HOUR.times(2)
        )));
    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success r) {
      assertEquals(
          expectedGoalDefinition,
          r.value()
      );
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error r) {
      fail(r.toString());
    }
  }

  @Test
  void testOrGoal(){
    final var result = schedulingDSLCompilationService.compileSchedulingGoalDSL(
        missionModelService,
        PLAN_ID, """
                export default function myGoal() {
                  return Goal.ActivityRecurrenceGoal({
                    activityTemplate: ActivityTemplates.SampleActivityEmpty(),
                    interval: Temporal.Duration.from({ hours: 1 }) // 1 hour in microseconds
                  }).or(
                    Goal.ActivityRecurrenceGoal({
                      activityTemplate: ActivityTemplates.SampleActivityEmpty(),
                      interval: Temporal.Duration.from({ hours : 2 }) // 2 hour in microseconds
                    })
                  )
                }
            """);
    final var expectedGoalDefinition = new SchedulingDSL.GoalSpecifier.GoalOr(List.of(
        new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
            new SchedulingDSL.ActivityTemplate(
                "SampleActivityEmpty",
                Map.of()
            ),
            Duration.HOUR
        ), new SchedulingDSL.GoalSpecifier.RecurrenceGoalDefinition(
            new SchedulingDSL.ActivityTemplate(
                "SampleActivityEmpty",
                Map.of()
            ),
            Duration.HOUR.times(2)
        )));
    if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Success<SchedulingDSL.GoalSpecifier> r) {
      assertEquals(
          expectedGoalDefinition,
          r.value()
      );
    } else if (result instanceof SchedulingDSLCompilationService.SchedulingDSLCompilationResult.Error r) {
      fail(r.toString());
    }
  }

}

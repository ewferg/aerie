package gov.nasa.jpl.aerie.scheduler.server.services;

import gov.nasa.jpl.aerie.merlin.protocol.types.ValueSchema;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypescriptCodeGenerationServiceTest {

  public static final MissionModelService.MissionModelTypes MISSION_MODEL_TYPES =
      new MissionModelService.MissionModelTypes(
          List.of(new MissionModelService.ActivityType(
              "SampleActivity1",
              Map.of(
                  "variant",
                  ValueSchema.ofVariant(List.of(
                      new ValueSchema.Variant(
                          "option1", "option1"
                      ),
                      new ValueSchema.Variant(
                          "option2",
                          "option2"))),
                  "duration",
                  ValueSchema.DURATION,
                  "fancy",
                  ValueSchema.ofStruct(Map.of(
                      "subfield1",
                      ValueSchema.STRING,
                      "subfield2",
                      ValueSchema.ofSeries(ValueSchema.ofStruct(Map.of("subsubfield1", ValueSchema.REAL)))
                  )))),
                  new MissionModelService.ActivityType(
                      "SampleActivity2",
                      Map.of(
                          "quantity",
                          ValueSchema.REAL
                      )
                  ),
                  new MissionModelService.ActivityType(
                      "SampleActivityEmpty",
                      Map.of()
                  )
          ),
          List.of(new MissionModelService.ResourceType("/sample/resource/1", ValueSchema.REAL),
                  new MissionModelService.ResourceType("/sample/resource/2", ValueSchema.ofStruct(
                      Map.of("field1", ValueSchema.BOOLEAN,
                             "field2", ValueSchema.ofVariant(List.of(
                                 new ValueSchema.Variant("ABC", "ABC"),
                                 new ValueSchema.Variant("DEF", "DEF")))))))
      );

  @Test
  void testCodeGen() {
    assertEquals(
        """
            /** Start Codegen */
            import type { ActivityTemplate } from './scheduler-edsl-fluent-api.js';
            import type { Windows } from './constraints-edsl-fluent-api.js';
            export enum ActivityType {
              SampleActivity1 = 'SampleActivity1',
              SampleActivity2 = 'SampleActivity2',
              SampleActivityEmpty = 'SampleActivityEmpty',
            }
            interface SampleActivity1 extends ActivityTemplate {}
            interface SampleActivity2 extends ActivityTemplate {}
            interface SampleActivityEmpty extends ActivityTemplate {}
            const ActivityTemplateConstructors = {
              SampleActivity1: function SampleActivity1Constructor(args: {
                duration: Temporal.Duration,
                fancy: { subfield1: string, subfield2: { subsubfield1: Double, }[], },
                variant: ("option1" | "option2"),
              }): SampleActivity1 {
                return { activityType: ActivityType.SampleActivity1, args };
              },
              SampleActivity2: function SampleActivity2Constructor(args: {
                quantity: Double,
              }): SampleActivity2 {
                return { activityType: ActivityType.SampleActivity2, args };
              },
              SampleActivityEmpty: function SampleActivityEmptyConstructor(): SampleActivityEmpty {
                return { activityType: ActivityType.SampleActivityEmpty, args: {} };
              },
            };
            export enum Resource {
              "/sample/resource/1" = "/sample/resource/1",
              "/sample/resource/2" = "/sample/resource/2",
            };
            declare global {
              var ActivityTemplates: typeof ActivityTemplateConstructors;
              var ActivityTypes: typeof ActivityType;
              var Resources: typeof Resource;
            }
            // Make ActivityTemplates and ActivityTypes available on the global object
            Object.assign(globalThis, {
              ActivityTemplates: ActivityTemplateConstructors,
              ActivityTypes: ActivityType,
              Resources: Resource,
            });
            /** End Codegen */""",
        TypescriptCodeGenerationService.generateTypescriptTypesFromMissionModel(MISSION_MODEL_TYPES));
  }
}

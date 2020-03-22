package gov.nasa.jpl.ammos.mpsa.aerie.adaptation.mocks;

import gov.nasa.jpl.ammos.mpsa.aerie.adaptation.app.App;
import gov.nasa.jpl.ammos.mpsa.aerie.adaptation.app.CreateSimulationMessage;
import gov.nasa.jpl.ammos.mpsa.aerie.adaptation.models.ActivityType;
import gov.nasa.jpl.ammos.mpsa.aerie.adaptation.models.AdaptationJar;
import gov.nasa.jpl.ammos.mpsa.aerie.adaptation.models.NewAdaptation;
import gov.nasa.jpl.ammos.mpsa.aerie.adaptation.models.SimulationResults;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.activities.representation.ParameterSchema;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.activities.representation.SerializedActivity;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.activities.representation.SerializedParameter;
import org.apache.commons.lang3.NotImplementedException;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class StubApp implements App {
    public static final String EXISTENT_ADAPTATION_ID = "abc";
    public static final String NONEXISTENT_ADAPTATION_ID = "def";
    public static final AdaptationJar EXISTENT_ADAPTATION;
    public static final Map<String, Object> VALID_NEW_ADAPTATION;
    public static final Map<String, Object> INVALID_NEW_ADAPTATION;

    public static final String EXISTENT_ACTIVITY_TYPE = "activity";
    public static final String NONEXISTENT_ACTIVITY_TYPE = "no-activity";
    public static final ActivityType EXISTENT_ACTIVITY = new ActivityType(
        EXISTENT_ACTIVITY_TYPE,
        Map.of("Param", ParameterSchema.STRING),
        Map.of("Param", SerializedParameter.of("Default")));

    public static final SerializedActivity VALID_ACTIVITY_INSTANCE = new SerializedActivity(
        EXISTENT_ACTIVITY_TYPE,
        Map.of("Param", SerializedParameter.of("Value")));
    public static final SerializedActivity INVALID_ACTIVITY_INSTANCE = new SerializedActivity(
        EXISTENT_ACTIVITY_TYPE,
        Map.of("Param", SerializedParameter.of("")));
    public static final SerializedActivity UNCONSTRUCTABLE_ACTIVITY_INSTANCE = new SerializedActivity(
        EXISTENT_ACTIVITY_TYPE,
        Map.of("Nonexistent", SerializedParameter.of("Value")));
    public static final SerializedActivity NONEXISTENT_ACTIVITY_INSTANCE = new SerializedActivity(
        NONEXISTENT_ACTIVITY_TYPE,
        Map.of());

    public static final List<String> NO_SUCH_ACTIVITY_TYPE_FAILURES = List.of("no such activity type");
    public static final List<String> INVALID_ACTIVITY_INSTANCE_FAILURES = List.of("just wrong");
    public static final List<String> UNCONSTRUCTABLE_ACTIVITY_INSTANCE_FAILURES = List.of("Unconstructable activity instance");

    static {
        VALID_NEW_ADAPTATION = new HashMap<>();
        VALID_NEW_ADAPTATION.put("name", "adaptation");
        VALID_NEW_ADAPTATION.put("version", "1.0");
        VALID_NEW_ADAPTATION.put("mission","mission");
        VALID_NEW_ADAPTATION.put("owner","owner");
        VALID_NEW_ADAPTATION.put("file", new FakeFile(
            "adaptation.jar",
            "application/java-archive",
            "valid-adaptation-file"));

        INVALID_NEW_ADAPTATION = new HashMap<>();
        INVALID_NEW_ADAPTATION.put("name", "adaptation");
        INVALID_NEW_ADAPTATION.put("version", "FAILFAILFAILFAILFAIL");
        INVALID_NEW_ADAPTATION.put("mission","mission");
        INVALID_NEW_ADAPTATION.put("owner","owner");
        INVALID_NEW_ADAPTATION.put("file", new FakeFile(
            "adaptation.jar",
            "application/java-archive",
            "invalid-adaptation-file"));

        EXISTENT_ADAPTATION = new AdaptationJar();
        EXISTENT_ADAPTATION.name = "adaptation";
        EXISTENT_ADAPTATION.version = "1.0a";
        EXISTENT_ADAPTATION.mission = "mission";
        EXISTENT_ADAPTATION.owner = "Tester";
        EXISTENT_ADAPTATION.path = Path.of("existent-adaptation");
    }

    @Override
    public Map<String, AdaptationJar> getAdaptations() {
        return Map.of(EXISTENT_ADAPTATION_ID, EXISTENT_ADAPTATION);

    }

    @Override
    public AdaptationJar getAdaptationById(final String adaptationId) throws NoSuchAdaptationException {
        if (!Objects.equals(adaptationId, EXISTENT_ADAPTATION_ID)) {
            throw new NoSuchAdaptationException(adaptationId);
        }

        return EXISTENT_ADAPTATION;
    }

    @Override
    public String addAdaptation(final NewAdaptation adaptation) throws AdaptationRejectedException {
        if (adaptation.version.equals("FAILFAILFAILFAILFAIL")) {
            throw new AdaptationRejectedException("could not load adaptation");
        }

        return EXISTENT_ADAPTATION_ID;
    }

    @Override
    public void removeAdaptation(final String adaptationId) throws NoSuchAdaptationException {
        if (!Objects.equals(adaptationId, EXISTENT_ADAPTATION_ID)) {
            throw new NoSuchAdaptationException(adaptationId);
        }
    }

    @Override
    public Map<String, ActivityType> getActivityTypes(final String adaptationId) throws NoSuchAdaptationException {
        if (!Objects.equals(adaptationId, EXISTENT_ADAPTATION_ID)) {
            throw new NoSuchAdaptationException(adaptationId);
        }

        return Map.of(EXISTENT_ACTIVITY_TYPE, EXISTENT_ACTIVITY);
    }

    @Override
    public ActivityType getActivityType(final String adaptationId, final String activityType) throws NoSuchAdaptationException, NoSuchActivityTypeException {
        if (!Objects.equals(adaptationId, EXISTENT_ADAPTATION_ID)) {
            throw new NoSuchAdaptationException(adaptationId);
        }

        if (!Objects.equals(activityType, EXISTENT_ACTIVITY_TYPE)) {
            throw new NoSuchActivityTypeException(activityType);
        }

        return EXISTENT_ACTIVITY;
    }

    @Override
    public List<String> validateActivityParameters(final String adaptationId, final SerializedActivity activityParameters)
        throws NoSuchAdaptationException
    {
        if (!Objects.equals(adaptationId, EXISTENT_ADAPTATION_ID)) {
            throw new NoSuchAdaptationException(adaptationId);
        }

        if (Objects.equals(activityParameters, NONEXISTENT_ACTIVITY_INSTANCE)) {
            return NO_SUCH_ACTIVITY_TYPE_FAILURES;
        } else if (Objects.equals(activityParameters, UNCONSTRUCTABLE_ACTIVITY_INSTANCE)) {
            return UNCONSTRUCTABLE_ACTIVITY_INSTANCE_FAILURES;
        } else if (Objects.equals(activityParameters, INVALID_ACTIVITY_INSTANCE)) {
            return INVALID_ACTIVITY_INSTANCE_FAILURES;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public SimulationResults runSimulation(final CreateSimulationMessage message) {
        throw new NotImplementedException("TODO");
    }
}

package gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.activities;

import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.engine.ControlChannel;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.engine.JobContext;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.states.StateContainer;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.time.Duration;
import gov.nasa.jpl.ammos.mpsa.aerie.merlinsdk.time.Time;

public class ActivityJob<T extends StateContainer> implements Comparable<ActivityJob<T>> {

    // TODO: detach threads for garbage collection?

    public enum ActivityStatus {
        NotStarted, InProgress, Complete
    }

    /**
     * The start or resumption time of the effect model owned by this job.
     * 
     * At instantiation: the start time of the activity. Upon delays, this field is updated to reflect the resumption
     * time so that it may be re-inserted into the engine's pending event queue at the appropriate wake-up time. This 
     * value is the index by which the engine's priority queue orders activities.
     */
    private Time eventTime;

    /**
     * The activity instance that this job "owns"
     */
    private Activity<T> activity;

    /**
     * Serves as a bridge between this job and the engine. It is passed down to the activity's effect model as the
     * `SimulationContext` type to prevent adapter access to certain engine-related methods.
     */
    private JobContext<T> ctx;

    /**
     * A means of synchronously passing execution control between this job's thread and others (primarily the engine).
     */
    private ControlChannel channel;

    /**
     * The adapter-provided state index structure
     */
    private T states;

    private ActivityStatus status = ActivityStatus.NotStarted;

    public ActivityJob(Activity<T> activityInstance, Time startTime) {
        this.activity = activityInstance;
        this.eventTime = startTime;
    }

    /**
     * Executes the activity's effect model in coordination with the engine
     * 
     * This method will execute an activity's effect model, block until all child activities are complete, log the
     * duration of the effect model within an engine-level map, and notify the activity threads that are listening on
     * this target. A `ControlChannel` is used to ensure that the engine is not executing at the same time; execution
     * control is explicitly handed back-and-forth between the engine and activity job's thread via yields and takes.
     */
    public void execute() {
        this.channel.takeControl();

        this.status = ActivityStatus.InProgress;
        Time startTime = this.ctx.now();

        activity.modelEffects(this.ctx, this.states);
        this.ctx.waitForAllChildren();
        this.status = ActivityStatus.Complete;

        Duration activityDuration = this.ctx.now().subtract(startTime);
        this.ctx.logActivityDuration(activityDuration);
        this.ctx.notifyActivityListeners();
        
        this.channel.yieldControl();
    }

    /**
     * Suspends the activity job's thread until execution control is explicitly given back to it
     */
    public synchronized void suspend() {
        this.channel.yieldControl();
        this.channel.takeControl();
    }
    
    /**
     * Compares `ActivityJob` objects by their event times
     */
    @Override
    public int compareTo(ActivityJob<T> other) {
        return this.eventTime.compareTo(other.eventTime);
    }

    public Time getEventTime() {
        return this.eventTime;
    }

    public Activity<T> getActivity() {
        return this.activity;
    }

    public void setContext(JobContext<T> ctx) {
        this.ctx = ctx;
    }

    public void setEventTime(Time t) {
        this.eventTime = t;
    }

    public ControlChannel getChannel() {
        return this.channel;
    }

    public void setChannel(ControlChannel channel) {
        this.channel = channel;
    }

    public void setStates(T states) {
        this.states = states;
    }

    public String toString() {
        // TODO: map to actual name in the future
        return activity.getClass().getName();
    }

    public ActivityStatus getStatus() {
        return this.status;
    }

}
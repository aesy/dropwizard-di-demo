package io.aesy.dropwizard.job;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.reflections.Reflections;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// TODO Misfire policy
// TODO Repeat count
// TODO Delay every?
// TODO Every from each start or from start to end?

// JobManager should fire event on job start, abort, fail, completion
// JobManager should time jobs
// JobManager should count current job states and finishing job states


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface OnApplicationStart {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface OnApplicationStop {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Every {
    long value();
    TimeUnit unit();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Cron {
    String value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Delay {
    long value();
    TimeUnit unit();
}

class JobFeature implements Feature {
    @Inject
    private Environment environment;

    @Override
    public boolean configure(FeatureContext context) {
        environment.getApplicationContext().manage(JobManager.class);

        return true;
    }
}

class JobBinder extends AbstractBinder {
    @Override
    protected void configure() {
        Reflections reflections = new Reflections("");
        Set<Class<? extends Job>> jobs = reflections.getSubTypesOf(Job.class);

        for (Class<?> job : jobs) {
            addActiveDescriptor(job);
        }
    }
}

class JobManager implements Managed {
    private final IterableProvider<Job> jobs;
    private final Map<Job, Timer> timers;
    private final ExecutorService executorService;

    @Inject
    public JobManager(
        IterableProvider<Job> jobs,
        MetricRegistry metricRegistry
    ) {
        this.jobs = jobs;
        this.timers = new HashMap<>();
        this.executorService = Executors.newCachedThreadPool();

        for (Job job : jobs) {
            Timer timer = metricRegistry.timer(job.getName());
            timers.put(job, timer);
        }
    }

    @Override
    public void start() throws Exception {
        scheduleJobs();
    }

    @Override
    public void stop() throws Exception {
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    protected void scheduleJobs() {
        for (Job job : jobs) {
            executorService.submit(() -> {
                try (Timer.Context timerContext = timers.get(job).time()) {
                    job.run();
                }
            });
        }
    }
}

public abstract class Job implements Runnable {
    private final String name;

    protected Job(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {

    }
}

package org.wildfly.quickstarts.microprofile.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.logging.Logger;

@Liveness
@ApplicationScoped
public class SimpleHealthCheck implements HealthCheck {

    private Logger LOG = Logger.getLogger(SimpleHealthCheck.class.getName());
    private boolean fail = Boolean.getBoolean("APP_READINESS_FAIL");


    @Override
    public HealthCheckResponse call() {
        LOG.info("Readiness probe invoked="+fail);
        if (fail) {
            return HealthCheckResponse.named("Fail Readiness health check").down().build();
        }
        return HealthCheckResponse.named("Simple health check").up().build();
    }
}

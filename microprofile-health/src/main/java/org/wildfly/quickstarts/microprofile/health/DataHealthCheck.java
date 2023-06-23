package org.wildfly.quickstarts.microprofile.health;

import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.logging.Logger;

@Liveness
@ApplicationScoped
public class DataHealthCheck implements HealthCheck {
    private Logger LOG = Logger.getLogger(DataHealthCheck.class.getName());
    private boolean fail = Boolean.getBoolean("APP_LIVENESS_FAIL");

    @Override
    public HealthCheckResponse call() {
        LOG.info("Liveness probe called="+fail);
        if (fail) {
            return HealthCheckResponse.named("Health check failed")
                    .down()
                    .build();
        }
        return HealthCheckResponse.named("Health check with data")
            .up()
            .withData("foo", "fooValue")
            .withData("bar", "barValue")
            .build();
    }
}

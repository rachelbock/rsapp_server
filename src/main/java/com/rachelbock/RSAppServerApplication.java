package com.rachelbock;

import com.rachelbock.resources.*;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class RSAppServerApplication extends Application<RSAppServerConfiguration> {

    public static void main(final String[] args) throws Exception {
        new RSAppServerApplication().run(args);
    }

    @Override
    public String getName() {
        return "RSApp Server";
    }

    @Override
    public void initialize(final Bootstrap<RSAppServerConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/assets", "index.html"));
    }

    @Override
    public void run(final RSAppServerConfiguration configuration,
                    final Environment environment) {
        environment.jersey().register(new WallsResource());
        environment.jersey().register(new MessageResource());
    }

}

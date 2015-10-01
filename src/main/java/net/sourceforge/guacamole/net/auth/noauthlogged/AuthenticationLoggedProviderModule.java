/*
 * Copyright (C) 2015 Glyptodon LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package main.java.net.sourceforge.guacamole.net.auth.noauthlogged;

import org.glyptodon.guacamole.environment.Environment;
import org.glyptodon.guacamole.net.auth.AuthenticationProvider;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;

/**
 * Guice module which configures the injections used by the JDBC authentication
 * authentication providers based on JDBC will not function.
 *
 * @author Michael Jumper
 * @author James Muehlner
 */
public class AuthenticationLoggedProviderModule extends MyBatisModule {

    /**
     * The environment of the Guacamole server.
     */
    private final Environment environment;

    /**
     * The AuthenticationProvider which is using this module to configure
     * injection.
     */
    private final AuthenticationProvider authProvider;

    /**
     * Creates a new JDBC authentication provider module that configures the
     * various injected base classes using the given environment, and provides
     * connections using the given socket service.
     *
     * @param authProvider
     *     The AuthenticationProvider which is using this module to configure
     *     injection.
     *
     * @param environment
     *     The environment to use to configure injected classes.
     * 
     * @param tunnelService
     *     The tunnel service to use to provide tunnels sockets for connections.
     */
    public AuthenticationLoggedProviderModule(AuthenticationProvider authProvider,
            Environment environment) {
        this.authProvider = authProvider;
        this.environment = environment;
    }

    @Override
    protected void initialize() {
        
        // Datasource
        bindDataSourceProviderType(PooledDataSourceProvider.class);

        bind(AuthenticationProvider.class).toInstance(authProvider);
        bind(Environment.class).toInstance(environment);
        
    }

}

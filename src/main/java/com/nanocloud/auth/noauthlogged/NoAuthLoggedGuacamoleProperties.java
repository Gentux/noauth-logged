/*
 * Copyright (C) 2013 Glyptodon LLC
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

package com.nanocloud.auth.noauthlogged;

import org.glyptodon.guacamole.properties.IntegerGuacamoleProperty;
import org.glyptodon.guacamole.properties.StringGuacamoleProperty;

/**
 * Properties used by the MySQL Authentication plugin.
 * @author James Muehlner
 */
public class NoAuthLoggedGuacamoleProperties {

    /**
     * This class should not be instantiated.
     */
    private NoAuthLoggedGuacamoleProperties() {}

    /**
     * The URL of the logging server containing connection history
     */
    public static final StringGuacamoleProperty NOAUTHLOGGED_SERVERURL = new StringGuacamoleProperty() {

        @Override
        public String getName() { return "noauthlogged-server-url"; }

    };

     /**
     * The port of the logging server containing connection history
     */
    public static final IntegerGuacamoleProperty NOAUTHLOGGED_SERVERPORT = new IntegerGuacamoleProperty() {

        @Override
        public String getName() { return "noauthlogged-server-port"; }

    };

    /**
     * The endpoint on the logging server where history are sent
     */
    public static final StringGuacamoleProperty NOAUTHLOGGED_SERVERENDPOINT = new StringGuacamoleProperty() {

        @Override
        public String getName() { return "noauthlogged-server-endpoint"; }

    };

    /**
     * The username of the user which will perform the log request to the API
     */
    public static final StringGuacamoleProperty NOAUTHLOGGED_SERVERUSERNAME = new StringGuacamoleProperty() {

        @Override
        public String getName() { return "noauthlogged-server-username"; }

    };

    /**
     * The password of the user which will perform the log request to the API
     */
    public static final StringGuacamoleProperty NOAUTHLOGGED_SERVERPASSWORD = new StringGuacamoleProperty() {

        @Override
        public String getName() { return "noauthlogged-server-password"; }

    };

}

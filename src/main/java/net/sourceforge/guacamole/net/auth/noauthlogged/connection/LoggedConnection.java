package main.java.net.sourceforge.guacamole.net.auth.noauthlogged.connection;

import java.util.concurrent.atomic.AtomicBoolean;

import org.glyptodon.guacamole.GuacamoleException;
import org.glyptodon.guacamole.environment.Environment;
import org.glyptodon.guacamole.environment.LocalEnvironment;
import org.glyptodon.guacamole.net.GuacamoleSocket;
import org.glyptodon.guacamole.net.GuacamoleTunnel;
import org.glyptodon.guacamole.net.SimpleGuacamoleTunnel;
import org.glyptodon.guacamole.net.auth.simple.SimpleConnection;
import org.glyptodon.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.glyptodon.guacamole.protocol.GuacamoleClientInformation;
import org.glyptodon.guacamole.protocol.GuacamoleConfiguration;

import main.java.net.sourceforge.guacamole.net.auth.noauthlogged.tunnel.ManagedInetGuacamoleSocket;
import main.java.net.sourceforge.guacamole.net.auth.noauthlogged.tunnel.ManagedSSLGuacamoleSocket;

public class LoggedConnection extends SimpleConnection {
	
    /**
     * Backing configuration, containing all sensitive information.
     */
    private GuacamoleConfiguration config;
	
	public LoggedConnection(String name, String identifier, GuacamoleConfiguration config) {
		super(name, identifier, config);
		
		this.config = config;
	}
	
    /**
     * Task which handles cleanup of a connection associated with some given
     * ActiveConnectionRecord.
     */
    private class ConnectionCleanupTask implements Runnable {

        /**
         * Whether this task has run.
         */
        private final AtomicBoolean hasRun = new AtomicBoolean(false);


        @Override
        public void run() {

            // Only run once
            if (!hasRun.compareAndSet(false, true))
                return;
        	
        	System.err.print("It works !!!");

        }

    }
	
    @Override
    public GuacamoleTunnel connect(GuacamoleClientInformation info)
            throws GuacamoleException {

        Environment env = new LocalEnvironment();
        
        // Get guacd connection parameters
        String hostname = env.getProperty(Environment.GUACD_HOSTNAME, "localhost");
        int port = env.getProperty(Environment.GUACD_PORT, 4822);

        GuacamoleSocket socket;
        
        // Record new active connection
        Runnable cleanupTask = new ConnectionCleanupTask();
        
        // If guacd requires SSL, use it
        if (env.getProperty(Environment.GUACD_SSL, false))
            socket = new ConfiguredGuacamoleSocket(
                new ManagedSSLGuacamoleSocket(hostname, port, cleanupTask),
                config, info
            );

        // Otherwise, just connect directly via TCP
        else
            socket = new ConfiguredGuacamoleSocket(
                new ManagedInetGuacamoleSocket(hostname, port, cleanupTask),
                config, info
            );

        return new SimpleGuacamoleTunnel(socket);
        
    }
}

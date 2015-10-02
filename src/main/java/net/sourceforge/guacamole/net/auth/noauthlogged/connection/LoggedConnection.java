package main.java.net.sourceforge.guacamole.net.auth.noauthlogged.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

			HttpURLConnection connection = null;
			try {
				// Create connection
				URL url = new URL("http://127.0.0.1:8081");
				
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

				String urlParameters = "nanocloud=rocks&hello=bonjour";

				connection.setUseCaches(false);
				connection.setDoOutput(true);

				// Send request
				DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
				writer.writeBytes(urlParameters);
				writer.close();

				// Get Response
				InputStream input = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				StringBuilder response = new StringBuilder();
				
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				reader.close();

				System.err.print(response.toString());

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}

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

package main.java.net.sourceforge.guacamole.net.auth.noauthlogged.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
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

import main.java.net.sourceforge.guacamole.net.auth.noauthlogged.NoAuthLoggedGuacamoleProperties;
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
		private ActiveConnectionRecord connection;

        public ConnectionCleanupTask(ActiveConnectionRecord connection) {
        	this.connection = connection;
        }

        @Override
        public void run() {

            // Only run once
            if (!hasRun.compareAndSet(false, true))
                return;

			HttpURLConnection http = null;
			try {
				Environment env = new LocalEnvironment();
				String serverURL = env.getProperty(NoAuthLoggedGuacamoleProperties.NOAUTHLOGGED_SERVERURL);
				Integer serverPort = env.getProperty(NoAuthLoggedGuacamoleProperties.NOAUTHLOGGED_SERVERPORT);
				String serverEndpoint = env.getProperty(NoAuthLoggedGuacamoleProperties.NOAUTHLOGGED_SERVERENDPOINT);
				URL url = new URL("http", serverURL, serverPort, serverEndpoint);

				http = (HttpURLConnection) url.openConnection();
				http.setRequestMethod("POST");
				http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

				String urlParameters = "UserId=" + this.connection.getUsername() +
						"&ConnectioniD=" + this.connection.getConnectionName() +
						"&StartDate=" + this.connection.getStartDate() +
						"&EndDate=" + new Date();

				http.setUseCaches(false);
				http.setDoOutput(true);

				// Send request (for some reason we actually need to wait for response)
				DataOutputStream writer = new DataOutputStream(http.getOutputStream());
				writer.writeBytes(urlParameters);
				writer.close();

				// Get Response
				InputStream input = http.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				StringBuilder response = new StringBuilder();

				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				reader.close();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (http != null) {
					http.disconnect();
				}
			}

        }

    }
	
    @Override
    public GuacamoleTunnel connect(GuacamoleClientInformation info)
            throws GuacamoleException {

        Environment env = new LocalEnvironment();
        ActiveConnectionRecord connection = new ActiveConnectionRecord(this.getName());

        // Get guacd connection parameters
        String hostname = env.getProperty(Environment.GUACD_HOSTNAME, "localhost");
        int port = env.getProperty(Environment.GUACD_PORT, 4822);

        GuacamoleSocket socket;

        // Record new active connection
        Runnable cleanupTask = new ConnectionCleanupTask(connection);

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

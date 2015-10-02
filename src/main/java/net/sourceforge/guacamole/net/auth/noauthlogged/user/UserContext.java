package main.java.net.sourceforge.guacamole.net.auth.noauthlogged.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.glyptodon.guacamole.GuacamoleException;
import org.glyptodon.guacamole.form.Form;
import org.glyptodon.guacamole.net.auth.ActiveConnection;
import org.glyptodon.guacamole.net.auth.AuthenticatedUser;
import org.glyptodon.guacamole.net.auth.AuthenticationProvider;
import org.glyptodon.guacamole.net.auth.Connection;
import org.glyptodon.guacamole.net.auth.ConnectionGroup;
import org.glyptodon.guacamole.net.auth.Directory;
import org.glyptodon.guacamole.net.auth.User;
import org.glyptodon.guacamole.net.auth.simple.SimpleConnectionDirectory;
import org.glyptodon.guacamole.net.auth.simple.SimpleConnectionGroup;
import org.glyptodon.guacamole.net.auth.simple.SimpleConnectionGroupDirectory;
import org.glyptodon.guacamole.net.auth.simple.SimpleDirectory;
import org.glyptodon.guacamole.net.auth.simple.SimpleUser;
import org.glyptodon.guacamole.net.auth.simple.SimpleUserDirectory;
import org.glyptodon.guacamole.protocol.GuacamoleConfiguration;

import main.java.net.sourceforge.guacamole.net.auth.noauthlogged.connection.LoggedConnection;

public class UserContext implements org.glyptodon.guacamole.net.auth.UserContext {

    /**
     * The unique identifier of the root connection group.
     */
    private static final String ROOT_IDENTIFIER = "ROOT";
	
	private User self;
	private AuthenticationProvider authProvider;
	private Directory<User> userDirectory;
	private Directory<Connection> connectionDirectory;
	private Directory<ConnectionGroup> connectionGroupDirectory;
	private ConnectionGroup rootGroup;
	
	public UserContext(AuthenticationProvider authProvider,
			AuthenticatedUser authenticatedUser, Map<String, GuacamoleConfiguration> configs) throws GuacamoleException {

        // Return as unauthorized if not authorized to retrieve configs
        if (configs == null)
        		throw new GuacamoleException("No configuration file");
        
        Collection<String> connectionIdentifiers = new ArrayList<String>(configs.size());
        Collection<String> connectionGroupIdentifiers = Collections.singleton(ROOT_IDENTIFIER);
        
        // Produce collection of connections from given configs
        Collection<Connection> connections = new ArrayList<Connection>(configs.size());
        for (Map.Entry<String, GuacamoleConfiguration> configEntry : configs.entrySet()) {

            // Get connection identifier and configuration
            String identifier = configEntry.getKey();
            GuacamoleConfiguration config = configEntry.getValue();

            // Add as simple connection
            Connection connection = new LoggedConnection(identifier, identifier, config);
            connection.setParentIdentifier(ROOT_IDENTIFIER);
            connections.add(connection);

            // Add identifier to overall set of identifiers
            connectionIdentifiers.add(identifier);
            
        }
        
        // Add root group that contains only the given configurations
        this.rootGroup = new SimpleConnectionGroup(
            ROOT_IDENTIFIER, ROOT_IDENTIFIER,
            connectionIdentifiers, Collections.<String>emptyList()
        );

        // Build new user from credentials
        this.self = new SimpleUser(authenticatedUser.getIdentifier(), connectionIdentifiers,
                connectionGroupIdentifiers);

        // Create directories for new user
        this.userDirectory = new SimpleUserDirectory(self);
        this.connectionDirectory = new SimpleConnectionDirectory(connections);
        this.connectionGroupDirectory = new SimpleConnectionGroupDirectory(Collections.singleton(this.rootGroup));

        // Associate provided AuthenticationProvider
        this.authProvider = authProvider;
	}
	
	@Override
	public User self() {
		return self;
	}

	@Override
	public AuthenticationProvider getAuthenticationProvider() {
		return authProvider;
	}

	@Override
	public Directory<User> getUserDirectory() throws GuacamoleException {
		return userDirectory;
	}

	@Override
	public Directory<Connection> getConnectionDirectory() throws GuacamoleException {
		return connectionDirectory;
	}

	@Override
	public Directory<ConnectionGroup> getConnectionGroupDirectory() throws GuacamoleException {
		return connectionGroupDirectory;
	}

	@Override
	public Directory<ActiveConnection> getActiveConnectionDirectory() throws GuacamoleException {
		return new SimpleDirectory<ActiveConnection>();
	}

	@Override
	public ConnectionGroup getRootConnectionGroup() throws GuacamoleException {
		return rootGroup;
	}

	@Override
	public Collection<Form> getUserAttributes() {
		return Collections.<Form>emptyList();
	}

	@Override
	public Collection<Form> getConnectionAttributes() {
		 return Collections.<Form>emptyList();
	}

	@Override
	public Collection<Form> getConnectionGroupAttributes() {
		return Collections.<Form>emptyList();
	}

}

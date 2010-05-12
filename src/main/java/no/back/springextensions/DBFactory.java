/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package no.back.springextensions;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.jcouchdb.db.Database;
import org.jcouchdb.db.Server;
import org.jcouchdb.db.ServerImpl;

/**
 * Factory for creating a {@link Database} (CouchDB database 'connection') where required properties are set.
 * {@link #initialize()} can only be called after the required properties are set on {@link DBFactory}.
 * 
 * @author christopher.olaussen 2010
 */
class DBFactory {

	private String hostname = null;
	private String database = null;
	private int port = Server.DEFAULT_PORT;
	private String username = null;
	private String password = null;

	DBFactory hostname(String hostname) {
		this.hostname = hostname;
		return this;
	}

	DBFactory database(String database) {
		this.database = database;
		return this;
	}

	DBFactory port(int port) {
		org.springframework.util.Assert.isTrue(port >= 0 && port <= 65536);
		this.port = port;
		return this;
	}

	DBFactory username(String username) {
		this.username = username;
		return this;
	}

	DBFactory password(String password) {
		this.password = password;
		return this;
	}

	/**
	 * Create a new {@link Database} connection
	 * 
	 * @return initialized database
	 * @throws IllegalStateException
	 *             if not all required properties are set before attempting to
	 *             initialize.
	 */
	Database initialize() {
		if (hostname == null)
			throw new IllegalStateException("You must specify " + hostname
					+ " before initializing a database connection");
		if (database == null)
			throw new IllegalStateException("You must specify a " + database
					+ " name before initializing a database connection");
		if (!(port >= 0 && port <= 65536))
			throw new IllegalStateException(
					"You must specify a "
							+ port
							+ " within 0-65536 before initializing a database connection");

		Server server = new ServerImpl(hostname, port);
		if (username != null && password != null) {
			Credentials credentials = new UsernamePasswordCredentials(username,
					password);
			AuthScope authScope = new AuthScope(hostname, port,null);
			server.setCredentials(authScope, credentials);
		}
		
		return new Database(server, database);
	}
}

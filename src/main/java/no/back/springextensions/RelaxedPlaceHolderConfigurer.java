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

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jcouchdb.db.Database;
import org.jcouchdb.db.Server;
import org.jcouchdb.document.ValueRow;
import org.jcouchdb.document.ViewResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.Assert;
import org.svenson.JSONParser;

/**
 * Reads properties from CouchDB database filling place holders for Spring
 * beans.
 * 
 * @author christopher.olaussen
 * @see DBFactory
 */
public class RelaxedPlaceHolderConfigurer extends PropertyPlaceholderConfigurer {

	Logger logger = LoggerFactory.getLogger(RelaxedPlaceHolderConfigurer.class);

	// default values for bean definition
	public static final String DEFAULT_HOSTNAME = "localhost";
	public static final String DEFAULT_DATABASE = "properties";
	public static final int DEFAULT_PORT = Server.DEFAULT_PORT; // 5984
	public static final String DEFAULT_ENVIRONMENT = null; 		// will check for JVM variable instead 

	public static final String JVM_ENIVRONMENT_ARG	=	"relaxed.env";
	
	// active couchdb properties
	private String hostname = DEFAULT_HOSTNAME;
	private String database = DEFAULT_DATABASE;
	private int port = DEFAULT_PORT;
	private String environment = DEFAULT_ENVIRONMENT;

	private String username = null;
	private String password = null;

	private DBFactory dbFactory = new DBFactory();

	public void setDBFactory(DBFactory dbFactory) {
		this.dbFactory = dbFactory;
	}

	/**
	 * Set the placeholder prefix to a unique symbol @{} as in @{mydatabaseConn.url}
	 */
	public RelaxedPlaceHolderConfigurer() {
		super();
		setPlaceholderPrefix("@{");
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void loadProperties(Properties props) throws IOException {
		
		verifyEnvironment();
		
		logger.info("CouchDB loadProperties() with active environment: "
				+ environment);
		Database db = dbFactory.hostname(hostname).port(port)
				.database(database).username(username).password(password)
				.initialize();
		
		// Currently we simply emit all documents from properties database with a custom view.
		// TODO add option for specifying design views
		ViewResult<Map> result = db.queryAdHocView(Map.class,
				"{ \"map\" : \"function(doc) { emit(doc._id, doc);} \"}", null,
				new JSONParser());
		if (result != null) {
			logger.info("Amount of properties collected from CouchDB: "
					+ result.getTotalRows());
			for (ValueRow<Map> row : result.getRows()) {
				Set<String> keys = row.getValue().keySet();
				keys.retainAll(Arrays.asList(new String[] { environment })); // skipping
																				// return
																				// value

				for (String key : keys) {
					String value = (String) row.getValue().get(key);
					props.put(row.getId(), value);
				}
			}
		}
	}

	/**
	 * Assert that the environment is specified.
	 * Either by {@link #setEnvironment(String)} or adding a JVM argument -Drelaxedenv=testing
	 */
	private void verifyEnvironment() {
		if (environment == null) {
			String envVarEnvironment = System
					.getProperty(JVM_ENIVRONMENT_ARG);
			if (envVarEnvironment != null) {
				environment = envVarEnvironment;
			} else {
				throw new IllegalStateException(
						"You must specify an active environment for selecting properties with the"
								+ RelaxedPlaceHolderConfigurer.class.getName());
			}
		}
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		Assert.notNull(database);
		this.hostname = hostname;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		Assert.notNull(database);
		this.database = database;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		Assert.isTrue(port >= 0 && port <= 65536,
				"The network port must be between 0 and 65536. You specified:"
						+ port);
		this.port = port;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		Assert.notNull(environment);
		this.environment = environment;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		Assert.notNull(username);
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		Assert.notNull(password);
		this.password = password;
	}
}

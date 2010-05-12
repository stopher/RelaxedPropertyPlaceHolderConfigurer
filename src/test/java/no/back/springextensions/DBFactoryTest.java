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

import static org.junit.Assert.assertNotNull;

import no.back.springextensions.DBFactory;

import org.jcouchdb.db.Database;
import org.junit.Test;

/**
 * Unit tests for {@link DBFactory}
 * @author christopher.olaussen
 */
public class DBFactoryTest {

	private DBFactory dbFactory = new DBFactory();

	@Test(expected = IllegalStateException.class)
	public void dbFactoryInitializeBeforeAllPropertiesSetFails()
			throws Exception {
		dbFactory.initialize();
	}

	@Test(expected = IllegalStateException.class)
	public void dbFactoryInitializeHostnameOnlySetFails() throws Exception {
		dbFactory.hostname("host").initialize();
	}

	@Test(expected = IllegalArgumentException.class)
	public void dbFactoryInitializePortOutOfRangeFails() throws Exception {
		dbFactory.port(9999999).initialize();
	}

	@Test(expected = IllegalArgumentException.class)
	public void dbFactoryInitializePortOutOfRangeNegativeFails()
			throws Exception {
		dbFactory.port(-1).initialize();
	}

	@Test(expected = IllegalStateException.class)
	public void dbFactoryInitializePortOnlySetFails() throws Exception {
		dbFactory.port(444).initialize();
	}

	@Test(expected = IllegalStateException.class)
	public void dbFactoryInitializeDatabaseOnlySetFails() throws Exception {
		dbFactory.database("databasename").initialize();
	}

	@Test
	public void dbFactoryInitializeAfterAllButCredentialsPropertiesSetWorks()
			throws Exception {
		Database db = dbFactory.hostname("unittesting").database("testingdb")
				.port(3).initialize();
		assertNotNull(db);
	}

	@Test
	public void dbFactoryInitializeAfterAllPropertiesSetWorks()
			throws Exception {
		Database db = dbFactory.hostname("unittesting").database("testingdb")
				.port(3).username("user").password("password").initialize();
		assertNotNull(db);
	}

}

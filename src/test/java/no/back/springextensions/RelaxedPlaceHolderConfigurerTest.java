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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import no.back.springextensions.DBFactory;
import no.back.springextensions.RelaxedPlaceHolderConfigurer;

import org.jcouchdb.db.Database;
import org.jcouchdb.db.Options;
import org.jcouchdb.document.ValueRow;
import org.jcouchdb.document.ViewResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.svenson.JSONParser;

/**
 * Unit tests for {@link RelaxedPlaceHolderConfigurer}
 * @author christopher.olaussen
 */
public class RelaxedPlaceHolderConfigurerTest {

	RelaxedPlaceHolderConfigurer relaxedPlaceHolderConfigurer = null;
	DBFactory dbFactoryMock = mock(DBFactory.class);
	Database databaseMock = mock(Database.class);

	@Before
	public void setup() {
		relaxedPlaceHolderConfigurer = new RelaxedPlaceHolderConfigurer();
		relaxedPlaceHolderConfigurer.setDBFactory(dbFactoryMock);
	}

	@Test
	public void usernameGetterAndSetterCorrect() {
		assertNull(relaxedPlaceHolderConfigurer.getUsername());
		String dummy = "modifiedValue";
		relaxedPlaceHolderConfigurer.setUsername(dummy);
		assertEquals(dummy, relaxedPlaceHolderConfigurer.getUsername());
	}

	@Test
	public void passwordGetterAndSetterCorrect() {
		assertNull(relaxedPlaceHolderConfigurer.getPassword());
		String dummy = "modifiedValue";
		relaxedPlaceHolderConfigurer.setPassword(dummy);
		assertEquals(dummy, relaxedPlaceHolderConfigurer.getPassword());
	}

	@SuppressWarnings("static-access")
	@Test
	public void environmentGetterAndSetterCorrect() {
		assertEquals(relaxedPlaceHolderConfigurer.DEFAULT_ENVIRONMENT,
				relaxedPlaceHolderConfigurer.getEnvironment());
		String dummy = "modifiedValue";
		relaxedPlaceHolderConfigurer.setEnvironment(dummy);
		assertEquals(dummy, relaxedPlaceHolderConfigurer.getEnvironment());
	}

	@SuppressWarnings("static-access")
	@Test
	public void hostnameGetterAndSetterCorrect() {
		assertEquals(relaxedPlaceHolderConfigurer.DEFAULT_HOSTNAME,
				relaxedPlaceHolderConfigurer.getHostname());
		String dummy = "modifiedValue";
		relaxedPlaceHolderConfigurer.setHostname(dummy);
		assertEquals(dummy, relaxedPlaceHolderConfigurer.getHostname());
	}

	@SuppressWarnings("static-access")
	@Test
	public void portGetterAndSetterCorrect() {
		assertEquals(relaxedPlaceHolderConfigurer.DEFAULT_PORT,
				relaxedPlaceHolderConfigurer.getPort());
		int dummy = 4444;
		relaxedPlaceHolderConfigurer.setPort(dummy);
		assertEquals(dummy, relaxedPlaceHolderConfigurer.getPort());
	}

	@Test(expected = IllegalArgumentException.class)
	public void portSetOutOfRangeThrowsException() {
		relaxedPlaceHolderConfigurer.setPort(65537);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void portSetOutOfRangeNegativeThrowsException() {
		relaxedPlaceHolderConfigurer.setPort(-4444);
	}
	
	@SuppressWarnings("unchecked")
	private ViewResult<Map> resultFixture() {
		List<ValueRow<Map>> rows = new LinkedList<ValueRow<Map>>();
		ValueRow<Map> row = new ValueRow<Map>();
		row.setKey("testing");
		row.setId("testing");
		Map<String, String> value = new HashMap<String, String>();
		value.put("testing", "testvalue");
		row.setValue(value);
		rows.add(row);
		final ViewResult<Map> result = new ViewResult<Map>();
		result.setRows(rows);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadPropertiesWorks() {
		relaxedPlaceHolderConfigurer.setEnvironment("testing");
		Properties props = new Properties();
		ViewResult<Map> result = resultFixture();

		when(dbFactoryMock.hostname(Mockito.anyString())).thenReturn(
				dbFactoryMock);
		when(dbFactoryMock.port(Mockito.anyInt())).thenReturn(dbFactoryMock);
		when(dbFactoryMock.database(Mockito.anyString())).thenReturn(
				dbFactoryMock);
		when(dbFactoryMock.username(Mockito.anyString())).thenReturn(
				dbFactoryMock);
		when(dbFactoryMock.password(Mockito.anyString())).thenReturn(
				dbFactoryMock);
		when(dbFactoryMock.initialize()).thenReturn(databaseMock);
		when(
				databaseMock.queryAdHocView((Class) Mockito.any(), Mockito
						.anyString(), (Options) Mockito.anyObject(),
						(JSONParser) Mockito.any())).thenReturn(result);

		try {
			relaxedPlaceHolderConfigurer.loadProperties(props);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(props.size() > 0);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=IllegalStateException.class)
	public void loadPropertiesFailsWithoutSpecifiedEnvironment() {
		Properties props = new Properties();
		ViewResult<Map> result = resultFixture();

		when(dbFactoryMock.hostname(Mockito.anyString())).thenReturn(
				dbFactoryMock);
		when(dbFactoryMock.port(Mockito.anyInt())).thenReturn(dbFactoryMock);
		when(dbFactoryMock.database(Mockito.anyString())).thenReturn(
				dbFactoryMock);
		when(dbFactoryMock.username(Mockito.anyString())).thenReturn(
				dbFactoryMock);
		when(dbFactoryMock.password(Mockito.anyString())).thenReturn(
				dbFactoryMock);
		when(dbFactoryMock.initialize()).thenReturn(databaseMock);
		when(
				databaseMock.queryAdHocView((Class) Mockito.any(), Mockito
						.anyString(), (Options) Mockito.anyObject(),
						(JSONParser) Mockito.any())).thenReturn(result);

		try {
			relaxedPlaceHolderConfigurer.loadProperties(props);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(props.size() > 0);
	}

}

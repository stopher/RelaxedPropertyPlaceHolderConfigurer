
# RelaxedPropertyPlaceHolderConfigurer v.0.1-Alpha
Puts your properties in CouchDB for ease of administration and enables you to specify environment(what properties to use for Spring beans) at runtime, and deploy the same WAR anywhere.

- This version is not recommended for production!
- Only tested with CouchDB 0.11


# Configuration:
Add it to your application context by defining the configurer as a bean:


	<bean id="relaxedProperties" class="com.appwerk.springextensions.RelaxedPlaceHolderConfigurer" >

		<!-- Host of Couch DB instance (Defaults to localhost if left out)-->
		<property name="hostname" value="localhost" />
		
		<!-- Couch DB port (Defaults to 5984 if left out)-->
		<property name="port" value="5984" />
		
		<!-- The database name (Defaults to properties if left out) -->
		<property name="database" value="properties" />
		
		<!-- The environment 
			(If left out you need to specify the environment as a jvm arg -Drelaxed.env=testing)
		-->
		<property name="environment" value="testing" />
			 
		<!-- Provide username & password if your couchdb instance is locked for queries-->
		<property name="username" value="reader" />
		<property name="password" value="th3s3cr3t" />
	
	</bean>


You can also use maven filters for specifying the environment, but this will be locking the WAR to a specific environment:

	<!-- The environment -->
	<property name="environment" value="${environment}" />


To use the same WAR with different configurations you should leave it out and start you application with the argument -Drelaxedenv=testing. Where 'testing' is the active environment.


# Creating documents in properties database:

When you create documents in CouchDB:
- Documents must have an id matching the placeholder.
- Documents must have a field for each environment with a value for that environment.

Example document:


	id         = database.driverClassName
	testing    = jdbc\:hsqldb\:/opt/test/mydb;shutdown\=true
	production = jdbc\:hsqldb\:/opt/prod/mydb;shutdown\=true


When using the placeholder 'database.driverClassName':

	<bean class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close" id="dataSource">
        <property name="driverClassName" value="@{database.driverClassName}"/>
	</bean>
Take note that we use the sign @ to mark placeholders for properties stored in CouchDB.


# FAQ:
Q: Need to lock access to the database:
A: If you need to lock access to the database you must enable this setting in your local.ini for authentication to work properly:


	WWW-Authenticate = Basic realm="administrator"


# Contributors:
- Christopher Holst-Pedersen Olaussen



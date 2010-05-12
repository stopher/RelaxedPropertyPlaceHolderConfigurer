
# RelaxedPropertyPlaceHolderConfigurer v.0.1-Alpha
Put your properties in CouchDB, and specify environment at runtime!
 
- This is an alpha version and it is not recommended for production!
- This library has only been tested with CouchDB 0.11
- This project enables you to store properties in CouchDB databases
- Enabling you to specify environment(what properties to use for Spring beans) at runtime, and deploy the same WAR anywhere.

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


To make the setup perfect you should use a maven filter for specifying the environment, 
and put a properties file placeholder as value. E.g.:


	<!-- The environment -->
	<property name="environment" value="${environment}" />


If not leave it out and start you application with the argument -Drelaxedenv=testing. Where testing is the environment.


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


Good luck!
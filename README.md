# Run the connector

## Docker image

There are two ways to run the e-CODEX connector from a Docker container:

* get the latest built image from our JFrog repository
* build your own image from source code

```
The following section assumes that you are familiar with Docker and have it installed on your development machine. If not, proceed to install it.
```

### Already built image

A built image of the connector is available here: [here](https://scm.ecodex.eu[]).

Pull the image by running:

```shell
docker pull https://scm.ecodex.eu/connector:latest
```

### Build your own image

By default, there is a ```Dockerfile``` in the root of this project.

```Before building the image, we assume you have a working development environment.```

Build the connector project in production mode by running:

```shell
mvn build install -Pproduction
```

Build the connector image by running:

```shell
docker build -t connector:ltest .
```

## Run the docker container
### Run the standalone container
#### DBMS installation and configuration
If you choose this path, we assume you have a DBMS installed and populated. If not install an DBMS and run appropriate
structure and data script. The following DBMS were tested with the connector:

* MySQL
* MariaDB
* PostgreSQL
* Oracle

Following [previous steps](#docker-image), create a database and a user with a password.

The appropriate script for your database initialisation can be found here:

```
./domibusConnectorPersistence/src/resources/dbscripts/initial
```

or after your build here:

```
./domibusConnectorDistribution/target/domibusConnector/documentation/database-scripts/initial
```

With a DBMS installed and configured, we are now ready to run our container.

#### Connector configuration
To make the connector container work during its launch, we need

* attach some environment variable for configuring its datasource
* as the configuration properties, logs, configuration & output folder and keystore are connector agnostic, we need to bind these configs folders to the container.

To do this, create a working directory and name it as you wish. In our example, we called this directory `container`.

```shell
mkdir container
```

create a subfolder within the `container` folder

```shell
mkdir -p data config logs
```

Place sample configurations in the `config` folder. Examples can be found in the connector source code.

* A `Keystore` sample can be found at:

```
domibusConnectorDistribution/target/domibusConnector/standalone/config
```
Copy `keystore` folder and paste it into the `container/config` folder.

* `application.properties` sample can be found at:

```
domibusConnectorDistribution/target/domibusConnector/documentation/config/prpoperties
```
Copy `application.properties` file and paste it into `container/config` folder.

* `log4j2.xml` sample can be found at:

```
domibusConnectorDistribution/target/domibusConnector/documentation/config/prpoperties
```
Copy `log4j2.xml` file and paste it into `container/config` folder.

Grant other users write access to the `container` folder by running.

```shell
chmod -R 777 container
```

Move to the `container` folder.

```shell
cd /path/to/container_folder
```

Now we are ready to run our container.

```
**NOTE**
During our test, MySQL DBMS was used, so adapt the following script to your context.
```

```shell
docker run -d \
  -e SPRING_DATASOURCE_DRIVER_CLASS_NAME="com.mysql.cj.jdbc.Driver" \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/ecodex?useSSL=false&allowPublicKeyRetrieval=true" \
  -e SPRING_DATASOURCE_USERNAME="ecodex" \
  -e SPRING_DATASOURCE_PASSWORD="ecodex" \
  -v ./logs:/logs
  -v ./data:/app/data
  -v ./config:/app/config
```

### Run the container via docker-compose
```
**NOTE**
In this section we assume that you have downloaded the connector image from the eulisa JFrog registry or built it from the connector source code. If not, please refer to the previous section.
```

```
**NOTE**
Again, we assume that you have docker-compose installed on your local machine. If not, proceed to install it. 
```

YYou can also run the connector container and its database as a service (avoiding DBMS installation and configuration).
To achieve this goal, we have provided a `docker-compose` configuration file. In our example, we are using MySQL as the DBMS service, so feel free to customise the `docker-compose.yml` file with your preferred DBMS.

We also have to do very little configuration to make things work.

Follow the steps bellow:
* create a `container` folder
* copy and paste the `docker-compose.yml` file available at the root of the connector source code into the `container` folder.
* in addition to the `data`, `config`, and `logs` subfolders and their associated configurations ([see previous section](#connector-configuration)), add a `sql` folder.
* depending on the DBMS (MySQL in our case) copy and paste the DBMS initialisation scripts into the `sql` subfolder:
```
  /domibusConnectorDistribution/target/domibusConnector/documentation/database-scripts/initial/MySQL
```
* in the `sql` subfolder rename the files as follows:
  * `mysql_initial.sql` -> `0.sql`
  * `mysql_initial_data.sql` -> `1.sql`
  * `mysql_quartz.sql` -> `2.sql`

We can now run our `connector' and `mysql' container as a service by running:

```shell
docker-compose up -d
```

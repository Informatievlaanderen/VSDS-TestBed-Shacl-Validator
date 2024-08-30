# VSDS TestBed Shacl Validator

<!-- TOC -->

* [VSDS TestBed Shacl Validator](#vsds-testbed-shacl-validator)
    * [Introduction](#introduction)
        * [Validation service implementation](#validation-service-implementation)
    * [Prerequisites](#prerequisites)
    * [Building and running](#building-and-running)
        * [Live reload for development](#live-reload-for-development)
        * [Packaging using Docker](#packaging-using-docker)

<!-- TOC -->

## Introduction

This application implements the [GITB test service APIs](https://www.itb.ec.europa.eu/docs/services/latest/) in a  
[Spring Boot](https://spring.io/projects/spring-boot) web application that is meant to support
[GITB TDL test cases](https://www.itb.ec.europa.eu/docs/tdl/latest/) running in the Interoperability Test Bed.

### Validation service implementation

The sample validation service validates a text against an (also provided) expected value. The user of the service can
also select whether he/she wants to have a mismatch reported as an error or a warning. Finally, an information message
is also returned in case values match but when ignoring casing.

Once running, the validation endpoint's WDSL is available at http://localhost:8080/services/validation?WSDL. See
[here](https://www.itb.ec.europa.eu/docs/services/latest/validation/) for further information on processing service
implementations.

This validation services requires in the validation call two parameters:

1. **ldes-url**: the url of the LDES to validate
2. **shacl-shape**: the shacl shape that will be used to validate the server against to

## How to run in Docker

### Prerequisites

* Docker
* A complete up and running TestBed instance
* An LDI Orchestrator
* An [Ontotext GraphDB](https://www.ontotext.com/products/graphdb/) instance

All these services are set up in the [`./docker/docker-compose.yaml`](./docker/docker-compose.yaml) config file, but
here are dummy environment variables used that must be changed in a production environment

### Steps to use the TestBed Shacl Validator

1. Add the TestBed Shacl Validator to `docker-compose.yaml` file

```yaml
  testbed-shacl-validator:
    image: ghcr.io/testbed-shacl-validator:latest
    depends_on:
      - <GRAPH_DB_SERVICE_NAME>
      - <LDIO_WORKBENCH_SERVICE_NAME>
    environment:
      - LDIO_SPARQLHOST=http://<LDIO_WORKBENCH_SERVICE_NAME>:<INTERNAL_LDIO_CONTAINER_PORT>
      - LDIO_SPARQLHOST=http://<GRAPH_DB_SERVICE_NAME>:<INTERNAL_GRAPHDB_CONTAINER_PORT>
      - SERVER_PORT=8080
```

In this specific tutorial, this would result in the following config:

```yaml
  testbed-shacl-validator:
    image: ghcr.io/testbed-shacl-validator:latest
    depends_on:
      - graphdb
      - ldio-workbench
    environment:
      - LDIO_HOST=http://ldio-workbench:8080
      - LDIO_SPARQLHOST=http://graphdb:7200
      - SERVER_PORT=8080
```

2. Start up all the services

```shell
docker compose up -d --wait
```

3. Add a test suite to TestBed that uses this service

In this step, a test suite will be written and can be written in many different ways. In this specific case, a test
suite will be written that contains only one test case. This test case will prompt for a Linked Data Event Stream url,
which will be validated against the provided SHACL shape.

First of all, make a folder that will contain all required files for the test suite:

```shell
mkdir validate_ldes_to_shacl_shape
cd validate_ldes_to_shacl_shape
mkdir test_cases
```

Secondly, test case xml file can be added to the `test_cases` folder.

```xml

<testcase id="ts1_tc1" xmlns="http://www.gitb.com/tdl/v1/" xmlns:gitb="http://www.gitb.com/core/v1/">
    <metadata>
        <gitb:name>[TC1] Validate LDES</gitb:name>
        <gitb:version>1.0</gitb:version>
        <gitb:description>Validate an LDES against a specific</gitb:description>
    </metadata>
    <actors>
        <gitb:actor id="LDESServer" role="SUT"/>
        <gitb:actor id="SHACLValidator"/>
    </actors>

    <steps stopOnError="true">
        <!-- Prompt the user to enter the required data to run the test -->
        <interact id="validationData" desc="Setup parameters to validate the LDES">
            <request desc="URL of the LDES to validate" name="ldes"/>
            <request desc="Shacl shape that must be used for validating" name="shaclShape" inputType="UPLOAD"/>
        </interact>
        <log>"Starting shacl validation test"</log>
        <!-- Step 3: Check relation timestamp consistency. -->
        <!-- Notice here how we refer to the address of the validation service using the domain-level "validationServiceAddress" configuration property. -->
        <verify output="validatorOutput" id="shaclValidationStep" desc="validate against shacl"
                handler="http://testbed-shacl-validator:8080/services/validation?wsdl">
            <input name="ldes-url">$validationData{ldes}</input>
            <input name="shacl-shape">$validationData{shaclShape}</input>
        </verify>
        <log>"shacl verification finished"</log>
    </steps>

    <output>
        <success>
            <default>"Test session completed successfully."</default>
        </success>
        <failure>
            <default>
                "Test session failed. Please check the failed step report and the test session log for details."
            </default>
        </failure>
    </output>
</testcase>
```

Some interesting things we see in this test case, are the `interact` block, as well as the `verify` block.

#### interact

This block is responsible for prompting the user/tester to enter some data that will be used for the test. In this
specific case, it will be the URL of the LDES that must be validated.

#### verify

This block is responsible for the verifying the LDES by calling the external service, which is this TestBed Shacl
Validator service. The most important part to notice here is the `handler` attribute, where the external TestBed Shacl
Validator service will be set. In this case, the docker compose service name must be used, followed by the port. After
that, the "fixed" uri is placed, which will call the right validation service, which is in this case
`/services/validation?wsdl`

Another thing that can be noticed here, are the input parameters. These two parameters are required to let the test run.

1. `ldes-url`: the url of the LDES that must be validated. The value here is fetched from the prompted input
2. `shacl-shape`: the SHACL shape that will be used to validate all the members against to. Here is the value also
   fetched from the prompted input

To finish up this step, a test suite itself must be added as well. This can be done by adding `test_suite.xml` to the
`validate_ldes_to_shacl_shape` folder.
This is how the file should look like:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<testsuite id="ts1" xmlns="http://www.gitb.com/tdl/v1/"
           xmlns:gitb="http://www.gitb.com/core/v1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <!--
        This is the test suite's entry point file as it points to the test cases which in turn point to other scriptlets and resources. In general there is
        no requirement on the structure of a test suite but it is a good practice to keep things organised with a consistent structure that makes sense to you
        (e.g. a "test_cases" folder for the test cases, a "scriptlets" folder for scriptlets, "resources" for files to import etc.).
    -->
    <metadata>
        <gitb:name>LDES MUST be valid against the provided SHACL shape</gitb:name>
        <gitb:version>1.0</gitb:version>
        <gitb:description>Test suite to validate the LDES against a certain SHACL shape</gitb:description>
    </metadata>
    <actors>
        <!--
            The IDs of the actors need to match the ones you see on the UI. Note that when you deploy a test suite it will automatically create actors based on
            the below definitions if missing.
        -->
        <gitb:actor id="LDESServer">
            <gitb:name>LDES Server</gitb:name>
            <gitb:desc>The system acting as an LDES server that contains an event stream full of members</gitb:desc>
        </gitb:actor>
        <gitb:actor id="SHACLValidator">
            <gitb:name>Shacl Validator</gitb:name>
            <gitb:desc>The independent shacl validator service that will validate an LDES againt a SHACL shape
            </gitb:desc>
        </gitb:actor>
    </actors>

    <!-- 
        The test case IDs refer to the IDs of the relevant test cases (see their root element). You can place test cases anywhere you want in the test suite archive
        and they will be picked up automatically when parsing the test suite.
    -->
    <testcase id="ts1_tc1"/>
</testsuite>
```

Most important to notice here, is that de test case declared in the `test_cases` folder, must be referred from this file
by adding the `test case` tag with as attribute the id that has been assigned to the test case in its file.

4. Compress the `validate_ldes_to_shacl_shape` folder to a zip and upload it to TestBed

If everything is set up, the folder containing everything must be zipped. After that, it can be uploaded to TestBed. This can be done either by the UI, or via the REST API. If you choose to do it via the REST API, the 
```shell
curl -F updateSpecification=true -F specification=<SPECIFICATION_API_KEY> -F testSuite=@test_shacl_validator.zip --header "ITB_API_KEY: <DOMAIN_API_KEY>" -X POST http://localhost:9000/api/rest/testsuite/deploy;
```

5. Run the test via the TestBed UI

## Building and running the project

1. Build using `mvn clean package`.
2. Once built you can run the application using `mvn spring-boot:run`.

### Live reload for development

This project uses Spring Boot's live reloading capabilities. When running the application from your IDE or through
Maven, any change in classpath resources is automatically detected to restart the application.

### Packaging using Docker

Running this application as a [Docker](https://www.docker.com/) container is very simple as described in Spring Boot's
[Docker documentation](https://spring.io/guides/gs/spring-boot-docker/). The first step is to
[Install Docker](https://docs.docker.com/install/) and ensure it is up and running. You can now build the Docker image
through Maven:

1. Build the JAR file with `mvn package`.
2. Build the Docker image with `docker build -f ./.github/Dockerfile .`.


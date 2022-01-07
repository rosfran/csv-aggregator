# CSV Aggregator

A solution to merge in a sorted way a list of CSV files. 

# Description

    The main feature is implemented by the service CSVAggregatorService. It reads a list of CSV files asynchronously, 
    sort all this files in increasing order (based on the only column value), write all these ordered values into
    a unique CSV file. The service has a dependency to the Apache Spark framework, which is used here because
    it has awesome performance when dealing with huge volume of data, and is highly scalable (it is easy to
    add more service instances and make them work together).

    This solution is composed by: a REST Backend (which creates endpoints for file listing and CSV files aggregation),
    some test cases implemented with JUnit and MockMVC, and a service component that uses the Map Reduce architectural
    component from Apache Spark to merge a list of CSV files and sort all of them.

# Test cases

    The class com.mendix.csv.aggregator.AggregatorApplicationTests, in the test Maven profile, uses Mockito (MockMVC)
    to implement some interesting test cases.
* allMediumFilesRequested
* allSmallFilesRequeste
* aggregateCSVMediumFiles
* aggregateCSVSmallFiles
* testSendingFiles

# Command-line (shell) utilities

## run_csv_aggregator.sh
    This shell script runs the JAR file directly (doesn't uses the Docker image)

## run_csv_aggregator_docker.sh
    Build and run a Docker image created for this CSV Aggregator service.

## run_csv_aggregator_docker_compose.sh
    Build and run a Docker image created for this CSV Aggregator service, 
    but using the docker compose structure (docker-compose.yaml).

## run_csv_aggregator_docker_scale.sh
    Build and run a Docker image created for this CSV Aggregator service (using scalability features from docker compose), 
    but using the docker compose structure (docker-compose-scaling.yaml).
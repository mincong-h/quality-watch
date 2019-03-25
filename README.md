# quality-watch [![CircleCI](https://circleci.com/gh/mincong-h/quality-watch.svg?style=svg)](https://circleci.com/gh/mincong-h/quality-watch)

Quality Watch (qWatch) is a data aggregator for code quality, based on
different metrics. Here're the modules of the project:

- **Logs** - Logs analysis based on CSV exported from Datadog.
- **Jenkins** - Build analysis based on Jenkins build executions.
- **Assembly** - Modules assembly for creating a flat JAR.

## Build

If you want to build this project, you need to have JDK 12 and Maven 3.5+
installed.

    mvn clean install

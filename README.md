# Handycapper

A simple application for demonstrating [chart-parser](https://github.com/robinhowlett/chart-parser), a horse racing PDF results chart parser.
  
## TL;DR

An extremely simple web application that uploads a sample Equibase PDF chart file:

![before](https://i.imgur.com/qidVJim.png)

and converts each race to JSON or CSV (the latter containing basic individual Split time information) with a helpful file name:

![after](https://i.imgur.com/iMxjKI0.png)

## Quick Start

Handycapper is a [Spring Boot](https://projects.spring.io/spring-boot/) (Java) application. 

1. Install [Java 8](https://www.java.com/en/download/help/download_options.xml), if not already installed (check by running `java -version` in your terminal or command prompt)
1. Download the JAR file from [the releases section](https://github.com/robinhowlett/handycapper/releases).
1. From the location where you downloaded the JAR file, run `java -jar handycapper-0.0.1-SNAPSHOT.jar`
1. Open [http://localhost:8080](http://localhost:8080) in your browser
1. Upload a PDF result chart and select either JSON or CSV

_It is recommended to install a JSON-viewer browser extension (e.g. [JSON Formatter](https://chrome.google.com/webstore/detail/json-formatter/bcjindcccaagfpapjjmafapmmgkkhgoa?hl=en)) for easier viewing of the generated JSON content_

## Notes

This is alpha-level software and has minimal error handling.

This software is open-source and released under the [MIT License](https://github.com/robinhowlett/chart-parser/blob/master/LICENSE). 

This project contains [a single sample Equibase PDF chart and its JSON file equivalent](https://github.com/robinhowlett/handycapper/blob/master/src/main/resources) included for testing, educational and demonstration purposes only.

It is recommended users of this software be aware of the conditions on the PDF charts that may apply.

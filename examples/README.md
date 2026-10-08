# FastCLICommand Examples

This folder contains standalone example and microbenchmark projects demonstrating FastCLICommand.

## Demo

The `Demo` project showcases multi-command routing, option specs, aliases, and sub-microsecond CLI dispatch.

To run locally:
```bash
cd Demo
mvn compile exec:java
```
Or run `run-demo.bat` from the root directory.

## Benchmark

The `Benchmark` project measures end-to-end routing and parameter parsing throughput using OpenJDK JMH.

To run locally:
```bash
cd Benchmark
mvn clean package
java -jar target/benchmarks.jar
```
Or run `run-benchmark.bat` from the root directory.

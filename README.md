# Hello Radix

My radix playground. Currently the program just sends a message to the faucet.

## Requirements

* Java 8
* Scala 2.12.6
* SBT 1.1.4

## Run

To run the program during development use:

```
sbt run
```

## Build

You can build a fat jar with all dependencies (including Scala).

```
sbt assembly
```

You can then run the jar like this:

```
java -jar target/scala-2.12/hello-radix-0.1.jar
```

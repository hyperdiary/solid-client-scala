## sbt project compiled with Scala 3

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).

Use `sbt assembly` to build command line Solid Pod loader. To use the loader run:
```commandline
java -jar ./target/scala-3.3.1/solid-client-scala.jar
```
Note that when running the Solid Server locally, the Pod URL provided to the loader must match the resources in the Turtle file being loaded. For example if the resources in the file are like: 
```
<http://pod.example.org/person/1>
```
then the Pod URL passed to the loader must be `http://pod.example.org/` and not `http://pod.localhost:3000/`
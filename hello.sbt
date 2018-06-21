name := "Hello Radix"

version := "0.1"

mainClass := Some("machisuji.HelloRadix")

assemblyJarName in assembly := s"hello-radix-${version.value}.jar"

resolvers += "JCenter" at "https://jcenter.bintray.com"

libraryDependencies += "com.radixdlt" % "radixdlt-java" % "0.9.1"

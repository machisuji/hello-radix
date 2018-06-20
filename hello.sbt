resolvers += "Radix Bintray Repo" at "https://dl.bintray.com/radixdlt/maven2"

// doesn't work, hence the self-built JAR under ./lib
libraryDependencies += "com.radixdlt" % "radixdlt-java" % "0.9.0"

// radix dependencies for the self-built JAR
libraryDependencies += "org.bouncycastle" % "bcprov-jdk15on" % "1.56"
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.2"
libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "3.10.0"
libraryDependencies += "com.squareup.okhttp3" % "logging-interceptor" % "3.10.0"
libraryDependencies += "io.reactivex.rxjava2" % "rxjava" % "2.1.14"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"

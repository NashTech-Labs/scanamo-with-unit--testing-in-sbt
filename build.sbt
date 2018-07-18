name := "scanamo"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
    "com.gu" %% "scanamo-alpakka" % "1.0.0-M7",
    "org.scalatest" %% "scalatest" % "3.0.1",
    "com.amazonaws" % "DynamoDBLocal" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "sqlite4java" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "sqlite4java-win32-x86" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "sqlite4java-win32-x64" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "libsqlite4java-osx" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "libsqlite4java-linux-i386" % "latest.integration" % "test",
    "com.almworks.sqlite4java" % "libsqlite4java-linux-amd64" % "latest.integration" % "test")

resolvers += "DynamoDB Local Release Repository" at "https://s3-us-west-2.amazonaws.com/dynamodb-local/release"

parallelExecution in test := false

lazy val copyJars = taskKey[Unit]("copyJars")
copyJars := {
    import java.nio.file.Files
    import java.io.File
    val artifactTypes = Set("dylib", "so", "dll")
    val files = Classpaths.managedJars(Test, artifactTypes, update.value).files
    Files.createDirectories(new File(baseDirectory.value, "native-libs").toPath)
    files.foreach { f =>
        val fileToCopy = new File("native-libs", f.name)
        if (!fileToCopy.exists()) {
            Files.copy(f.toPath, fileToCopy.toPath)
        }
    }
}

(compile in Compile) := (compile in Compile).dependsOn(copyJars).value

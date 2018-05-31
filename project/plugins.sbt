
// ----------------
// Dependencies
// ----------------
addSbtPlugin("com.typesafe.sbt"         % "sbt-native-packager"           % "1.3.2")
addSbtPlugin("com.eed3si9n"             % "sbt-buildinfo"                 % "0.7.0")
addSbtPlugin("net.virtual-void"         % "sbt-dependency-graph"          % "0.9.0")
addSbtPlugin("org.scalastyle"           %% "scalastyle-sbt-plugin"        % "1.0.0")
addSbtPlugin("org.scoverage"            % "sbt-scoverage"                 % "1.5.1")


// ---------------
// Force dependencies to remove WARN in log during compilation
dependencyOverrides += "org.codehaus.plexus"            % "plexus-utils"                    % "3.0.17"
dependencyOverrides += "com.google.guava"               % "guava"                           % "20.0"



// Comiple/run under either Scala 2.11 or 2.12
crossScalaVersions  := Seq("2.11.8", "2.12.4")

// Default is 2.12
scalaVersion := (crossScalaVersions ).value.last

licenses += ("GPL-3.0",url("https://opensource.org/licenses/gpl-3.0.html"))

// in case we want to add some library dependencies later...
resolvers += Resolver.jcenterRepo
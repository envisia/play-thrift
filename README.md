# Thrift Generator based on [Scrooge](https://github.com/twitter/scrooge)

This Thrift generator is written in Scala since it's based on the Project Scrooge.

It's mean to be a replacement for Scrooge and the apache thrift code generator.
Actually it is aimed for Thrift usage while using the Thrift HTTP Mode on top of Play! 2.5.x.
It generates a easy to use AbstractController where you just need to define your services and attach them to your Router.

## Demonstration

you could actually run the generator via:

    sbt 'play-thrift-generator/run-main de.envisia.play.thrift.Main -d OUTFOLDER INPUTFILE.thrift'

and then add it to your project. and specifiy the following:

    resolvers += "Envisia" at "http://dl.bintray.com/envisia/maven"
    libraryDependencies ++= Seq(
      "org.apache.thrift" % "libthrift" % "0.9.3",
      "de.envisia" %% "play-thrift-runtime" % "0.0.1"
    )

After that you could actually just create a new "Controller" that doesn't extend Play's default Controller instead use the name of your service + Abstract.
I.e. if you have a service named 'NotificationService' you could actually extend that:

    NotificationController @Inject()()(implicit ec: ExecutionContext) extend AbstractNotificationService(new TBinaryProtocol.Factory()) {
    }

Then you just need to define your service Functions, so either your IDE will tell you that you need to implement them or you could add them manually like:

    def methodName(args): Future[Return]

That's it.

## Status

This project is in a really early stage and the internal API is likely to change soon.
Please be aware when using it.

## TODO

- [ ] Documentation
- [ ] Client API (Play-WS and a lightweight alternative, that don't use Finagle)
- [ ] A stable API
- [ ] Twirl templates instead of Mustache
- [ ] Tests
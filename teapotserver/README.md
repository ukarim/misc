## teapotserver

Example http server on top of Java NIO.

Server only works with http requests without entity body (simple GET request, POST request without body and etc).

### How to run

You need Java 11 preinstalled on your machine.

```bash
javac TeapotHttpServer.java
```

```bash
java TeapotHttpServer
```

Then visit http://localhost:8080/brew-coffee page.

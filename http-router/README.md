# http-router

Tree based http-router component for Java (>= v11)

### Usage example

```java
import com.ukarim.httprouter.*;

// create and setup http router instance
var router = new HttpRouter<HttpHandler>();
router.get("/users", new UsersListHandler());
router.get("/users/:login", new UserProfileHandler());
router.post("/users", new UserCreationHandler());

// get a handler for specific http request
var routerMatch = router.match("GET", "/users/ukarim");
var pathParams = routerMatch.getPathParams(); // contains path variables
var httpHandler = routerMatch.getHandler();
```

See [JettyExample.java](src/test/java/com/ukarim/httprouter/example/JettyExample.java)

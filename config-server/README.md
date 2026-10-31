# config-server

Spring Cloud Config Server. It serves the properties stored in the `config-repo/` folder of this Git repository to the other services, so each service no longer keeps its own copy.

Port: `8888`. Spring Boot 4.0.8, Spring Cloud 2025.1.3, Java 17.

## How it fits together

```
config-repo/ (Git)  -->  config-server :8888  -->  demo-service
                                              -->  fileupload-service
```

A client asks the server for `/{application-name}/{profile}`. The server merges, from most specific to least:

1. `{application-name}-{profile}.properties` (for example `demo-service-dev.properties`)
2. `{application-name}.properties` (for example `demo-service.properties`)
3. `application.properties` (shared by every service)

The file name must exactly match the client's `spring.application.name`.

## Run it

1. Merge the `config-repo/` folder into the branch the server reads (default `main`), or set `CONFIG_GIT_LABEL` to your branch name.
2. Start the server:

```bash
cd config-server
mvn spring-boot:run
```

In STS: Run As > Spring Boot App.

The repository is public, so no credentials are needed. If you ever make it private, set `CONFIG_GIT_USERNAME` to your GitHub username and `CONFIG_GIT_PASSWORD` to a personal access token (environment variables, never in a file).

| Variable | Default | Purpose |
| --- | --- | --- |
| `CONFIG_GIT_URI` | this repository | Repository to read from |
| `CONFIG_GIT_SEARCH_PATHS` | `config-repo` | Folder inside the repository |
| `CONFIG_GIT_LABEL` | `main` | Branch to read |
| `CONFIG_GIT_USERNAME` / `CONFIG_GIT_PASSWORD` | empty | Only for a private repository |

## Check the server first

Open these in a browser or Postman:

- `http://localhost:8888/demo-service/default`
- `http://localhost:8888/demo-service/dev`
- `http://localhost:8888/fileupload-service/default`

You should see JSON with a `propertySources` list. If it is empty or returns an error, fix the server before touching any client.

## Connect a client (demo-service, fileupload-service)

1. Add the dependency:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

2. Reduce `application.properties` to the two lines the client needs before it can reach the server:

```properties
spring.application.name=demo-service
spring.config.import=optional:configserver:http://localhost:8888
```

3. Set the database password as an environment variable on the machine running the service. The config files use `${DB_PASSWORD}`, not the real value:

```bash
export DB_PASSWORD=<your-password>
```

4. Start the config server first, then the service. Remove the duplicated properties from the service's local `application.properties` so there is one source of truth.

To use a profile: `spring.profiles.active=dev`.

## Refresh a value without restarting (class demo)

Add to `demo-service`:

```java
@RestController
@RefreshScope
public class MessageController {

    @Value("${app.message:default}")
    private String message;

    @GetMapping("/message")
    public String message() {
        return message;
    }
}
```

1. Open `http://localhost:8080/message`. It shows the current `app.message`.
2. Edit `app.message` in `config-repo/demo-service.properties`, commit and push.
3. Call `POST http://localhost:8080/actuator/refresh`.
4. Open `/message` again. The new value appears with no restart.

## Things to remember

- Start order: config-server, then Eureka, then the other services.
- `optional:` lets a client start when the server is down, but then it runs without the central config.
- `@RefreshScope` refreshes only the beans that use it. The datasource and `server.port` need a restart.
- The server reads the committed state of the branch, so commit and push before refreshing.
- Never commit real passwords. Use environment variables, or encrypt values with `{cipher}`.

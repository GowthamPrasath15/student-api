# config-repo

Properties served by `config-server`. Do not run anything from this folder.

| File | Used by |
| --- | --- |
| `application.properties` | Every service (Eureka client settings) |
| `demo-service.properties` | `demo-service` |
| `demo-service-dev.properties` | `demo-service` with profile `dev` |
| `fileupload-service.properties` | `fileupload-service` |
| `fileupload-service-dev.properties` | `fileupload-service` with profile `dev` |

The file name must exactly match the service's `spring.application.name`.

Passwords are not stored here. The files use `${DB_PASSWORD}`, which each service reads from its own environment.

See `../config-server/README.md` for how to run the server and connect clients.

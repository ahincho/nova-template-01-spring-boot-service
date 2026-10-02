# nova-template-spring-boot-service

The Nova Platform template for a Spring Boot service: a real, minimal service that compiles, passes its
tests and builds its container image, with a sample resource. Every Nova service on Spring Boot starts
from here ([ADR-051](https://github.com/ahincho/nova-shared-01-docs/blob/main/adrs/shared/ADR-051-plantillas-de-servicio.md)).

A template is not a skeleton with placeholders. The code is real and carries a sample name, so its own CI
proves it works the way it proves any service, and a service created from it inherits a shape that is
known to build.

## What it brings

| Piece | What it gives the service |
|---|---|
| `pe.edu.nova.java.spring-boot-service` 2.1.0 | the Java toolchain of Nova ([ADR-044](https://github.com/ahincho/nova-shared-01-docs/blob/main/adrs/java/ADR-044-toolchain-de-java.md)): Java 25, Spring Boot with its BOM, formatting, Checkstyle, a minimum of 80 % line coverage, commit validation and its git hook, OWASP, the SBOM, the container image, and for the tests Spring Boot's test starters with MockMvc and `nova-architecture-rules`, which a service does not declare |
| `nova-spring-boot-starter` 3.0.0 | the meta-starter: Spring Boot web, Jackson and Actuator, the API envelope with the layered errors of [ADR-031](https://github.com/ahincho/nova-shared-01-docs/blob/main/adrs/shared/ADR-031-modulo-de-errores-por-capas-con-trazabilidad.md), masking of annotated fields, observability and secrets |
| a sample resource, `items` | a `POST` with Bean Validation and a `GET` by id that throws `DomainError.notFound(...)` with its own code, organized in `controller`, `service`, `repository`, `entity` and `dto` |
| tests | the architecture rules of `nova-architecture-rules`, the API contract with the envelope, the 404 and the 400, the masking of annotated fields only, and the use case without Spring |
| the container image | `./gradlew novaDocker`, from the toolchain |
| CI | the reusable workflows of [`nova-shared-02-pipelines`](https://github.com/ahincho/nova-shared-02-pipelines), the commit validation and a check of the image |
| `.env.example` and the license | the variables the service reads, and EPL-2.0 |

It has no database, no secret store configured and no idempotency, and it is not published: a template
produces services, not artifacts. It has no release flow either; a service adds one when it starts to
publish versions.

## The sample

| Method | Path | What it does |
|---|---|---|
| `POST` | `/v1/items` | creates an item and answers 201 with `Location`. A title that is blank or longer than 100 characters, or a quantity that is missing or not positive, is a 400 that names each failed field |
| `GET` | `/v1/items/{id}` | returns an item. A missing one is a 404 with the code `ITEM_NOT_FOUND` and `metadata.traceId` |

The service does not decide any error status. `ItemService` throws what went wrong as a Nova error, and the
starter answers it with the status of its layer, the code, the message and the `traceId` of the request:

```json
{
  "success": false,
  "status": 404,
  "errors": [{"code": "ITEM_NOT_FOUND", "message": "El ítem 00000000-0000-0000-0000-000000000001 no existe"}],
  "metadata": {"traceId": "b0a9da1c6bd3427f89fcdf6b5e1843ad"}
}
```

A success is the same envelope with `"success": true` and the item in `data`.

## Create a service from this template

1. Press **Use this template** on GitHub, or from the command line:

   ```bash
   gh repo create <owner>/<service> --template ahincho/nova-template-01-spring-boot-service --public --clone
   ```

   GitHub copies the files with a clean history.
2. Rename what the template names, as below.
3. Replace the sample with the service's own resource, and this README with the service's own.
4. Run `./gradlew novaFormat`, then `./gradlew build` and `./gradlew novaDocker`, before the first commit. The
   formatter wraps a line by its length, so a shorter name can change how a line is wrapped, and the format
   check of `build` fails on it until `novaFormat` rewrites the file.

### Renaming is manual for now

[ADR-051](https://github.com/ahincho/nova-shared-01-docs/blob/main/adrs/shared/ADR-051-plantillas-de-servicio.md)
decides a `novaRename` task in the toolchain that changes the group, the package and the name. **It does not
exist yet**, so until it ships the rename is by hand. This is everything the template names:

| What | Where | In the template |
|---|---|---|
| the group | `gradle.properties`, `group` | `pe.edu.nova.java.templates` |
| the project name, which is also the name of the image | `settings.gradle.kts`, `rootProject.name` | `nova-template-spring-boot-service` |
| the application name | `src/main/resources/application.yaml`, `spring.application.name` | `nova-template-spring-boot-service` |
| the Java package | the folders `src/main/java/pe/edu/nova/java/templates/springboot` and `src/test/java/pe/edu/nova/java/templates/springboot`; every `package` and `import` line; and `BASE_PACKAGE` in `ArchitectureTest`, the only place a test names it | `pe.edu.nova.java.templates.springboot` |
| the application class | `ServiceApplication` | the service's own, such as `OrdersApplication` |
| the sample | the `Item*` classes, `/v1/items`, `ITEM_NOT_FOUND`, `ItemApiTest` and `ItemServiceTest`, and the two calls to `/v1/items` in the `image` job of `.github/workflows/ci.yml` | the service's own resource |
| the image name in CI | `.github/workflows/ci.yml`, `IMAGE` in the `image` job | `nova-template-spring-boot-service:ci` |

Keep the five layers under the new package. `LayeredArchitectureTest` looks for `controller`, `service`,
`repository`, `entity` and `dto` there, and a rule that finds no class in its layer fails instead of
passing, so a service without persistence still keeps a `repository` package. A service that really has no
such layer can allow it with `archRule.failOnEmptyShould=false` in `src/test/resources/archunit.properties`,
which turns the check off for every rule.

## Run it

It needs JDK 25, and Docker only for the image. GitHub Packages asks for credentials even to read, so
Gradle needs `GITHUB_ACTOR` and a `GITHUB_TOKEN` with `read:packages`:

```bash
export GITHUB_ACTOR=<your GitHub user> GITHUB_TOKEN=<a token with read:packages>
./gradlew bootRun
```

```bash
curl localhost:8080/actuator/health
curl -X POST localhost:8080/v1/items -H 'Content-Type: application/json' -d '{"title": "Mug", "quantity": 3}'
curl localhost:8080/v1/items/<the id of the response>
```

`.env.example` lists the variables the service reads. Copy it to `.env`, which is not versioned.

| Variable | What it does |
|---|---|
| `SERVER_PORT` | the HTTP port. 8080 is the one the image exposes |
| `NOVA_OBSERVABILITY_OTLP_ENDPOINT` | the OTLP endpoint of a collector. Empty, the service exports nothing: traces and metrics stay inside the process |

## Test and quality

```bash
./gradlew build
./gradlew novaFormat
```

`build` runs what the CI runs: the format check, Checkstyle, the tests and a minimum of 80 % line coverage.
`novaFormat` fixes the format. The first local build installs a hook that validates each commit message with
Conventional Commits, and the CI validates every commit of a pull request.

| Test | What it proves |
|---|---|
| `ArchitectureTest` | the layer rules of `nova-architecture-rules`: a controller never reaches a repository, a service never reaches a controller, a DTO never reaches an entity |
| `ItemApiTest` | the whole service with its starters: the envelope, the 404 with its own code and `metadata.traceId`, and the 400 with one error per field |
| `ItemServiceTest` | the use case without Spring or HTTP, which is what throwing a Nova error and not a status buys |
| `HealthEndpointTest` | the service is up with no collector configured, and Actuator is not wrapped in the envelope |
| `MaskingTest` | masking is opt-in: a field called `name` is answered as is, and only a field with `@Masked` comes out masked |

## The container image

```bash
./gradlew novaDocker
docker run --rm -p 8080:8080 --env-file .env nova-template-spring-boot-service:0.1.0-SNAPSHOT
```

`novaDocker` builds the image with the Dockerfile of the toolchain
([ADR-044](https://github.com/ahincho/nova-shared-01-docs/blob/main/adrs/java/ADR-044-toolchain-de-java.md),
[ADR-046](https://github.com/ahincho/nova-shared-01-docs/blob/main/adrs/java/ADR-046-imagenes-base-distroless.md)):
a distroless JRE 25, an unprivileged user, the jar in layers and port 8080. It is one image for every
environment, and the configuration reaches it as environment variables.

## Things to know

- **Masking is opt-in.** The mask starter masks a field only when the code marks it, with `@Masked` on the
  field or `@MaskedClass` on its class. A `name` or an `email` without annotation is answered as is.
  `nova.mask.infer-by-field-name: true` brings back the masking by the name of the field, and
  `nova.mask.enabled: false` turns the masking off.
- **A controller may call the Nova libraries, not the starters.** `LayeredArchitectureTest` lets a controller
  call the layers of the service, `java`, `jakarta`, Spring and the framework-free libraries,
  `pe.edu.nova.java.libs..`, so it can throw an `ApplicationError` or a `DomainError`. A call into a starter,
  `pe.edu.nova.java.starters..`, or into any package outside that list fails the architecture test; an
  annotation is not a call. The sample still throws from `ItemService`, because a missing item is a rule of
  the domain that holds behind any entry point, and the controller only translates HTTP.
- **Bean Validation is declared by the service.** The meta-starter does not declare it. It only arrives
  through the observability starter, and a service should not rely on that.
- **The secrets starter is on the classpath and reads no store** until the service sets `nova.secrets.import`
  ([ADR-042](https://github.com/ahincho/nova-shared-01-docs/blob/main/adrs/shared/ADR-042-secretos-detras-de-un-contrato.md)).
- **Idempotency is left out.** `nova-idempotency-spring-boot-starter` switches itself on and needs the table of
  its JDBC store, so a service declares it when it has a database.

## How the template stays compiling

**The template is kept compiling by its own CI.** Every pull request runs:

| Job | What it checks |
|---|---|
| `build` | `./gradlew build`, `checkstyleMain` and `javadoc`, the same pipeline every Nova Java service uses |
| `commit-lint` | the message of every commit of the pull request |
| `owasp` | the dependencies against known CVEs, failing at a CVSS of 7 or more |
| `sbom` | that the CycloneDX SBOM is generated |
| `image` | that `novaDocker` builds the image, that the container answers `UP` on `/actuator/health`, and that a `POST` and a `GET` of an item work inside it |

The jobs need no secret or variable of the repository: they pass with the `GITHUB_TOKEN` of the workflow.
`NOVA_PACKAGES_READ_TOKEN` and `NVD_API_KEY` are optional, and without the second one OWASP warns and uses the
shared NVD mirror.

A pull request that breaks any of them is not merged, so `main` always builds. A template also ages: when
a starter or the toolchain publishes a version, the template moves to it in its own pull request, and the CI
says whether it still holds. It is the source of truth of its stack, and the generators of level 6 are built
from it.

## License

Eclipse Public License 2.0 — see [LICENSE](LICENSE).

Copyright © 2026 Angel Hincho.

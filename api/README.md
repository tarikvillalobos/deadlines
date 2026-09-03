# Deadlines API

Kotlin + Ktor backend, organised as a Gradle multi-module build.

## Modules

| Module      | Purpose                                                        | Depends on                  |
|-------------|----------------------------------------------------------------|-----------------------------|
| `core`      | Shared kernel: money, errors, pagination, domain events        | —                           |
| `contracts` | API DTOs and the version registry, pure Kotlin                 | —                           |
| `platform`  | Reusable SaaS chassis: identity, access, billing, workflow, …  | `core`, `contracts`         |
| `domains`   | Business modules: commercial, orders, inventory, …             | `core`, `contracts`, `platform` |
| `app`       | Ktor bootstrap: plugins, routes, configuration                 | everything                  |

Inside `platform` and `domains` every context follows the same layers: `domain`, `application`, `infrastructure`, `api`. Architecture rules are enforced by Konsist tests in `app`.

## Requirements

- JDK 21 (a local toolchain is auto-detected, or downloaded by Gradle)

## Commands

```bash
./gradlew build        # compile, lint and test everything
./gradlew test         # tests only
./gradlew ktlintFormat # fix formatting
./gradlew :app:run     # start the server on http://localhost:8080
```

`PORT` overrides the listening port.

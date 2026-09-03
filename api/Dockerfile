FROM gradle:9-jdk21-alpine AS build
WORKDIR /build
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY core/build.gradle.kts ./core/
COPY contracts/build.gradle.kts ./contracts/
COPY platform/build.gradle.kts ./platform/
COPY domains/build.gradle.kts ./domains/
COPY app/build.gradle.kts ./app/
RUN gradle --no-daemon dependencies --quiet || true
COPY . .
RUN gradle --no-daemon :app:installDist -x test -x ktlintCheck

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S deadlines && adduser -S -G deadlines deadlines
WORKDIR /app
COPY --from=build /build/app/build/install/app ./
USER deadlines
EXPOSE 8080
ENTRYPOINT ["./bin/app"]

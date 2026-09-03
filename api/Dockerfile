FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY core/build.gradle.kts ./core/
COPY contracts/build.gradle.kts ./contracts/
COPY platform/build.gradle.kts ./platform/
COPY domains/build.gradle.kts ./domains/
COPY app/build.gradle.kts ./app/
RUN ./gradlew --no-daemon dependencies --quiet || true
COPY . .
RUN ./gradlew --no-daemon :app:installDist -x test -x ktlintCheck

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S deadlines && adduser -S -G deadlines deadlines
WORKDIR /app
COPY --from=build /build/app/build/install/app ./
USER deadlines
EXPOSE 8080
ENTRYPOINT ["./bin/app"]

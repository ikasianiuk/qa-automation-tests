FROM debian:bookworm-slim

RUN apt-get update && apt-get install -y --no-install-recommends openjdk-17-jdk-headless chromium chromium-driver && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY gradlew *.gradle ./
COPY gradle gradle
RUN ./gradlew --no-daemon dependencies > /dev/null
COPY src src

ENV JAVA_TOOL_OPTIONS="-Dchromeoptions.args=--no-sandbox"

CMD ["./gradlew", "test"]
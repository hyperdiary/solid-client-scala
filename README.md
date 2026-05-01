# solid-client-scala

A Scala 3 client library and CLI for interacting with [Solid Pods](https://solidproject.org/) — personal linked data stores. Part of the [Hyperdiary](https://hyperdiary.io) project.

## What it does

`solid-client-scala` lets you authenticate with a Solid server, manage [LDP](https://www.w3.org/TR/ldp/) containers and resources, and bulk-load RDF data into a Pod. Key capabilities:

- **OAuth 2.0 + DPoP authentication** — obtains access tokens using Demonstration of Proof-of-Possession (DPoP) with ES256 JWT proofs
- **CRUD operations** — PUT, POST, GET, DELETE, PATCH, HEAD, and OPTIONS against Solid resources
- **RDF loading** — parses Turtle files (including hash-URI/fragment variants) and writes them to a Pod
- **SPARQL Update patching** — updates existing resources via SPARQL Update or N3 Patch
- **Multiple content types** — Turtle, JSON-LD, plain text, XML, and JPEG images
- **Container management** — creates LDP containers and generates resources with server-assigned URIs

## Building

Requires [sbt](https://www.scala-sbt.org/) and a JDK 11+.

```bash
# Compile and run tests
sbt test

# Build a self-contained fat JAR
sbt assembly
```

The assembled JAR is written to `target/scala-3.3.1/solid-client-scala.jar`.

## CLI usage

The JAR exposes a command-line Pod loader:

```bash
java -jar ./target/scala-3.3.1/solid-client-scala.jar \
  --file <path-to-turtle-file> \
  --pod <pod-url> \
  [--base <base-url>] \
  [--collection <collection-name>] \
  [--local <local-name>]
```

| Flag | Short | Required | Description |
|------|-------|----------|-------------|
| `--file` | `-f` | Yes | Path to the Turtle file to load |
| `--pod` | `-p` | Yes | Pod URL (no trailing slash) |
| `--base` | `-b` | No | Base URL for RDF subjects if different from the Pod URL |
| `--collection` | `-c` | No | Collection name when using hash/fragment URIs |
| `--local` | `-l` | No | Local name when using hash/fragment URIs |

### Local server note

When running a Solid server locally, the Pod URL you pass to the loader must exactly match the base URI used inside the Turtle file. For example, if resources look like:

```turtle
<http://pod.example.org/person/1> a foaf:Person .
```

then you must pass `--pod http://pod.example.org` — not `http://pod.localhost:3000`.

## Project structure

```
src/main/scala/org/hyperdiary/solid/
├── client/          # HTTP client, authenticator, JSON helpers
├── dpop/            # DPoP JWT proof generation (ES256)
├── model/           # Domain models: Credentials, Token, AccessToken, WebId, Label
├── pod/             # PodLoader business logic and PodLoaderApp CLI entry point
└── common/          # Shared exceptions
```

## Key dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| [STTP](https://sttp.softwaremill.com/) | 3.9.5 | HTTP client (OkHttp backend) |
| [Apache Jena](https://jena.apache.org/) | 4.10.0 | RDF parsing and SPARQL |
| [Circe](https://circe.github.io/circe/) | 0.14.7 | JSON parsing |
| [jose4j](https://bitbucket.org/b_c/jose4j/) | 0.9.5 | JWT / DPoP proof creation |
| [Decline](https://ben.schwartz.name/decline/) | 2.4.1 | CLI argument parsing |
| [PureConfig](https://pureconfig.github.io/) | 0.17.6 | Configuration file loading |
| [munit](https://scalameta.org/munit/) | 0.7.29 | Test framework |

## Development

```bash
sbt compile   # compile
sbt test      # run tests
sbt console   # Scala 3 REPL with project on classpath
```

Code style is enforced by [Scalafmt](https://scalameta.org/scalafmt/) — run `sbt scalafmtAll` or configure your editor to format on save using `.scalafmt.conf`.

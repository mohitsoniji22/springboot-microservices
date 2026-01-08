# Diagrams for springboot-microservices

Contents
- architecture.puml — PlantUML component/deployment diagram
- architecture.md — Mermaid variant for quick viewing on GitHub

Overview
This repo contains a small Spring Boot microservices example with the following components:

- cloud-gateway — API Gateway (Spring Cloud Gateway)
- service-registry — Eureka server for service discovery
- springboot-cloud-config-server — Centralized configuration server
- order-service, payment-service, security-service — Spring Boot microservices
- ELK stack (Elasticsearch, Logstash/Filebeat, Kibana) — centralized logging
- logs/ — local host volume where services write logs and Filebeat picks them up

Diagram highlights
- Services register with Eureka and discover peers via the registry.
- Each service fetches configuration from the Config Server at startup.
- cloud-gateway routes external HTTP requests to backend services.
- Order Service uses a LoadBalanced RestTemplate to call Payment Service.
- Logs are written to a host volume and collected by Filebeat/Logstash, then indexed in Elasticsearch and viewed in Kibana.
- Docker Compose orchestrates the services and ELK stack for local/dev runs.

How to render

- PlantUML (recommended for detailed diagrams):
  - Install Java and download plantuml.jar: https://plantuml.com/starting
  - Render PNG: 

```bash
# from repository root
cd diagrams
java -jar ~/path/to/plantuml.jar architecture.puml
```

  - Or use the PlantUML VSCode extension — open `architecture.puml` and use the preview.

- Mermaid (quick GitHub rendering):
  - GitHub will render `architecture.md` automatically in the browser.
  - Use the Mermaid Live Editor or VSCode Mermaid Preview extension for local preview.

Repository mapping
- `cloud-gateway/` => cloud-gateway component
- `service-registry/` => Eureka server
- `springboot-cloud-config-server/` => Config Server
- `order-service/`, `payment-service/`, `security-service/` => backend services
- `elk/` => Filebeat / Logstash configuration
- `docker-compose.yml` (root) and `elk/docker-compose.yml` => orchestration for services and ELK
- `logs/` => host log volume used by compose and Filebeat

Assumptions
- All services communicate over HTTP/REST.
- Services register with Eureka and fetch config from springboot-cloud-config-server.
- Order Service uses a LoadBalanced RestTemplate to call Payment Service (evidence: RestTemplate bean annotated with @LoadBalanced in `order-service`).
- Ports and exact Docker Compose service names are inferred from folder names; if you want port-level deployment details added, provide the compose files or ask me to extract them.

Next steps (optional)
- Add a sequence diagram for a typical request path (Client -> Gateway -> Order -> Payment).
- Extract exact port mappings and environment variables from compose files and annotate the diagrams with them.
- Generate Docker Compose-specific deployment view with container names and exposed ports.



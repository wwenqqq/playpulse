# PlayPulse

PlayPulse is a cloud-native game QA and player-feedback platform for the INFS3208 Type I Individual Project.

Players can register, submit bug reports / balance concerns / feature ideas, search community feedback, and vote for the issues that matter. The project uses Spring Boot, MySQL, Redis, Docker and Kubernetes. Authentication, feedback and interaction APIs run as independent service roles behind the Nginx gateway.

## Run locally

```powershell
# Build and start frontend, API, MySQL and Redis.
docker compose up --build

# Open http://localhost:8088
```

## Assessment demonstrations

```powershell
# Apply the cloud deployment after replacing image names and secret placeholders.
kubectl apply -f k8s/playpulse.yaml

# Demonstrate manual scale-out.
kubectl scale deployment feedback-service -n playpulse --replicas=4

# Demonstrate Kubernetes self-healing.
kubectl delete pod -n playpulse -l app=feedback-service

# Demonstrate a rolling update and rollback.
kubectl set image deployment/feedback-service feedback-service=REGISTRY/playpulse-api:1.0.1 -n playpulse
kubectl rollout status deployment/feedback-service -n playpulse
kubectl rollout undo deployment/feedback-service -n playpulse
```

## Validation

```powershell
.\mvnw.cmd test
```

Do not commit real cloud credentials or registry tokens. The Kubernetes MySQL workload is for an assessment demonstration only; a persistent or managed database should be used for a long-lived deployment.

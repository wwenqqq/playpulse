# GameShelf

GameShelf is a cloud-native game catalogue, review community and personal library built for the INFS3208 Type I Individual Project. Visitors can browse and search original game entries. Signed-in players can add a missing game, track titles as Wishlist, Playing or Completed, publish Review, Recommend or Critique posts, vote on useful posts and join discussions.

The implementation uses a plain HTML/CSS/JavaScript frontend, an Nginx gateway, Spring Boot service roles, MySQL and Redis. Docker Compose runs the complete stack locally. Kubernetes Deployments, Services, health probes, replicas, a Horizontal Pod Autoscaler, a persistent volume and a public LoadBalancer provide the cloud demonstration path.

> The repository and several internal image/database identifiers retain the historical `playpulse` name so the tested deployment path and GitHub URL remain stable. The product shown to users is GameShelf.

## Architecture

```text
Browser
  -> Nginx frontend gateway / public LoadBalancer
     -> Auth service (registration, sign-in, JWT)
     -> Feedback service (catalogue, library, reviews)
     -> Interaction service (votes, comments)
        -> MySQL persistent volume
        -> Redis
```

The three Spring Boot containers use the same build artifact but expose separate service roles through `APP_ROLE`. This keeps deployment and routing independent while sharing a small codebase suitable for an individual project.

## Run locally with Docker

Requirements: Docker Desktop with Docker Compose.

```powershell
docker compose up --build
```

Open `http://localhost:8088`. To stop the stack without deleting the database volume:

```powershell
docker compose down
```

## Run backend tests

```powershell
.\mvnw.cmd test
```

The frontend JavaScript can be checked with Node.js:

```powershell
node --check frontend/app.js
```

## Deploy to Google Kubernetes Engine

The deployment script builds the API and frontend with Cloud Build, pushes both images to Artifact Registry, optionally creates a one-node zonal GKE Standard cluster, creates runtime secrets and applies the Kubernetes manifest. It creates billable resources, so run it only when education credit and the selected project are confirmed.

```bash
bash k8s/deploy-gke.sh --create-cluster YOUR_PROJECT_ID australia-southeast1-a
```

Wait for the frontend service to receive an external IP:

```bash
kubectl get service frontend -n playpulse --watch
```

The default demonstration configuration uses one `e2-standard-4` node and a 10 GiB MySQL persistent volume. Delete the cluster after marking to stop compute charges:

```bash
gcloud container clusters delete playpulse-cluster --zone australia-southeast1-a
```

## Four-minute demonstration commands

Each command below includes its purpose so it can be submitted or narrated directly.

```bash
# Show deployments, desired/ready replicas, Pods, Services, HPA and persistent storage.
kubectl get deployments,pods,services,hpa,pvc -n playpulse -o wide

# Show that the feedback service starts with multiple replicas.
kubectl get deployment feedback-service -n playpulse

# Watch HPA targets and replica changes during generated traffic.
kubectl get hpa feedback-service -n playpulse --watch

# Show the public load balancer and its external IP.
kubectl get service frontend -n playpulse -o wide

# Show self-healing: delete one Pod, then watch Kubernetes create its replacement.
kubectl delete pod -n playpulse -l app=feedback-service --wait=false
kubectl get pods -n playpulse -l app=feedback-service --watch

# Deploy a new immutable image tag and wait for a zero-downtime rolling update.
kubectl set image deployment/feedback-service feedback-service=IMAGE_REPOSITORY/playpulse-api:NEW_TAG -n playpulse
kubectl rollout status deployment/feedback-service -n playpulse --timeout=5m

# Show rollout revisions, then roll back and verify the restored revision.
kubectl rollout history deployment/feedback-service -n playpulse
kubectl rollout undo deployment/feedback-service -n playpulse
kubectl rollout status deployment/feedback-service -n playpulse --timeout=5m

# Prove that MySQL is bound to persistent storage.
kubectl describe pvc mysql-data -n playpulse
```

For an HPA demonstration, generate sustained requests from a temporary Pod while watching the HPA in a second terminal:

```bash
kubectl run load-generator -n playpulse --rm -it --restart=Never --image=busybox:1.36 -- /bin/sh -c "while true; do wget -q -O- http://feedback-service:8080/api/games >/dev/null; done"
```

## Security and submission hygiene

- Runtime passwords and JWT secrets are created during deployment and are not committed.
- Replace all demonstration secrets before any public or long-running deployment.
- Do not commit `evidence/`, generated screenshots, build output or local environment files.
- The in-cluster MySQL deployment is suitable for this assessment demonstration; a production system should use a managed database and secret manager.

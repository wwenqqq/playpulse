# PlayPulse

PlayPulse is a cloud-native game QA and player-feedback platform for the INFS3208 Type I Individual Project. Players can register, submit bug reports, balance concerns and feature ideas, search community feedback, vote, comment, and review their own submissions.

The system uses Spring Boot, MySQL, Redis, Docker and Kubernetes. Authentication, feedback and interaction run as separate service roles behind an Nginx frontend gateway.

## Run locally

```powershell
docker compose up --build
```

Open `http://localhost:8088`.

## Deploy to Google Kubernetes Engine

The deployment script builds the API and frontend with Cloud Build, uploads them to Artifact Registry, creates a one-node GKE Standard cluster only when it does not already exist, generates Kubernetes secrets, and deploys the application. It does not commit credentials or passwords to Git.

1. Open Google Cloud Shell and clone this repository.
2. Find the GCP project ID with `gcloud projects list`.
3. From the repository root, run the following command. It creates billable resources, so only run it after confirming that the education credit is active.

```bash
bash k8s/deploy-gke.sh --create-cluster YOUR_PROJECT_ID australia-southeast1-a
```

4. Wait for the final `kubectl get service frontend -n playpulse` output to show an `EXTERNAL-IP`, then open that IP in a browser.

The script uses the `australia-southeast1-a` zone and an `e2-standard-4` node. The MySQL database has a 10 GiB persistent disk. When the demo is complete, delete the cluster to stop compute charges:

```bash
gcloud container clusters delete playpulse-cluster --zone australia-southeast1-a
```

## Demonstrate cloud capabilities

```bash
# Scale the feedback service.
kubectl scale deployment feedback-service -n playpulse --replicas=4

# Show self-healing: Kubernetes creates a replacement pod.
kubectl delete pod -n playpulse -l app=feedback-service

# Show rolling update and rollback after deploying a new image tag.
kubectl rollout history deployment/feedback-service -n playpulse
kubectl rollout undo deployment/feedback-service -n playpulse
```

## Validate the backend

```powershell
.\mvnw.cmd test
```

Do not commit cloud credentials, coupon codes or generated secrets. The in-cluster MySQL deployment is appropriate for this assessment demonstration; a production system should use a managed database.

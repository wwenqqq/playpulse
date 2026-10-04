#!/usr/bin/env bash
set -euo pipefail

# This script is intentionally opt-in because creating a GKE cluster consumes GCP credit.
if [[ "${1:-}" != "--create-cluster" || -z "${2:-}" ]]; then
  echo "Usage: bash k8s/deploy-gke.sh --create-cluster PROJECT_ID [ZONE]"
  echo "Example: bash k8s/deploy-gke.sh --create-cluster my-gcp-project australia-southeast1-a"
  exit 1
fi

PROJECT_ID="$2"
ZONE="${3:-australia-southeast1-a}"
REGION="${ZONE%-*}"
CLUSTER_NAME="playpulse-cluster"
REPOSITORY="playpulse"
IMAGE_TAG="$(git rev-parse --short HEAD)"
IMAGE_REPOSITORY="${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPOSITORY}"

gcloud config set project "$PROJECT_ID"
gcloud services enable container.googleapis.com artifactregistry.googleapis.com cloudbuild.googleapis.com

if ! gcloud artifacts repositories describe "$REPOSITORY" --location "$REGION" >/dev/null 2>&1; then
  gcloud artifacts repositories create "$REPOSITORY" \
    --repository-format=docker \
    --location="$REGION" \
    --description="PlayPulse container images"
fi

gcloud builds submit --tag "${IMAGE_REPOSITORY}/playpulse-api:${IMAGE_TAG}" .
gcloud builds submit --tag "${IMAGE_REPOSITORY}/playpulse-frontend:${IMAGE_TAG}" ./frontend

if ! gcloud container clusters describe "$CLUSTER_NAME" --zone "$ZONE" >/dev/null 2>&1; then
  gcloud container clusters create "$CLUSTER_NAME" \
    --zone "$ZONE" \
    --machine-type=e2-standard-4 \
    --disk-size=20 \
    --num-nodes=1 \
    --enable-ip-alias \
    --release-channel=regular
fi

gcloud container clusters get-credentials "$CLUSTER_NAME" --zone "$ZONE"
kubectl create namespace playpulse --dry-run=client -o yaml | kubectl apply -f -

# Preserve the existing secret on redeploy so the persisted MySQL credentials stay valid.
if ! kubectl get secret playpulse-secrets -n playpulse >/dev/null 2>&1; then
  kubectl create secret generic playpulse-secrets -n playpulse \
    --from-literal=DB_PASSWORD="$(openssl rand -hex 18)" \
    --from-literal=MYSQL_ROOT_PASSWORD="$(openssl rand -hex 24)" \
    --from-literal=JWT_SECRET="$(openssl rand -hex 32)"
fi

sed \
  -e "s|__IMAGE_REPOSITORY__|${IMAGE_REPOSITORY}|g" \
  -e "s|__IMAGE_TAG__|${IMAGE_TAG}|g" \
  k8s/playpulse.yaml | kubectl apply -f -

kubectl rollout status deployment/mysql -n playpulse --timeout=5m
kubectl rollout status deployment/auth-service -n playpulse --timeout=5m
kubectl rollout status deployment/feedback-service -n playpulse --timeout=5m
kubectl rollout status deployment/interaction-service -n playpulse --timeout=5m
kubectl rollout status deployment/frontend -n playpulse --timeout=5m

echo
echo "Deployment complete. Wait for an EXTERNAL-IP, then open it in a browser:"
kubectl get service frontend -n playpulse
echo
echo "When the demonstration is finished, delete the cluster to stop compute charges:"
echo "gcloud container clusters delete ${CLUSTER_NAME} --zone ${ZONE}"

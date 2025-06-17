# --- deploy-con-load.ps1 ---
# Questo script automatizza il flusso di lavoro manuale che usa 'docker build' e 'minikube image load'.

# Impostazione per interrompere lo script in caso di errore in qualsiasi comando
$ErrorActionPreference = "Stop"

# Funzione per stampare messaggi colorati
function Write-Step {
    param(
        [string]$Message
    )
    Write-Host ""
    Write-Host "----------------------------------------------------" -ForegroundColor Cyan
    Write-Host $Message -ForegroundColor Cyan
    Write-Host "----------------------------------------------------"
}

# --- INIZIO DELLO SCRIPT ---
Write-Step "FASE 1: PULIZIA DELLE RISORSE ESISTENTI"

# Cancelliamo i Deployment su Kubernetes.
# '--ignore-not-found=true' evita errori se il deployment non esiste (es. alla prima esecuzione).
Write-Host "Cancellazione dei vecchi Deployment..."
kubectl delete deployment ordini-service -n microservizi-app --ignore-not-found=true
kubectl delete deployment catalogo-service -n microservizi-app --ignore-not-found=true

# Cancelliamo le immagini da Minikube per forzare il ricaricamento.
# '-ErrorAction SilentlyContinue' ignora l'errore se l'immagine non è presente.
Write-Host "Cancellazione delle vecchie immagini da Minikube..."
minikube image rm docker.io/library/ordini-service:latest
minikube image rm docker.io/library/catalogo-service:latest

# Cancelliamo anche le immagini locali per sicurezza
docker rmi docker.io/library/ordini-service:latest
docker rmi docker.io/library/catalogo-service:latest


Write-Step "FASE 2: BUILD DEI MICROSERVIZI (JAR E IMMAGINI DOCKER)"

# --- ordini-service ---
Write-Host "Costruendo ordini-service..."
./gradlew :ordini:clean :ordini:build
docker build --no-cache -t docker.io/library/ordini-service:latest -f ordini/src/main/docker/Dockerfile.jvm ordini
minikube image load docker.io/library/ordini-service:latest

# --- catalogo-service ---
Write-Host "Costruendo catalogo-service..."
./gradlew :catalogo:clean :catalogo:build
docker build --no-cache -t docker.io/library/catalogo-service:latest -f catalogo/src/main/docker/Dockerfile.jvm catalogo
minikube image load docker.io/library/catalogo-service:latest


Write-Step "FASE 3: DEPLOY DI TUTTE LE RISORSE SU KUBERNETES"

# Applichiamo nuovamente tutte le configurazioni. Questo ricreerà i Deployment cancellati.
kubectl apply -f k8s/


Write-Step "FASE 4: ATTESA CHE I NUOVI POD SIANO PRONTI"

# Attendiamo che i nuovi pod, creati con le nuove immagini, siano nello stato 'Running'.
kubectl wait --for=condition=ready pod -l app=ordini-service -n microservizi-app --timeout=180s
kubectl wait --for=condition=ready pod -l app=catalogo-service -n microservizi-app --timeout=180s


Write-Step "PROCESSO COMPLETATO CON SUCCESSO!"
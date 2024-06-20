# Project 57

Point of Failures in System

[https://gitorko.github.io/points-of-failure/](https://gitorko.github.io/points-of-failure/)

### Version

Check version

```bash
$java --version
openjdk 21.0.3 2024-04-16 LTS
```

### Postgres DB

```bash
docker run -p 5432:5432 --name pg-container -e POSTGRES_PASSWORD=password -d postgres:14
docker ps
docker exec -it pg-container psql -U postgres -W postgres
CREATE USER test WITH PASSWORD 'test@123';
CREATE DATABASE "test-db" WITH OWNER "test" ENCODING UTF8 TEMPLATE template0;
grant all PRIVILEGES ON DATABASE "test-db" to test;

docker stop pg-container
docker start pg-container
```

### Dev

To run the backend in dev mode.

```bash
./gradlew clean build
./gradlew bootRun

```

Command to check port on Mac

```bash
lsof -i tcp:8080
```

### Kubernetes

Stop any existing postgres db

```bash
docker stop pg-container
brew services stop postgresql@14
```

```bash
kubectl config use-context docker-desktop

mkdir /tmp/data
 
./gradlew clean build
docker build -f k8s/Dockerfile --force-rm -t project57:1.0.0 .
kubectl apply -f k8s/deployment.yaml
kubectl get pods -w

kubectl logs -f service/project57-service

kubectl delete -f k8s/deployment.yaml
```


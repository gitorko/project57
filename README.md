# Project 57

Point of Failures in System

[https://gitorko.github.io/points-of-failure/](https://gitorko.github.io/points-of-failure/)

### Version

Check version

```bash
$java --version
openjdk 17.0.3 2022-04-19 LTS
```

### Postgres DB

```bash
docker run -p 5432:5432 --name pg-container -e POSTGRES_PASSWORD=password -d postgres:9.6.10
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

### Kubernetes

```bash
docker stop pg-container

./gradlew clean build
docker build -f k8s/Dockerfile --force-rm -t project57:1.0.0 .
kubectl apply -f k8s/Postgres.yaml
kubectl apply -f k8s/Deployment.yaml
kubectl get pods -w

kubectl logs -f service/project57-service

kubectl delete -f k8s/Postgres.yaml
kubectl delete -f k8s/Deployment.yaml
```

# Project 57

Distributed System Essentials

[https://gitorko.github.io/distributed-system-essentials/](https://gitorko.github.io/distributed-system-essentials/)

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

To build a small docker image

```bash
docker build -f k8s/Dockerfile-Small --force-rm -t project57:1.0.0 . 
docker run -d -p 8080:8080 -e POSTGRES_HOST="10.177.140.150" -e POSTGRES_DB="test-db" -e POSTGRES_USER="test" -e POSTGRES_PASSWORD="test@123" project57:1.0.0
```

### Swagger

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
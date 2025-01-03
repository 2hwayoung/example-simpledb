# simpleDb-review

## Environment
- Docker: v24.0.7
- Docker-Compose: v2.23.3

## Running Local MySQL Server
### Start Docker Container
Start the containers using Docker Compose
```bash
docker-compose up -d
# -d : background mode
```
**Note**: The first time you run this command, it may take some time because Docker will need to:
1. **Download the necessary images** from Docker Hub if they are not already available on your local machine.
2. **Build the images** from the Dockerfile if it is defined in the docker-compose.yml file.

### Additional Commands
To Stop and remove container
```bash
docker-compose down
```
View container logs
```bash
docker-compose logs mysql
```
Restart the container
```bash
docker-compose start
```
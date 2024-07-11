#To run local runtime tests using using aws runtime simulator:
1. Gradlew build ./gradlew build to build lambda executable
2. Modify docker file to set proper path to lambda function executable
3. Run docker build -t sample:latest . 
4. Start server docker run -p 9000:8080 sample:latest
5. Execute function via curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{}'
6. docker ps; docker stop CONTAINER_ID to stop the execution
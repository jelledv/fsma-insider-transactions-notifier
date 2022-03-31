## Testing the image locally 
1) build the latest version of the code
```
mvn package
```
2) build the Docker image
```
docker build -t fsma-notifier .
```
3) Run the image
```
docker run -p 9000:8080 fsma-notifier
```
4) Send a test EventBridge / Cloudwatch scheduled event to the running container
```
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{"id":"cdc73f9d-aea9-11e3-9d5a-835b769c0d9c","detail-type":"Scheduled Event","source":"aws.events","account":"123456789012","time":"1970-01-01T00:00:00Z","region":"us-east-1","resources":["arn:aws:events:us-east-1:123456789012:rule/ExampleRule"],"detail":{}}'
```
Example event (pretty format)
```
{
  "id": "cdc73f9d-aea9-11e3-9d5a-835b769c0d9c",
  "detail-type": "Scheduled Event",
  "source": "aws.events",
  "account": "123456789012",
  "time": "1970-01-01T00:00:00Z",
  "region": "us-east-1",
  "resources": [
    "arn:aws:events:us-east-1:123456789012:rule/ExampleRule"
  ],
  "detail": {}
}
```

## Serverless commands
Deploy the service via CloudFormation (run this command when you made infrastructure changes)
```
serverless deploy
```
Deploy new code changes and quickly update your Lambda
```
mvn install
serverless deploy function -f fsma-notifier
```
Remove the deployed service from the Cloud provider
```
serverless remove
```




# akka-http-swagger-sample

An Sample application [Scala](scala-lang.org) that leverages  and [Akka](akka.io) 
[swagger-akka-http](https://github.com/swagger-akka-http/swagger-akka-http) which is built using [swagger.io](http://swagger.io/) libs.

An Sample application that leverages [Akka-http](akka.io) and [Spray](spray.io) to expose Rest API. 
Description is exposed as Swagger Json page.
 
 
### Running
Clone to your computer and run via sbt:

```
sbt run
```


## Option push to ECS
```
sbt clean compile docker:publishLocal
docker tag sample-akka-http-swagger:latest 677537359471.dkr.ecr.eu-west-1.amazonaws.com/sample-akka-http-swagger:latest 
$(aws ecr get-login --no-include-email --region eu-west-1 --profile=newsbridge)  
docker push 677537359471.dkr.ecr.eu-west-1.amazonaws.com/sample-akka-http-swagger:latest
```


## Option create docker instance locally
```
$(aws ecr get-login --no-include-email --region eu-west-1)
docker run --name sample-akka-http-swagger -p 8080:8080 677537359471.dkr.ecr.eu-west-1.amazonaws.com/sample-akka-http-swagger:latest
curl http://0.0.0.0:8080/hello/fred
```


### License
This library is licensed under the Apache License, Version 2.0.

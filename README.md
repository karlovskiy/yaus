# yaus
Yet Another URL Shortener

## Build
Build `yaus` docker image with command:
```shell script
docker build -t yaus .
```

## Run
Run `yaus` with [docker compose](https://docs.docker.com/compose/install/) or as standalone docker images.

### Docker compose
You can run `yaus` with command:
```shell script
docker-compose up -d
```

### Standalone
Also, you can run standalone docker images without docker-compose.
First, create `yausnet` network and later run `yausredis` and `yaus` containers manually:
```shell script
# Create a new user-defined network
docker network create yausnet

# Run a redis container
docker run --name yausredis --network yausnet -d redis

# Run a yaus container
docker run --name yaus --network yausnet -d -p 8080:8080 yaus
```
Default configuration file located in container at `/etc/yaus/application.properties`.\
You can override it with `-v` docker volume bind, for example you can run from project root directory like this:
```shell script
docker run --name yaus --network yausnet -v $(pwd)/application.properties:/etc/yaus/application.properties -d -p 8080:8080 yaus
```

## Use
After [build](#build) and [run](#run) `yaus` should be ready to use [http://localhost:8080/](http://localhost:8080/)

## Monitoring and metrics
As a Spring Boot Application, `yaus` can use all Spring Boot Actuator features.
By default, application info, heath and metrics enabled.  

### Application info
```shell script
$ curl 'http://localhost:8080/actuator/info' -i -X GET
```

```http request
HTTP/1.1 200 
Content-Type: application/vnd.spring-boot.actuator.v3+json
Transfer-Encoding: chunked
Date: Tue, 10 Mar 2020 19:11:29 GMT

{
  "git": {
    "commit": {
      "time": "2020-03-09T19:49:10Z",
      "id": "d3c3c79"
    },
    "branch": "master"
  }
}
```

### Health
```shell script
$ curl 'http://localhost:8080/actuator/health' -i -X GET \
    -H 'Accept: application/json'
```

```http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Tue, 10 Mar 2020 19:15:00 GMT

{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 28630495232,
        "free": 13478739968,
        "threshold": 10485760
      }
    },
    "ping": {
      "status": "UP"
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "5.0.7"
      }
    }
  }
}
```

### Metrics
`yaus` provide some useful metrics. 

####  Request timers
- `yaus.page.home.timer` - Home page requests timer
- `yaus.page.shorten.timer` - Shorten requests timer
- `yaus.page.forward.timer` - Forward requests timer

For example, for `yaus.page.forward.timer`:
```shell script
$ curl 'http://localhost:8080/actuator/metrics/yaus.page.forward.timer' -i -X GET \
    -H 'Accept: application/json'
```
```http request
HTTP/1.1 200 
Content-Disposition: inline;filename=f.txt
Content-Type: application/vnd.spring-boot.actuator.v3+json
Transfer-Encoding: chunked
Date: Tue, 10 Mar 2020 19:33:43 GMT

{
  "name": "yaus.page.forward.timer",
  "description": null,
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1.0
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 0.01134161
    },
    {
      "statistic": "MAX",
      "value": 0.01134161
    }
  ],
  "availableTags": [
    {
      "tag": "exception",
      "values": [
        "None"
      ]
    },
    {
      "tag": "method",
      "values": [
        "GET"
      ]
    },
    {
      "tag": "uri",
      "values": [
        "/f/{shortKey}"
      ]
    },
    {
      "tag": "outcome",
      "values": [
        "REDIRECTION"
      ]
    },
    {
      "tag": "status",
      "values": [
        "303"
      ]
    }
  ]
}
```

#### Redis operations timers
- `yaus.redis.increment.timer` - Unique id increment operation timer
- `yaus.redis.save.timer` - Save original URL by generated id operation timer
- `yaus.redis.load.timer` - Load original URL by short key operation timer

For example, for `yaus.redis.increment.timer`:
```shell script
$ curl 'http://localhost:8080/actuator/metrics/yaus.redis.increment.timer' -i -X GET
```
```http request
Content-Disposition: inline;filename=f.txt
Content-Type: application/vnd.spring-boot.actuator.v3+json
Transfer-Encoding: chunked
Date: Tue, 10 Mar 2020 19:44:38 GMT

{
  "name": "yaus.redis.increment.timer",
  "description": null,
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1.0
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 0.018702971
    },
    {
      "statistic": "MAX",
      "value": 0.0
    }
  ],
  "availableTags": []
}
```

#### Distribution of original URLs sizes
```shell script
$ curl 'http://localhost:8080/actuator/metrics/yaus.url.size' -i -X GET
```

```http request
HTTP/1.1 200 
Content-Disposition: inline;filename=f.txt
Content-Type: application/vnd.spring-boot.actuator.v3+json
Transfer-Encoding: chunked
Date: Tue, 10 Mar 2020 19:59:06 GMT

{
  "name": "yaus.url.size",
  "description": null,
  "baseUnit": "symbols",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 2.0
    },
    {
      "statistic": "TOTAL",
      "value": 74.0
    },
    {
      "statistic": "MAX",
      "value": 0.0
    }
  ],
  "availableTags": []
}
```

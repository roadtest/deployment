# Integrated Customer Environment Management Service - ICEMAN

**MANAGE FUSION CUSTOMER ENVIRONMENT**

[![Build Status](https://jenkins.cloud.autodesk.com/master2/buildStatus/icon?job=OneFusion/ICEMAN/iceman-core/master)](https://jenkins.cloud.autodesk.com/master2/job/OneFusion/job/ICEMAN/job/iceman-core/job/master/)


## Building
```bash
$ cd <project-dir>
$ ./gradlew [clean] build
```
## Run
```bash
$ cd <project-dir>
$ ./gradlew bootRun
```
## Docker
### Build local image
```bash
$ ./gradlew build buildImage
```
### Pull image from [Artifactory](https://art-bobcat.autodesk.com/artifactory/webapp/#/artifacts/browse/tree/General/autodesk-docker-build-images/iceman/latest) 
```bash
$ docker pull autodesk-docker-build-images.art-bobcat.autodesk.com:10873/iceman
```
### Pull image from [Quay](https://quay.io/repository/autodeskcloud/ctr-iceman)
```bash
$ docker pull quay.io/autodeskcloud/ctr-iceman
```

## Endpoints
- [Spring Boot Actuator](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints)
  features `http://<host>:<port>/system/<feature>`
- Jersey generated WADL `http://<host>:<port>/application.wadl[?detail=true]`

## Test
```bash
$ cd <project-dir>
$ ./gradlew [clean cleanTest] <test | integrationTest --continue> [jacocoTestReport]
```
The reports of unit test, integration and JaCoCo coverage are available <project-dir>/build/reports

## Environments
- [Dev](http://iceman-dev.flc.autodesk.com/) - [_health check_](http://iceman-dev.flc.autodesk.com/system/health)
- [Staging](http://iceman-stg.flc.autodesk.com/) - [_health check_](http://iceman-stg.flc.autodesk.com/system/health)
- [Prod](http://iceman.flc.autodesk.com/) - [_health check_](http://iceman.flc.autodesk.com/system/health)


## Environment configuration

### Application mandatory configuration
- `spring_application_client_id` ICEMAN client id
- `spring_application_client_secret` ICEMAN client secret
- `adsk_forge_base_url` Autodesk Forge API base URL

### Database configuration
- todo

### Outstanding works
[Outstanding work](TODO.md)

## Introduction
This project is an example of a HTTP API consuming and producing CSV files with Spring Boot.

## Run the project
### Database
Install postgres locally or run it through docker with :
```
docker run -p 5432:5432 -e POSTGRES_DB=movie_db -e POSTGRES_USER=user -e POSTGRES_PASSWORD=user postgres
```

### Application
Once the postgres database has started, you can launch the Spring Boot project and try it out.

Get all movies as CSV
```
curl --request GET \
  --url http://localhost:8090/csv-api-example/movies
```

Create movies from CSV
```
curl --request POST \
  --url http://localhost:8090/csv-api-example/movies \
  --header 'content-type: text/csv' \
  --data 'id,title,release_date
,"Some movie",2022-02-22
,"Some other movie",2022-02-23
,"Some third movie",2018-12-14
'
```

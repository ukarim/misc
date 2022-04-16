# ruby_app

Explore ruby/sinatra as a tech stack for fast prototyping and small apps.

### Things to check

[ ] DB access (postgres, mysql, maybe oracle)
[ ] DB connection pooling
[ ] http client
[x] docker image creation
[ ] logger with custom format (and MDC)
...

### Run locally

```
bundle config set --local path 'vendor/bundle'

bundle install

bundle exec rackup
```

### Docker build & run

```
docker build -t ruby_app_test .
docker run -p 9292:9292 -m 50m ruby_app_test
```

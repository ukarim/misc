FROM ruby:3.1.2

WORKDIR /usr/src/app

COPY . .

# throw errors if Gemfile has been modified since Gemfile.lock

RUN bundle config --global frozen 1 && \
    bundle install

ENV RACK_ENV production

CMD ["bundle", "exec", "rackup", "--host", "0.0.0.0"]

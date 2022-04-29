require "net/http"
require "json"
require "uri"

require "sinatra"
require "sinatra/json"


get "/" do
    json :message => "not implemented yet"
end

post "/http-client-test" do
    # send req to /mock endpoint
    uri = URI("http://localhost:9292/mock")
    req_body = { "message" => "Test msg OK" }.to_json
    headers = { "Content-Type" => "application/json" }
    resp = Net::HTTP.post(uri, req_body, headers)

    # return response as is
    json resp.body
end

post "/mock" do
    msg = nil
    req_body = request.body.read
    unless req_body.nil? || req_body.empty?
        msg = JSON.parse(req_body)["message"]
    end
    resp = { :code => "OK", :id => 12421, :message => msg }
    json resp
end

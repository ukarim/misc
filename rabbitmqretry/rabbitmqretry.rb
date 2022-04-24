require "bunny"
require "json"

conn = Bunny.new("amqp://test:test@localhost:5672")
conn.start

ch = conn.create_channel
ch.confirm_select

# create main queue
main_queue = ch.queue("main_queue")

# declare delayed retry queue
# set dead letter exchange to default exchange and messages ttl to 10s
# all expired messages will be delivered to default exchange
retry_queue_args = {"x-message-ttl" => 10000, "x-dead-letter-exchange" => ""}
retry_queue = ch.queue("retry_queue", :arguments => retry_queue_args)

# create special retry_exchange with fanout type and bind it to retry_queue
retry_exchange = ch.fanout("retry_exchange")
retry_queue.bind(retry_exchange)

# send test message
test_msg = JSON.generate({"msg" => "Hello World!", "retry_count": 0})
main_queue.publish(test_msg)

blocker = Queue.new

# consume test message
# if retry count less than 3, then send it to retry exchange
main_queue.subscribe(manual_ack: false) do |delivery_info, metadata, payload|
  test_msg = JSON.parse(payload)
  retry_count = test_msg["retry_count"]
  puts "[x] received message: #{test_msg["msg"]}. Retry count: #{retry_count}"
  if retry_count > 2
    puts "[x] max retry count reached"
    puts "[x] closing connection to rabbitmq"
    blocker.push("stop") # unblock main thread
  else
    test_msg["retry_count"] = retry_count + 1
    # send to retry_exchange
    # message will be routed to the retry_queue but original routing key will not be changed
    # so, after expiration the message will be routed to default exchange with routing key equal to main_queue
    retry_exchange.publish(JSON.generate(test_msg), :routing_key => "main_queue")
    puts "[x] message was sent to retry queue. wait 10s"
  end
end

# block and wait
blocker.pop

ch.close
conn.close

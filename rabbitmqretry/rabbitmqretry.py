import pika
import json

def main():
    connection = None
    try:
        # connect to rabbitmq
        creds = pika.PlainCredentials('test', 'test')
        conn_params = pika.ConnectionParameters(host='localhost', credentials=creds)
        connection = pika.BlockingConnection(conn_params)
        channel = connection.channel()

        # create main queue
        channel.queue_declare(queue = 'main_queue')

        # declare delayed retry queue
        # set dead letter exchange to default exchange and messages ttl to 10s
        # all expired messages will be delivered to default exchange
        retry_queue_params = {'x-dead-letter-exchange': '', 'x-message-ttl': 10000}
        channel.queue_declare(queue='retry_queue', arguments=retry_queue_params)

        # create special retry_exchange with fanout type and bind it to retry_queue
        channel.exchange_declare(exchange='retry_exchange', exchange_type='fanout')
        channel.queue_bind(queue='retry_queue', exchange='retry_exchange')

        # send test message
        msg = {'msg': 'Hello World', 'retry_count': 0}
        channel.basic_publish(exchange='', routing_key='main_queue', body=json.dumps(msg))

        # consume test message
        # see callback function
        channel.basic_consume(queue='main_queue', auto_ack=True, on_message_callback=callback)
        channel.start_consuming()

    except KeyboardInterrupt:
        print("Interrupted")
    finally:
        if connection != None:
            connection.close()


def callback(ch, method, properties, body):
    msg = json.loads(body)
    print("[x] message received '%s'"%(msg))
    retry_count = msg['retry_count']
    if retry_count > 2:
        print('[x] max retry count reached.')
        print('[x] closing connection to rabbitmq')
        ch.close()
    else:
        print('[x] message will be retried after 10s')
        msg['retry_count'] = retry_count + 1
        # send to retry_exchange
        # message will be routed to the retry_queue but original routing key will not be changed
        # so, after expiration the message will be routed to default exchange with routing key equal to main_queue
        ch.basic_publish(exchange='retry_exchange', routing_key='main_queue', body=json.dumps(msg))


if __name__ == '__main__':
    main()


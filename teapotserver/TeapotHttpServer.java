import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeapotHttpServer {

    private static final String TEAPOT_HTTP_RESP_STR =
            "HTTP/1.1 418 I'm a teapot\n" +
            "Content-Length: 130\n" +
            "Content-Type: text/html\n" +
            "Server: java-nio\n\n" +
            "<!doctype html><html><head><meta charset=\"utf-8\"><title>418 I'm teapot</title></head><body><h1>418 I'm a teapot</h1></body></html>";

    private static final byte[] TEAPOT_HTTP_RESP_BYTES = TEAPOT_HTTP_RESP_STR.getBytes(StandardCharsets.UTF_8);

    private static final String HOSTNAME = "localhost";
    private static final int HTTP_PORT = 8080;
    private static final Logger LOGGER = Logger.getLogger("TeapotHttpServer");

    public static void main(String[] args) throws IOException {
        var isShuttingDown = new AtomicBoolean(false);
        addShutdownHook(isShuttingDown);

        try (var serverSocketChannel = ServerSocketChannel.open();
             var selector = Selector.open())
        {

            serverSocketChannel.configureBlocking(false); // use non-blocking io

            // listen for ACCEPT events on server's socket channel
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            serverSocketChannel.bind(new InetSocketAddress(HOSTNAME, HTTP_PORT));
            LOGGER.info("Listen on http://" + HOSTNAME + ":" + HTTP_PORT);

            while (!isShuttingDown.get()) {
                try {
                    if (selector.select() < 1) { // selector.select() method is blocking method (waits for new events)
                        // no new events
                        // for examples in case of selector.wakeup() invocation
                        continue;
                    }
                    var selectedKeysIterator = selector.selectedKeys().iterator();
                    while (selectedKeysIterator.hasNext()) {
                        var selectionKey = selectedKeysIterator.next();
                        selectedKeysIterator.remove(); // do not forget to remove

                        if (selectionKey.isAcceptable()) {
                            // handle ACCEPT event on server's socket channel
                            handleAccept(selectionKey, selector);
                        } else if (selectionKey.isReadable()) {
                            // handle READ event on client's socket channel
                            handleRead(selectionKey);
                        } else if (selectionKey.isWritable()) {
                            // handle WRITE event on client's socket channel
                            handleWrite(selectionKey);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error in server loop", e);
                }
            }
        }
    }

    private static void addShutdownHook(AtomicBoolean isShuttingDown) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> isShuttingDown.set(true)));
    }

    private static void handleAccept(SelectionKey selectionKey, Selector selector) {
        try {
            SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
            if (socketChannel == null) {
                return;
            }
            socketChannel.configureBlocking(false); // use non-blocking io for client's channel

            // listen for READ events
            socketChannel.register(selector, SelectionKey.OP_READ, new ChannelAttachment());

            LOGGER.info("Connection from " + socketChannel.getRemoteAddress() + " accepted");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in `handleAccept`", e);
        }
    }

    private static void handleRead(SelectionKey selectionKey) {
        try {
            var socketChannel = (SocketChannel) selectionKey.channel();
            var remoteAddress = socketChannel.getRemoteAddress();
            var channelAttachment = (ChannelAttachment) selectionKey.attachment();
            var httpMessageParser = (HttpMessageParser) channelAttachment.getMessageParser();

            var reqByteBuf = ByteBuffer.allocate(256);
            // Read request body from client's channel
            int read;
            while ((read = socketChannel.read(reqByteBuf)) > 0) {
                httpMessageParser.consume(reqByteBuf);
            }

            if (read == -1) {
                // seems we need to close channel
                LOGGER.info("Got CLOSE from " + socketChannel.getRemoteAddress());
                httpMessageParser.clearState();
                selectionKey.attach(null);
                selectionKey.cancel();
                selectionKey.channel().close();
                return;
            }

            if (!httpMessageParser.isFullyRead()) {
                // http message was not fully read
                // wait for next read cycle
                return;
            }

            var method = httpMessageParser.getMethod();
            var url = httpMessageParser.getUrl();

            LOGGER.info("Got request from " + remoteAddress + ". Method: " + method + ", url: " + url);
            httpMessageParser.clearState(); // do not forget to clear state if message was fully read

            var respByteBuf = ByteBuffer.wrap(TEAPOT_HTTP_RESP_BYTES);
            int remaining = respByteBuf.remaining();
            int write = socketChannel.write(respByteBuf);
            if (write < remaining) {
                channelAttachment.setRespByteBuffer(respByteBuf);
                // register also for WRITE events
                // we will write remaining bytes when socket will be ready for writing
                selectionKey.interestOpsOr(SelectionKey.OP_WRITE);
            } else {
                LOGGER.info("Teapot http response has been sent to " + remoteAddress);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in `handleRead`", e);
        }
    }

    private static void handleWrite(SelectionKey selectionKey) {
        try {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            var remoteAddress = socketChannel.getRemoteAddress();
            var channelAttachment = (ChannelAttachment) selectionKey.attachment();
            var respByteBuf = (ByteBuffer) channelAttachment.getRespByteBuffer();

            int remaining = respByteBuf.remaining();
            int write = socketChannel.write(respByteBuf);
            if (write == remaining) {
                // message has been fully sent to the client
                LOGGER.info("Teapot http response has been sent to " + remoteAddress);
                // we don't interested on WRITE events now
                selectionKey.interestOps(SelectionKey.OP_READ);
                channelAttachment.setRespByteBuffer(null);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in `handleWrite`");
        }
    }
}

class ChannelAttachment {

    // one instance per channel is enough
    private final HttpMessageParser messageParser = new HttpMessageParser();

    private ByteBuffer respByteBuffer;

    public HttpMessageParser getMessageParser() {
        return messageParser;
    }

    public ByteBuffer getRespByteBuffer() {
        return respByteBuffer;
    }

    public void setRespByteBuffer(ByteBuffer respByteBuffer) {
        this.respByteBuffer = null;
        this.respByteBuffer = respByteBuffer;
    }
}

// Only parses http messages without body
class HttpMessageParser {

    private static final char[] END_MARK = new char[]{'\r', '\n', '\r', '\n'};

    private final StringBuilder messageCollector = new StringBuilder();

    private String url;

    private String method;

    private boolean fullyRead;

    public void consume(ByteBuffer byteBuffer) {
        byteBuffer.flip(); // switch to read-mode

        // copy bytes
        for (int i = 0; i < byteBuffer.limit(); i++) {
            // NOTE!!! only works for one-byte characters
            this.messageCollector.append((char) byteBuffer.get(i));
        }

        if (checkIsFullyRead()) {
            String fullHttpMessage = messageCollector.toString();
            String[] lines = fullHttpMessage.split("\r\n");
            String[] firstLineParts = lines[0].split("\\s");
            this.method = firstLineParts[0];
            this.url = firstLineParts[1];
            this.fullyRead = true;
        }
    }

    // check if reached \r\n\r\n char sequence
    private boolean checkIsFullyRead() {
        int len = messageCollector.length();
        boolean matched = false;
        for (int i = 0; i < END_MARK.length; i++) {
            matched = messageCollector.charAt((len - 4) + i) == END_MARK[i];
            if (!matched) {
                break;
            }
        }
        return matched;
    }

    public boolean isFullyRead() {
        return this.fullyRead;
    }

    public String getUrl() {
        return this.url;
    }

    public String getMethod() {
        return this.method;
    }

    public void clearState() {
        this.url = null;
        this.method = null;
        this.fullyRead = false;
        this.messageCollector.delete(0, messageCollector.length());
    }
}

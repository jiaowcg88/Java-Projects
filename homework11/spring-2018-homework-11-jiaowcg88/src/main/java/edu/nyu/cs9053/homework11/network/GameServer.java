package edu.nyu.cs9053.homework11.network;


import edu.nyu.cs9053.homework11.game.Difficulty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Map;

/**
 * User: blangel
 *
 * A NIO implementation of a NetworkGameProvider.
 *
 * The server takes the following commands:
 * <pre>
 *     foes Difficulty
 * </pre>
 * <pre>
 *     move
 * </pre>
 * where the String "foes Easy" would be a call to {@linkplain NetworkGameProvider#getRandomNumberOfNextFoes(String)}
 * with "Easy"
 * and a call using String "move" would be a call to {@linkplain NetworkGameProvider#getRandomNextMove()}
 */
public class GameServer implements NetworkGameProvider, Runnable {

    public static final String SERVER_HOST = "localhost";

    public static final int SERVER_PORT = 8080;

    private final InetSocketAddress listenAddress;

    private final Map<SocketChannel, String> buffers;

    private static final int READ_BUFFER_SIZE = 256;

    private static final int WRITE_BUFFER_SIZE = 56;

    private final ByteBuffer readBuffer;

    private final ByteBuffer writeBuffer;

    private final ServerSocketChannel channel;

    private final Selector selector;

    private final Random random;


    public static void main(String[] args) throws IOException {
        GameServer server = new GameServer();
        server.run();
    }

    public GameServer() throws IOException {
        listenAddress = new InetSocketAddress(SERVER_HOST, SERVER_PORT);
        channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(listenAddress);
        random = new Random();
        buffers = new HashMap<>();
        readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
        writeBuffer = ByteBuffer.allocate(WRITE_BUFFER_SIZE);
        selector = Selector.open();
        channel.register(this.selector, SelectionKey.OP_ACCEPT);
        System.out.println("Sever started on port >> " + SERVER_PORT );
    }

    @Override
    public String getRandomNumberOfNextFoes(String difficulty) {
        switch (difficulty) {
            case "1":
                return String.valueOf(random.nextInt(Difficulty.Easy.getLevel()) + 1);
            case "2":
                return String.valueOf(random.nextInt(Difficulty.Medium.getLevel()) + 1);
            case "3":
                return String.valueOf(random.nextInt(Difficulty.Hard.getLevel()) + 1);
            default:
                throw new AssertionError("Difficulty must be Easy, Medium or Hard");
        }
    }

    @Override
    public String getRandomNextMove() {
        if (random.nextBoolean()) {
            if (random.nextBoolean()) {
                return "Up";
            } else {
                return "Down";
            }
        } else {
            if (random.nextInt(100) >= 95) {
                return "Right";
            } else {
                return "Left";
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                go();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private void go() throws IOException {
        int readyCount = selector.select();
        if (readyCount == 0) return;
        Set<SelectionKey> readyKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = readyKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            try {
                if (key.isAcceptable()) {
                    accept();
                } else if(key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
            } finally {
                iterator.remove();
            }
        }
    }

    private void accept() throws IOException {
        SocketChannel client = channel.accept();
        client.configureBlocking(false);
        System.out.println("Connection is created");
        client.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        readBuffer.clear();
        client.read(readBuffer);
        readBuffer.flip();
        String request = bufferByteToString(readBuffer, StandardCharsets.UTF_8);
        if (request.contains("operation")) {
            String message = messageProcess(request);
            buffers.put(client, message);
        }
    }

    private void write(SelectionKey key) throws IOException{
        SocketChannel client = (SocketChannel) key.channel();
        String message = buffers.get(client);
        if (message != null) {
            processRequest(client, message);
            buffers.remove(client);
        }
    }

    private String bufferByteToString(ByteBuffer buffer, Charset charset) {
        byte[] bytes;
        if (buffer.hasArray()) {
            bytes = buffer.array();
        } else {
            buffer.rewind();
            bytes = new byte[buffer.remaining()];
        }
        return new String(bytes, charset);
    }

    private void processRequest(SocketChannel client, String request) throws IOException {
        String operation = request.split(",")[0];
        String response;
        if (GameClient.NEXT_MOVE.equals(operation)) {
            response = String.format(getRandomNextMove() + "%n");
            System.out.println("Response move");
        } else {
            String difficulty = request.split(",")[1];
            response = String.format(getRandomNumberOfNextFoes(difficulty) + "%n");
            System.out.println("Response foes");
        }
        writeBuffer.clear();
        writeBuffer.put(response.getBytes());
        writeBuffer.flip();
        while (writeBuffer.hasRemaining()) {
            client.write(writeBuffer);
        }
    }

    private String messageProcess(String request) {
        StringBuilder sb = new StringBuilder();
        String trimRequest = request.split("\n")[0];
        sb.append(trimRequest.substring(request.indexOf(":") + 1, request.indexOf(",")));
        sb.append(",");
        request = trimRequest.split(",")[1];
        sb.append(request.substring(request.indexOf(":") + 1, request.indexOf("}")));
        return sb.toString();
    }
}
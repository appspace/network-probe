package ca.appspace.netprobe;

import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by Eugene on 2015-06-05.
 */
public class MainObservable {

    private final static int PORT_INCREMENT = 8000;

    public static void main(String... args) throws IOException {

        final ServerSocketChannel tcpSocketChannel = ServerSocketChannel.open();
        tcpSocketChannel.configureBlocking(false);

        //This is a stream of all ports that we want to work with
        final Observable<Integer> portNumbers = Observable.from(StandardPortMapping.getAllPorts());

        //Combining ServerSocket with port number gives us bound socket
        Observable<ServerSocket> boundSockets = portNumbers.map(new Func1<Integer, ServerSocket>() {
            @Override
            public ServerSocket call(Integer port) {
                ServerSocket socket = tcpSocketChannel.socket();
                        System.out.println("Mapping port " + (PORT_INCREMENT + port) +" on new socket");
                try {

                    socket.bind(new InetSocketAddress(PORT_INCREMENT + port));
                    return socket;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });

        boundSockets.forEach(new Action1<ServerSocket>() {
            @Override
            public void call(ServerSocket serverSocket) {
                if(serverSocket!=null && serverSocket.getLocalSocketAddress()!=null) {
                    System.out.println("Blah: " + serverSocket.getLocalSocketAddress().toString());
                }
            }
        });

        final PublishSubject<SocketChannel> socketChannelPublishSubject = PublishSubject.create();
        final PublishSubject<ConnectionOpenedEvent> connectionOpenedSubject = PublishSubject.create();

        connectionOpenedSubject.forEach(new Action1<ConnectionOpenedEvent>() {
                @Override
                public void call(ConnectionOpenedEvent connectionOpenedEvent) {
                    InetSocketAddress localAddr = connectionOpenedEvent.getLocal();
                    InetSocketAddress remoteAddr = connectionOpenedEvent.getRemote();
                    StringBuilder sb = new StringBuilder("Received TCP connection ");
                    sb.append(" on port ");
                    sb.append(localAddr.getPort());
                    sb.append(": ");
                    appendAddress(sb, remoteAddr);
                    System.out.println(sb.toString());
                }
            }
        );

        socketChannelPublishSubject.forEach(new Action1<SocketChannel>() {
            @Override
            public void call(SocketChannel socketChannel) {
                try {
                    if (socketChannel != null) {

                        SocketAddress remoteAddress = socketChannel.getRemoteAddress();
                        SocketAddress localAddress = socketChannel.getLocalAddress();

                        connectionOpenedSubject.onNext(new ConnectionOpenedEvent((InetSocketAddress)localAddress, (InetSocketAddress)remoteAddress));

                        String hello = "Hello";
                        ByteBuffer buf = ByteBuffer.allocate(hello.length());
                        buf.clear();
                        buf.put(hello.getBytes());
                        buf.flip();
                        socketChannel.write(buf);
                        socketChannel.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                System.out.println("Error in forEach: " + throwable.toString());
            }
        }, new Action0() {
            @Override
            public void call() {
                System.out.println("Complete in forEach");
            }
        });

        while (true) {
            if (socketChannelPublishSubject.hasObservers()) {
                try {
                    socketChannelPublishSubject.onNext(tcpSocketChannel.accept());
                } catch (Exception e) {
                    e.printStackTrace();
                    socketChannelPublishSubject.onError(e);
                }
            } else {
                try {   Thread.sleep(50);  } catch (Exception e) {}
            }
        }

    }

    private static void appendAddress(StringBuilder sb, SocketAddress remoteAddress) {
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress) remoteAddress;
            sb.append("\n  Host Name  : " + addr.getHostName());
            sb.append("\n  Host String: " + addr.getHostString());
            sb.append("\n  Address    : " + addr.getAddress());
            sb.append("\n  Port       : " + addr.getPort());
        }
    }

}

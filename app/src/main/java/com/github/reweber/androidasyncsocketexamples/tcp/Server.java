package com.github.reweber.androidasyncsocketexamples.tcp;

import com.koushikdutta.async.*;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.ListenCallback;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by reweber on 12/20/14.
 */
public class Server {

    private InetAddress host;
    private int port;

    public Server(String host, int port) {
        try {
            this.host = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        this.port = port;

        setup();
    }

    private void setup() {
        AsyncServer.getDefault().listen(host, port, new ListenCallback() {

            @Override
            public void onAccepted(final AsyncSocket socket) {
                handleAccept(socket);
            }

            @Override
            public void onListening(AsyncServerSocket socket) {
                System.out.println("[Server] Server started listening for connections");
            }

            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully shutdown server");
            }
        });
    }

    private void handleAccept(final AsyncSocket socket) {
        System.out.println("[Server] New Connection " + socket.toString());

        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                System.out.println("[Server] Received Message " + new String(bb.getAllByteArray()));

                Util.writeAll(socket, "Hello Client".getBytes(), new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        if (ex != null) throw new RuntimeException(ex);
                        System.out.println("[Server] Successfully wrote message");
                    }
                });
            }
        });

        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully closed connection");
            }
        });

        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully end connection");
            }
        });
    }
}

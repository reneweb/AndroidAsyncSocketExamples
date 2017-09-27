package com.github.reneweb.androidasyncsocketexamples.udp;

import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private InetSocketAddress host;
    private AsyncDatagramSocket asyncDatagramSocket;

    public Server(String host, int port) {
        this.host = new InetSocketAddress(host, port);
        setup();
    }

    private void setup() {

        try {
            asyncDatagramSocket = AsyncServer.getDefault().openDatagram(host, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        asyncDatagramSocket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                System.out.println("[Server] Received Message " + new String(bb.getAllByteArray()));
            }
        });

        asyncDatagramSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully closed connection");
            }
        });

        asyncDatagramSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully end connection");
            }
        });
    }
}

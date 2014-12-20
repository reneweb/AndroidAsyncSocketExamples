package com.github.reweber.androidasyncsocketexamples.tcp;

import com.koushikdutta.async.*;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.WritableCallback;
import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Created by reweber on 12/20/14.
 */
public class ClientSSL {
    private String host;
    private int port;

    public ClientSSL(String host, int port) {
        this.host = host;
        this.port = port;
        setup();
    }

    private void setup() {
        AsyncServer.getDefault().connectSocket(new InetSocketAddress(host, port), new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                try {
                    handleConnectCompleted(ex, socket);
                } catch(NoSuchAlgorithmException nsae) {
                    nsae.printStackTrace();
                    throw new RuntimeException(nsae);
                } catch(KeyManagementException ke) {
                    ke.printStackTrace();
                    throw new RuntimeException(ke);
                }
            }
        });
    }

    private void handleConnectCompleted(Exception ex, final AsyncSocket socket) throws NoSuchAlgorithmException, KeyManagementException {
        if(ex != null) throw new RuntimeException(ex);

        //You would want to use a "real" trust manager, instead of one that ignores the certificates
        TrustManager[] trustManagers = new TrustManager[] { createTrustAllTrustManager() };
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, new SecureRandom());
        SSLEngine sslEngine = sslContext.createSSLEngine();

        AsyncSSLSocketWrapper.handshake(socket, host, port, sslEngine, trustManagers, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER, true,
            new AsyncSSLSocketWrapper.HandshakeCallback() {
                @Override
                public void onHandshakeCompleted(Exception ex, final AsyncSSLSocket socket) {
                    if(ex != null) throw new RuntimeException(ex);
                    socket.setWriteableCallback(new WritableCallback() {
                        @Override
                        public void onWriteable() {
                            Util.writeAll(socket, "Hello Server".getBytes(), new CompletedCallback() {
                                @Override
                                public void onCompleted(Exception ex) {
                                    if (ex != null) throw new RuntimeException(ex);
                                    System.out.println("[Client] Successfully wrote message");
                                }
                            });
                        }
                    });

                    socket.setDataCallback(new DataCallback() {
                        @Override
                        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                            System.out.println("[Client] Received Message " + new String(bb.getAllByteArray()));
                        }
                    });

                    socket.setClosedCallback(new CompletedCallback() {
                        @Override
                        public void onCompleted(Exception ex) {
                            if(ex != null) throw new RuntimeException(ex);
                            System.out.println("[Client] Successfully closed connection");
                        }
                    });

                    socket.setEndCallback(new CompletedCallback() {
                        @Override
                        public void onCompleted(Exception ex) {
                            if(ex != null) throw new RuntimeException(ex);
                            System.out.println("[Client] Successfully end connection");
                        }
                    });
                }
            });
    }

    private TrustManager createTrustAllTrustManager() {
        return new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {

            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        };
    }
}

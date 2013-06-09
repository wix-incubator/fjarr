package com.wixpress.fjarr.it;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;

/**
 * @author: ittaiz
 * @since: 6/9/13
 */
public class JEmbeddedJettyResponder {
    private ServerResponse serverResponse;
    private final Server server;

    public JEmbeddedJettyResponder(int serverPort) {
        server = new Server(serverPort);
        AbstractHandler handler = new AbstractHandler() {

            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                serverResponse.respond(request, response);
                baseRequest.setHandled(true);
            }
        };
        server.setHandler(handler);
    }

    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void respondWith(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
    }

    public static ServerResponse emptyResponse() {
        return new ServerResponse() {
            @Override
            public void respond(HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.setStatus(HttpURLConnection.HTTP_OK);
                response.getWriter().print("");
            }
        };
    }

    public static ServerResponse forbiddenResponse() {
        return new ServerResponse() {
            @Override
            public void respond(HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.sendError(HttpURLConnection.HTTP_FORBIDDEN);
            }
        };
    }

    public static ServerResponse busyWaitingResponse(final CountDownLatch busyWait) {
        return new ServerResponse() {
            @Override
            public void respond(HttpServletRequest request, HttpServletResponse response) throws IOException {
                try {
                    busyWait.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
    }

    public static interface ServerResponse {
        public void respond(HttpServletRequest request, HttpServletResponse response) throws IOException;
    }
}

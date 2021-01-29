package org.example.server;

import org.example.common.ISettings;
import org.example.server.handlers.IUserHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServerTest {

    @Mock
    private ISettings settings;

    @Mock
    private IUserHandler userHandler;

    @Mock
    private Logger logger;

    @InjectMocks
    private ChatServer chatServer;

    @Test
    void test_createServerSocket_expectedBehaviour() {
        try (ServerSocket serverSocket = chatServer.createServerSocket(0)) {

            assertNotNull(serverSocket);
            assertTrue(serverSocket.isBound());

        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail("Failed creating ServerSocket!");
        }
    }

    @Test
    void test_createServerSocket_ifPortIsBind_throwsBindException() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {

            int port = serverSocket.getLocalPort();
            assertThrows(BindException.class, () -> chatServer.createServerSocket(port));

        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail("Failed creating ServerSocket!");
        }
    }

    @Test
    void test_startExecutorService_expectedBehaviour() {
        final int connections = 5;
        assertNotNull(chatServer.createExecutorService(connections));
    }

    @Test
    void test_serverAcceptsConnections() throws InterruptedException {
        final int port = 9999;
        final int connections = 5;
        final String host = "127.0.0.1";

        when(settings.getPort()).thenReturn(port);
        when(settings.getConnections()).thenReturn(connections);

        final ChatServer spyServer = spy(new ChatServer(userHandler, settings, logger));
        final ExecutorService service = mock(ExecutorService.class);
        doReturn(service).when(spyServer).createExecutorService(anyInt());
        doNothing().when(service).execute(any(Runnable.class));

        Thread serverThread = new Thread(() -> {
            try {
                spyServer.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                fail("Failed starting server!");
            }
        });

        serverThread.start();

        TimeUnit.MILLISECONDS.sleep(1000);

        try (Socket socket = new Socket(host, port)) {

            assertTrue(socket.isConnected());

        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail("Test failed, check server");
        }
        spyServer.stop();
    }

    @Test
    void test_stop_expectedBehaviour() throws IOException {
        final int port = 9999;
        final int connections = 5;

        when(settings.getPort()).thenReturn(port);
        when(settings.getConnections()).thenReturn(connections);

        final ChatServer spyServer = spy(new ChatServer(userHandler, settings, logger));
        doNothing().when(userHandler).stopAll();

        final ExecutorService service = Executors.newSingleThreadExecutor();
        doReturn(service).when(spyServer).createExecutorService(anyInt());

        final ServerSocket serverSocket = new ServerSocket();
        doReturn(serverSocket).when(spyServer).createServerSocket(anyInt());

        spyServer.start();
        spyServer.stop();

        assertTrue(serverSocket.isClosed());
        assertTrue(service.isShutdown());
    }

    @Test
    void test_stop_isSafeToUseIfNotInitialized() {
        assertDoesNotThrow(() -> new ChatServer(userHandler, settings, logger).stop());
    }
}
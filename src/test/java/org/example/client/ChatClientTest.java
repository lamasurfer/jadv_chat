package org.example.client;

import org.example.common.ISettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatClientTest {

    private final String host = "127.0.0.1";
    private final int port = 9999;

    @Mock
    private ISettings settings;

    @Mock
    private Logger logger;

    @Spy
    @InjectMocks
    private ChatClient client;

    @Mock
    private Socket socket;

    @Test
    void test_createSocket_expectedBehaviour() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {

            int localPort = serverSocket.getLocalPort();
            Socket socket = client.createSocket(host, localPort);
            assertNotNull(socket);
            assertTrue(socket.isConnected());
            socket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            fail("Failed creating ServerSocket!");
        }
    }

    @Test
    void test_createSocket_ifNoServer_throwsConnectException() {
        assertThrows(ConnectException.class, () -> client.createSocket(host, port));
    }

    @Test
    void test_start_ifNoServer_throwsIOException() {
        when(settings.getHost()).thenReturn(host);
        when(settings.getPort()).thenReturn(port);

        assertThrows(IOException.class, () -> client.start());
    }

    @Test
    void test_sendMessage_expectedBehaviour() throws IOException {
        when(settings.getHost()).thenReturn(host);
        when(settings.getPort()).thenReturn(port);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getInputStream()).thenReturn(ByteArrayInputStream.nullInputStream());

        doReturn(socket).when(client).createSocket(host, port);

        final String message = "test message";

        client.start();
        client.sendMessage(message);

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        assertEquals(message, reader.readLine());
    }

    @Test
    void test_readMessage_expectedBehaviour() throws IOException {
        when(settings.getHost()).thenReturn(host);
        when(settings.getPort()).thenReturn(port);

        when(socket.getOutputStream()).thenReturn(ByteArrayOutputStream.nullOutputStream());

        final String message = "test message";
        final InputStream inputStream = new ByteArrayInputStream(message.getBytes());

        when(socket.getInputStream()).thenReturn(inputStream);

        doReturn(socket).when(client).createSocket(host, port);

        client.start();
        assertEquals(message, client.readMessage());
    }

    @Test
    void test_stop_expectedBehaviour() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int localPort = serverSocket.getLocalPort();

            when(settings.getHost()).thenReturn(host);
            when(settings.getPort()).thenReturn(localPort);

            final ChatClient client = spy(new ChatClient(settings, logger));

            final Socket socket = new Socket(host, localPort);
            doReturn(socket).when(client).createSocket(host, localPort);

            client.start();
            assertTrue(socket.isConnected());
            client.stop();
            assertTrue(socket.isClosed());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void test_stopIsSafeToUseIfNotInitialized() {
        assertDoesNotThrow(() -> new ChatClient(settings, logger).stop());
    }
}
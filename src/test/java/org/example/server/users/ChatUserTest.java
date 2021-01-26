package org.example.server.users;

import org.example.server.handlers.IUserHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatUserTest {

    @Mock
    private Socket socket;

    @Mock
    private Logger logger;

    @Mock
    private IUserHandler userHandler;

    @InjectMocks
    private ChatUser chatUser;

    @Test
    void test_start_ifSocketProblems_throwsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> new ChatUser(null, userHandler, logger).start());
        assertThrows(IllegalStateException.class, () -> new ChatUser(new Socket(), userHandler, logger).start());
    }

    @Test
    void test_sendMessage_expectedBehaviour() throws IOException {
        when(socket.isConnected()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.getInputStream()).thenReturn(ByteArrayInputStream.nullInputStream());

        final String message = "test message";

        chatUser.start();
        chatUser.sendMessage(message);

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        assertEquals(message, reader.readLine());
    }

    @Test
    void test_readMessage_expectedBehaviour() throws IOException {
        when(socket.isConnected()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);
        when(socket.getOutputStream()).thenReturn(ByteArrayOutputStream.nullOutputStream());

        final String message = "test message";
        final InputStream inputStream = new ByteArrayInputStream(message.getBytes());

        when(socket.getInputStream()).thenReturn(inputStream);

        chatUser.start();
        assertEquals(message, chatUser.readMessage());
    }

    @Test
    void test_stop_expectedBehaviour() {
        final String host = "127.0.0.1";
        final int port = 0;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            int localPort = serverSocket.getLocalPort();
            Socket socket = new Socket(host, localPort);

            assertTrue(socket.isConnected());

            ChatUser chatUser = new ChatUser(socket, userHandler, logger);

            chatUser.start();
            chatUser.stop();
            assertTrue(socket.isClosed());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void test_stop_isSafeToUseIfNotInitialized() {
        assertDoesNotThrow(() -> new ChatUser(null, userHandler, logger).stop());
        assertDoesNotThrow(() -> new ChatUser(new Socket(), userHandler, logger).stop());
    }
}
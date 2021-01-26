package org.example.server.handlers;

import org.example.server.users.IChatUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.example.common.Codes.*;
import static org.example.server.handlers.UserHandler.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    private final UserHandler userHandler = new UserHandler();

    @Mock
    private IChatUser sender;

    @Mock
    private IChatUser receiver1;

    @Mock
    private IChatUser receiver2;

    @Test
    void test_process_ExpectedBehaviour() {
        final UserHandler spyHandler = spy(new UserHandler());

        final String nameMessage = NAME_CODE.get() + "name";
        spyHandler.process(sender, nameMessage);
        verify(spyHandler).processNameCode(sender, nameMessage);

        final String contactsMessage = CONTACTS_CODE.get();
        spyHandler.process(sender, contactsMessage);
        verify(spyHandler).processContactsCode(sender);

        final String helpMessage = HELP_CODE.get();
        spyHandler.process(sender, helpMessage);
        verify(spyHandler).processHelpCode(sender);

        final String exitMessage = EXIT_CODE.get();
        spyHandler.process(sender, exitMessage);
        verify(spyHandler).processExitCode(sender);

        final String testMessage = "test message";
        spyHandler.process(sender, testMessage);
        verify(spyHandler).processMessage(sender, testMessage);
    }

    @Test
    void test_processNameCode_setsNameAtStart() {
        final String name = "newName";
        final String nameMessage = NAME_CODE.get() + " " + name;

        when(sender.getName()).thenReturn(null)
                .thenReturn(name);

        userHandler.addUser(receiver1);
        userHandler.processNameCode(sender, nameMessage);

        verify(sender).setName(name);
        verify(sender).sendMessage(REG_CONFIRMATION + name);
        verify(receiver1).sendMessage(name + REG_NOTIFICATION);
    }

    @Test
    void test_processNameCode_changesName() {
        final String oldName = "oldName";
        final String newName = "newName";

        when(sender.getName()).thenReturn(oldName)
                .thenReturn(oldName)
                .thenReturn(newName);

        userHandler.addUser(receiver1);

        final String nameMessage = NAME_CODE.get() + " " + newName;

        userHandler.processNameCode(sender, nameMessage);
        verify(sender).setName(newName);
        verify(sender).sendMessage(NAME_CONFIRMATION + newName);
        verify(receiver1).sendMessage(oldName + NAME_NOTIFICATION + newName);
    }

    @Test
    void test_processNameCode_ifNameIsInvalid_sendsWarning() {
        final String nameMessage = NAME_CODE.get() + " ";
        userHandler.processNameCode(sender, nameMessage);
        verify(sender).sendMessage(NAME_WARNING);
    }

    @Test
    void test_processContactsCode() {
        userHandler.addUser(sender);
        userHandler.addUser(receiver1);
        userHandler.addUser(receiver2);

        final String contacts = userHandler.getUsersAsString();
        userHandler.processContactsCode(sender);
        verify(sender).sendMessage(contacts);
    }

    @Test
    void test_processHelpCode() {
        userHandler.processHelpCode(sender);
        verify(sender).sendMessage(HELP_MESSAGE);
    }

    @Test
    void test_processExitCode_stopsSenderAndNotifiesOthers() {
        userHandler.addUser(sender);
        userHandler.addUser(receiver1);
        userHandler.addUser(receiver2);

        userHandler.processExitCode(sender);

        verify(sender).stop();
        verify(receiver1).sendMessage(sender.getName() + EXIT_MESSAGE);
        verify(receiver2).sendMessage(sender.getName() + EXIT_MESSAGE);
    }

    @Test
    void test_processMessage_sendsSpecificMessagesCorrectly() {
        when(sender.getName()).thenReturn("sender");

        userHandler.addUser(sender);
        userHandler.addUser(receiver1);
        userHandler.addUser(receiver2);

        final String sentMessage = "test message";
        final String receivedMessage = sender.getName() + MSG_NOTIFICATION + sentMessage;

        userHandler.processMessage(sender, sentMessage);

        verify(sender).sendMessage(MSG_CONFIRMATION + sentMessage);
        verify(receiver1).sendMessage(receivedMessage);
        verify(receiver2).sendMessage(receivedMessage);
    }

    @Test
    void test_sendAll_sendsMessageToAllUsers() {
        userHandler.addUser(receiver1);
        userHandler.addUser(receiver2);

        final String message = "test message";

        userHandler.sendAll(message);

        verify(receiver1).sendMessage(message);
        verify(receiver2).sendMessage(message);
    }

    @Test
    void test_sendOther_doesNotSendToSender() {
        userHandler.addUser(sender);
        userHandler.addUser(receiver1);
        userHandler.addUser(receiver2);

        final String message = "test message";

        userHandler.sendOther(sender, message);

        verify(sender, never()).sendMessage(anyString());
        verify(receiver1).sendMessage(message);
        verify(receiver2).sendMessage(message);
    }

    @Test
    void test_addUser_expectedBehaviour() {
        userHandler.addUser(sender);
        Set<IChatUser> users = userHandler.getUsers();
        assertEquals(1, users.size());
    }

    @Test
    void test_removeUser_expectedBehaviour() {
        userHandler.addUser(sender);
        Set<IChatUser> before = userHandler.getUsers();
        assertEquals(1, before.size());

        userHandler.removeUser(sender);
        Set<IChatUser> after = userHandler.getUsers();
        assertTrue(after.isEmpty());
    }

    @Test
    void test_getUsers_expectedBehaviour() {
        assertNotNull(userHandler.getUsers());
    }

    @Test
    void test_stopAll_sendsWarningAndStopsAllUsers() {
        userHandler.addUser(sender);
        userHandler.addUser(receiver1);
        userHandler.addUser(receiver2);

        userHandler.stopAll();

        verify(sender).sendMessage(STOP_MESSAGE);
        verify(receiver1).sendMessage(STOP_MESSAGE);
        verify(receiver2).sendMessage(STOP_MESSAGE);

        verify(sender).stop();
        verify(receiver1).stop();
        verify(receiver2).stop();

        assertTrue(userHandler.getUsers().isEmpty());
    }
}
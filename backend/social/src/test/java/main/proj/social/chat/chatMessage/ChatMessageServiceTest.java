package main.proj.social.chat.chatMessage;

import main.proj.social.chat.chatRoom.ChatRoom;
import main.proj.social.chat.chatRoom.ChatRoomService;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository; // Use this single repository mock consistently

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ChatMessageService chatMessageService;

    @Test
    void whenFindChatMessages_thenReturnSortedMessages() {
        // Mock users
        User sender = new User();
        sender.setId(1L); // Ensure IDs are set to match the service logic
        sender.setUsername("senderUsername");

        User recipient = new User();
        recipient.setId(2L);
        recipient.setUsername("recipientUsername");

        // Mock chat room
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setSender(sender);
        chatRoom.setRecipient(recipient);

        // Mock messages
        ChatMessage msg1 = new ChatMessage(1L, chatRoom, sender, recipient, "Hello", new Date(System.currentTimeMillis() - 10000));
        ChatMessage msg2 = new ChatMessage(2L, chatRoom, sender, recipient, "World", new Date());

        // Setup mocks
        when(userRepository.findByUsername(sender.getUsername())).thenReturn(Optional.of(sender));
        when(userRepository.findByUsername(recipient.getUsername())).thenReturn(Optional.of(recipient));
        when(chatRoomService.getChatRoom(sender, recipient, false)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findByChatRoomIdAndSenderUsername(chatRoom.getId(), sender.getUsername()))
                .thenReturn(List.of(msg1)); // Assume this direction only returns msg1 for simplicity
        when(chatMessageRepository.findByChatRoomIdAndSenderUsername(chatRoom.getId(), recipient.getUsername()))
                .thenReturn(List.of(msg2)); // Assume this direction only returns msg2

        // Execute
        List<ChatMessage> messages = chatMessageService.findChatMessages(sender.getUsername(), recipient.getUsername());

        // Assert
        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertTrue(messages.get(0).getTimestamp().before(messages.get(1).getTimestamp()));
    }

    @Test
    void saveMessageSucceeds() {
        // Arrange
        User sender = new User();
        sender.setId(1L);
        sender.setUsername("senderUsername");

        User recipient = new User();
        recipient.setId(2L);
        recipient.setUsername("recipientUsername");

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(1L);
        chatRoom.setSender(sender);
        chatRoom.setRecipient(recipient);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(null); // ID should be null before saving
        chatMessage.setSender(sender);
        chatMessage.setRecipient(recipient);
        chatMessage.setContent("Test message");
        chatMessage.setTimestamp(new Date());
        chatMessage.setChatRoom(chatRoom);

        // Mock the necessary repository and service calls
        when(chatRoomService.getChatRoom(sender, recipient, true)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage msg = invocation.getArgument(0);
            msg.setId(1L); // Simulate setting an ID upon saving
            return msg;
        });

        // Act
        ChatMessage savedMessage = chatMessageService.save(chatMessage);

        // Assert
        assertNotNull(savedMessage);
        assertNotNull(savedMessage.getId(), "Saved message should have an ID");
        assertEquals(chatMessage.getContent(), savedMessage.getContent(), "Content should match");
        assertEquals(chatRoom.getId(), savedMessage.getChatRoom().getId(), "Chat room should match");
        assertEquals(sender.getUsername(), savedMessage.getSender().getUsername(), "Sender should match");
        assertEquals(recipient.getUsername(), savedMessage.getRecipient().getUsername(), "Recipient should match");

        // Verify repository interactions
        verify(chatMessageRepository).save(chatMessage);
        verify(chatRoomService).getChatRoom(sender, recipient, true); // Ensure we attempt to get or create a chat room
    }
}
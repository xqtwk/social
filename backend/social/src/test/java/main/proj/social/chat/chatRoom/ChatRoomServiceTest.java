package main.proj.social.chat.chatRoom;

import main.proj.social.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {
    @Mock
    private ChatRoomRepository chatRoomRepository;


    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    void getChatRoom_ReturnsExistingRoom() {
        User sender = new User();
        sender.setUsername("sender");

        User recipient = new User();
        recipient.setUsername("recipient");

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSender(sender);
        chatRoom.setRecipient(recipient);

        when(chatRoomRepository.findBySenderUsernameAndRecipientUsername(sender.getUsername(), recipient.getUsername()))
                .thenReturn(Optional.of(chatRoom));

        Optional<ChatRoom> result = chatRoomService.getChatRoom(sender, recipient, false);

        assertTrue(result.isPresent());
        assertEquals(chatRoom, result.get());
    }
    @Test
    void getChatRoom_CreatesNewRoomIfNotExist() {
        User sender = new User();
        sender.setUsername("sender");

        User recipient = new User();
        recipient.setUsername("recipient");


        when(chatRoomRepository.findBySenderUsernameAndRecipientUsername(sender.getUsername(), recipient.getUsername()))
                .thenReturn(Optional.empty());

        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<ChatRoom> result = chatRoomService.getChatRoom(sender, recipient, true);

        assertTrue(result.isPresent());
        assertEquals(sender, result.get().getSender());
        assertEquals(recipient, result.get().getRecipient());
    }
}
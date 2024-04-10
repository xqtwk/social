package main.proj.social.chat.chatMessage;


import lombok.RequiredArgsConstructor;
import main.proj.social.chat.chatRoom.ChatRoom;
import main.proj.social.chat.chatRoom.ChatRoomService;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    public ChatMessage save(ChatMessage chatMessage) {
        ChatRoom chatRoom = chatRoomService
                .getChatRoom(chatMessage.getSender(), chatMessage.getRecipient(), true)
                .orElseThrow(() -> new RuntimeException("Chat room could not be created"));

        chatMessage.setChatRoom(chatRoom);
        return repository.save(chatMessage);
    }

    public List<ChatMessage> findChatMessages(String senderUsername, String recipientUsername) {
        System.out.println("findchatmessages fired");
        var chatId = chatRoomService.getChatRoom(
                userRepository.findByUsername(senderUsername).orElseThrow(() ->
                        new UsernameNotFoundException("User not found")),
                userRepository.findByUsername(recipientUsername).orElseThrow(() ->
                        new UsernameNotFoundException("User not found")),
                false);

        // Retrieve messages for both directions (sender to recipient and recipient to sender)
        List<ChatMessage> senderToRecipientMessages = chatId.map(id ->
                        repository.findByChatRoomIdAndSenderUsername(id.getId(), senderUsername))
                .orElse(new ArrayList<>());
        List<ChatMessage> recipientToSenderMessages = chatId.map(id ->
                        repository.findByChatRoomIdAndSenderUsername(id.getId(), recipientUsername))
                .orElse(new ArrayList<>());

        // Combine and sort messages by timestamp or any relevant criteria
        List<ChatMessage> allMessages = new ArrayList<>();
        allMessages.addAll(senderToRecipientMessages);
        allMessages.addAll(recipientToSenderMessages);

        allMessages.sort(Comparator.comparing(ChatMessage::getTimestamp));

        return allMessages;
    }
}
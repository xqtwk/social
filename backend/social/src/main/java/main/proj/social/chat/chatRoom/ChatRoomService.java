package main.proj.social.chat.chatRoom;

import lombok.RequiredArgsConstructor;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public Optional<ChatRoom> getChatRoom(User sender, User recipient, boolean createNewRoomIfNotExists) {
        return chatRoomRepository
                .findBySenderUsernameAndRecipientUsername(sender.getUsername(), recipient.getUsername()) // Assuming you add this method to the repository
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        ChatRoom newChatRoom = new ChatRoom();
                        newChatRoom.setSender(sender);
                        newChatRoom.setRecipient(recipient);
                        return Optional.of(chatRoomRepository.save(newChatRoom));
                    }
                    return Optional.empty();
                });
    }

   /* private String createChatId(User sender, User recipient) {
        // Ensure the chatId is constructed consistently to enforce uniqueness
        String chatId = String.format("%s_%s", sender.getUsername(), recipient.getUsername());

        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .sender(sender)
                .recipient(recipient)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .sender(recipient)
                .recipient(sender)
                .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }*/

    public List<String> getChatListForUser(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String username = user.getUsername();

        return chatRoomRepository.findBySenderUsernameOrRecipientUsername(username, username)
                .stream()
                .map(chatRoom ->
                        chatRoom.getSender().getUsername().equals(username) ?
                                chatRoom.getRecipient().getUsername() :
                                chatRoom.getSender().getUsername()
                )
                .distinct()
                .collect(Collectors.toList());
    }

}

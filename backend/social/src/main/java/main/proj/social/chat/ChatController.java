package main.proj.social.chat;


import lombok.RequiredArgsConstructor;
import main.proj.social.chat.chatMessage.ChatMessage;
import main.proj.social.chat.chatMessage.ChatMessageService;
import main.proj.social.chat.chatRoom.ChatRoomService;
import main.proj.social.user.UserRepository;
import main.proj.social.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        if (!Objects.equals(chatMessage.getRecipient(), chatMessage.getSender())) {
            ChatMessage savedMsg = chatMessageService.save(chatMessage);
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getRecipient().getUsername(), "/queue/messages",
                    new ChatNotification(
                            savedMsg.getId(),
                            savedMsg.getSender(),
                            savedMsg.getRecipient(),
                            savedMsg.getContent()
                    )
            );
        } else {
            messagingTemplate.convertAndSendToUser(chatMessage.getSender().getUsername(), "/queue/errors", "Can't send a message for yourself");
        }
    }

    @GetMapping("/messages/{senderUsername}/{recipientUsername}")
    public ResponseEntity<?> findChatMessages(@PathVariable String senderUsername,
                                              @PathVariable String recipientUsername,
                                              Principal principal) {
        System.out.println("blablabla findchatmessages fired");
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String username = user.getUsername();
        if (!Objects.equals(username, senderUsername) || !Objects.equals(username, recipientUsername)) {
            return ResponseEntity.badRequest().body("This chat log is not yours");
        } else {
            return ResponseEntity
                    .ok(chatMessageService.findChatMessages(senderUsername, recipientUsername));
        }
    }

    /* @GetMapping("/chat-list/{username}")
     public ResponseEntity<List<String>> getChatList(@PathVariable String username) {
         List<String> chatList = chatRoomService.getChatListForUser(username);
         return ResponseEntity.ok(chatList);
     }*/
    @GetMapping("/chat-list")
    public ResponseEntity<List<String>> getChatList(Principal principal) {
        List<String> chatList = chatRoomService.getChatListForUser(principal);
        return ResponseEntity.ok(chatList);
    }


}
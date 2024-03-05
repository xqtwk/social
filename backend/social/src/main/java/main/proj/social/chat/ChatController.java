package main.proj.social.chat;


import lombok.RequiredArgsConstructor;
import main.proj.social.chat.chatMessage.ChatMessage;
import main.proj.social.chat.chatMessage.ChatMessageService;
import main.proj.social.chat.chatRoom.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        if (!Objects.equals(chatMessage.getRecipientUsername(), chatMessage.getSenderUsername())) {
            ChatMessage savedMsg = chatMessageService.save(chatMessage);
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getRecipientUsername(), "/queue/messages",
                    new ChatNotification(
                            savedMsg.getId(),
                            savedMsg.getSenderUsername(),
                            savedMsg.getRecipientUsername(),
                            savedMsg.getContent()
                    )
            );
        } else {
            messagingTemplate.convertAndSendToUser(chatMessage.getSenderUsername(), "/queue/errors", "Can't send a message for yourself");
        }
    }

    @GetMapping("/messages/{senderUsername}/{recipientUsername}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderUsername,
                                                              @PathVariable String recipientUsername) {
        System.out.println("blablabla findchatmessages fired");
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderUsername, recipientUsername));
    }

    @GetMapping("/chat-list/{username}")
    public ResponseEntity<List<String>> getChatList(@PathVariable String username) {
        List<String> chatList = chatRoomService.getChatListForUser(username);
        return ResponseEntity.ok(chatList);
    }


}
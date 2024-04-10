package main.proj.social.chat.chatMessage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomId(Long chat_roomId);
    List<ChatMessage> findByChatRoomIdAndSenderUsername(Long chat_roomId, String username);
}

package pl.lukbol.dyplom.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.lukbol.dyplom.classes.Conversation;
import pl.lukbol.dyplom.classes.Message;
import pl.lukbol.dyplom.classes.User;
import pl.lukbol.dyplom.exceptions.ApplicationException;
import pl.lukbol.dyplom.records.ConversationResponse;
import pl.lukbol.dyplom.repositories.ConversationRepository;
import pl.lukbol.dyplom.repositories.MessageRepository;
import pl.lukbol.dyplom.services.ChatService;

import java.util.List;

import static pl.lukbol.dyplom.common.Messages.CONVERSATION_NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository messageRepository;

    private final ConversationRepository conversationRepository;

    private final ChatService chatService;



    @MessageMapping("/sendToConversation/{conversationId}")
    @SendTo("/topic/employees")
    public Message sendMessageToClient(@DestinationVariable Long conversationId, Message message) {
        Conversation conversation = conversationRepository.findById(conversationId).orElse(null);
        if (conversation == null) {
            throw new ApplicationException.ConversationNotFoundException(CONVERSATION_NOT_FOUND);
        }
        return chatService.sendMessageToClient(conversation, message);

    }

    @MessageMapping("/sendToEmployees")
    @SendTo("/topic/employees")
    public Message sendMessageToEmployees(Message message) {
        return chatService.sendMessageToEmployees(message);
    }

    @GetMapping("/api/conversation")
    public ResponseEntity<List<Message>> getClientConversation(Authentication authentication) {
        return chatService.getClientConversation(authentication);
    }

    @GetMapping("/api/employee/conversations")
    public ResponseEntity<List<Message>> getAllEmployeeConversationMessages(Authentication authentication) {
        return chatService.getAllEmployeeConversationMessages(authentication);
    }

    @GetMapping("/get_conversations")
    public List<Conversation> getAllConversations() {
        List<Conversation> conversations = conversationRepository.findAll();
        return conversations;
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<Message>> getMessagesForConversation(@PathVariable Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId).orElse(null);

        if (conversation == null) {
            throw new ApplicationException.ConversationNotFoundException(CONVERSATION_NOT_FOUND);
        }
        List<Message> messages = messageRepository.findByConversation(conversation);
        return ResponseEntity.ok(messages);

    }

    @GetMapping("/{conversationId}/latest-message")
    public ResponseEntity<Message> getLatestMessageForConversation(@PathVariable Long conversationId) {
        Message latestMessage = chatService.getLatestMessageForConversation(conversationId);
        return ResponseEntity.ok(latestMessage);
    }

    @PostMapping("/api/createConversation")
    public ResponseEntity<ConversationResponse> createConversation(
            Authentication authentication,
            @RequestParam("name") String name,
            @RequestParam("participantIds") String participantIds
    ) {
        return chatService.createConversation(authentication, name, participantIds);
    }

    @PostMapping("/markConversationAsRead/{conversationId}")
    public ResponseEntity<String> markAllMessagesAsRead(Authentication authentication, @PathVariable Long conversationId) {
        return chatService.markConversationAsRead(authentication, conversationId);
    }

    @PutMapping("/clearSeenByUserIds/{conversationId}")
    public ResponseEntity<String> clearSeenByUserIds(@PathVariable Long conversationId) {
        return chatService.clearSeenByUserIds(conversationId);
    }

    @GetMapping("/checkIfConversationRead/{conversationId}")
    public ResponseEntity<Boolean> checkIfConversationRead(Authentication authentication, @PathVariable Long conversationId) {
        return chatService.checkIfConversationRead(authentication, conversationId);
    }

    @GetMapping("/getConversationParticipants/{conversationId}")
    public ResponseEntity<List<User>> getConversationParticipants(@PathVariable Long conversationId) {
        return chatService.getConversationParticipants(conversationId);
    }

    @GetMapping("/checkSeen/{conversationId}")
    public ResponseEntity<List<User>> getParticipantsBySeen(@PathVariable Long conversationId) {
        return chatService.getParticipantsBySeen(conversationId);
    }

    @PostMapping("/hide/{conversationId}")
    public ResponseEntity<String> hideConversation(@PathVariable Long conversationId) {
        return chatService.hideConversation(conversationId);
    }
}



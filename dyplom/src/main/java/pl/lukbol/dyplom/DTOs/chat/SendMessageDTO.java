package pl.lukbol.dyplom.DTOs.chat;

import pl.lukbol.dyplom.classes.Conversation;
import pl.lukbol.dyplom.classes.User;

import java.util.Date;

public record SendMessageDTO(User sender, Conversation conversation, String content, Date messageDate) {
}

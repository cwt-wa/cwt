package com.cwtsite.cwt.message.view.model;

import com.cwtsite.cwt.message.entity.Message;
import com.cwtsite.cwt.message.entity.enumeration.MessageCategory;
import com.cwtsite.cwt.user.repository.entity.User;

import java.util.List;

public class MessageDto {

    private String body;
    private MessageCategory category;
    private List<Long> recipients;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public MessageCategory getCategory() {
        return category;
    }

    public void setCategory(MessageCategory category) {
        this.category = category;
    }

    public List<Long> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Long> recipients) {
        this.recipients = recipients;
    }

    public static Message map(MessageDto dto, User author, List<User> recipients) {
        final Message message = new Message();

        message.setAuthor(author);
        message.setBody(dto.body);
        message.setCategory(dto.category);
        message.setRecipients(recipients);

        return message;
    }
}

package com.cwtsite.cwt.message.service;

import com.cwtsite.cwt.message.entity.Message;
import com.cwtsite.cwt.message.entity.enumeration.MessageCategory;
import com.cwtsite.cwt.user.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository repository) {
        this.messageRepository = repository;
    }

    public List<Message> findMessagesForGuest() {
        return messageRepository.findTop100ByCategoryInOrderByCreatedDesc(MessageCategory.guestCategories());
    }

    public List<Message> findMessagesForUser(User user) {
        return messageRepository.findTop100ByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(
                user, Collections.singletonList(user), MessageCategory.guestCategories());
    }
}

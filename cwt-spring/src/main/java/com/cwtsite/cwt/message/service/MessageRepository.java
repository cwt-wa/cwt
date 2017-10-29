package com.cwtsite.cwt.message.service;

import com.cwtsite.cwt.message.entity.Message;
import com.cwtsite.cwt.message.entity.enumeration.MessageCategory;
import com.cwtsite.cwt.user.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByCategoryIn(List<MessageCategory> categories);
    List<Message> findByAuthorOrRecipientsInOrCategoryIn(User author, List<User> recipients, List<MessageCategory> categories);
}

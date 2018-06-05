package com.cwtsite.cwt.domain.message.service;

import com.cwtsite.cwt.domain.message.entity.Message;
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findTop100ByCategoryInOrderByCreatedDesc(List<MessageCategory> categories);
    List<Message> findTop100ByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(User author, List<User> recipients, List<MessageCategory> categories);
}

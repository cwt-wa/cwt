package com.cwtsite.cwt.domain.message.service;

import com.cwtsite.cwt.domain.message.entity.Message;
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findAllByCategoryInOrderByCreatedDesc(Pageable pageable, List<MessageCategory> categories);
    Page<Message> findAllByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(Pageable pageable, User author, List<User> recipients, List<MessageCategory> categories);
}

package com.cwtsite.cwt.domain.message.service;

import com.cwtsite.cwt.domain.message.entity.Message;
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m where m.created > :created and ((:author member of m.recipients or m.author = :author or m.category <> 'PRIVATE') and m.category in :categories) order by m.created desc")
    List<Message> findNewByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(
            @Param("author") User author,
            @Param("categories") List<MessageCategory> categories,
            @Param("created") Timestamp created);

    @Query("select m from Message m where m.created < :created and ((:author member of m.recipients or m.author = :author or m.category <> 'PRIVATE') and m.category in :categories) order by m.created desc")
    List<Message> findOldByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(
            @Param("author") User author,
            @Param("categories") List<MessageCategory> categories,
            @Param("created") Timestamp created);

    List<Message> findAllByCreatedBeforeOrderByCreatedDesc(Timestamp created);

    List<Message> findAllByCreatedAfterOrderByCreatedDesc(Timestamp created);
}

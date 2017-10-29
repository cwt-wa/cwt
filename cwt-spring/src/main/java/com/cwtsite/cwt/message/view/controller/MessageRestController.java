package com.cwtsite.cwt.message.view.controller;

import com.cwtsite.cwt.message.entity.Message;
import com.cwtsite.cwt.message.service.MessageService;
import com.cwtsite.cwt.user.repository.entity.User;
import com.cwtsite.cwt.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/message")
public class MessageRestController {

    private final MessageService messageService;
    private final AuthService authService;

    @Autowired
    public MessageRestController(MessageService messageService, AuthService authService) {
        this.messageService = messageService;
        this.authService = authService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessages(HttpServletRequest request) {
        final User authenticatedUser = authService.getUserFromToken(request.getHeader(authService.getTokenHeaderName()));

        List<Message> messages = authenticatedUser == null
                ? messageService.findMessagesForGuest()
                : messageService.findMessagesForUser(authenticatedUser);

        return ResponseEntity.ok(messages);
    }
}

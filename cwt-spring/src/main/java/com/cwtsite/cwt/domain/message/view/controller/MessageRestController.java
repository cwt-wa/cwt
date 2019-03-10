package com.cwtsite.cwt.domain.message.view.controller;

import com.cwtsite.cwt.domain.message.entity.Message;
import com.cwtsite.cwt.domain.message.service.MessageService;
import com.cwtsite.cwt.domain.message.view.model.MessageDto;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.domain.user.service.AuthService;
import com.cwtsite.cwt.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final UserService userService;

    @Autowired
    public MessageRestController(MessageService messageService, AuthService authService, UserService userService) {
        this.messageService = messageService;
        this.authService = authService;
        this.userService = userService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessages(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(authService.getTokenHeaderName());
        User authenticatedUser = null;
        if (authorizationHeader != null) {
            authenticatedUser = authService.getUserFromToken(authorizationHeader);
        }

        List<Message> messages = authenticatedUser == null
                ? messageService.findMessagesForGuest()
                : messageService.findMessagesForUser(authenticatedUser);

        return ResponseEntity.ok(messages);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Message> AddMessages(@RequestBody MessageDto messageDto, HttpServletRequest request) {
        final User authenticatedUser = authService.getUserFromToken(request.getHeader(authService.getTokenHeaderName()));
        return ResponseEntity.ok(messageService.save(MessageDto.map(messageDto, authenticatedUser, userService.getByIds(messageDto.getRecipients()))));
    }
}

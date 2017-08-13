package com.cwtsite.cwt.user.view.controller;

import com.cwtsite.cwt.user.repository.entity.User;
import com.cwtsite.cwt.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/user")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<List<User>> register() {
        return ResponseEntity.ok(userService.getAllOrderedByUsername());
    }

    @RequestMapping(path = "/{id}/can-apply", method = RequestMethod.GET)
    public ResponseEntity<Boolean> userCanApplyForTournament(@PathVariable("id") long id) {
        User user = userService.getById(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(this.userService.userCanApplyForCurrentTournament(user));
    }
}

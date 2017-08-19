package com.cwtsite.cwt.user.view.controller;

import com.cwtsite.cwt.application.service.ApplicationService;
import com.cwtsite.cwt.core.exception.ResourceNotFoundException;
import com.cwtsite.cwt.entity.Application;
import com.cwtsite.cwt.user.repository.entity.User;
import com.cwtsite.cwt.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ApplicationService applicationService;

    @Autowired
    public UserRestController(UserService userService, ApplicationService applicationService) {
        this.userService = userService;
        this.applicationService = applicationService;
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    public ResponseEntity<List<User>> register() {
        return ResponseEntity.ok(userService.getAllOrderedByUsername());
    }

    @RequestMapping(path = "/{id}/can-apply", method = RequestMethod.GET)
    public ResponseEntity<Boolean> userCanApplyForTournament(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.userService.userCanApplyForCurrentTournament(assertUser(id)));
    }

    @RequestMapping(path = "/{id}/application", method = RequestMethod.POST)
    public ResponseEntity<Application> applyForTournament(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.applicationService.apply(assertUser(id)));
    }

    private User assertUser(final long id) {
        User user = userService.getById(id);

        if (user == null) {
            throw new ResourceNotFoundException("No user with ID " + id + " was found.");
        }

        return user;
    }
}

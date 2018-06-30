package com.cwtsite.cwt.domain.user.view.controller;

import com.cwtsite.cwt.domain.application.service.ApplicationService;
import com.cwtsite.cwt.domain.core.exception.ResourceNotFoundException;
import com.cwtsite.cwt.entity.Application;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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

    @RequestMapping(path = "/{id}/can-report", method = RequestMethod.GET)
    public ResponseEntity<Boolean> userCanReportForCurrentTournament(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.userService.userCanReportForCurrentTournament(assertUser(id)));
    }

    @RequestMapping(path = "/{id}/application", method = RequestMethod.POST)
    public ResponseEntity<Application> applyForTournament(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.applicationService.apply(assertUser(id)));
    }

    @RequestMapping(path = "/{id}/group/remaining-opponents", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getRemainingOpponents(@PathVariable("id") long id) {
        final User user = assertUser(id);
        return ResponseEntity.ok(userService.getRemainingOpponents(user));
    }

    private User assertUser(final long id) {
        return userService.getById(id).orElseThrow(ResourceNotFoundException::new);
    }
}

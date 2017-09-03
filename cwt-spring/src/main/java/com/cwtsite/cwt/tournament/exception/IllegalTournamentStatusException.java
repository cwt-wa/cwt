package com.cwtsite.cwt.tournament.exception;

import com.cwtsite.cwt.tournament.entity.enumeration.TournamentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class IllegalTournamentStatusException extends RuntimeException {

    public IllegalTournamentStatusException() {
    }

    public IllegalTournamentStatusException(String message) {
        super(message);
    }

    public IllegalTournamentStatusException(TournamentStatus... expectedValidTournamentStatuses) {
        super("Expected tournament status are "
                + String.join(", ", (CharSequence) Arrays.asList(expectedValidTournamentStatuses)));
    }
}

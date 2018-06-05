package com.cwtsite.cwt.domain.message.entity.enumeration;

import java.util.Arrays;
import java.util.List;

public enum MessageCategory {

    PRIVATE,
    SHOUTBOX,
    NEWS;

    public static List<MessageCategory> guestCategories() {
        return Arrays.asList(SHOUTBOX, NEWS);
    }
}

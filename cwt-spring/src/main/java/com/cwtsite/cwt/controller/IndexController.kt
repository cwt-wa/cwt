package com.cwtsite.cwt.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class IndexController {

    @RequestMapping(path = ["/**/{[path:[^\\.]*}"])
    fun redirect(): String {
        return "forward:/"
    }
}

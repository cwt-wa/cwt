package com.cwtsite.cwt.core.event

import org.springframework.context.ApplicationEvent

class CustomEvent(source: Any, val message: String) : ApplicationEvent(source)

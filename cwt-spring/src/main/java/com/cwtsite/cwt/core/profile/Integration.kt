package com.cwtsite.cwt.core.profile

import org.springframework.context.annotation.Profile


@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Profile("int", "integration")
annotation class Integration

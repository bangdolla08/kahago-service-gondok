package com.kahago.kahagoservice.configuration;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * @author Hendro yuwono
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@RequestMapping("/api")
public @interface BaseController {

    @AliasFor(annotation = Component.class)
    String value() default "";
}

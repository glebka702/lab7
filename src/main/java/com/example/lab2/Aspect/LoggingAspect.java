package com.example.lab2.Aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.lab2.Service.*.*(..))")
    public void logServiceMethods(JoinPoint joinPoint) {
        log.info("Вызов метода: {} с аргументами: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterThrowing(pointcut = "execution(* com.example.lab2..*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("Исключение в методе: {} с сообщением: {}", joinPoint.getSignature(), ex.getMessage());
    }
}

package com.pandaer.pan.validator;

import com.pandaer.pan.core.constants.MPanConstants;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


/**
 * 全局统一的验证器配置
 */
@SpringBootConfiguration
@Log4j2
public class WebValidatorConfig {
    private static final String FAIL_FAST_KEY = "hibernate.validator.fail_fast";


    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(MPanValidator());
        log.info(AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, "全局参数验证器启动成功"));
        return methodValidationPostProcessor;
    }

    private Validator MPanValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty(FAIL_FAST_KEY, MPanConstants.TRUE_STR)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}

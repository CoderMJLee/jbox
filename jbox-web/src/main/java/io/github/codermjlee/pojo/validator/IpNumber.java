package io.github.codermjlee.pojo.validator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 *
 * @author MJ
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IpNumber.Validator.class)
public @interface IpNumber {
    String message() default "取值范围是2~254";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<IpNumber, Integer> {
        @Override
        public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
            return integer == null || (integer != null && integer > 1 && integer < 255);
        }
    }
}

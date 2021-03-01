package pl.com.tt.alpha.account.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
	String message() default "Password must not be null";
	int min() default 6;
	int max() default 25;
	String characters() default "!@#$%^&*()_-+=?.,";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}

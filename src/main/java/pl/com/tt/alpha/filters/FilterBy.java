package pl.com.tt.alpha.filters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FilterBy {
    String fieldName();
    ConditionType condition() default ConditionType.EQUAL;
}

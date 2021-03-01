package pl.com.tt.alpha.user;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value="testadmin", roles="ADMIN")
public @interface WithMockTestAdmin {}

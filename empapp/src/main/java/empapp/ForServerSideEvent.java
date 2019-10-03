package empapp;

import javax.inject.Qualifier;
import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Qualifier
public @interface ForServerSideEvent {
}

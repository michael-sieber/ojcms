package de.adito.ojcms.beans.annotations;

import java.lang.annotation.*;

/**
 * Marks a bean field as 'private'.
 * This information is used for data encapsulation like the Java private field modifier.
 *
 * @author Simon Danner, 31.01.2017
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Private
{
}

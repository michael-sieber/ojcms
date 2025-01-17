package de.adito.ojcms.persistence;

import de.adito.ojcms.beans.annotations.ObserveCreation;
import de.adito.ojcms.persistence.util.*;

import java.lang.annotation.*;

/**
 * Persistence annotation of the persistence framework.
 * A bean annotated by this type will be persisted according to a specific {@link EPersistenceMode}.
 * Per default, all beans of the annotated bean type will be stored in a persistent bean container.
 *
 * @author Simon Danner, 10.02.2018
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ObserveCreation
public @interface Persist
{
  /**
   * The container id of the element, in which the bean/beans are stored.
   *
   * @return the container id
   */
  String containerId();

  /**
   * The mode in which the beans should be persisted.
   * Default: they will be stored in containers.
   *
   * @return the persistence mode
   */
  EPersistenceMode mode() default EPersistenceMode.CONTAINER;

  /**
   * The mode in which persistent beans will be added to the persistent container.
   * This configuration is only relevant, if the persistence mode is {@link EPersistenceMode#CONTAINER}.
   * Default: all beans have to be stored manually.
   *
   * @return the storage mode
   */
  EStorageMode storageMode() default EStorageMode.MANUAL;
}

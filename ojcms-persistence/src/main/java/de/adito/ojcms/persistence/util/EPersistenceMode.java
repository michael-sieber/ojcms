package de.adito.ojcms.persistence.util;

/**
 * Defines all possible persistence modes for the beans.
 *
 * @author Simon Danner, 26.02.2018
 */
public enum EPersistenceMode
{
  /**
   * Persist all instances of the annotated bean type in one container.
   */
  CONTAINER,

  /**
   * Persist the annotated bean type as singleton in a common table for such.
   */
  SINGLE
}

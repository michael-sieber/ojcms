package de.adito.ojcms.beans.fields.util;

/**
 * Any type that is based on or holds a bean field tuple.
 *
 * @param <VALUE> the value type of the bean field tuple
 * @author Simon Danner, 07.12.2018
 */
public interface IBeanFieldTupleBased<VALUE>
{
  /**
   * The bean field tuple this instance is based on.
   *
   * @return a bean field tuple
   */
  FieldValueTuple<VALUE> getFieldValueTuple();
}

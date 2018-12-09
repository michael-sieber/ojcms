package de.adito.ojcms.beans.util;

import de.adito.ojcms.beans.*;
import de.adito.ojcms.beans.fields.IField;
import de.adito.ojcms.beans.fields.util.FieldValueTuple;
import org.jetbrains.annotations.*;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

/**
 * General utility class for the bean modell.
 *
 * @author Simon Danner, 29.06.2017
 */
public final class BeanUtil
{
  private BeanUtil()
  {
  }

  /**
   * Checks, if a bean type is a valid declared type.
   * It has to be public and an extension of {@link Bean}.
   * Throws a runtime exception, if the type is invalid.
   * This check can be used in any cases, where especially transformed types are not allowed.
   *
   * @param pBeanType the bean type to check
   * @return the valid bean type
   */
  public static Class<? extends IBean> requiresDeclaredBeanType(Class<? extends IBean> pBeanType)
  {
    if (!Modifier.isPublic(pBeanType.getModifiers()))
      throw new RuntimeException(pBeanType.getName() + " is not a valid bean type! It has to be declared public to create fields!");

    if (!Bean.class.isAssignableFrom(pBeanType) && !MapBean.class.isAssignableFrom(pBeanType)) //To make sure it isn't a transformed type
      throw new RuntimeException(pBeanType.getName() + " is not a valid bean type to reflect fields from. Do not use transformed bean types!");
    return pBeanType;
  }

  /**
   * Finds a field by its name.
   * This method will lead to a runtime exception, if the search isn't successful.
   *
   * @param pBean      the bean, where the field should exist
   * @param pFieldName the name of the searched field
   * @return the found bean field
   */
  public static IField<?> findFieldByName(IBean<?> pBean, String pFieldName)
  {
    return findFieldByName(pBean.streamFields(), pFieldName);
  }

  /**
   * Finds a field by its name.
   * This method will lead to a runtime exception, if the search isn't successful.
   *
   * @param pFieldStream a stream of bean fields, which should contain the field
   * @param pFieldName   the name of the searched field
   * @return the found bean field
   */
  public static IField<?> findFieldByName(Stream<IField<?>> pFieldStream, String pFieldName)
  {
    return pFieldStream
        .filter(pField -> pField.getName().equals(pFieldName))
        .findAny()
        .orElseThrow(() -> new RuntimeException("A field with the name '" + pFieldName + "' is not present."));
  }

  /**
   * Compares the values of two beans of some fields.
   * Both beans must contain all fields to compare, otherwise a runtime exception will be thrown.
   *
   * @param pBean1         the first bean to compare
   * @param pBean2         the second bean to compare
   * @param pFieldsToCheck a stream of fields, which should be used for the comparison
   * @return an Optional that may contain the field with a different value (it is empty if all values are equal)
   */
  public static Optional<IField> compareBeanValues(IBean pBean1, IBean pBean2, Stream<IField<?>> pFieldsToCheck)
  {
    return pFieldsToCheck
        .map(pField -> (IField) pField)
        .filter(pField -> !Objects.equals(pBean1.getValue(pField), pBean2.getValue(pField)))
        .findAny();
  }

  /**
   * Finds the equivalent bean from a collection of beans.
   * The equivalence depends on the bean fields annotated as {@link de.adito.ojcms.beans.annotations.Identifier}.
   * The values of these fields have to be equal to fulfil this condition.
   * The bean will be removed from the collection, if the equivalent is found.
   *
   * @param pBean      the bean for which the equivalent should be found
   * @param pToCompare the collection of beans to compare
   * @return the equivalent bean that was removed (optional, because it may have not been found)
   */
  public static <BEAN extends IBean<BEAN>> Optional<BEAN> findRelatedBeanAndRemove(BEAN pBean, Collection<BEAN> pToCompare)
  {
    Iterator<BEAN> it = pToCompare.iterator();
    Set<FieldValueTuple<?>> identifiers = pBean.getIdentifiers();
    while (it.hasNext())
    {
      BEAN oldBean = it.next();
      if (pBean.getClass() == oldBean.getClass() && //same types
          ((identifiers.isEmpty() && Objects.equals(oldBean, pBean)) || //no identifiers -> use default equals()
              (identifiers.equals(oldBean.getIdentifiers()) && //else use identifiers
                  !compareBeanValues(oldBean, pBean, identifiers.stream()
                      .map(FieldValueTuple::getField))
                      .isPresent())))
      {
        it.remove();
        return Optional.of(oldBean);
      }
    }
    return Optional.empty();
  }

  /**
   * Resolves a deep bean within a parent bean in a hierarchical way of thinking.
   * The bean will be resolved based on a chain of bean fields, which lead the way to the deep bean.
   *
   * @param pParentBean the base parent bean
   * @param pChain      the chain of bean fields that describes the way to the deep bean
   * @return the deep bean within the parent bean
   */
  @NotNull
  public static IBean<?> resolveDeepBean(IBean<?> pParentBean, List<IField<?>> pChain)
  {
    for (IField<?> field : pChain)
    {
      if (!pParentBean.hasField(field))
        throw new RuntimeException("The chain is invalid. The parent bean '" + pParentBean + "'" +
                                       " does not contain a field " + field.getName() + ".");

      Object value = pParentBean.getValue(field);
      assert value instanceof IBean;
      pParentBean = (IBean<?>) value;
    }

    return pParentBean;
  }

  /**
   * Resolves a bean value of a deep bean field within a hierarchical structure.
   * The starting point is a parent bean, from which a chain of bean fields lead to the certain field.
   *
   * @param pParentBean the parent bean
   * @param pDeepField  the deep field to resolve the value to
   * @param pChain      the chain of bean fields that describes the way to the deep bean
   * @param <VALUE>     the data type of the deep field
   * @return the value of the deep field
   */
  @Nullable
  public static <VALUE> VALUE resolveDeepValue(IBean<?> pParentBean, IField<VALUE> pDeepField, List<IField<?>> pChain)
  {
    IBean<?> deepBean = resolveDeepBean(pParentBean, pChain);

    if (!deepBean.hasField(pDeepField))
      throw new RuntimeException("The resolved deep bean '" + deepBean + "' does not contain the field '" + pDeepField.getName() +
                                     "' to evaluate the value for.");

    return deepBean.getValue(pDeepField);
  }
}
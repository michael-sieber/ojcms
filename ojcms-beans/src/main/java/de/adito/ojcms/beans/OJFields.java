package de.adito.ojcms.beans;

import de.adito.ojcms.beans.annotations.GenericBeanField;
import de.adito.ojcms.beans.exceptions.OJInternalException;
import de.adito.ojcms.beans.exceptions.field.BeanFieldCreationException;
import de.adito.ojcms.beans.fields.IField;
import de.adito.ojcms.beans.util.BeanReflector;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

/**
 * Static entry point to create bean fields.
 * All concrete beans of the application should use this class to create their fields.
 * For an example take a look at {@link OJBean}.
 *
 * @author Simon Danner, 25.12.2018
 */
public final class OJFields
{
  private OJFields()
  {
  }

  /**
   * Takes a look at all static bean fields of a certain bean type and creates the first not initialized field automatically.
   * In this way all bean fields can be created through this method.
   * Usage: "public static final TextField name = OJFields.create(CLASSNAME.class);"
   * This method takes care about the whole initialization of the fields. (name, type, annotations, etc.)
   *
   * @param pBeanType the bean type to which the created field should belong to
   * @param <BEAN>    the generic type of the bean the field is for
   *                  (is here based on  {@link OJBean} rather than on the interface, so it can not be a transformed bean type)
   * @param <VALUE>   the data type of the field to create
   * @param <FIELD>   the generic type of the field that will be created
   * @return the newly created field instance
   */
  public static <BEAN extends OJBean<BEAN>, VALUE, FIELD extends IField<VALUE>> FIELD create(Class<BEAN> pBeanType)
  {
    return _createField(pBeanType, Optional.empty());
  }

  /**
   * Takes a look at all static bean fields of a certain bean type and creates the first not initialized field automatically.
   * In this way all bean fields can be created through this method.
   * Usage: "public static final TextField name = OJFields.createOptional(CLASSNAME.class);"
   * This method takes care about the whole initialization of the fields. (name, type, annotations, etc.)
   *
   * Use this method to create optional bean fields. These field are only active when a certain predicate is positive.
   *
   * @param pBeanType        the bean type to which the created field should belong to
   * @param pActiveCondition the condition determining the active state of the bean field
   * @param <BEAN>           the generic type of the bean the field is for
   *                         (is here based on  {@link OJBean} rather than on the interface, so it can not be a transformed bean type)
   * @param <VALUE>          the data type of the field to create
   * @param <FIELD>          the generic type of the field that will be created
   * @return the newly created field instance
   */
  public static <BEAN extends OJBean<BEAN>, VALUE, FIELD extends IField<VALUE>> FIELD createOptional(Class<BEAN> pBeanType,
                                                                                                     BiPredicate<BEAN, VALUE> pActiveCondition)
  {
    return _createField(pBeanType, Optional.of(pActiveCondition));
  }

  /**
   * Takes a look at all static bean fields of a certain bean type and creates the first not initialized field automatically.
   *
   * @param pBeanType        the bean type to which the created field should belong to
   * @param pActiveCondition an optional active condition determining the active state of the bean field
   * @param <BEAN>           the generic type of the bean the field is for
   *                         (is here based on  {@link OJBean} rather than on the interface, so it can not be a transformed bean type)
   * @param <VALUE>          the data type of the field to create
   * @param <FIELD>          the generic type of the field that will be created
   * @return the newly created field instance
   */
  private static <BEAN extends OJBean<BEAN>, VALUE, FIELD extends IField<VALUE>> FIELD _createField(Class<BEAN> pBeanType,
                                                                                                    Optional<BiPredicate<BEAN, VALUE>> pActiveCondition)
  {
    final Field declaredFieldToCreate = BeanReflector.reflectDeclaredBeanFields(pBeanType).stream()
        .filter(pField -> {
          try
          {
            return pField.get(null) == null;
          }
          catch (IllegalAccessException pE)
          {
            throw new OJInternalException(pE);
          }
        })
        .findAny()
        .orElseThrow(() -> new BeanFieldCreationException(pBeanType));

    //noinspection unchecked
    final Class<FIELD> beanFieldType = (Class<FIELD>) declaredFieldToCreate.getType();
    final Supplier<Class<?>> genericTypeSupplier = _genericTypeSupplier(declaredFieldToCreate, beanFieldType);
    final String fieldName = declaredFieldToCreate.getName();
    final List<Annotation> annotations = Arrays.asList(declaredFieldToCreate.getAnnotations());
    return BeanFieldFactory.createField(beanFieldType, genericTypeSupplier, fieldName, annotations, pActiveCondition);
  }

  /**
   * Reflects an optional generic type of the bean field to create.
   * A generic type will be reflected from field types annotated by {@link GenericBeanField}.
   * If this annotation is present it either provides a generic wrapper type for the actual generic type (e.g. List)
   * or the actual field type is required to use exactly one generic type that is the data type of the field directly.
   *
   * @param pDeclaredField the field instance from the reflection API
   * @param pBeanFieldType the type of the bean field
   * @param <FIELD>        the type of the bean field as generic
   * @return an optional generic type of the field instance
   */
  private static <FIELD extends IField> Supplier<Class<?>> _genericTypeSupplier(Field pDeclaredField, Class<FIELD> pBeanFieldType)
  {
    return () -> {
      try
      {
        final Type genericType = ((ParameterizedType) pDeclaredField.getGenericType()).getActualTypeArguments()[0];
        return (Class<?>) genericType;
      }
      catch (Exception pE)
      {
        throw new BeanFieldCreationException("Unable to reflect generic type of bean field " + pBeanFieldType.getName() + "! " +
                                                 "The annotation " + GenericBeanField.class.getSimpleName() + " can only be used " +
                                                 "if the field type uses exactly one generic type which is the data type of the field.", pE);
      }
    };
  }
}

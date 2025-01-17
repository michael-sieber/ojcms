package de.adito.ojcms.beans.fields.types;

import de.adito.ojcms.beans.annotations.NeverNull;
import de.adito.ojcms.beans.annotations.internal.TypeDefaultField;
import de.adito.ojcms.beans.fields.serialization.ISerializableFieldToString;
import de.adito.ojcms.beans.fields.util.CustomFieldCopy;
import de.adito.ojcms.beans.util.*;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * A bean field that holds a decimal number (double).
 *
 * @author Simon Danner, 23.08.2016
 */
@NeverNull
@TypeDefaultField(types = Double.class)
public class DecimalField extends AbstractField<Double> implements ISerializableFieldToString<Double>
{
  protected DecimalField(@NotNull String pName, Collection<Annotation> pAnnotations, boolean pIsOptional)
  {
    super(Double.class, pName, pAnnotations, pIsOptional);
  }

  @Override
  public Double getInitialValue()
  {
    return 0.0;
  }

  @Override
  public Double copyValue(Double pDecimalValue, ECopyMode pMode, CustomFieldCopy<?>... pCustomFieldCopies)
  {
    return pDecimalValue;
  }

  @Override
  public Double fromPersistent(String pSerialString)
  {
    return pSerialString == null ? getDefaultValue() : Double.parseDouble(pSerialString);
  }
}

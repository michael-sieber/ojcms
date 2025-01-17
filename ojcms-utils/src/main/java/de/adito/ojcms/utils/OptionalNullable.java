package de.adito.ojcms.utils;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.*;

/**
 * A container for a nullable value.
 * The presence of this value is optional.
 * If the value isn't available, it is possible to provide a default value or throw a exception.
 * Furthermore there are several other methods, which allow a comfortable usage in a functional way.
 * This implementation is based on the concept of {@link java.util.Optional}.
 *
 * @param <VALUE> the data type of the value of this optional
 * @author Simon Danner, 21.05.2018
 */
public final class OptionalNullable<VALUE>
{
  private static final OptionalNullable<?> NOT_PRESENT = new OptionalNullable();

  private final boolean present;
  private final VALUE value;

  /**
   * Creates an optional with a not present value.
   * This means the optional condition is negative.
   *
   * @param <VALUE> the generic data type of the optional value
   * @return an optional with a not present value
   */
  public static <VALUE> OptionalNullable<VALUE> notPresent()
  {
    //noinspection unchecked
    return (OptionalNullable<VALUE>) NOT_PRESENT;
  }

  /**
   * Creates an optional with a present value.
   *
   * @param pValue  the nullable value
   * @param <VALUE> the type of the value
   * @return an optional with a present value
   */
  public static <VALUE> OptionalNullable<VALUE> of(@Nullable VALUE pValue)
  {
    return new OptionalNullable<>(pValue);
  }

  /**
   * Creates the optional with a not present value.
   */
  private OptionalNullable()
  {
    present = false;
    value = null;
  }

  /**
   * Creates the optional with a present value.
   *
   * @param pValue the value of the optional (may be null)
   */
  private OptionalNullable(@Nullable VALUE pValue)
  {
    present = true;
    value = pValue;
  }

  /**
   * Determines, if the value of this optional is present.
   *
   * @return <tt>true</tt>, if the value is present
   */
  public boolean isPresent()
  {
    return present;
  }

  /**
   * Performs a value based action (consumer), if the value is present.
   *
   * @param pAction the action to perform
   */
  public void ifPresent(Consumer<? super VALUE> pAction)
  {
    if (present)
      pAction.accept(value);
  }

  /**
   * Maps the value of this optional.
   *
   * @param pMapper the function to map the value. (may result in a new value type)
   * @param <NEW>   the type of the new value
   * @return the new optional based on the mapped value
   */
  public <NEW> OptionalNullable<NEW> map(Function<? super VALUE, ? extends NEW> pMapper)
  {
    return present ? of(pMapper.apply(value)) : notPresent();
  }

  /**
   * Returns the present value or a given default value as replacement.
   *
   * @param pReplacement the replacement value (if the actual value is not present)
   * @return a guaranteed value from this optional
   */
  public VALUE orIfNotPresent(VALUE pReplacement)
  {
    return present ? value : pReplacement;
  }

  /**
   * Returns the present value or a replacement given by a supplier.
   *
   * @param pReplacementSupplier the supplier for the replacement
   * @return a guaranteed value from this optional
   */
  public VALUE orIfNotPresentGet(Supplier<? extends VALUE> pReplacementSupplier)
  {
    return present ? value : pReplacementSupplier.get();
  }

  /**
   * Returns the present value or throws a exception, if the value is not present.
   *
   * @param pExceptionSupplier the supplier for the exception
   * @param <EXCEPTION>        the generic type of the exception
   * @return the value from this optional
   * @throws EXCEPTION if the value of this optional is not present
   */
  public <EXCEPTION extends Throwable> VALUE orIfNotPresentThrow(Supplier<? extends EXCEPTION> pExceptionSupplier) throws EXCEPTION
  {
    if (present)
      return value;
    else
      throw pExceptionSupplier.get();
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + (present ? "[" + value + "]" : ".notPresent");
  }

  @Override
  public boolean equals(Object pO)
  {
    if (this == pO)
      return true;
    if (pO == null || getClass() != pO.getClass())
      return false;
    OptionalNullable<?> that = (OptionalNullable<?>) pO;
    return present == that.present && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(present, value);
  }
}

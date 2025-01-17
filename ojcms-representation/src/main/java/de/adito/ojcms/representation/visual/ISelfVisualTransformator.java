package de.adito.ojcms.representation.visual;

/**
 * An analog {@link IVisualTransformator} for {@link ISelfTransformable} instances.
 * Self-transformable instances represent a source directly.
 * This interface provides a default implementation for the methods that return a graphical component for a logical component.
 * The visual component always has to be the component itself, because it is a direct representation of the logical counterpart.
 *
 * @author Simon Danner, 27.01.2017
 */
interface ISelfVisualTransformator<SOURCE extends ILinkable, TRANSFORMATOR extends ISelfVisualTransformator<SOURCE, TRANSFORMATOR>>
    extends IVisualTransformator<SOURCE, TRANSFORMATOR, SOURCE>
{
  @Override
  default TRANSFORMATOR createVisualComponent(SOURCE pLogicComponent)
  {
    //noinspection unchecked
    return (TRANSFORMATOR) this;
  }

  @Override
  default TRANSFORMATOR createLinkedVisualComponent(SOURCE pLogicComponent)
  {
    //noinspection unchecked
    return (TRANSFORMATOR) this;
  }
}

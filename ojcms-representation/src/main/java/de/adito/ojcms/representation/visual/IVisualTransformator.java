package de.adito.ojcms.representation.visual;

import java.util.Queue;

/**
 * The tool to perform a transformation of a {@link ITransformable} instance.
 * It mainly creates the visual components for the logical counterparts and links them.
 * It also holds several data, containers etc. (replacement for an abstract class).
 *
 * @param <LOGIC>  the logical level of the transformation
 * @param <VISUAL> the type of the graphical components to which the logical components will be transformed to
 * @param <SOURCE> the type of the source that will be used for the transformation
 * @author Simon Danner, 27.01.2017
 * @see ITransformable
 */
interface IVisualTransformator<LOGIC, VISUAL, SOURCE extends ILinkable>
{
  /**
   * Transfers the reference to the data core of the original source to the transferable instance.
   *
   * @param pSourceToTransform the source to transform
   */
  void initTransformation(SOURCE pSourceToTransform);

  /**
   * The original source of the transformation.
   *
   * @return the source the transformation is based on
   */
  SOURCE getOriginalSource();

  /**
   * Creates a new graphical presentation for a logical counterpart.
   *
   * @param pLogicComponent the logical counterpart
   * @return a graphical component that represents the logical part
   */
  VISUAL createVisualComponent(LOGIC pLogicComponent);

  /**
   * Links the logical and graphical component based on the data model.
   * For example: When a user types a text into a visual textfield, the value will also be stored within the bean data core.
   *
   * @param pLogicComponent  the logical component
   * @param pVisualComponent the graphical component that represents the logical counterpart
   */
  void link(LOGIC pLogicComponent, VISUAL pVisualComponent);

  /**
   * A linked graphical component for a logical counterpart.
   * Combines {@link IVisualTransformator#createVisualComponent(LOGIC)} and {@link IVisualTransformator#link(LOGIC, VISUAL)}
   *
   * @param pLogicComponent the logical component
   * @return a graphical component that represents the logical part
   */
  default VISUAL createLinkedVisualComponent(LOGIC pLogicComponent)
  {
    VISUAL visualComponent = createVisualComponent(pLogicComponent);
    link(pLogicComponent, visualComponent);
    return visualComponent;
  }

  /**
   * A queue to store operations which can only be executed after a completed transformation.
   * It's not supported by default.
   *
   * @return a queue for operations
   * @throws UnsupportedOperationException if not supported by the component
   */
  default Queue<Runnable> getBeforeTransformationQueue()
  {
    throw new UnsupportedOperationException("A before transformation queue is not supported. Provide a container/queue!");
  }
}

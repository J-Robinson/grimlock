// Copyright 2014-2015 Commonwealth Bank of Australia
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package au.com.cba.omnia.grimlock.framework.transform

import au.com.cba.omnia.grimlock.framework._
import au.com.cba.omnia.grimlock.framework.position._
import au.com.cba.omnia.grimlock.framework.utility._

/** Base trait for transformations. */
trait Transformer

/** Base trait for transformers that do not modify the number of dimensions. */
trait Present extends PresentWithValue { self: Transformer =>
  type V = Any

  def present[P <: Position](cell: Cell[P], ext: V): Collection[Cell[P]] = present(cell)

  /**
   * Present the transformed content(s).
   *
   * @param pos The position of the cell.
   * @param con The content to transform.
   *
   * @return Optional of either a cell or a `List` of cells where the position is creating by modifiying `pos` and the
   *         content is derived from `con`.
   */
  def present[P <: Position](cell: Cell[P]): Collection[Cell[P]]

  /**
   * Operator for chaining transformations.
   *
   * @param that The transformation to perform after `this`.
   *
   * @return A transformer that runs `this` and then `that`.
   */
  def andThen(that: Transformer with Present): AndThenTransformer = AndThenTransformer(this, that)
}

/** Base trait for transformers that use a user supplied value but do not modify the number of dimensions. */
trait PresentWithValue { self: Transformer =>
  /** Type of the external value. */
  type V

  /**
   * Present the transformed content(s).
   *
   * @param pos The position of the cell.
   * @param con The content to transform.
   * @param ext The external value.
   *
   * @return Optional of either a cell or a `List` of cells where the position is creating by modifiying `pos` and the
   *         content is derived from `con`.
   */
  def present[P <: Position](cell: Cell[P], ext: V): Collection[Cell[P]]

  /**
   * Operator for chaining transformations.
   *
   * @param that The transformation to perform after `this`.
   *
   * @return A transformer that runs `this` and then `that`.
   */
  def andThen[W <: V](that: Transformer with PresentWithValue { type V = W }): AndThenTransformerWithValue[W] = {
    AndThenTransformerWithValue[W](this, that)
  }
}

/** Base trait for transformers that expand the position by appending a dimension. */
trait PresentExpanded extends PresentExpandedWithValue { self: Transformer =>
  type V = Any

  def present[P <: Position with ExpandablePosition](cell: Cell[P], ext: V): Collection[Cell[P#M]] = present(cell)

  /**
   * Present the transformed content(s).
   *
   * @param pos The position of the cell.
   * @param con The content to transform.
   *
   * @return Optional of either a cell or a `List` of cells where the position is creating by appending to `pos` and
   *         the content is derived from `con`.
   */
  def present[P <: Position with ExpandablePosition](cell: Cell[P]): Collection[Cell[P#M]]
}

/** Base trait for transformers that use a user supplied value and expand the position by appending a dimension. */
trait PresentExpandedWithValue { self: Transformer =>
  /** Type of the external value. */
  type V

  /**
   * Present the transformed content(s).
   *
   * @param pos The position of the cell.
   * @param con The content to transform.
   * @param ext The external value.
   *
   * @return Optional of either a cell or a `List` of cells where the position is creating by appending to `pos` and
   *         the content is derived from `con`.
   */
  def present[P <: Position with ExpandablePosition](cell: Cell[P], ext: V): Collection[Cell[P#M]]
}

/**
 * Transformer that is a composition of two transformers with `Present`.
 *
 * @param first  The first transformation to appy.
 * @param second The second transformation to appy.
 *
 * @note This need not be called in an application. The `andThen` method will create it.
 */
case class AndThenTransformer(first: Transformer with Present, second: Transformer with Present) extends Transformer
  with Present {
  def present[P <: Position](cell: Cell[P]): Collection[Cell[P]] = {
    Collection(first.present(cell).toList.flatMap { case c => second.present(c).toList })
  }
}

/**
 * Transformer that is a composition of two transformers with `PresentWithValue`.
 *
 * @param first  The first transformation to appy.
 * @param second The second transformation to appy.
 *
 * @note This need not be called in an application. The `andThen` method will create it.
 */
case class AndThenTransformerWithValue[W](first: Transformer with PresentWithValue { type V >: W },
  second: Transformer with PresentWithValue { type V >: W }) extends Transformer with PresentWithValue {
  type V = W
  def present[P <: Position](cell: Cell[P], ext: V): Collection[Cell[P]] = {
    Collection(first.present(cell, ext).toList.flatMap { case c => second.present(c, ext).toList })
  }
}

/**
 * Transformer that is a combination of one or more transformers with `Present`.
 *
 * @param singles `List` of transformers that are combined together.
 *
 * @note This need not be called in an application. The `Transformable` type class will convert any `List[Transformer]`
 *       automatically to one of these.
 */
case class CombinationTransformer[T <: Transformer with Present](singles: List[T]) extends Transformer with Present {
  def present[P <: Position](cell: Cell[P]): Collection[Cell[P]] = {
    Collection(singles.flatMap { case s => s.present(cell).toList })
  }
}

/**
 * Transformer that is a combination of one or more transformers with `PresentWithValue`.
 *
 * @param singles `List` of transformers that are combined together.
 *
 * @note This need not be called in an application. The `TransformableWithValue` type class will convert any
 *       `List[Transformer]` automatically to one of these.
 */
case class CombinationTransformerWithValue[T <: Transformer with PresentWithValue { type V >: W }, W](singles: List[T])
  extends Transformer with PresentWithValue {
  type V = W
  def present[P <: Position](cell: Cell[P], ext: V): Collection[Cell[P]] = {
    Collection(singles.flatMap { case s => s.present(cell, ext).toList })
  }
}

/**
 * Transformer that is a combination of one or more transformers with `PresentExpanded`.
 *
 * @param singles `List` of transformers that are combined together.
 *
 * @note This need not be called in an application. The `TransformableExpanded` type class will convert any
 *       `List[Transformer]` automatically to one of these.
 */
case class CombinationTransformerExpanded[T <: Transformer with PresentExpanded](singles: List[T]) extends Transformer
  with PresentExpanded {
  def present[P <: Position with ExpandablePosition](cell: Cell[P]): Collection[Cell[P#M]] = {
    Collection(singles.flatMap { case s => s.present(cell).toList })
  }
}

/**
 * Transformer that is a combination of one or more transformers with `PresentExpandedWithValue`.
 *
 * @param singles `List` of transformers that are combined together.
 *
 * @note This need not be called in an application. The `TransformableExpandedWithValue` type class will convert any
 *       `List[Transformer]` automatically to one of these.
 */
case class CombinationTransformerExpandedWithValue[T <: Transformer with PresentExpandedWithValue { type V >: W }, W](
  singles: List[T]) extends Transformer with PresentExpandedWithValue {
  type V = W
  def present[P <: Position with ExpandablePosition](cell: Cell[P], ext: W): Collection[Cell[P#M]] = {
    Collection(singles.flatMap { case s => s.present(cell, ext).toList })
  }
}

/** Type class for transforming a type `T` to a `Transformer with Present`. */
trait Transformable[T] {
  /**
   * Returns a `Transformer with Present` for type `T`.
   *
   * @param t Object that can be converted to a `Transformer with Present`.
   */
  def convert(t: T): Transformer with Present
}

/** Companion object for the `Transformable` type class. */
object Transformable {
  /**
   * Converts a `List[Transformer with Present]` to a single `Transformer with Present` using `CombinationTransformer`.
   */
  implicit def LT2T[T <: Transformer with Present]: Transformable[List[T]] = {
    new Transformable[List[T]] { def convert(t: List[T]): Transformer with Present = CombinationTransformer(t) }
  }
  /** Converts a `Transformer with Present` to a `Transformer with Present`; that is, it is a pass through. */
  implicit def T2T[T <: Transformer with Present]: Transformable[T] = {
    new Transformable[T] { def convert(t: T): Transformer with Present = t }
  }
}

/** Type class for transforming a type `T` to a `Transformer with PresentWithValue`. */
trait TransformableWithValue[T, W] {
  /**
   * Returns a `Transformer with PresentWithValue` for type `T`.
   *
   * @param t Object that can be converted to a `Transformer with PresentWithValue`.
   */
  def convert(t: T): Transformer with PresentWithValue { type V >: W }
}

/** Companion object for the `TransformableWithValue` type class. */
object TransformableWithValue {
  /**
   * Converts a `List[Transformer with PresentWithValue]` to a single `Transformer with PresentWithValue` using
   * `CombinationTransformerWithValue`.
   */
  implicit def LT2TWV[T <: Transformer with PresentWithValue { type V >: W }, W]: TransformableWithValue[List[T], W] = {
    new TransformableWithValue[List[T], W] {
      def convert(t: List[T]): Transformer with PresentWithValue { type V >: W } = {
        CombinationTransformerWithValue[T, W](t)
      }
    }
  }
  /**
   * Converts a `Transformer with PresentWithValue` to a `Transformer with PresentWithValue`; that is, it is a pass
   * through.
   */
  implicit def T2TWV[T <: Transformer with PresentWithValue { type V >: W }, W]: TransformableWithValue[T, W] = {
    new TransformableWithValue[T, W] { def convert(t: T): Transformer with PresentWithValue { type V >: W } = t }
  }
}

/** Type class for transforming a type `T` to a `Transformer with PresentExpanded`. */
trait TransformableExpanded[T] {
  /**
   * Returns a `Transformer with PresentExpanded` for type `T`.
   *
   * @param t Object that can be converted to a `Transformer with PresentExpanded`.
   */
  def convert(t: T): Transformer with PresentExpanded
}

/** Companion object for the `TransformableExpanded` type class. */
object TransformableExpanded {
  /**
   * Converts a `List[Transformer with PresentExpanded]` to a single `Transformer with PresentExpanded` using
   * `CombinationTransformerExpanded`.
   */
  implicit def LT2TE[T <: Transformer with PresentExpanded]: TransformableExpanded[List[T]] = {
    new TransformableExpanded[List[T]] {
      def convert(t: List[T]): Transformer with PresentExpanded = CombinationTransformerExpanded(t)
    }
  }
  /**
   * Converts a `Transformer with PresentExpanded` to a `Transformer with PresentExpanded`; that is, it is a pass
   * through.
   */
  implicit def T2TE[T <: Transformer with PresentExpanded]: TransformableExpanded[T] = {
    new TransformableExpanded[T] { def convert(t: T): Transformer with PresentExpanded = t }
  }
}

/** Type class for transforming a type `T` to a `Transformer with PresentExpandedWithValue`. */
trait TransformableExpandedWithValue[T, W] {
  /**
   * Returns a `Transformer with PresentExpandedWithValue` for type `T`.
   *
   * @param t Object that can be converted to a `Transformer with PresentExpandedWithValue`.
   */
  def convert(t: T): Transformer with PresentExpandedWithValue { type V >: W }
}

/** Companion object for the `TransformableExpandedWithValue` type class. */
object TransformableExpandedWithValue {
  /**
   * Converts a `List[Transformer with PresentExpandedWithValue]` to a single `Transformer with
   * PresentExpandedWithValue` using `CombinationTransformerExpandedWithValue`.
   */
  implicit def LT2TEWV[T <: Transformer with PresentExpandedWithValue { type V >: W }, W]: TransformableExpandedWithValue[List[T], W] = {
    new TransformableExpandedWithValue[List[T], W] {
      def convert(t: List[T]): Transformer with PresentExpandedWithValue { type V >: W } = {
        CombinationTransformerExpandedWithValue[T, W](t)
      }
    }
  }
  /**
   * Converts a `Transformer with PresentExpandedWithValue` to a `Transformer with PresentExpandedWithValue`; that is,
   * it is a pass through.
   */
  implicit def T2TEWV[T <: Transformer with PresentExpandedWithValue { type V >: W }, W]: TransformableExpandedWithValue[T, W] = {
    new TransformableExpandedWithValue[T, W] {
      def convert(t: T): Transformer with PresentExpandedWithValue { type V >: W } = t
    }
  }
}


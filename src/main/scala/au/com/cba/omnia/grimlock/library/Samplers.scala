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

package au.com.cba.omnia.grimlock.library.sample

import au.com.cba.omnia.grimlock.framework._
import au.com.cba.omnia.grimlock.framework.content._
import au.com.cba.omnia.grimlock.framework.position._
import au.com.cba.omnia.grimlock.framework.sample._

import scala.util.Random

/**
 * Randomly sample to a ratio.
 *
 * @param ratio The sampling ratio.
 * @param rnd   The random number generator.
 *
 * @note This randomly samples ignoring the position.
 */
case class RandomSample(ratio: Double, rnd: Random = new Random()) extends Sampler with Select {
  def select[P <: Position](cell: Cell[P]): Boolean = rnd.nextDouble() < ratio
}

/**
 * Sample based on the hash code of a dimension.
 *
 * @param dim   The dimension to sample from.
 * @param ratio The sample ratio (relative to `base`).
 * @param base  The base of the sampling ratio.
 */
case class HashSample(dim: Dimension, ratio: Int, base: Int) extends Sampler with Select {
  def select[P <: Position](cell: Cell[P]): Boolean = math.abs(cell.position(dim).hashCode % base) < ratio
}

/**
 * Sample to a defined size based on the hash code of a dimension.
 *
 * @param dim  The dimension to sample from.
 * @param size The size to sample to.
 */
case class HashSampleToSize(dim: Dimension, size: Long) extends Sampler with SelectWithValue {
  type V = Map[Position1D, Content]

  def select[P <: Position](cell: Cell[P], ext: V): Boolean = {
    ext(Position1D(dim.toString)).value.asDouble match {
      case Some(s) => math.abs(cell.position(dim).hashCode % s) < size
      case _ => false
    }
  }
}


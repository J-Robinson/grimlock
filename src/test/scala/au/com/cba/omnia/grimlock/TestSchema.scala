// Copyright 2015 Commonwealth Bank of Australia
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

package au.com.cba.omnia.grimlock

import au.com.cba.omnia.grimlock.framework.content._
import au.com.cba.omnia.grimlock.framework.content.metadata._
import au.com.cba.omnia.grimlock.framework.encoding._

class TestContinuousSchema extends TestGrimlock {

  "A ContinuousSchema" should "return its string representation" in {
    ContinuousSchema[Codex.DoubleCodex]().toString shouldBe "ContinuousSchema[DoubleCodex]()"
    ContinuousSchema[Codex.DoubleCodex](-3.1415, 1.4142).toString shouldBe
      "ContinuousSchema[DoubleCodex](-3.1415,1.4142)"

    ContinuousSchema[Codex.LongCodex]().toString shouldBe "ContinuousSchema[LongCodex]()"
    ContinuousSchema[Codex.LongCodex](-1, 1).toString shouldBe "ContinuousSchema[LongCodex](-1,1)"
  }

  it should "validate a correct value" in {
    ContinuousSchema[Codex.DoubleCodex]().isValid(DoubleValue(1)) shouldBe true
    ContinuousSchema[Codex.DoubleCodex](-3.1415, 1.4142).isValid(DoubleValue(1)) shouldBe true

    ContinuousSchema[Codex.LongCodex]().isValid(LongValue(1)) shouldBe true
    ContinuousSchema[Codex.LongCodex](-1, 1).isValid(LongValue(1)) shouldBe true
  }

  it should "not validate an incorrect value" in {
    ContinuousSchema[Codex.DoubleCodex](-3.1415, 1.4142).isValid(DoubleValue(4)) shouldBe false
    ContinuousSchema[Codex.DoubleCodex](-3.1415, 1.4142).isValid(DoubleValue(-4)) shouldBe false

    ContinuousSchema[Codex.LongCodex](-1, 1).isValid(LongValue(4)) shouldBe false
    ContinuousSchema[Codex.LongCodex](-1, 1).isValid(LongValue(-4)) shouldBe false
  }

  it should "throw an exception for an invalid value" in {
    a [ClassCastException] should be thrownBy { ContinuousSchema[Codex.DoubleCodex]().isValid(StringValue("a")) }
    a [ClassCastException] should be thrownBy {
      ContinuousSchema[Codex.DoubleCodex](-3.1415, 1.4142).isValid(StringValue("a"))
    }

    a [ClassCastException] should be thrownBy { ContinuousSchema[Codex.LongCodex]().isValid(StringValue("a")) }
    a [ClassCastException] should be thrownBy { ContinuousSchema[Codex.LongCodex](-1, 1).isValid(StringValue("a")) }
  }

  it should "decode a correct value" in {
    ContinuousSchema[Codex.DoubleCodex]().decode("1").map(_.value) shouldBe Some(DoubleValue(1))
    ContinuousSchema[Codex.DoubleCodex](-3.1415, 1.4142).decode("1").map(_.value) shouldBe Some(DoubleValue(1))

    ContinuousSchema[Codex.LongCodex]().decode("1").map(_.value) shouldBe Some(LongValue(1))
    ContinuousSchema[Codex.LongCodex](-1, 1).decode("1").map(_.value) shouldBe Some(LongValue(1))
  }

  it should "not decode an incorrect value" in {
    ContinuousSchema[Codex.DoubleCodex]().decode("a") shouldBe None
    ContinuousSchema[Codex.DoubleCodex](-3.1415, 1.4142).decode("4") shouldBe None
    ContinuousSchema[Codex.DoubleCodex](-3.1415, 1.4142).decode("-4") shouldBe None
    ContinuousSchema[Codex.DoubleCodex](-3.1415, 1.4142).decode("a") shouldBe None

    ContinuousSchema[Codex.LongCodex]().decode("a") shouldBe None
    ContinuousSchema[Codex.LongCodex](-1, 1).decode("4") shouldBe None
    ContinuousSchema[Codex.LongCodex](-1, 1).decode("-4") shouldBe None
    ContinuousSchema[Codex.LongCodex](-1, 1).decode("a") shouldBe None
  }
}

class TestDiscreteSchema extends TestGrimlock {

  "A DiscreteSchema" should "return its string representation" in {
    DiscreteSchema[Codex.LongCodex]().toString shouldBe "DiscreteSchema[LongCodex]()"
    DiscreteSchema[Codex.LongCodex](-1, 1, 1).toString shouldBe "DiscreteSchema[LongCodex](-1,1,1)"
  }

  it should "validate a correct value" in {
    DiscreteSchema[Codex.LongCodex]().isValid(LongValue(1)) shouldBe true
    DiscreteSchema[Codex.LongCodex](-1, 1, 1).isValid(LongValue(1)) shouldBe true
    DiscreteSchema[Codex.LongCodex](-4, 4, 2).isValid(LongValue(2)) shouldBe true
  }

  it should "not validate an incorrect value" in {
    DiscreteSchema[Codex.LongCodex](-1, 1, 1).isValid(LongValue(4)) shouldBe false
    DiscreteSchema[Codex.LongCodex](-1, 1, 1).isValid(LongValue(-4)) shouldBe false
    DiscreteSchema[Codex.LongCodex](-4, 4, 2).isValid(LongValue(3)) shouldBe false
  }

  it should "throw an exception for an invalid value" in {
    a [ClassCastException] should be thrownBy { DiscreteSchema[Codex.LongCodex]().isValid(DoubleValue(1)) }
    a [ClassCastException] should be thrownBy { DiscreteSchema[Codex.LongCodex](-1, 1, 1).isValid(DoubleValue(1)) }
    a [ClassCastException] should be thrownBy { DiscreteSchema[Codex.LongCodex]().isValid(StringValue("a")) }
    a [ClassCastException] should be thrownBy { DiscreteSchema[Codex.LongCodex](-1, 1, 1).isValid(StringValue("a")) }
  }

  it should "decode a correct value" in {
    DiscreteSchema[Codex.LongCodex]().decode("1").map(_.value) shouldBe Some(LongValue(1))
    DiscreteSchema[Codex.LongCodex](-1, 1, 1).decode("1").map(_.value) shouldBe Some(LongValue(1))
  }

  it should "not decode an incorrect value" in {
    DiscreteSchema[Codex.LongCodex]().decode("3.1415") shouldBe None
    DiscreteSchema[Codex.LongCodex]().decode("a") shouldBe None
    DiscreteSchema[Codex.LongCodex](-1, 1, 1).decode("4") shouldBe None
    DiscreteSchema[Codex.LongCodex](-1, 1, 1).decode("-4") shouldBe None
    DiscreteSchema[Codex.LongCodex](-4, 4, 2).decode("3") shouldBe None
    DiscreteSchema[Codex.LongCodex](-1, 1, 1).decode("a") shouldBe None
  }
}

class TestNominalSchema extends TestGrimlock {

  "A NominalSchema" should "return its string representation" in {
    NominalSchema[Codex.LongCodex]().toString shouldBe "NominalSchema[LongCodex]()"
    NominalSchema[Codex.LongCodex](List[Long](1,2,3)).toString shouldBe "NominalSchema[LongCodex](List(1, 2, 3))"

    NominalSchema[Codex.DoubleCodex]().toString shouldBe "NominalSchema[DoubleCodex]()"
    NominalSchema[Codex.DoubleCodex](List[Double](1,2,3)).toString shouldBe
      "NominalSchema[DoubleCodex](List(1.0, 2.0, 3.0))"

    NominalSchema[Codex.StringCodex]().toString shouldBe "NominalSchema[StringCodex]()"
    NominalSchema[Codex.StringCodex](List("a","b","c")).toString shouldBe "NominalSchema[StringCodex](List(a, b, c))"
  }

  it should "validate a correct value" in {
    NominalSchema[Codex.LongCodex]().isValid(LongValue(1)) shouldBe true
    NominalSchema[Codex.LongCodex](List[Long](1,2,3)).isValid(LongValue(1)) shouldBe true

    NominalSchema[Codex.DoubleCodex]().isValid(DoubleValue(1)) shouldBe true
    NominalSchema[Codex.DoubleCodex](List[Double](1,2,3)).isValid(DoubleValue(1)) shouldBe true

    NominalSchema[Codex.StringCodex]().isValid(StringValue("a")) shouldBe true
    NominalSchema[Codex.StringCodex](List("a","b","c")).isValid(StringValue("a")) shouldBe true
  }

  it should "not validate an incorrect value" in {
    NominalSchema[Codex.LongCodex](List[Long](1,2,3)).isValid(LongValue(4)) shouldBe false

    NominalSchema[Codex.DoubleCodex](List[Double](1,2,3)).isValid(DoubleValue(4)) shouldBe false

    NominalSchema[Codex.StringCodex](List("a","b","c")).isValid(StringValue("d")) shouldBe false
  }

  it should "throw an exception for an invalid value" in {
    a [ClassCastException] should be thrownBy { NominalSchema[Codex.LongCodex]().isValid(DoubleValue(1)) }
    a [ClassCastException] should be thrownBy {
      NominalSchema[Codex.LongCodex](List[Long](1,2,3)).isValid(DoubleValue(1))
    }
    a [ClassCastException] should be thrownBy { NominalSchema[Codex.LongCodex]().isValid(StringValue("a")) }
    a [ClassCastException] should be thrownBy {
      NominalSchema[Codex.LongCodex](List[Long](1,2,3)).isValid(StringValue("a"))
    }

    a [ClassCastException] should be thrownBy { NominalSchema[Codex.DoubleCodex]().isValid(LongValue(1)) }
    a [ClassCastException] should be thrownBy {
      NominalSchema[Codex.DoubleCodex](List[Double](1,2,3)).isValid(LongValue(1))
    }
    a [ClassCastException] should be thrownBy { NominalSchema[Codex.DoubleCodex]().isValid(StringValue("a")) }
    a [ClassCastException] should be thrownBy {
      NominalSchema[Codex.DoubleCodex](List[Double](1,2,3)).isValid(StringValue("a"))
    }

    a [ClassCastException] should be thrownBy { NominalSchema[Codex.StringCodex]().isValid(LongValue(1)) }
    a [ClassCastException] should be thrownBy {
      NominalSchema[Codex.StringCodex](List("a","b","c")).isValid(LongValue(1))
    }
    a [ClassCastException] should be thrownBy { NominalSchema[Codex.StringCodex]().isValid(DoubleValue(1)) }
    a [ClassCastException] should be thrownBy {
      NominalSchema[Codex.StringCodex](List("a","b","c")).isValid(DoubleValue(1))
    }
  }

  it should "decode a correct value" in {
    NominalSchema[Codex.LongCodex]().decode("1").map(_.value) shouldBe Some(LongValue(1))
    NominalSchema[Codex.LongCodex](List[Long](1,2,3)).decode("1").map(_.value) shouldBe Some(LongValue(1))

    NominalSchema[Codex.DoubleCodex]().decode("1").map(_.value) shouldBe Some(DoubleValue(1))
    NominalSchema[Codex.DoubleCodex](List[Double](1,2,3)).decode("1").map(_.value) shouldBe Some(DoubleValue(1))

    NominalSchema[Codex.StringCodex]().decode("a").map(_.value) shouldBe Some(StringValue("a"))
    NominalSchema[Codex.StringCodex](List("a","b","c")).decode("a").map(_.value) shouldBe Some(StringValue("a"))
  }

  it should "not decode an incorrect value" in {
    NominalSchema[Codex.LongCodex]().decode("3.1415") shouldBe None
    NominalSchema[Codex.LongCodex]().decode("a") shouldBe None
    NominalSchema[Codex.LongCodex](List[Long](1,2,3)).decode("4") shouldBe None
    NominalSchema[Codex.LongCodex](List[Long](1,2,3)).decode("a") shouldBe None

    NominalSchema[Codex.DoubleCodex]().decode("a") shouldBe None
    NominalSchema[Codex.DoubleCodex](List[Double](1,2,3)).decode("4") shouldBe None
    NominalSchema[Codex.DoubleCodex](List[Double](1,2,3)).decode("a") shouldBe None

    NominalSchema[Codex.StringCodex](List("a","b","c")).decode("1") shouldBe None
    NominalSchema[Codex.StringCodex](List("a","b","c")).decode("d") shouldBe None
  }
}

class TestOrdinalSchema extends TestGrimlock {

  "A OrdinalSchema" should "return its string representation" in {
    OrdinalSchema[Codex.LongCodex]().toString shouldBe "OrdinalSchema[LongCodex]()"
    OrdinalSchema[Codex.LongCodex](List[Long](1,2,3)).toString shouldBe "OrdinalSchema[LongCodex](List(1, 2, 3))"

    OrdinalSchema[Codex.DoubleCodex]().toString shouldBe "OrdinalSchema[DoubleCodex]()"
    OrdinalSchema[Codex.DoubleCodex](List[Double](1,2,3)).toString shouldBe
      "OrdinalSchema[DoubleCodex](List(1.0, 2.0, 3.0))"

    OrdinalSchema[Codex.StringCodex]().toString shouldBe "OrdinalSchema[StringCodex]()"
    OrdinalSchema[Codex.StringCodex](List("a","b","c")).toString shouldBe "OrdinalSchema[StringCodex](List(a, b, c))"
  }

  it should "validate a correct value" in {
    OrdinalSchema[Codex.LongCodex]().isValid(LongValue(1)) shouldBe true
    OrdinalSchema[Codex.LongCodex](List[Long](1,2,3)).isValid(LongValue(1)) shouldBe true

    OrdinalSchema[Codex.DoubleCodex]().isValid(DoubleValue(1)) shouldBe true
    OrdinalSchema[Codex.DoubleCodex](List[Double](1,2,3)).isValid(DoubleValue(1)) shouldBe true

    OrdinalSchema[Codex.StringCodex]().isValid(StringValue("a")) shouldBe true
    OrdinalSchema[Codex.StringCodex](List("a","b","c")).isValid(StringValue("a")) shouldBe true
  }

  it should "not validate an incorrect value" in {
    OrdinalSchema[Codex.LongCodex](List[Long](1,2,3)).isValid(LongValue(4)) shouldBe false

    OrdinalSchema[Codex.DoubleCodex](List[Double](1,2,3)).isValid(DoubleValue(4)) shouldBe false

    OrdinalSchema[Codex.StringCodex](List("a","b","c")).isValid(StringValue("d")) shouldBe false
  }

  it should "throw an exception for an invalid value" in {
    a [ClassCastException] should be thrownBy { OrdinalSchema[Codex.LongCodex]().isValid(DoubleValue(1)) }
    a [ClassCastException] should be thrownBy {
      OrdinalSchema[Codex.LongCodex](List[Long](1,2,3)).isValid(DoubleValue(1))
    }
    a [ClassCastException] should be thrownBy { OrdinalSchema[Codex.LongCodex]().isValid(StringValue("a")) }
    a [ClassCastException] should be thrownBy {
      OrdinalSchema[Codex.LongCodex](List[Long](1,2,3)).isValid(StringValue("a"))
    }

    a [ClassCastException] should be thrownBy { OrdinalSchema[Codex.DoubleCodex]().isValid(LongValue(1)) }
    a [ClassCastException] should be thrownBy {
      OrdinalSchema[Codex.DoubleCodex](List[Double](1,2,3)).isValid(LongValue(1))
    }
    a [ClassCastException] should be thrownBy { OrdinalSchema[Codex.DoubleCodex]().isValid(StringValue("a")) }
    a [ClassCastException] should be thrownBy {
      OrdinalSchema[Codex.DoubleCodex](List[Double](1,2,3)).isValid(StringValue("a"))
    }

    a [ClassCastException] should be thrownBy { OrdinalSchema[Codex.StringCodex]().isValid(LongValue(1)) }
    a [ClassCastException] should be thrownBy {
      OrdinalSchema[Codex.StringCodex](List("a","b","c")).isValid(LongValue(1))
    }
    a [ClassCastException] should be thrownBy { OrdinalSchema[Codex.StringCodex]().isValid(DoubleValue(1)) }
    a [ClassCastException] should be thrownBy {
      OrdinalSchema[Codex.StringCodex](List("a","b","c")).isValid(DoubleValue(1))
    }
  }

  it should "decode a correct value" in {
    OrdinalSchema[Codex.LongCodex]().decode("1").map(_.value) shouldBe Some(LongValue(1))
    OrdinalSchema[Codex.LongCodex](List[Long](1,2,3)).decode("1").map(_.value) shouldBe Some(LongValue(1))

    OrdinalSchema[Codex.DoubleCodex]().decode("1").map(_.value) shouldBe Some(DoubleValue(1))
    OrdinalSchema[Codex.DoubleCodex](List[Double](1,2,3)).decode("1").map(_.value) shouldBe Some(DoubleValue(1))

    OrdinalSchema[Codex.StringCodex]().decode("a").map(_.value) shouldBe Some(StringValue("a"))
    OrdinalSchema[Codex.StringCodex](List("a","b","c")).decode("a").map(_.value) shouldBe Some(StringValue("a"))
  }

  it should "not decode an incorrect value" in {
    OrdinalSchema[Codex.LongCodex]().decode("3.1415") shouldBe None
    OrdinalSchema[Codex.LongCodex]().decode("a") shouldBe None
    OrdinalSchema[Codex.LongCodex](List[Long](1,2,3)).decode("4") shouldBe None
    OrdinalSchema[Codex.LongCodex](List[Long](1,2,3)).decode("a") shouldBe None

    OrdinalSchema[Codex.DoubleCodex]().decode("a") shouldBe None
    OrdinalSchema[Codex.DoubleCodex](List[Double](1,2,3)).decode("4") shouldBe None
    OrdinalSchema[Codex.DoubleCodex](List[Double](1,2,3)).decode("a") shouldBe None

    OrdinalSchema[Codex.StringCodex](List("a","b","c")).decode("1") shouldBe None
    OrdinalSchema[Codex.StringCodex](List("a","b","c")).decode("d") shouldBe None
  }
}

class TestDateSchema extends TestGrimlock {

  val dfmt = new java.text.SimpleDateFormat("yyyy-MM-dd")
  val dtfmt = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

  "A DateSchema" should "return its string representation" in {
    DateSchema[Codex.DateCodex]().toString shouldBe "DateSchema[DateCodex]()"
    DateSchema[Codex.DateTimeCodex]().toString shouldBe "DateSchema[DateTimeCodex]()"
  }

  it should "validate a correct value" in {
    DateSchema[Codex.DateCodex]().isValid(DateValue(dfmt.parse("2001-01-01"), DateCodex)) shouldBe true
    DateSchema[Codex.DateTimeCodex]().isValid(DateValue(dtfmt.parse("2001-01-01 01:02:02"), DateTimeCodex)) shouldBe
      true
  }

  it should "throw an exception for an invalid value" in {
    a [ClassCastException] should be thrownBy { DateSchema[Codex.DateCodex]().isValid(LongValue(1)) }
    a [ClassCastException] should be thrownBy { DateSchema[Codex.DateTimeCodex]().isValid(LongValue(1)) }
  }

  it should "decode a correct value" in {
    DateSchema[Codex.DateCodex]().decode("2001-01-01").map(_.value) shouldBe
      Some(DateValue(dfmt.parse("2001-01-01"), DateCodex))
    DateSchema[Codex.DateTimeCodex]().decode("2001-01-01 01:02:02").map(_.value) shouldBe
      Some(DateValue(dtfmt.parse("2001-01-01 01:02:02"), DateTimeCodex))
  }

  it should "not decode an incorrect value" in {
    DateSchema[Codex.DateCodex]().decode("a") shouldBe None
    DateSchema[Codex.DateTimeCodex]().decode("a") shouldBe None
  }
}


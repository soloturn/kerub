package com.github.K0zka.kerub.model.expectations

import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.ExpectationLevel

@JsonTypeName("availability")
data class VirtualMachineAvailabilityExpectation constructor(
		override val level: ExpectationLevel = ExpectationLevel.DealBreaker,
		val up: Boolean = true
                                                                         ) : Expectation
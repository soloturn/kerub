package com.github.K0zka.kerub.model

import java.util.UUID

data class VirtualMachine : Entity<UUID> {
	override var id: UUID? = null

	var name : String? = null

	var nrOfCpus : Int? = 1

	var memory : Range<Int> = Range(1024, 2048)

	var expectations : List<Expectation>? = null


}
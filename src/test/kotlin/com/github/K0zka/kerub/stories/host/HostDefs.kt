package com.github.K0zka.kerub.stories.host

import cucumber.api.java.en.Given
import com.github.K0zka.kerub.utils.getLogger
import cucumber.api.java.en.When
import cucumber.api.java.After

public class HostDefs {

	companion object {
		private val logger = getLogger(HostDefs::class)
	}

	Given("^a host$")
	fun initializeFakeSshHost() {
		logger.info("initialize emulated host")
	}

	When("^the host is joined$")
	fun joinHost() {
		logger.info("Join host")
	}

	After
	fun cleanupHost() {

	}
}
package com.github.kerubistan.kerub.planner.steps.host.security.clear

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.hosts.UnusedService
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.join
import kotlin.reflect.KClass

object ClearSshKeyFactory : AbstractOperationalStepFactory<ClearSshKey>() {

	override val problemHints = setOf(UnusedService::class)
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<ClearSshKey> {

		val allInstalledSshKeys = state.hosts.values.map { it.config?.acceptedPublicKeys }.filterNotNull().join().toSet()

		return state.hosts.values.filter { it.dynamic?.status == HostStatus.Up }
				.filter { it.config?.publicKey != null && !(it.config.publicKey in allInstalledSshKeys) }
				.map { ClearSshKey(it.stat) }

	}
}
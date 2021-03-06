package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.utils.iscsiShareableDisks
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.junix.iscsi.tgtd.TgtAdmin
import kotlin.reflect.KClass

object TgtdIscsiShareFactory : AbstractOperationalStepFactory<TgtdIscsiShare>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<TgtdIscsiShare> =
			factoryFeature(state.controllerConfig.storageTechnologies.iscsiEnabled) {
				iscsiShareableDisks(state).mapNotNull { (disk, allocations) ->
					allocations.filter {
						isTgtdAvailable(requireNotNull(state.hosts[it.hostId]))
					}.map { allocation ->
						val hostColl = requireNotNull(state.hosts[allocation.hostId])
						TgtdIscsiShare(
								host = hostColl.stat,
								vstorage = disk.stat,
								allocation = allocation
						)
					}
				}.join()
			}

	private fun isTgtdAvailable(hostColl: HostDataCollection) =
			TgtAdmin.available(hostColl.stat.capabilities)

}
package com.github.kerubistan.kerub.planner.steps.base

import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.join
import kotlin.reflect.KClass

abstract class AbstractUnAllocateFactory<T : UnAllocate<S>, S : VirtualStorageAllocation> : AbstractOperationalStepFactory<T>() {

	final override fun produce(state: OperationalState): List<T> =
			state.vStorage.values.filter { it.stat.recycling || hasOverAllocation(it) }.mapNotNull { coll ->
				coll.dynamic?.allocations?.map {
					if (type.isInstance(it) && (state.hosts[it.hostId]?.dynamic?.status == HostStatus.Up)) {
						unAllocate(it as S, coll, state)
					} else {
						null
					}
				}
			}.join().filterNotNull()

	private fun hasOverAllocation(it: VirtualStorageDataCollection) =
			it.stat.readOnly && it.dynamic?.allocations?.size ?: 0 > 1

	abstract fun unAllocate(allocation: S, vStorage: VirtualStorageDataCollection, state: OperationalState): T

	abstract val type: KClass<S>

}
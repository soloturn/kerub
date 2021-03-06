package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.utils.update
import java.math.BigInteger

data class CreateThinLv(
		override val host: Host,
		override val capability: LvmStorageCapability,
		override val disk: VirtualStorageDevice,
		val poolName: String) : AbstractCreateLv() {
	override val allocation: VirtualStorageLvmAllocation by lazy {
		VirtualStorageLvmAllocation(
				hostId = host.id,
				actualSize = BigInteger.ZERO,
				pool = poolName,
				path = "",
				vgName = volumeGroupName,
				capabilityId = capability.id
		)
	}

	override fun take(state: OperationalState): OperationalState = state.copy(
			vStorage = state.vStorage.update(disk.id) {
				it.copy(dynamic = VirtualStorageDeviceDynamic(
						id = disk.id,
						allocations = listOf(allocation)
				))
			}
	)
}
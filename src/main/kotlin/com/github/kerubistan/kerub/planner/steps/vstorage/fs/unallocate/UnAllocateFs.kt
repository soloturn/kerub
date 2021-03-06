package com.github.kerubistan.kerub.planner.steps.vstorage.fs.unallocate

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.steps.base.UnAllocate

data class UnAllocateFs(override val vstorage: VirtualStorageDevice,
						override val allocation: VirtualStorageFsAllocation,
						override val host: Host) : UnAllocate<VirtualStorageFsAllocation>() {
	init {
		check(allocation.hostId == host.id) {
			"allocation host id (${allocation.hostId} must be equal to the host id (${host.id}))"
		}
	}

}
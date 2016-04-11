package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.model.FsStorageCapability
import com.github.K0zka.kerub.model.io.VirtualDiskFormat
import com.github.K0zka.kerub.planner.OperationalState

object CreateImageFactory : AbstractVStorageCreateFactory<CreateImage>() {
	override fun produce(state: OperationalState): List<CreateImage> {
		val storageNotAllocated = listStorageNotAllocated(state)
		val runningHosts = listRunningHosts(state)

		var steps = listOf<CreateImage>()

		runningHosts.forEach {
			host ->
			host.capabilities?.storageCapabilities?.filter {
				capability
				->
				capability is FsStorageCapability && capability.mountPoint.startsWith("/var")
			}?.forEach {
				mount ->
				steps += storageNotAllocated.map {
					storage ->
					CreateImage(
							disk = storage,
							host = host,
							format = VirtualDiskFormat.qcow2,
							path = (mount as FsStorageCapability).mountPoint
					)
				}
			}
		}

		return steps
	}


}
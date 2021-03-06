package com.github.kerubistan.kerub.planner.steps.vstorage.fs.base

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorageFactory
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.junix.common.OsCommand

abstract class AbstractCreateFileVirtualStorageFactory<S : AbstractOperationalStep> : AbstractOperationalStepFactory<S>() {

	abstract val requiredOsCommand : OsCommand

	final override fun produce(state: OperationalState): List<S> {
		val storageNotAllocated = AbstractCreateVirtualStorageFactory.listStorageNotAllocated(state)

		val storageTechnologies = state.controllerConfig.storageTechnologies
		return state.runningHosts.filter { requiredOsCommand.available(it.stat.capabilities) }
				.mapNotNull { hostData ->
					hostData.stat.capabilities?.storageCapabilities
							?.filterIsInstance<FsStorageCapability>()?.filter { capability ->
								capability.mountPoint in storageTechnologies.fsPathEnabled
								&& capability.fsType in storageTechnologies.fsTypeEnabled
					}?.map { mount ->
						storageNotAllocated.map { storage ->
							createStep(storage, hostData, mount)
						}
					}
				}.join().join()
	}

	abstract fun createStep(storage: VirtualStorageDevice, hostData: HostDataCollection, mount: FsStorageCapability): S

}
package com.github.kerubistan.kerub.planner.steps.vm.start.kvm

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.vm.allStorageAvailable
import com.github.kerubistan.kerub.planner.steps.vm.match
import com.github.kerubistan.kerub.planner.steps.vm.start.AbstractStartVmFactory
import com.github.kerubistan.kerub.planner.steps.vm.virtualStorageLinkInfo
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.junix.virt.virsh.LibvirtCapabilities
import com.github.kerubistan.kerub.utils.junix.virt.virsh.Virsh
import kotlin.reflect.KClass

object KvmStartVirtualMachineFactory : AbstractStartVmFactory<KvmStartVirtualMachine>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<KvmStartVirtualMachine> =
			getVmsToStart(state).map { vm ->
				state.runningHosts.mapNotNull { hostData ->
					val virtualStorageLinks = lazy {
						virtualStorageLinkInfo(
								state = state,
								links = vm.virtualStorageLinks,
								targetHostId = hostData.stat.id)
					}
					if (hostData.stat.capabilities?.os == OperatingSystem.Linux
							&& isHwVirtualizationSupported(hostData.stat)
							&& isKvmInstalled(hostData.stat)
							&& isKvmCapable(hostData.stat.capabilities.hypervisorCapabilities, vm)
							&& match(hostData, vm)
							&& allStorageAvailable(vm, virtualStorageLinks.value)) {
						KvmStartVirtualMachine(
								vm = vm,
								host = hostData.stat,
								storageLinks = virtualStorageLinks.value)

					} else null
				}
			}.join()

	internal fun isKvmCapable(hypervisorCapabilities: List<Any>, vm: VirtualMachine): Boolean {
		return hypervisorCapabilities.any {
			it is LibvirtCapabilities && it.guests.any { it.arch.name == vm.architecture }
		}
	}

	internal fun isKvmInstalled(host: Host)
			= Virsh.available(host.capabilities)
			&& host.capabilities?.installedSoftware?.any { it.name == "qemu-kvm" } ?: false

}
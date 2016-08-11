package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

abstract class AbstractStartVmFactory<S : AbstractOperationalStep> : AbstractOperationalStepFactory<S>() {

	fun getWorkingHosts(state: OperationalState, filter: (Host, HostDynamic) -> Boolean): Map<Host, HostDynamic> {
		return state.hosts.values.map {
			host ->
			val dyn = state.hostDyns[host.id]
			if (dyn != null && dyn.status == HostStatus.Up) host to dyn else null
		}.filterNotNull().toMap()
	}

	fun getWorkingHosts(state: OperationalState): Map<Host, HostDynamic> = getWorkingHosts(state) { host, dyn -> true }

	fun getVmsToStart(state: OperationalState, filter: (VirtualMachine) -> Boolean): List<VirtualMachine> {
		val vmsToRun = state.vms.values.filter {
			vm ->
			vm.expectations.any {
				it is VirtualMachineAvailabilityExpectation
						&& it.up
			}
					&& filter(vm)
		}
		val vmsActuallyRunning = state.vmDyns.values.filter { it.status == VirtualMachineStatus.Up }.map { it.id }
		val vmsToStart = vmsToRun.filter { !vmsActuallyRunning.contains(it.id) }.filter { checkStartRequirements(it, state) }
		return vmsToStart
	}

	fun getVmsToStart(state: OperationalState): List<VirtualMachine> = getVmsToStart(state) { true }

	private fun checkStartRequirements(virtualMachine: VirtualMachine, state: OperationalState): Boolean {
		return allDisksAvailable(virtualMachine, state)
	}

	private fun allDisksAvailable(virtualMachine: VirtualMachine, state: OperationalState): Boolean =
			virtualMachine.virtualStorageLinks.all {
				link ->
				state.vStorageDyns[link.virtualStorageId] != null

			}

}
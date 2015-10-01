package com.github.K0zka.kerub.stories.planner

import com.github.K0zka.kerub.model.*
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.expectations.CpuArchitectureExpectation
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import com.github.K0zka.kerub.model.messages.EntityUpdateMessage
import com.github.K0zka.kerub.planner.*
import com.github.K0zka.kerub.planner.steps.vm.start.StartVirtualMachine
import com.github.K0zka.kerub.services.impl.GB
import com.github.K0zka.kerub.services.impl.toStorageSize
import com.github.k0zka.finder4j.backtrack.BacktrackService
import cucumber.api.DataTable
import cucumber.api.PendingException
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.junit.Assert
import org.mockito.Matchers
import org.mockito.Mockito
import kotlin.reflect.jvm.java

public class PlannerDefs {

	var vms = listOf<VirtualMachine>()
	var hosts = listOf<Host>()

	var vmDyns = listOf<VirtualMachineDynamic>()
	var hostDyns = listOf<HostDynamic>()

	val backtrack: BacktrackService = BacktrackService(1)
	val executor: PlanExecutor = Mockito.mock(PlanExecutor::class.java)
	val builder: OperationalStateBuilder = Mockito.mock(OperationalStateBuilder::class.java)

	val planner: Planner = PlannerImpl(
			backtrack,
			executor,
			builder
	                                  )

	var executedPlans = listOf<Plan>()

	Before
	fun setup() {
		Mockito.`when`(builder.buildState()).then {
			OperationalState.fromLists(
					hosts = hosts,
					hostDyns = hostDyns,
					vms = vms,
					vmDyns = vmDyns
			                )
		}
		Mockito.doAnswer({
			                 executedPlans += (it.getArguments()[0] as Plan)
			                 Unit
		                 }).`when`(executor).execute(Matchers.any(Plan::class.java) ?: Plan(OperationalState()))
	}

	Given("^VMs:$")
	fun setVms(vmsTable: DataTable) {
		val raw = vmsTable.raw()
		for (row in raw.filter { it != raw.first() }) {
			val vm = VirtualMachine(
					name = row[0],
					memoryMb = Range<Int>(
							min = row[1].toStorageSize().toInt(),
							max = row[2].toStorageSize().toInt()
					                     ),
					nrOfCpus = row[3].toInt(),
					expectations = listOf(
							CpuArchitectureExpectation(
									cpuArchitecture = row[4]
							                          )
					                     )
			                       )
			this.vms = this.vms + vm
		}
	}

	Given("^hosts:$")
	fun hosts(hostsTable: DataTable) {
		for (row in hostsTable.raw().filter { it != hostsTable.raw().first() } ) {
			val host = Host(
					address = row[0],
					dedicated = true,
					publicKey = "",
					capabilities = HostCapabilities(
							os = OperatingSystem.Linux,
							cpuArchitecture = row[4],
							cpus = listOf(ProcessorInformation(
									manufacturer = "Test",
									coreCount = row[2].toInt(),
									threadCount = row[3].toInt(),
									flags = listOf(),
									l1cache = null,
									l2cache = null,
									l3cache = null,
									maxSpeedMhz = 3200,
									socket = "",
									version = "",
									voltage = null
							                                  )
							             ),
							chassis = null,
							distribution = null,
							devices = listOf(),
							installedSoftware = listOf(),
							system = null,
							totalMemory = 16.GB()
					                               )
			               )
			hosts += host
		}
	}

	When("^VM (\\S+) is started$")
	fun startVm(vm: String) {
		vms = vms.map {
			if (it.name == vm) {
				it.copy(expectations = (
						it.expectations
								+ VirtualMachineAvailabilityExpectation(
								level = ExpectationLevel.Want,
								up = true
								                                       )
						)
				       )
			} else {
				it
			}
		}
		planner.onEvent(EntityUpdateMessage(
				obj = vms.filter { it.name == vm }.first(),
				date = System.currentTimeMillis()
		                                   ));
	}

	Then("^VM (\\S+) gets scheduled on host (\\S+)$")
	fun verifyVmScheduledOnHost(vmName: String, hostAddress: String) {
		Assert.assertTrue(executedPlans.any {
			it.steps.any {
				it is StartVirtualMachine
						&& it.host.address == hostAddress
						&& it.vm.name == vmName
			}
		})
	}

	Given("^registered host:$")
	fun registered_host(hosts: DataTable) {
		throw PendingException();
	}

	Given("^no virtual machines$")
	fun no_virtual_machines() {
		throw PendingException();
	}

	When("^optimization is triggered$")
	fun optimization_is_triggered() {
		throw PendingException();
	}

	Then("^host (\\S+) should be go to power-save$")
	fun host_should_be_go_to_power_save(host: String) {
		throw PendingException();
	}

	Given("^status:$")
	fun status(vmstatus: DataTable) {
		throw PendingException();
	}

	Given("^host (\\S+) is Up$")
	fun setHostDyn(address : String) {
		var host = hosts.firstOrNull {it.address == address}!!
		hostDyns = hostDyns + HostDynamic(
				id = host.id,
		        status = HostStatus.Up
		                                 )

	}
}
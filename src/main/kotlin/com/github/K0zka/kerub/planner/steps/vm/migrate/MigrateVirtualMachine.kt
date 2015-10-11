package com.github.K0zka.kerub.planner.steps.vm.migrate

import com.github.K0zka.kerub.model.Constrained
import com.github.K0zka.kerub.model.Expectation
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.EccMemoryExpectation
import com.github.K0zka.kerub.model.expectations.NoMigrationExpectation
import com.github.K0zka.kerub.model.expectations.NotSameHostExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.costs.ComputationCost
import com.github.K0zka.kerub.planner.costs.Cost
import com.github.K0zka.kerub.planner.costs.NetworkCost
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import java.util.*

public class MigrateVirtualMachine(
		val vm: VirtualMachine,
		val source: Host,
		val target: Host) : AbstractOperationalStep() {

	override fun violations(state: OperationalState): Map<Constrained<Expectation>, List<Expectation>> {
		/**
		 * TODO
		 */
		val ret = HashMap<Constrained<Expectation>, List<Expectation>>()
		val vmViolations = ArrayList<Expectation>()
		for(expectation in vm.expectations) {
			when(expectation) {
				is NoMigrationExpectation ->
						vmViolations.add(expectation)
				is NotSameHostExpectation ->
						if(state.vmsOnHost(target.id).any { expectation.otherVmIds.contains(it.id) }) {
							vmViolations.add(expectation)
						}
				//TODO: and so on
			}
		}
		return ret
	}

	override fun take(state: OperationalState): OperationalState {
		return state
	}

	override fun getCost(state: OperationalState): List<Cost> = listOf(
			/*
			 * TODO
			 * This calculates cost based on the max, which is pessimistic
			 * rather than realistic.
			 */
			NetworkCost(
					hosts = listOf(source, target),
					bytes = vm.memory.max.toLong()
			           ),
			ComputationCost(
					host = target,
					cycles = vm.memory.max.toLong()
			               ),
			ComputationCost(
					host = source,
					cycles = vm.memory.max.toLong()
			               )

	                                           )
}
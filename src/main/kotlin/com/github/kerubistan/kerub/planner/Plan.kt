package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.join

data class Plan(
		val states: List<OperationalState>,
		val steps: List<AbstractOperationalStep> = listOf()
) {

	companion object {

		fun noop(step : AbstractOperationalStep, plan: Plan) : Boolean = true

		inline fun planBy(initial: OperationalState,
				   steps: List<AbstractOperationalStep>,
				   verify : (AbstractOperationalStep, Plan) -> Boolean = ::noop): Plan {
			var states = listOf(initial)
			steps.forEachIndexed { index, step ->
				val state = states.last()
				val subPlan = Plan(steps = steps.subList(fromIndex = 0, toIndex = index), states = states)
				if(verify(step, subPlan)) {
					states += step.take(state)
				} else {
					throw IllegalStateException("next step not found")
				}
			}
			return Plan(
					steps = steps,
					states = states
			)
		}
	}

	constructor(state: OperationalState, steps: List<AbstractOperationalStep> = listOf()) : this(listOf(state), steps)

	val state: OperationalState
		get() = states.last()

	fun reservations(): Collection<Reservation<*>> =
			steps.map { it.reservations() }.join().toSet()
}
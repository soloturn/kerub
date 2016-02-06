package com.github.K0zka.kerub.planner.costs

import java.util.Comparator

object ComputationCostComparator : Comparator<ComputationCost> {
	override fun compare(first: ComputationCost, second: ComputationCost): Int {
		return (first.cycles - second.cycles).toInt()
	}
}
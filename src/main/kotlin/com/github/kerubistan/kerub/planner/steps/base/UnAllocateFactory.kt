package com.github.kerubistan.kerub.planner.steps.base

import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.issues.problems.vstorage.VStorageDeviceOnRecyclingHost
import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.vstorage.fs.unallocate.UnAllocateFsFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.gvinum.unallocate.UnAllocateGvinumFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.unallocate.UnAllocateLvFactory

object UnAllocateFactory : StepFactoryCollection(
		listOf(UnAllocateLvFactory, UnAllocateFsFactory, UnAllocateGvinumFactory)
) {
	override val expectationHints = super.expectationHints + VirtualMachineAvailabilityExpectation::class
	override val problemHints = super.problemHints + setOf(VStorageDeviceOnRecyclingHost::class)

}
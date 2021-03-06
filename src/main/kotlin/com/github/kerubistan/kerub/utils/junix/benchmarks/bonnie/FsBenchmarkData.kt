package com.github.kerubistan.kerub.utils.junix.benchmarks.bonnie

import java.io.Serializable

data class FsBenchmarkData(
		val sequentialOutputPerChr: IoBenchmarkItem,
		val sequentialOutputPerBlock: IoBenchmarkItem,
		val sequentialOutputRewrite: IoBenchmarkItem,
		val sequentialInputPerChr: IoBenchmarkItem,
		val sequentialInputPerBlock: IoBenchmarkItem,
		val random: IoBenchmarkItem
) : Serializable
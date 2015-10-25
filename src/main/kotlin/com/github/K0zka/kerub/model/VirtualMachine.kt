package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonView
import com.github.K0zka.kerub.model.views.Detailed
import com.github.K0zka.kerub.model.views.Simple
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Indexed
import java.math.BigInteger
import java.util.*

/**
 * A virtual machine.
 */
@Indexed
@JsonTypeName("vm")
data class VirtualMachine constructor(
		override
        @JsonView(Simple::class)
        @DocumentId
        @JsonProperty("id")
		val id: UUID = UUID.randomUUID(),
		/**
		 * Name of the VM, which is not necessarily a unique identifier, but helps to find the VM.
		 */
        @Field
        @JsonView(Simple::class)
        @JsonProperty("name")
		val name: String,
		/**
		 * The number of vCPUs of the VM.
		 */
        @Field
        @JsonView(Detailed::class)
        @JsonProperty("nrOfCpus")
		val nrOfCpus: Int = 1,
		/**
		 * Memory allocation, a range (minimum-maximum)
		 */
        @Field
        @JsonView(Simple::class)
        @JsonProperty("memory")
		val memory: Range<BigInteger> = Range(BigInteger("1024"), BigInteger("2048")),
		/**
		 * List of expectations against the VM.
		 */
        @Field
        @JsonView(Detailed::class)
        @JsonProperty("expectations")
		override
		val expectations: List<Expectation> = listOf(),
		/**
		 * Storage devices of the VM
		 */
        @Field
        @JsonView(Detailed::class)
        @JsonProperty("storagedevices")
		val virtualStorageLinks: List<VirtualStorageLink> = listOf()
                         )
: Entity<UUID>, Constrained<Expectation>


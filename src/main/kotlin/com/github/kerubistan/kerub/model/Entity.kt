package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.kerubistan.kerub.data.alerts.DataLossAlert
import com.github.kerubistan.kerub.data.alerts.HostLostAlert
import com.github.kerubistan.kerub.data.alerts.HostOverheatingAlert
import com.github.kerubistan.kerub.data.alerts.NetworkLinkDownAlert
import com.github.kerubistan.kerub.data.alerts.StorageFailureAlert
import com.github.kerubistan.kerub.data.alerts.UnsatisfiedExpectationAlert
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import java.io.Serializable

/**
 * Generic entity type.
 * The only sure thing about an entity is that it has got an ID
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(Account::class),
		JsonSubTypes.Type(Host::class),
		JsonSubTypes.Type(HostDynamic::class),
		JsonSubTypes.Type(VirtualMachine::class),
		JsonSubTypes.Type(VirtualNetwork::class),
		JsonSubTypes.Type(VirtualStorageDevice::class),
		JsonSubTypes.Type(Project::class),
		JsonSubTypes.Type(Pool::class),
		JsonSubTypes.Type(Template::class),
		JsonSubTypes.Type(Network::class),
		JsonSubTypes.Type(AddEntry::class),
		JsonSubTypes.Type(DeleteEntry::class),
		JsonSubTypes.Type(UpdateEntry::class),
		JsonSubTypes.Type(Event::class),
		JsonSubTypes.Type(DataLossAlert::class),
		JsonSubTypes.Type(NetworkLinkDownAlert::class),
		JsonSubTypes.Type(StorageFailureAlert::class),
		JsonSubTypes.Type(HostLostAlert::class),
		JsonSubTypes.Type(HostOverheatingAlert::class),
		JsonSubTypes.Type(UnsatisfiedExpectationAlert::class)
)
interface Entity<T> : Serializable {
	//	@DocumentId
	//	@JsonProperty("id")
	val id: T
	val idStr : String
		@JsonIgnore
		get() = id.toString()
}
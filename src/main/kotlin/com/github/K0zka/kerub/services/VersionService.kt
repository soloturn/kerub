package com.github.K0zka.kerub.services

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.wordnik.swagger.annotations.*
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@ApiModel("VersionInfo", description = "Version information")

class VersionInfo @JsonCreator constructor (
        @JsonProperty("version")
        @ApiModelProperty(required = true, value = "Version")
		val version: String,
        @JsonProperty("vendor")
        @ApiModelProperty(required = true, value = "Vendor")
		val vendor: String,
        @JsonProperty("title")
        @ApiModelProperty(required = true, value = "Title")
		val title: String) {
}

@Api(description = "Version information", value = "s/r/meta/version")
@Produces("application/json")
@Consumes("application/json")
@Path("/meta/version")
public interface VersionService {

    @ApiOperation(value = "Get version", notes = "Get the Kerub version")
    @ApiResponses(ApiResponse(code = 200, message = "OK", response = VersionInfo::class))
    @Path("/")
    @GET
	fun getVersionInfo(): VersionInfo
}
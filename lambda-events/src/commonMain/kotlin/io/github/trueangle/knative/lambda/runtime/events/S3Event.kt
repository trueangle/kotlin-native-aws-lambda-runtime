package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class S3Event(
    @SerialName("Records") val records: List<Record>
) {

    @Serializable
    data class Record(
        @SerialName("eventVersion") val eventVersion: String,
        @SerialName("eventSource") val eventSource: String,
        @SerialName("awsRegion") val awsRegion: String,
        @SerialName("eventTime") val eventTime: String,
        @SerialName("eventName") val eventName: String,
        @SerialName("userIdentity") val userIdentity: UserIdentity,
        @SerialName("requestParameters") val requestParameters: RequestParameters,
        @SerialName("responseElements") val responseElements: Map<String, String>,
        @SerialName("s3") val s3: Entity
    )

    @Serializable
    data class RequestParameters(
        @SerialName("sourceIPAddress") val sourceIPAddress: String
    )

    @Serializable
    data class UserIdentity(
        @SerialName("principalId") val principalId: String
    )

    @Serializable
    data class Entity(
        @SerialName("configurationId") val configurationId: String,
        @SerialName("s3SchemaVersion") val schemaVersion: String,
        @SerialName("bucket") val bucket: Bucket,
        @SerialName("object") val s3object: S3Object
    )

    @Serializable
    data class Bucket(
        @SerialName("name") val name: String,
        @SerialName("ownerIdentity") val ownerIdentity: UserIdentity,
        @SerialName("arn") val arn: String
    )

    @Serializable
    data class S3Object(
        @SerialName("key") val key: String,
        @SerialName("size") val size: ULong? = null,
        @SerialName("versionId") val versionId: String,
        @SerialName("eTag") val eTag: String,
        @SerialName("sequencer") val sequencer: String
    )
}
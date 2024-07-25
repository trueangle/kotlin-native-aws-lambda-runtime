package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DynamoDBEvent(
    @SerialName("Records") val records: List<Record>
) {
    @Serializable
    data class Record(
        @SerialName("awsRegion") val awsRegion: String,
        @SerialName("dynamodb") val dynamodb: DynamoDBStreamRecord,
        @SerialName("eventID") val eventID: String,
        @SerialName("eventName") val eventName: String,
        @SerialName("eventSource") val eventSource: String,
        @SerialName("eventVersion") val eventVersion: String,
        @SerialName("eventSourceARN") val eventSourceArn: String,
        @SerialName("userIdentity") val userIdentity: UserIdentity?
    ) {
        @Serializable
        data class UserIdentity(
            @SerialName("type") val type: String,
            @SerialName("principalId") val principalId: String
        )
    }

    @Serializable
    data class DynamoDBStreamRecord(
        @SerialName("ApproximateCreationDateTime") val approximateCreationDateTime: SecondsEpochTime?,
        @SerialName("Keys") val keys: Map<String, String>?,
        @SerialName("NewImage") val newImage: Map<String, String>?,
        @SerialName("OldImage") val oldImage: Map<String, String>?,
        @SerialName("SequenceNumber") val sequenceNumber: String,
        @SerialName("SizeBytes") val sizeBytes: Long,
        @SerialName("StreamViewType") val streamViewType: StreamViewType
    ) {
        @Serializable
        data class SecondsEpochTime(val seconds: Long)
    }

    @Serializable
    enum class StreamViewType {
        @SerialName("NEW_IMAGE")
        NEW_IMAGE,

        @SerialName("OLD_IMAGE")
        OLD_IMAGE,

        @SerialName("NEW_AND_OLD_IMAGES")
        NEW_AND_OLD_IMAGES,

        @SerialName("KEYS_ONLY")
        KEYS_ONLY
    }
}

@Serializable
data class DynamoDBTimeWindowEvent(
    @SerialName("Records") val records: List<DynamoDBEvent.Record>,
    @SerialName("TimeWindowProperties") val timeWindowProperties: TimeWindowProperties
)

@Serializable
data class DynamoDBTimeWindowEventResponse(
    @SerialName("TimeWindowEventResponseProperties") val timeWindowEventResponseProperties: Map<String, String>,
    @SerialName("BatchItemFailures") val batchItemFailures: List<String>
)

@Serializable
enum class DynamoDBKeyType {
    @SerialName("HASH")
    HASH,

    @SerialName("RANGE")
    RANGE
}

@Serializable
enum class DynamoDBOperationType {
    @SerialName("INSERT")
    INSERT,

    @SerialName("MODIFY")
    MODIFY,

    @SerialName("REMOVE")
    REMOVE
}

@Serializable
enum class DynamoDBSharedIteratorType {
    @SerialName("TRIM_HORIZON")
    TRIM_HORIZON,

    @SerialName("LATEST")
    LATEST,

    @SerialName("AT_SEQUENCE_NUMBER")
    AT_SEQUENCE_NUMBER,

    @SerialName("AFTER_SEQUENCE_NUMBER")
    AFTER_SEQUENCE_NUMBER
}

@Serializable
enum class DynamoDBStreamStatus {
    @SerialName("ENABLING")
    ENABLING,

    @SerialName("ENABLED")
    ENABLED,

    @SerialName("DISABLING")
    DISABLING,

    @SerialName("DISABLED")
    DISABLED
}

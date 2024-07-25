package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SQSEvent(
    @SerialName("Records") val records: List<Record>
) {

    @Serializable
    data class Record(
        @SerialName("messageId") val messageId: String,
        @SerialName("receiptHandle") val receiptHandle: String,
        @SerialName("body") var body: String,
        @SerialName("md5OfBody") val md5OfBody: String,
        @SerialName("md5OfMessageAttributes") val md5OfMessageAttributes: String,
        @SerialName("attributes") val attributes: Map<String, String>,
        @SerialName("messageAttributes") val messageAttributes: Map<String, Attribute>,
        @SerialName("eventSourceARN") val eventSourceArn: String,
        @SerialName("eventSource") val eventSource: String,
        @SerialName("awsRegion") val awsRegion: String
    )

    @Serializable
    data class Attribute(
        @SerialName("stringValue") val stringValue: String?,
        @SerialName("binaryValue") val binaryValue: ByteArray?,
        @SerialName("stringListValues") val stringListValues: List<String>,
        @SerialName("binaryListValues") val binaryListValues: List<ByteArray>,
        @SerialName("dataType") val dataType: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Attribute) return false
            return stringValue == other.stringValue &&
                    binaryValue?.contentEquals(other.binaryValue) == true &&
                    stringListValues == other.stringListValues &&
                    binaryListValues == other.binaryListValues &&
                    dataType == other.dataType
        }

        override fun hashCode(): Int {
            var result = stringValue?.hashCode() ?: 0
            result = 31 * result + (binaryValue?.contentHashCode() ?: 0)
            result = 31 * result + stringListValues.hashCode()
            result = 31 * result + binaryListValues.hashCode()
            result = 31 * result + dataType.hashCode()
            return result
        }
    }
}

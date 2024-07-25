package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SESEvent(
    @SerialName("Records") val records: List<Record>
) {
    @Serializable
    data class Record(
        @SerialName("eventSource") val eventSource: String,
        @SerialName("eventVersion") val eventVersion: String,
        @SerialName("ses") val ses: Message
    )

    @Serializable
    data class Message(
        @SerialName("mail") val mail: Mail,
        @SerialName("receipt") val receipt: Receipt
    )

    @Serializable
    data class Mail(
        @SerialName("commonHeaders") val commonHeaders: CommonHeaders,
        @SerialName("destination") val destination: List<String>,
        @SerialName("headers") val headers: List<Header>,
        @SerialName("headersTruncated") val headersTruncated: Boolean,
        @SerialName("messageId") val messageId: String,
        @SerialName("source") val source: String,
        @SerialName("timestamp") val timestamp: String
    )

    @Serializable
    data class CommonHeaders(
        @SerialName("bcc") val bcc: List<String>?,
        @SerialName("cc") val cc: List<String>?,
        @SerialName("date") val date: String,
        @SerialName("from") val from: List<String>,
        @SerialName("messageId") val messageId: String?,
        @SerialName("returnPath") val returnPath: String?,
        @SerialName("subject") val subject: String?,
        @SerialName("to") val to: List<String>?
    )

    @Serializable
    data class Header(
        @SerialName("name") val name: String,
        @SerialName("value") val value: String
    )

    @Serializable
    data class Receipt(
        @SerialName("action") val action: Action,
        @SerialName("dmarcPolicy") val dmarcPolicy: DMARCPolicy?,
        @SerialName("dmarcVerdict") val dmarcVerdict: Verdict?,
        @SerialName("dkimVerdict") val dkimVerdict: Verdict,
        @SerialName("processingTimeMillis") val processingTimeMillis: Int,
        @SerialName("recipients") val recipients: List<String>,
        @SerialName("spamVerdict") val spamVerdict: Verdict,
        @SerialName("spfVerdict") val spfVerdict: Verdict,
        @SerialName("timestamp") val timestamp: String,
        @SerialName("virusVerdict") val virusVerdict: Verdict
    )

    @Serializable
    data class Action(
        @SerialName("functionArn") val functionArn: String,
        @SerialName("invocationType") val invocationType: String,
        @SerialName("type") val type: String
    )

    @Serializable
    data class Verdict(
        @SerialName("status") val status: Status
    )

    @Serializable
    enum class DMARCPolicy {
        @SerialName("none")
        NONE,
        @SerialName("quarantine")
        QUARANTINE,
        @SerialName("reject")
        REJECT
    }

    @Serializable
    enum class Status {
        @SerialName("PASS")
        PASS,
        @SerialName("FAIL")
        FAIL,
        @SerialName("GRAY")
        GRAY,
        @SerialName("PROCESSING_FAILED")
        PROCESSING_FAILED
    }
}

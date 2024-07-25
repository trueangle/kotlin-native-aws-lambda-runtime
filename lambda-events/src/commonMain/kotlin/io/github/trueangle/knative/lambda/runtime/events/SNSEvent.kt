package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SNSEvent(
    @SerialName("Records") val records: List<Record>
) {

    @Serializable
    data class Record(
        @SerialName("EventVersion") val eventVersion: String,
        @SerialName("EventSubscriptionArn") val eventSubscriptionArn: String,
        @SerialName("EventSource") val eventSource: String,
        @SerialName("Sns") val sns: Message
    )

    @Serializable
    data class Message(
        @SerialName("Signature") val signature: String,
        @SerialName("MessageId") val messageId: String,
        @SerialName("Type") val type: String,
        @SerialName("TopicArn") val topicArn: String,
        @SerialName("MessageAttributes") val messageAttributes: Map<String, Attribute>,
        @SerialName("SignatureVersion") val signatureVersion: String,
        @SerialName("Timestamp") val timestamp: String,
        @SerialName("SigningCertUrl") val signingCertURL: String,
        @SerialName("Message") val message: String,
        @SerialName("UnsubscribeUrl") val unsubscribeURL: String,
        @SerialName("Subject") val subject: String?
    )

    @Serializable
    data class Attribute(@SerialName("Type") val type: String, @SerialName("Value") val value: String)
}

@Serializable
data class CloudWatchAlarmSNSPayload(
    @SerialName("AlarmName") val alarmName: String,
    @SerialName("AlarmDescription") val alarmDescription: String,
    @SerialName("AWSAccountId") val awsAccountId: String,
    @SerialName("NewStateValue") val newStateValue: String,
    @SerialName("NewStateReason") val newStateReason: String,
    @SerialName("StateChangeTime") val stateChangeTime: String,
    @SerialName("Region") val region: String,
    @SerialName("AlarmArn") val alarmArn: String,
    @SerialName("OldStateValue") val oldStateValue: String,
    @SerialName("Trigger") val trigger: CloudWatchAlarmTrigger
)

@Serializable
data class CloudWatchAlarmTrigger(
    @SerialName("Period") val period: Long,
    @SerialName("EvaluationPeriods") val evaluationPeriods: Long,
    @SerialName("ComparisonOperator") val comparisonOperator: String,
    @SerialName("Threshold") val threshold: Double,
    @SerialName("TreatMissingData") val treatMissingData: String,
    @SerialName("EvaluateLowSampleCountPercentile") val evaluateLowSampleCountPercentile: String,
    @SerialName("Metrics") val metrics: List<CloudWatchMetricDataQuery>?,
    @SerialName("MetricName") val metricName: String?,
    @SerialName("Namespace") val namespace: String?,
    @SerialName("StatisticType") val statisticType: String?,
    @SerialName("Statistic") val statistic: String?,
    @SerialName("Unit") val unit: String?,
    @SerialName("Dimensions") val dimensions: List<CloudWatchDimension>?
)

@Serializable
data class CloudWatchMetricDataQuery(
    @SerialName("Expression") val expression: String?,
    @SerialName("Id") val id: String,
    @SerialName("Label") val label: String?,
    @SerialName("MetricStat") val metricStat: CloudWatchMetricStat?,
    @SerialName("Period") val period: Long?,
    @SerialName("ReturnData") val returnData: Boolean?
)

@Serializable
data class CloudWatchMetricStat(
    @SerialName("Metric") val metric: CloudWatchMetric,
    @SerialName("Period") val period: Long,
    @SerialName("Stat") val stat: String,
    @SerialName("Unit") val unit: String?
)

@Serializable
data class CloudWatchMetric(
    @SerialName("Dimensions") val dimensions: List<CloudWatchDimension>?,
    @SerialName("MetricName") val metricName: String,
    @SerialName("Namespace") val namespace: String
)

@Serializable
data class CloudWatchDimension(
    @SerialName("name") val name: String,
    @SerialName("value") val value: String
)

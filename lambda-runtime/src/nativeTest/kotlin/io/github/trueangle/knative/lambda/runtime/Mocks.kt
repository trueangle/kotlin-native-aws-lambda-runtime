package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.api.Context
import kotlinx.serialization.Serializable

internal fun mockContext(awsRequestId: String = "awsRequestId") = Context(
    awsRequestId = awsRequestId,
    xrayTracingId = "dummyXrayTracingId",
    deadlineTimeInMs = 100,
    invokedFunctionArn = "arn:aws:lambda:us-west-2:123456789012:function:dummy-function",
    invokedFunctionName = "dummy-function",
    invokedFunctionVersion = "\$LATEST",
    memoryLimitMb = 512,
    clientContext = null,
    cognitoIdentity = null
)

@Serializable
internal data class SampleObject(val hello: String)
internal data class NonSerialObject(val hello: String)
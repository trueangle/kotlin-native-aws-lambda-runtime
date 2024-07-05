package io.github.trueangle.knative.lambda.runtime.api

class InvocationEvent(val payload: String, val context: Context)

data class Context(
    /**
     * The Lambda request ID associated with the request.
     */
    val requestId: String,
    /**
     * The X-Ray tracing ID.
     */
    val xrayTracingId: String?,
    /**
     * Function execution deadline counted in milliseconds since the Unix epoch.
     */
    val deadlineTimeInMs: Long,
    /**
     * The ARN of the Lambda function being invoked.
     */
    val invokedFunctionArn: String,
    /**
     * The client context header. This field is populated when the function is invoked from a mobile client.
     */
    val clientContext: String?,
    /**
     * The Cognito Identity context for the invocation. This field is populated when the function is invoked with AWS
     * credentials obtained from Cognito Identity.
     */
    val cognitoIdentity: String?
)
package io.github.trueangle.knative.lambda.runtime.api

data class Context(
    /**
     * The Lambda request ID associated with the request.
     */
    val awsRequestId: String,
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
     * Gets the name of the function being executed.
     */
    val invokedFunctionName: String,
    /**
     * Gets the version of the function being executed.
     */
    val invokedFunctionVersion: String,
    /**
     * Gets the memory size configured for the Lambda function
     *
     */
    val memoryLimitMb: Int,
    /**
     * The client context header. This field is populated when the function is invoked from a mobile client.
     */
    val clientContext: ClientContext?,
    /**
     * The Cognito Identity context for the invocation. This field is populated when the function is invoked with AWS
     * credentials obtained from Cognito Identity.
     */
    val cognitoIdentity: CognitoIdentity?,
) {
    data class ClientContext(
        /**
         * Gets the client information provided by the AWS Mobile SDK
         */
        val client: Client,
        /**
         * Gets custom values set by the client application
         */
        val customValues: Map<String, String>,
        /**
         * Gets environment information provided by mobile SDK, immutable.
         */
        val environment: Map<String, String>
    )

    data class Client(
        val installationId: String,
        val appTitle: String,
        val appVersionName: String,
        val appVersionCode: String,
        val appPackageName: String
    )

    data class CognitoIdentity(
        val identityId: String,
        val identityPoolId: String
    )
}
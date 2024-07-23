import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// `ApiGatewayProxyRequest` contains data coming from the API Gateway proxy
@Serializable
data class ApiGatewayProxyRequest(
    @SerialName("resource") val resource: String,
    @SerialName("path") val path: String,
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, List<String>>,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, List<String>>,
    @SerialName("multiValueQueryStringParameters") val multiValueQueryStringParameters: Map<String, List<String>>,
    @SerialName("pathParameters") val pathParameters: Map<String, String>,
    @SerialName("stageVariables") val stageVariables: Map<String, String>,
    @SerialName("requestContext") val requestContext: ApiGatewayProxyRequestContext,
    @SerialName("body") val body: String,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean
)

// `ApiGatewayProxyResponse` configures the response to be returned by API Gateway for the request
@Serializable
data class ApiGatewayProxyResponse(
    @SerialName("statusCode") val statusCode: Long,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, List<String>>,
    @SerialName("body") val body: String,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean
)

// `ApiGatewayProxyRequestContext` contains the information to identify the AWS account and resources invoking the Lambda function.
@Serializable
data class ApiGatewayProxyRequestContext(
    @SerialName("accountId") val accountId: String,
    @SerialName("resourceId") val resourceId: String,
    @SerialName("operationName") val operationName: String,
    @SerialName("stage") val stage: String,
    @SerialName("domainName") val domainName: String,
    @SerialName("domainPrefix") val domainPrefix: String,
    @SerialName("requestId") val requestId: String,
    @SerialName("protocol") val protocol: String,
    @SerialName("identity") val identity: ApiGatewayRequestIdentity,
    @SerialName("resourcePath") val resourcePath: String,
    @SerialName("path") val path: String,
    @SerialName("authorizer") val authorizer: ApiGatewayRequestAuthorizer,
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("requestTime") val requestTime: String,
    @SerialName("requestTimeEpoch") val requestTimeEpoch: Long,
    @SerialName("apiId") val apiId: String
)

// `ApiGatewayV2httpRequest` contains data coming from the new HTTP API Gateway
@Serializable
data class ApiGatewayV2httpRequest(
    @SerialName("type") val kind: String,
    @SerialName("methodArn") val methodArn: String,
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("identitySource") val identitySource: String,
    @SerialName("authorizationToken") val authorizationToken: String,
    @SerialName("resource") val resource: String,
    @SerialName("version") val version: String,
    @SerialName("routeKey") val routeKey: String,
    @SerialName("rawPath") val rawPath: String,
    @SerialName("rawQueryString") val rawQueryString: String,
    @SerialName("cookies") val cookies: List<String>,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, List<String>>,
    @SerialName("pathParameters") val pathParameters: Map<String, String>,
    @SerialName("requestContext") val requestContext: ApiGatewayV2httpRequestContext,
    @SerialName("stageVariables") val stageVariables: Map<String, String>,
    @SerialName("body") val body: String,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean
)

// `ApiGatewayV2httpRequestContext` contains the information to identify the AWS account and resources invoking the Lambda function.
@Serializable
data class ApiGatewayV2httpRequestContext(
    @SerialName("routeKey") val routeKey: String,
    @SerialName("accountId") val accountId: String,
    @SerialName("stage") val stage: String,
    @SerialName("requestId") val requestId: String,
    @SerialName("authorizer") val authorizer: ApiGatewayRequestAuthorizer?,
    @SerialName("apiId") val apiId: String,
    @SerialName("domainName") val domainName: String,
    @SerialName("domainPrefix") val domainPrefix: String,
    @SerialName("time") val time: String,
    @SerialName("timeEpoch") val timeEpoch: Long,
    @SerialName("http") val http: ApiGatewayV2httpRequestContextHttpDescription,
    @SerialName("authentication") val authentication: ApiGatewayV2httpRequestContextAuthentication?
)

// `ApiGatewayRequestAuthorizer` contains authorizer information for the request context.
@Serializable
data class ApiGatewayRequestAuthorizer(
    @SerialName("jwt") val jwt: ApiGatewayRequestAuthorizerJwtDescription?,
    @SerialName("lambda") val fields: Map<String, String>,
    @SerialName("iam") val iam: ApiGatewayRequestAuthorizerIamDescription?
)

// `ApiGatewayRequestAuthorizerJwtDescription` contains JWT authorizer information for the request context.
@Serializable
data class ApiGatewayRequestAuthorizerJwtDescription(
    @SerialName("claims") val claims: Map<String, String>,
    @SerialName("scopes") val scopes: List<String>?
)

// `ApiGatewayRequestAuthorizerIamDescription` contains IAM information for the request context.
@Serializable
data class ApiGatewayRequestAuthorizerIamDescription(
    @SerialName("accessKey") val accessKey: String?,
    @SerialName("accountId") val accountId: String?,
    @SerialName("callerId") val callerId: String?,
    @SerialName("cognitoIdentity") val cognitoIdentity: ApiGatewayRequestAuthorizerCognitoIdentity?,
    @SerialName("principalOrgId") val principalOrgId: String?,
    @SerialName("userArn") val userArn: String?,
    @SerialName("userId") val userId: String?
)

// `ApiGatewayRequestAuthorizerCognitoIdentity` contains Cognito identity information for the request context.
@Serializable
data class ApiGatewayRequestAuthorizerCognitoIdentity(
    @SerialName("amr") val amr: List<String>,
    @SerialName("identityId") val identityId: String?,
    @SerialName("identityPoolId") val identityPoolId: String?
)

// `ApiGatewayV2httpRequestContextHttpDescription` contains HTTP information for the request context.
@Serializable
data class ApiGatewayV2httpRequestContextHttpDescription(
    @SerialName("method") val method: String,
    @SerialName("path") val path: String,
    @SerialName("protocol") val protocol: String,
    @SerialName("sourceIp") val sourceIp: String,
    @SerialName("userAgent") val userAgent: String
)

// `ApiGatewayV2httpResponse` configures the response to be returned by API Gateway V2 for the request
@Serializable
data class ApiGatewayV2httpResponse(
    @SerialName("statusCode") val statusCode: Long,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, List<String>>,
    @SerialName("body") val body: String,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean,
    @SerialName("cookies") val cookies: List<String>
)

// `ApiGatewayRequestIdentity` contains identity information for the request caller.
@Serializable
data class ApiGatewayRequestIdentity(
    @SerialName("cognitoIdentityPoolId") val cognitoIdentityPoolId: String?,
    @SerialName("accountId") val accountId: String?,
    @SerialName("cognitoIdentityId") val cognitoIdentityId: String?,
    @SerialName("caller") val caller: String?,
    @SerialName("apiKey") val apiKey: String?,
    @SerialName("apiKeyId") val apiKeyId: String?,
    @SerialName("accessKey") val accessKey: String?,
    @SerialName("sourceIp") val sourceIp: String?,
    @SerialName("cognitoAuthenticationType") val cognitoAuthenticationType: String?,
    @SerialName("cognitoAuthenticationProvider") val cognitoAuthenticationProvider: String?,
    @SerialName("userArn") val userArn: String?,
    @SerialName("userAgent") val userAgent: String?,
    @SerialName("user") val user: String?
)

// `ApiGatewayWebsocketProxyRequest` contains data coming from the API Gateway proxy
@Serializable
data class ApiGatewayWebsocketProxyRequest(
    @SerialName("resource") val resource: String,
    @SerialName("path") val path: String,
    @SerialName("httpMethod") val httpMethod: String?,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, List<String>>,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, List<String>>,
    @SerialName("multiValueQueryStringParameters") val multiValueQueryStringParameters: Map<String, List<String>>,
    @SerialName("pathParameters") val pathParameters: Map<String, String>,
    @SerialName("stageVariables") val stageVariables: Map<String, String>,
    @SerialName("requestContext") val requestContext: ApiGatewayWebsocketProxyRequestContext,
    @SerialName("body") val body: String,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean
)

// Represents the API Gateway WebSocket Proxy Request Context
@Serializable
data class ApiGatewayWebsocketProxyRequestContext(
    @SerialName("accountId") val accountId: String? = null,
    @SerialName("resourceId") val resourceId: String? = null,
    @SerialName("stage") val stage: String? = null,
    @SerialName("requestId") val requestId: String? = null,
    @SerialName("identity") val identity: ApiGatewayRequestIdentity,
    @SerialName("resourcePath") val resourcePath: String? = null,
    @SerialName("authorizer") val authorizer: ApiGatewayRequestAuthorizer,
    @SerialName("httpMethod") val httpMethod: String? = null,
    @SerialName("apiId") val apiId: String? = null,
    @SerialName("connectedAt") val connectedAt: Long,
    @SerialName("connectionId") val connectionId: String? = null,
    @SerialName("domainName") val domainName: String? = null,
    @SerialName("error") val error: String? = null,
    @SerialName("eventType") val eventType: String? = null,
    @SerialName("extendedRequestId") val extendedRequestId: String? = null,
    @SerialName("integrationLatency") val integrationLatency: String? = null,
    @SerialName("messageDirection") val messageDirection: String? = null,
    @SerialName("messageId") val messageId: String? = null,
    @SerialName("requestTime") val requestTime: String? = null,
    @SerialName("requestTimeEpoch") val requestTimeEpoch: Long,
    @SerialName("routeKey") val routeKey: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("disconnectStatusCode") val disconnectStatusCode: Long? = null,
    @SerialName("disconnectReason") val disconnectReason: String? = null
)

// Represents the API Gateway Custom Authorizer Request Type Request Identity
@Serializable
data class ApiGatewayCustomAuthorizerRequestTypeRequestIdentity(
    @SerialName("apiKeyId") val apiKeyId: String? = null,
    @SerialName("apiKey") val apiKey: String? = null,
    @SerialName("sourceIp") val sourceIp: String? = null,
    @SerialName("clientCert") val clientCert: ApiGatewayCustomAuthorizerRequestTypeRequestIdentityClientCert? = null
)

// Represents the client certificate information for the Custom Authorizer Request Type Request Identity
@Serializable
data class ApiGatewayCustomAuthorizerRequestTypeRequestIdentityClientCert(
    @SerialName("clientCertPem") val clientCertPem: String? = null,
    @SerialName("issuerDN") val issuerDn: String? = null,
    @SerialName("serialNumber") val serialNumber: String? = null,
    @SerialName("subjectDN") val subjectDn: String? = null,
    @SerialName("validity") val validity: ApiGatewayCustomAuthorizerRequestTypeRequestIdentityClientCertValidity
)

// Represents the validity information of the client certificate
@Serializable
data class ApiGatewayCustomAuthorizerRequestTypeRequestIdentityClientCertValidity(
    @SerialName("notAfter") val notAfter: String? = null,
    @SerialName("notBefore") val notBefore: String? = null
)

// Represents the authentication context information for the request caller
@Serializable
data class ApiGatewayV2httpRequestContextAuthentication(
    @SerialName("clientCert") val clientCert: ApiGatewayV2httpRequestContextAuthenticationClientCert? = null
)

// Represents the client certificate information for the V2 HTTP Request Context Authentication
@Serializable
data class ApiGatewayV2httpRequestContextAuthenticationClientCert(
    @SerialName("clientCertPem") val clientCertPem: String? = null,
    @SerialName("issuerDN") val issuerDn: String? = null,
    @SerialName("serialNumber") val serialNumber: String? = null,
    @SerialName("subjectDN") val subjectDn: String? = null,
    @SerialName("validity") val validity: ApiGatewayV2httpRequestContextAuthenticationClientCertValidity
)

// Represents the validity information of the client certificate in V2 HTTP Request Context Authentication
@Serializable
data class ApiGatewayV2httpRequestContextAuthenticationClientCertValidity(
    @SerialName("notAfter") val notAfter: String? = null,
    @SerialName("notBefore") val notBefore: String? = null
)

// Represents the custom authorizer V1 request type request context
@Serializable
data class ApiGatewayV2CustomAuthorizerV1RequestTypeRequestContext(
    @SerialName("path") val path: String? = null,
    @SerialName("accountId") val accountId: String? = null,
    @SerialName("resourceId") val resourceId: String? = null,
    @SerialName("stage") val stage: String? = null,
    @SerialName("requestId") val requestId: String? = null,
    @SerialName("identity") val identity: ApiGatewayCustomAuthorizerRequestTypeRequestIdentity,
    @SerialName("resourcePath") val resourcePath: String? = null,
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("apiId") val apiId: String? = null
)

// Represents the V2 custom authorizer request
@Serializable
data class ApiGatewayV2CustomAuthorizerV1Request(
    @SerialName("version") val version: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("methodArn") val methodArn: String? = null,
    @SerialName("identitySource") val identitySource: String? = null,
    @SerialName("authorizationToken") val authorizationToken: String? = null,
    @SerialName("resource") val resource: String? = null,
    @SerialName("path") val path: String? = null,
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("headers") val headers: Map<String, String> = emptyMap(),
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, String> = emptyMap(),
    @SerialName("pathParameters") val pathParameters: Map<String, String> = emptyMap(),
    @SerialName("stageVariables") val stageVariables: Map<String, String> = emptyMap(),
    @SerialName("requestContext") val requestContext: ApiGatewayV2CustomAuthorizerV1RequestTypeRequestContext
)

// Represents the V2 custom authorizer V2 request
@Serializable
data class ApiGatewayV2CustomAuthorizerV2Request(
    @SerialName("version") val version: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("routeArn") val routeArn: String? = null,
    @SerialName("identitySource") val identitySource: List<String>? = null,
    @SerialName("routeKey") val routeKey: String? = null,
    @SerialName("rawPath") val rawPath: String? = null,
    @SerialName("rawQueryString") val rawQueryString: String? = null,
    @SerialName("cookies") val cookies: List<String> = emptyList(),
    @SerialName("headers") val headers: Map<String, String> = emptyMap(),
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, String> = emptyMap(),
    @SerialName("requestContext") val requestContext: ApiGatewayV2httpRequestContext,
    @SerialName("pathParameters") val pathParameters: Map<String, String> = emptyMap(),
    @SerialName("stageVariables") val stageVariables: Map<String, String> = emptyMap()
)

// Represents the custom authorizer context
@Serializable
data class ApiGatewayCustomAuthorizerContext(
    @SerialName("principalId") val principalId: String? = null,
    @SerialName("stringKey") val stringKey: String? = null,
    @SerialName("numKey") val numKey: Long? = null,
    @SerialName("boolKey") val boolKey: Boolean = false
)

// Represents the custom authorizer request type request context
@Serializable
data class ApiGatewayCustomAuthorizerRequestTypeRequestContext(
    @SerialName("path") val path: String? = null,
    @SerialName("accountId") val accountId: String? = null,
    @SerialName("resourceId") val resourceId: String? = null,
    @SerialName("stage") val stage: String? = null,
    @SerialName("requestId") val requestId: String? = null,
    @SerialName("identity") val identity: ApiGatewayCustomAuthorizerRequestTypeRequestIdentity? = null,
    @SerialName("resourcePath") val resourcePath: String? = null,
    @SerialName("httpMethod") val httpMethod: String? = null,
    @SerialName("apiId") val apiId: String? = null
)

// Represents the custom authorizer request
@Serializable
data class ApiGatewayCustomAuthorizerRequest(
    @SerialName("type") val type: String? = null,
    @SerialName("authorizationToken") val authorizationToken: String? = null,
    @SerialName("methodArn") val methodArn: String? = null
)

// Represents the custom authorizer request type request
@Serializable
data class ApiGatewayCustomAuthorizerRequestTypeRequest(
    @SerialName("type") val type: String? = null,
    @SerialName("methodArn") val methodArn: String? = null,
    @SerialName("resource") val resource: String? = null,
    @SerialName("path") val path: String? = null,
    @SerialName("httpMethod") val httpMethod: String? = null,
    @SerialName("headers") val headers: Map<String, String> = emptyMap(),
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, List<String>> = emptyMap(),
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, List<String>> = emptyMap(),
    @SerialName("multiValueQueryStringParameters") val multiValueQueryStringParameters: Map<String, List<String>> = emptyMap(),
    @SerialName("pathParameters") val pathParameters: Map<String, String> = emptyMap(),
    @SerialName("stageVariables") val stageVariables: Map<String, String> = emptyMap(),
    @SerialName("requestContext") val requestContext: ApiGatewayCustomAuthorizerRequestTypeRequestContext
)

// Represents the custom authorizer response
@Serializable
data class ApiGatewayCustomAuthorizerResponse<T>(
    @SerialName("principalId") val principalId: String? = null,
    @SerialName("policyDocument") val policyDocument: ApiGatewayCustomAuthorizerPolicy,
    @SerialName("context") @Contextual val context: T,
    @SerialName("usageIdentifierKey") val usageIdentifierKey: String? = null
) where T : Any

// Represents the simple custom authorizer response
@Serializable
data class ApiGatewayV2CustomAuthorizerSimpleResponse<T>(
    @SerialName("isAuthorized") val isAuthorized: Boolean,
    @SerialName("context") @Contextual val context: T
) where T : Any

// Represents the IAM policy response for V2 custom authorizer
@Serializable
data class ApiGatewayV2CustomAuthorizerIamPolicyResponse<T>(
    @SerialName("principalId") val principalId: String? = null,
    @SerialName("policyDocument") val policyDocument: ApiGatewayCustomAuthorizerPolicy,
    @SerialName("context") @Contextual val context: T
) where T : Any

// Represents an IAM policy
@Serializable
data class ApiGatewayCustomAuthorizerPolicy(
    @SerialName("version") val version: String? = null,
    @SerialName("statement") val statement: List<IamPolicyStatement> = emptyList()
)

// todo remove
// Represents an IAM policy statement
@Serializable
data class IamPolicyStatement(
    @SerialName("Effect") val effect: String,
    @SerialName("Action") val action: List<String>,
    @SerialName("Resource") val resource: List<String>
)

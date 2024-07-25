package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CognitoEvent(
    @SerialName("datasetName") val datasetName: String,
    @SerialName("datasetRecords") val datasetRecords: Map<String, Record>,
    @SerialName("eventType") val eventType: String,
    @SerialName("identityId") val identityId: String,
    @SerialName("identityPoolId") val identityPoolID: String,
    @SerialName("region") val region: String,
    @SerialName("version") val version: Int
) {
    @Serializable
    data class Record(
        @SerialName("newValue") val newValue: String,
        @SerialName("oldValue") val oldValue: String,
        @SerialName("op") val op: String
    )
}

@Serializable
data class CognitoEventUserPoolsPreSignup(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: PreSignupRequest,
    @SerialName("response") val response: PreSignupResponse
) {
    @Serializable
    data class PreSignupRequest(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("validationData") val validationData: Map<String, String>,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>
    )

    @Serializable
    data class PreSignupResponse(
        @SerialName("autoConfirmUser") val autoConfirmUser: Boolean,
        @SerialName("autoVerifyEmail") val autoVerifyEmail: Boolean,
        @SerialName("autoVerifyPhone") val autoVerifyPhone: Boolean
    )
}

@Serializable
data class CognitoEventUserPoolsPreAuthentication(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: PreAuthenticationRequest,
    @SerialName("response") val response: Map<String, String>
) {
    @Serializable
    data class PreAuthenticationRequest(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("validationData") val validationData: Map<String, String>
    )
}

@Serializable
data class CognitoEventUserPoolsPostConfirmation(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: PostConfirmationRequest,
    @SerialName("response") val response: Map<String, String>
) {
    @Serializable
    data class PostConfirmationRequest(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>
    )
}

@Serializable
data class CognitoEventUserPoolsPreTokenGen(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: PreTokenGenRequest,
    @SerialName("response") val response: PreTokenGenResponse
) {
    @Serializable
    data class PreTokenGenRequest(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("groupConfiguration") val groupConfiguration: GroupConfiguration,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>
    )

    @Serializable
    data class PreTokenGenResponse(
        @SerialName("claimsOverrideDetails") val claimsOverrideDetails: ClaimsOverrideDetails
    )
}

@Serializable
data class CognitoEventUserPoolsPreTokenGenV2(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: PreTokenGenV2Request,
    @SerialName("response") val response: PreTokenGenV2Response
) {
    @Serializable
    data class PreTokenGenV2Request(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("groupConfiguration") val groupConfiguration: GroupConfiguration,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>? = null,
        @SerialName("scopes") val scopes: List<String>
    )

    @Serializable
    data class PreTokenGenV2Response(
        @SerialName("claimsAndScopeOverrideDetails") val claimsAndScopeOverrideDetails: ClaimsAndScopeOverrideDetails
    )
}

@Serializable
data class CognitoEventUserPoolsPostAuthentication(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: PostAuthenticationRequest,
    @SerialName("response") val response: Map<String, String>
) {
    @Serializable
    data class PostAuthenticationRequest(
        @SerialName("newDeviceUsed") val newDeviceUsed: Boolean,
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>
    )
}

@Serializable
data class CognitoEventUserPoolsMigrateUser(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: MigrateUserRequest,
    @SerialName("response") val response: MigrateUserResponse
) {
    @Serializable
    data class MigrateUserRequest(
        @SerialName("password") val password: String,
        @SerialName("validationData") val validationData: Map<String, String>,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>
    )

    @Serializable
    data class MigrateUserResponse(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("finalUserStatus") val finalUserStatus: String,
        @SerialName("messageAction") val messageAction: String,
        @SerialName("desiredDeliveryMediums") val desiredDeliveryMediums: List<String>,
        @SerialName("forceAliasCreation") val forceAliasCreation: Boolean
    )
}

@Serializable
data class CognitoEventUserPoolsCustomMessage(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: CustomMessageRequest,
    @SerialName("response") val response: CustomMessageResponse
) {
    @Serializable
    data class CustomMessageRequest(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("codeParameter") val codeParameter: String,
        @SerialName("usernameParameter") val usernameParameter: String,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>
    )

    @Serializable
    data class CustomMessageResponse(
        @SerialName("smsMessage") val smsMessage: String,
        @SerialName("emailMessage") val emailMessage: String,
        @SerialName("emailSubject") val emailSubject: String
    )
}

@Serializable
data class CognitoEventUserPoolsDefineAuthChallenge(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: DefineAuthChallengeRequest,
    @SerialName("response") val response: DefineAuthChallengeResponse
) {
    @Serializable
    data class DefineAuthChallengeRequest(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("session") val session: List<CognitoEventUserPoolsChallengeResult>,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>,
        @SerialName("userNotFound") val userNotFound: Boolean
    )

    @Serializable
    data class DefineAuthChallengeResponse(
        @SerialName("challengeName") val challengeName: String,
        @SerialName("issueTokens") val issueTokens: Boolean,
        @SerialName("failAuthentication") val failAuthentication: Boolean
    )
}

@Serializable
data class CognitoEventUserPoolsCreateAuthChallenge(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: CreateAuthChallengeRequest,
    @SerialName("response") val response: CreateAuthChallengeResponse
) {
    @Serializable
    data class CreateAuthChallengeRequest(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("challengeName") val challengeName: String,
        @SerialName("session") val session: List<CognitoEventUserPoolsChallengeResult>,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>
    )

    @Serializable
    data class CreateAuthChallengeResponse(
        @SerialName("publicChallengeParameters") val publicChallengeParameters: Map<String, String>,
        @SerialName("privateChallengeParameters") val privateChallengeParameters: Map<String, String>,
        @SerialName("challengeMetadata") val challengeMetadata: String
    )
}

@Serializable
data class CognitoEventUserPoolsVerifyAuthChallenge(
    @SerialName("version") val version: String,
    @SerialName("triggerSource") val triggerSource: String,
    @SerialName("region") val region: String,
    @SerialName("userPoolId") val userPoolID: String,
    @SerialName("callerContext") val callerContext: CognitoEventUserPoolsCallerContext,
    @SerialName("userName") val userName: String,
    @SerialName("request") val request: VerifyAuthChallengeRequest,
    @SerialName("response") val response: VerifyAuthChallengeResponse
) {
    @Serializable
    data class VerifyAuthChallengeRequest(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("privateChallengeParameters") val privateChallengeParameters: Map<String, String>,
        @SerialName("challengeAnswer") val challengeAnswer: Map<String, String>,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>
    )

    @Serializable
    data class VerifyAuthChallengeResponse(
        @SerialName("answerCorrect") val answerCorrect: Boolean
    )
}

@Serializable
data class CognitoEventUserPoolsCallerContext(
    @SerialName("awsSdkVersion") val awsSdkVersion: String,
    @SerialName("clientId") val clientId: String
)

@Serializable
data class CognitoEventUserPoolsChallengeResult(
    @SerialName("challengeName") val challengeName: String,
    @SerialName("challengeResult") val challengeResult: Boolean,
    @SerialName("challengeMetadata") val challengeMetadata: String
)

@Serializable
data class ClaimsAndScopeOverrideDetails(
    @SerialName("idTokenGeneration") val idTokenGeneration: IDTokenGeneration,
    @SerialName("accessTokenGeneration") val accessTokenGeneration: AccessTokenGeneration,
    @SerialName("groupOverrideDetails") val groupOverrideDetails: GroupConfiguration
) {
    @Serializable
    data class IDTokenGeneration(
        @SerialName("claimsToAddOrOverride") val claimsToAddOrOverride: Map<String, String>,
        @SerialName("claimsToSuppress") val claimsToSuppress: List<String>
    )

    @Serializable
    data class AccessTokenGeneration(
        @SerialName("claimsToAddOrOverride") val claimsToAddOrOverride: Map<String, String>,
        @SerialName("claimsToSuppress") val claimsToSuppress: List<String>,
        @SerialName("scopesToAdd") val scopesToAdd: List<String>,
        @SerialName("scopesToSuppress") val scopesToSuppress: List<String>
    )
}

@Serializable
data class ClaimsOverrideDetails(
    @SerialName("groupOverrideDetails") val groupOverrideDetails: GroupConfiguration,
    @SerialName("claimsToAddOrOverride") val claimsToAddOrOverride: Map<String, String>,
    @SerialName("claimsToSuppress") val claimsToSuppress: List<String>
)

@Serializable
data class GroupConfiguration(
    @SerialName("groupsToOverride") val groupsToOverride: List<String>,
    @SerialName("iamRolesToOverride") val iamRolesToOverride: List<String>,
    @SerialName("preferredRole") val preferredRole: String? = null
)

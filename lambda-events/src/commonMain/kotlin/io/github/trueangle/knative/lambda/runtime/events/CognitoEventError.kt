package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// todo
@Serializable
sealed class CognitoEvent {

    @Serializable
    data class CallerContext(
        @SerialName("awsSdkVersion") val awsSdkVersion: String,
        @SerialName("clientId") val clientId: String
    )

    @Serializable
    enum class TriggerSource(@SerialName("value") val value: String) {
        @SerialName("PreSignUp_SignUp") PRE_SIGN_UP_SIGN_UP("PreSignUp_SignUp"),
        @SerialName("PreSignUp_AdminCreateUser") PRE_SIGN_UP_ADMIN_CREATE_USER("PreSignUp_AdminCreateUser"),
        @SerialName("PreSignUp_ExternalProvider") PRE_SIGN_UP_EXTERNAL_PROVIDER("PreSignUp_ExternalProvider"),
        
        @SerialName("PostConfirmation_ConfirmSignUp") POST_CONFIRMATION_CONFIRM_SIGN_UP("PostConfirmation_ConfirmSignUp"),
        @SerialName("PostConfirmation_ConfirmForgotPassword") POST_CONFIRMATION_CONFIRM_FORGOT_PASSWORD("PostConfirmation_ConfirmForgotPassword"),
        
        @SerialName("PreAuthentication_Authentication") PRE_AUTHENTICATION_AUTHENTICATION("PreAuthentication_Authentication"),
        @SerialName("PostAuthentication_Authentication") POST_AUTHENTICATION_AUTHENTICATION("PostAuthentication_Authentication"),
        
        @SerialName("CustomMessage_SignUp") CUSTOM_MESSAGE_SIGN_UP("CustomMessage_SignUp"),
        @SerialName("CustomMessage_AdminCreateUser") CUSTOM_MESSAGE_ADMIN_CREATE_USER("CustomMessage_AdminCreateUser"),
        @SerialName("CustomMessage_ResendCode") CUSTOM_MESSAGE_RESEND_CODE("CustomMessage_ResendCode"),
        @SerialName("CustomMessage_ForgotPassword") CUSTOM_MESSAGE_FORGOT_PASSWORD("CustomMessage_ForgotPassword"),
        @SerialName("CustomMessage_UpdateUserAttribute") CUSTOM_MESSAGE_UPDATE_USER_ATTRIBUTE("CustomMessage_UpdateUserAttribute"),
        @SerialName("CustomMessage_VerifyUserAttribute") CUSTOM_MESSAGE_VERIFY_USER_ATTRIBUTE("CustomMessage_VerifyUserAttribute"),
        @SerialName("CustomMessage_Authentication") CUSTOM_MESSAGE_AUTHENTICATION("CustomMessage_Authentication"),
        
        @SerialName("DefineAuthChallenge_Authentication") DEFINE_AUTH_CHALLENGE_AUTHENTICATION("DefineAuthChallenge_Authentication"),
        @SerialName("CreateAuthChallenge_Authentication") CREATE_AUTH_CHALLENGE_AUTHENTICATION("CreateAuthChallenge_Authentication"),
        @SerialName("VerifyAuthChallengeResponse_Authentication") VERIFY_AUTH_CHALLENGE_RESPONSE_AUTHENTICATION("VerifyAuthChallengeResponse_Authentication"),
        
        @SerialName("TokenGeneration_HostedAuth") TOKEN_GENERATION_HOSTED_AUTH("TokenGeneration_HostedAuth"),
        @SerialName("TokenGeneration_Authentication") TOKEN_GENERATION_AUTHENTICATION("TokenGeneration_Authentication"),
        @SerialName("TokenGeneration_NewPasswordChallenge") TOKEN_GENERATION_NEW_PASSWORD_CHALLENGE("TokenGeneration_NewPasswordChallenge"),
        @SerialName("TokenGeneration_AuthenticateDevice") TOKEN_GENERATION_AUTHENTICATE_DEVICE("TokenGeneration_AuthenticateDevice"),
        @SerialName("TokenGeneration_RefreshTokens") TOKEN_GENERATION_REFRESH_TOKENS("TokenGeneration_RefreshTokens"),
        
        @SerialName("UserMigration_Authentication") USER_MIGRATION_AUTHENTICATION("UserMigration_Authentication"),
        @SerialName("UserMigration_ForgotPassword") USER_MIGRATION_FORGOT_PASSWORD("UserMigration_ForgotPassword")
    }

    @Serializable
    data class Parameters(
        @SerialName("version") val version: String,
        @SerialName("triggerSource") val triggerSource: TriggerSource,
        @SerialName("region") val region: AWSRegion,
        @SerialName("userPoolId") val userPoolId: String,
        @SerialName("userName") val userName: String,
        @SerialName("callerContext") val callerContext: CallerContext
    )

    @Serializable
    data class PreSignUp(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("validationData") val validationData: Map<String, String>? = null,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>? = null
    )

    @Serializable
    data class PostConfirmation(
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>? = null
    )

    @Serializable
    data class PostAuthentication(
        @SerialName("newDeviceUsed") val newDeviceUsed: Boolean,
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>? = null
    )

    @Serializable
    data class CustomMessage(
        @SerialName("codeParameter") val codeParameter: String? = null,
        @SerialName("usernameParameter") val usernameParameter: String? = null,
        @SerialName("userAttributes") val userAttributes: Map<String, String>,
        @SerialName("clientMetadata") val clientMetadata: Map<String, String>? = null
    )

    data class CognitoEventPreSignUp(
        @SerialName("params") val params: Parameters,
        @SerialName("preSignUp") val preSignUp: PreSignUp
    ) : CognitoEvent()

    data class CognitoEventPostConfirmation(
        @SerialName("params") val params: Parameters,
        @SerialName("postConfirmation") val postConfirmation: PostConfirmation
    ) : CognitoEvent()

    data class CognitoEventPostAuthentication(
        @SerialName("params") val params: Parameters,
        @SerialName("postAuthentication") val postAuthentication: PostAuthentication
    ) : CognitoEvent()

    data class CognitoEventCustomMessage(
        @SerialName("params") val params: Parameters,
        @SerialName("customMessage") val customMessage: CustomMessage
    ) : CognitoEvent()

    val commonParameters: Parameters
        get() = when (this) {
            is CognitoEventPreSignUp -> params
            is CognitoEventPostConfirmation -> params
            is CognitoEventPostAuthentication -> params
            is CognitoEventCustomMessage -> params
        }
}

@Serializable
sealed class CognitoEventResponse {

    @Serializable
    data class EmptyResponse(@SerialName("unused") val unused: Unit = Unit)

    @Serializable
    data class PreSignUp(
        @SerialName("autoConfirmUser") val autoConfirmUser: Boolean,
        @SerialName("autoVerifyEmail") val autoVerifyEmail: Boolean,
        @SerialName("autoVerifyPhone") val autoVerifyPhone: Boolean,
        @SerialName("mfaSetting") val mfaSetting: String? = null
    ) : CognitoEventResponse()

    @Serializable
    data class PostConfirmation(
        @SerialName("unused") val unused: Unit = Unit
    ) : CognitoEventResponse()

    @Serializable
    data class PostAuthentication(
        @SerialName("unused") val unused: Unit = Unit
    ) : CognitoEventResponse()

    @Serializable
    data class CustomMessage(
        @SerialName("smsMessage") val smsMessage: String? = null,
        @SerialName("emailMessage") val emailMessage: String? = null,
        @SerialName("emailSubject") val emailSubject: String? = null
    ) : CognitoEventResponse()
}
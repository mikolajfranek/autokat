import * as Secret from './../Secret';

export const URL = "https://docs.google.com/a/google.com/spreadsheets/d/";
export const URL_SUFFIX = "/gviz/tq";

export function getHeaders() {
    return {
        headers: {
            'Content-type': 'application/json',
            'Authorization': `Bearer ${getBearerToken()}`,
            'tqx': 'out:json',
            'tq': 'ASSIGN QUERY HERE',
        }
    }
}

function getBearerToken() {

    getToken();

    return null;
}

async function getToken() {
    const alg = 'RS256'
    const pkcs8 = Secret.getPrivateKey();
  

    /*
    const jwt = await new jose.SignJWT({ 'urn:example:claim': true })
        .setProtectedHeader({ alg })
        .setIssuedAt()
        .setIssuer('urn:example:issuer')
        .setAudience('urn:example:audience')
        .setExpirationTime('2h')
        .sign(privateKey)
*/

    /*
        val privateKey: RSAPrivateKey = KeyFactory.getInstance("RSA").generatePrivate(
            PKCS8EncodedKeySpec(
                Base64.decode(
                    if (apk) Secret.getApkPrivateKey() else Secret.getPrivateKey(),
            Base64.DEFAULT
                )
            )
        ) as RSAPrivateKey
        val timestamp: Long = Date().time
    
        val signedJwt = Jwts.builder().setClaims(
                mapOf(
                    TOKEN_SCOPE to TOKEN_SCOPE_VALUE,
                    Claims.ISSUER to if (apk) Secret.getApkEmail() else Secret.getEmail(),
                        Claims.AUDIENCE to TOKEN_URL,
                            Claims.ISSUED_AT to Date(timestamp),
                                Claims.EXPIRATION to Date(timestamp + Configuration.ONE_HOUR_IN_MILLISECONDS)
            )
        ).signWith(privateKey, SignatureAlgorithm.RS256).compact()
        
        
        
        val bodyJson =
            """{"grant_type":"urn: ietf: params: oauth: grant - type: jwt - bearer","assertion" : "$signedJwt"}"""
        val(_, response, result) = Fuel.post(TOKEN_URL).body(bodyJson).responseString()
        if (response.statusCode != 200) throw UnknownHostException()
    */

    return null;
}
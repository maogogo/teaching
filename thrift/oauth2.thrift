namespace java com.maogogo.teaching.thrift
#@namespace scala com.maogogo.teaching.thrift

#t_oauth_user
struct TUser {
  1: string id
  2: string username
  3: string password_hash
  4: optional string cellphone
  5: optional string cellphone_active
  6: optional string email
  7: optional string email_active
  8: string salt
}

struct TSession {
  1: TUser user
}

struct TAccessToken {
  1: string token
  2: optional string refresh_token
  3: optional string scope
  4: optional i64 expiresIn
  5: i64 createdAt  // Date
}

struct TAuthInfo {
  1: TSession session
  2: string clientId
  3: optional string scope
  4: optional string redirect_uri
}

service OAuth2Service {

  bool validateClient(string client_id, string client_secret, string grant_type)
  list<TSession> findUser(string username, string password)
  list<TAccessToken> createAccessToken(TAuthInfo auth_info)
  list<TAuthInfo> findAuthInfoByCode(string code)
  list<TAuthInfo> findAuthInfoByRefreshToken(string refresh_token)
  list<TSession> findClientUser(string client_id, string client_secret, string scope)
  list<TAccessToken> findAccessToken(string token)
  list<TAuthInfo> findAuthInfoByAccessToken(TAccessToken access_token)
  list<TAccessToken> getStoredAccessToken(TAuthInfo auth_info)
  list<TAccessToken> refreshAccessToken(TAuthInfo authInfo, string refresh_token)

}

namespace java com.maogogo.teaching.thrift
#@namespace scala com.maogogo.teaching.thrift

include "oauth2.thrift"

typedef string ROLE_ID
typedef string ORGANIZATION_ID

enum ExceptionCode {
  Ok  = 200
  ERROR = 500
}

exception CommonException {
  1: string msg
  2: optional ExceptionCode code = ExceptionCode.Ok
}

struct Role {
  1: ROLE_ID id
  2: string label
}

struct Teacher {
  1: oauth2.TUser user
  2: ORGANIZATION_ID organization_id
  3: optional list<Role> roles
}

struct Student {
  1: oauth2.TUser user
  2: ORGANIZATION_ID organization_id 
  3: optional list<Role> roles
}

struct RolePrivilege {
  1: Role role
  2: list<oauth2.TPrivilege> privileges
}

struct Organization {
  1: ORGANIZATION_ID organization_id
  2: string name
  3: optional string parent_id
}

service AccessControlService {
  
}


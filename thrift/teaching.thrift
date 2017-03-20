namespace java com.maogogo.teaching.thrift
#@namespace scala com.maogogo.teaching.thrift

enum ExceptionCode {
  Ok  = 200
  ERROR = 500
}

exception CommonException {
  1: string msg
  2: optional ExceptionCode code = ExceptionCode.Ok
}
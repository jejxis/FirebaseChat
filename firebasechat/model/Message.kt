package net.flow9.thisiskotlin.firebasechat.model

class Message {
    var id: String = ""//메시지 아이디(자동생성)
    var msg: String = ""//메시지
    var userName: String = ""//사용자명
    var timeStamp: Long = 0//전송시간 timestamp

    constructor()

    constructor(msg:String, userName: String){
        this.msg = msg
        this.userName = userName
        this.timeStamp = System.currentTimeMillis()
    }
}
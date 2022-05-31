package net.flow9.thisiskotlin.firebasechat.model

class Room {
    var id: String = ""//방 아이디
    var title: String = ""//방 이름
    var users: String = ""

    constructor()

    constructor(title: String, creatorName: String){
        this.title = title
        this.users = creatorName
    }
}
package com.test.recovermessages.models

class UserModel(var name: String, var lastmsg: String, var time: String, i: Int) {
    var isRead = false

    init {
        isRead = i != 0
    }
}
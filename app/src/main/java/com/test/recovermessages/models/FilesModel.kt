package com.test.recovermessages.models

import java.io.File

class FilesModel(
    var filename: String,
    var type: String,
    var size: String,
    var file: File,
    var isSelected: Boolean
)
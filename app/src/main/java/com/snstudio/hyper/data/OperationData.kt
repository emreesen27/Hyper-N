package com.snstudio.hyper.data

data class OperationData(
    val media: Media,
    val operationType: OperationType
)

enum class OperationType {
    PLAY, DOWNLOAD, INFO
}

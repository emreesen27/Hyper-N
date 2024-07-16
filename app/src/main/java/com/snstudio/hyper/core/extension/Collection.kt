package com.snstudio.hyper.core.extension

import com.snstudio.hyper.data.model.Media

fun <T> MutableCollection<T>.removeFirst(): T {
    val iterator = iterator()
    val element = iterator.next()
    iterator.remove()
    return element
}

fun <K, V> MutableMap<K, V>.removeFirst(): Map.Entry<K, V> {
    val iterator = iterator()
    val element = iterator.next()
    iterator.remove()
    return element
}

fun <T> MutableCollection<T>.removeFirst(predicate: (T) -> Boolean): T? {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.next()
        if (predicate(element)) {
            iterator.remove()
            return element
        }
    }
    return null
}

fun <K, V> MutableMap<K, V>.removeFirst(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? =
    entries.removeFirst(predicate)

fun List<HashMap<String, String>>.toMediaList(type: Int): MutableList<Media> {
    return this.map { hashMap ->
        Media(
            id = hashMap["id"] ?: "",
            title = hashMap["title"] ?: "",
            description = hashMap["description"] ?: "",
            author = hashMap["author"] ?: "",
            url = hashMap["url"] ?: "",
            duration = hashMap["duration"]?.toLong() ?: 0L,
            thumbnail = hashMap["thumbnail"] ?: "",
            publishYear = hashMap["publishYear"],
            uploadYear = hashMap["uploadYear"],
            viewCount = hashMap["viewCount"] ?: "",
            type = type,
            bitmap = null,
            localPath = null
        )
    }.toMutableList()
}
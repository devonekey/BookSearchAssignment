package com.booksearch.assignment.domain.model

import java.util.function.IntFunction

/**
 * 여러 도서들을 다루는 모델
 *
 * @property data   도서 목록
 * @property isEnd  마지막 도서 목록인지 여부
 */
data class Books(
    val data: List<Book>,
    val isEnd: Boolean = false
) : List<Book> by data {
    @Deprecated("It's only for Java")
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> toArray(p0: IntFunction<Array<out T?>?>): Array<out T?>? {
        val requested = p0.apply(size)
            ?: throw NullPointerException("p0 returned null")
        val result: Array<T?> =
            if (requested.size >= size) {
                requested as Array<T?>
            } else {
                java.lang.reflect.Array
                    .newInstance(requested.javaClass.componentType, size) as Array<T?>
            }

        for (i in data.indices) {
            result[i] = data[i] as T?
        }

        if (result.size > size) {
            result[size] = null
        }

        return result
    }

    operator fun plus(other: Books): Books =
        Books(
            data = data + other.data,
            isEnd = other.isEnd
        )

    override fun equals(other: Any?): Boolean {
        if (other !is List<*>) {
            return false
        }

        if (size != other.size) {
            return false
        }

        val iterator = iterator()
        val otherIterator = other.iterator()

        while (iterator.hasNext() && otherIterator.hasNext()) {
            if (iterator.next() != otherIterator.next()) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var hashCode = 1

        data.forEach { element ->
            hashCode = 31 * hashCode + element.hashCode()
        }

        return hashCode
    }

    override fun toString(): String = "isEnd: ${isEnd}, data: $data"
}

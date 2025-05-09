package com.vanphong.foodnfit.util

fun parseServingSize(serving: String): Pair<Float, String> {
    val regex = Regex("""([\d.]+)\s*([a-zA-Z]+)""") // Cho phép khoảng trắng giữa số và đơn vị
    val matchResult = regex.find(serving.trim())

    val number = matchResult?.groups?.get(1)?.value?.toFloatOrNull() ?: 0f
    val unit = matchResult?.groups?.get(2)?.value ?: ""

    return Pair(number, unit)
}

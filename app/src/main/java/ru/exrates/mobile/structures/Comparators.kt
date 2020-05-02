package ru.exrates.mobile.structures

class IntervalComparator : Comparator<String>{
    override fun compare(o1: String?, o2: String?): Int {
        if (o1 == null || o2 == null) return 0
        if (o1 == o2) return 0
        val regExp = "(\\d{1,2})(\\D)".toRegex()
        val groups1 = regExp.find(o1)?.groups ?: throw NullPointerException("Regexp is null")
        val numToken1 = groups1[1]!!.value.toInt()
        val stringToken1 = groups1[2]!!.value
        val groups2 = regExp.find(o2)?.groups ?: throw NullPointerException("Regexp is null")
        val numToken2 = groups2[1]!!.value.toInt()
        val stringToken2 = groups2[2]!!.value
        val seq = "mhdwMy"
        val idx1 = seq.indexOf(stringToken1)
        val idx2 = seq.indexOf(stringToken2)
        return if (idx1 != idx2)
            (if (idx1 > idx2) 1 else -1) else
            (if (numToken1 == numToken2) 0 else if (numToken1 > numToken2) 1 else -1)
    }

}
fun box(stepId: Int, isWasm: Boolean): String {
    val x = funA()
    if (x != stepId) {
        return "Fail: $x != $stepId"
    }
    return "OK"
}

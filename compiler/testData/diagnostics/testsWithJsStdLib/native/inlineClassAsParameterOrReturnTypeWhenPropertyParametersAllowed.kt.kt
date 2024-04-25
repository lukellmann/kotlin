// FIR_IDENTICAL
// !LANGUAGE: +InlineClasses, -JvmInlineValueClasses, -JsAllowValueClassesInExternals, +JsExternalPropertyParameters
// DIAGNOSTICS: -OPT_IN_USAGE

// FILE: uint.kt

package kotlin

inline class UInt(private val i: Int)

// FILE: test.kt

inline class SomeIC(val a: Int)

external val l: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>

external val ll
    get(): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!> = definedExternally

external var r: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>

external var rr: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
    get() = definedExternally
    set(<!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>v: SomeIC<!>) { definedExternally }

external fun foo(): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
external fun foo(<!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>c: SomeIC<!>): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
external fun foo(a: Int, <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>c: SomeIC<!>): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>

external fun foo(a: Int, <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!><!FORBIDDEN_VARARG_PARAMETER_TYPE!>vararg<!> args: SomeIC<!>)
external fun foo(a: Int, <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>ui: UInt<!>, <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>vararg args: UInt<!>)

external class CC(
    <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>a: SomeIC<!>,
    <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>val b: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!><!>,
    <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>var c: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!><!>
) {
    val l: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
    var r: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>

    fun foo(): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
    fun foo(<!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>c: SomeIC<!>): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
    fun foo(a: Int, <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>c: SomeIC<!>): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>

    class N(
        <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>a: SomeIC<!>,
        <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>val b: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!><!>,
        <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>var c: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!><!>
    ) {
        val l: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
        var r: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>

        fun foo(): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
        fun foo(<!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>c: SomeIC<!>): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
        fun foo(a: Int, <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>c: SomeIC<!>): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
    }
}

external interface EI {
    val l: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
    var r: <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>

    fun foo(): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
    fun foo(<!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>c: SomeIC<!>): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
    fun foo(a: Int, <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>c: SomeIC<!>): <!INLINE_CLASS_IN_EXTERNAL_DECLARATION!>SomeIC<!>
}

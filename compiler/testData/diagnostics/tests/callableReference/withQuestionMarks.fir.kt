// DIAGNOSTICS: -UNUSED_VARIABLE
// Issue: KT-35075

fun foo() {}

fun main() {
    val x1 = <!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>::info?::<!UNRESOLVED_REFERENCE!>print<!><!>
    val x2 = <!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>?::info<!>?::<!UNRESOLVED_REFERENCE!>print<!><!>
    val x3 = <!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>?::info<!>::<!UNRESOLVED_REFERENCE!>print<!>
    val x4 = <!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>?::info<!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>
    val x5 = <!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>::info?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>
    val x6 = <!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>!!::<!UNRESOLVED_REFERENCE!>info<!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>
    val x7 = <!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>::info<!NOT_NULL_ASSERTION_ON_CALLABLE_REFERENCE!>!!<!>::<!UNRESOLVED_REFERENCE!>print<!>?::<!UNRESOLVED_REFERENCE!>print<!><!>
    val x8 = <!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>?::info<!><!NOT_NULL_ASSERTION_ON_CALLABLE_REFERENCE!>!!<!>::<!UNRESOLVED_REFERENCE!>print<!>?::<!UNRESOLVED_REFERENCE!>print<!><!>
    val x9 = <!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>!!::<!UNRESOLVED_REFERENCE!>info<!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>
    val x10 = <!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>::info?::<!UNRESOLVED_REFERENCE!>print<!><!><!NOT_NULL_ASSERTION_ON_CALLABLE_REFERENCE!>!!<!>::<!UNRESOLVED_REFERENCE!>print<!>
    val x11 = <!UNRESOLVED_REFERENCE!>logger<!>!!::<!UNRESOLVED_REFERENCE!>info<!><!NOT_NULL_ASSERTION_ON_CALLABLE_REFERENCE!>!!<!>::<!UNRESOLVED_REFERENCE!>print<!><!NOT_NULL_ASSERTION_ON_CALLABLE_REFERENCE!>!!<!>::<!UNRESOLVED_REFERENCE!>print<!>
    val x12 = <!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>?::info<!><!NOT_NULL_ASSERTION_ON_CALLABLE_REFERENCE!>!!<!>::<!UNRESOLVED_REFERENCE!>print<!><!NOT_NULL_ASSERTION_ON_CALLABLE_REFERENCE!>!!<!>::<!UNRESOLVED_REFERENCE!>print<!>
    val x13 = <!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!>42?::<!UNRESOLVED_REFERENCE!>unresolved<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>

    val x14 = <!UNRESOLVED_REFERENCE!>logger<!><!SYNTAX!>?!!::info?::print?::print<!>
    val x15 = <!UNRESOLVED_REFERENCE!>logger<!>::info<!SYNTAX!>?!!::print?::print<!>
    val x16 = <!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>!!?::<!UNRESOLVED_REFERENCE!>info<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>
    val x17 = <!SAFE_CALLABLE_REFERENCE_CALL!><!SAFE_CALLABLE_REFERENCE_CALL!><!UNRESOLVED_REFERENCE!>logger<!>::info<!NOT_NULL_ASSERTION_ON_CALLABLE_REFERENCE!>!!<!>?::<!UNRESOLVED_REFERENCE!>print<!><!>?::<!UNRESOLVED_REFERENCE!>print<!><!>

    // It must be OK
    val x18 = String?::hashCode <!USELESS_ELVIS!>?: ::foo<!>
    val x19 = String::hashCode <!USELESS_ELVIS!>?: ::foo<!>
    val x20 = String?::hashCode::hashCode
    val x21 = kotlin.String?::hashCode::hashCode
}

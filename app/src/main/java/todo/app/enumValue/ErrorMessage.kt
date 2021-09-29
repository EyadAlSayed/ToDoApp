package todo.app.enumValue

enum class ErrorMessage(val message:String) {
    DATE_NOT_CHOSEN("You have to pick a date"),
    TIME_NOT_CHOSEN("You have to pick a time"),
    FIELD_REQUIRED("This field is required")
}
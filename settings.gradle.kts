rootProject.name = "ligature-kt"

include(
    "gaze",
    "idgen",
    "lig",
    "ligature",
    "ligature-http",
    "ligature-http-in-memory",
    "ligature-http-test-suite",
    "ligature-http-xodus",
    "ligature-in-memory",
    "ligature-js",
    "ligature-repl",
    "ligature-test-suite",
    "ligature-xodus",
    "wander"
    )

// below line is added for CircleCI
enableFeaturePreview("VERSION_CATALOGS")

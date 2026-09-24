
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

val buildInfoDir = layout.buildDirectory.dir("generated/resources/buildInfo")

sourceSets {
    main {
        resources {
            srcDir(buildInfoDir)
        }
    }

    patchedMc {
        resources {
            srcDir("tools/CropsNH-2.0.114/src/main/java")
        }
    }
}
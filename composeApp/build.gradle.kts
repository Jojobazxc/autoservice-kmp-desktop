import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation("io.insert-koin:koin-core:4.1.0")
            implementation("io.insert-koin:koin-compose:4.1.0")

            implementation("io.ktor:ktor-client-core:3.4.1")
            implementation("io.ktor:ktor-client-content-negotiation:3.4.1")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.1")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation("io.ktor:ktor-client-cio:3.4.1")
            implementation("io.ktor:ktor-client-logging:3.4.1")
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.example.autoservice_desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.example.autoservice_desktop"
            packageVersion = "1.0.0"
        }
    }
}
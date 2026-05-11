import java.util.Properties

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
        maven {
            // Important note:
            // To authenticate with GitHub Packages, you need to generate a fine-grained personal access token
            // The token needs access only to [Public repositories]
            // Add it as gpr.token=$token in local.properties file in the root of the project
            // For more details, refer to the documentation:
            // https://github.com/settings/personal-access-tokens/new
            // https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authenticating-with-a-personal-access-token

            val localProps = getLocalProps()

            name = "GitHubPackages"
            setUrl("https://maven.pkg.github.com/ARK-Builders/ark-android")
            credentials {
                username = "token"
                password = localProps.getProperty("gpr.token") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
rootProject.name = "arkrate"
include(":app")
include(":fiaticons")
include(":cryptoicons")
include(":core:domain")
include(":core:data")
include(":core:presentation")
include(":feature:quick")
include(":core:db")
include(":core:di")
include(":feature:portfolio")
include(":feature:pairalert")
include(":feature:quickwidget")
include(":feature:search")
include(":feature:settings")
include(":feature:onboarding")
include(":watchapp")


fun getLocalProps(): Properties {
    val props = Properties()
    val localPropsFile = File(rootDir, "local.properties")

    if (localPropsFile.exists()) {
        localPropsFile.inputStream().use { stream ->
            props.load(stream)
        }
    }

    return props
}

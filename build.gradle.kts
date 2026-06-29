plugins {
    id("ca.stellardrift.gitpatcher") version "2.0.0"
}

gitPatcher {
    patchedRepos {
        register("essentials") {
            submodule = "Essentials"
            target = file("Essentials-Patched")
            patches = file("patches")
        }
    }
}

tasks.register("updateUpstream") {
    group = "git patcher"
    description = "Met à jour le submodule vers le commit défini dans gradle.properties"
    doLast {
        val essentialsDir = file("Essentials")
        val targetCommit = project.property("essentials.upstream.commit") as String

        listOf(
            listOf("git", "fetch", "origin"),
            listOf("git", "checkout", targetCommit),
        ).forEach { cmd ->
            val result = java.lang.ProcessBuilder(cmd)
                .directory(essentialsDir)
                .redirectInput(java.lang.ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(java.lang.ProcessBuilder.Redirect.INHERIT)
                .redirectError(java.lang.ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()
            if (result != 0) error("Commande échouée : $cmd")
        }

        val result = java.lang.ProcessBuilder(listOf("git", "add", "Essentials"))
            .directory(projectDir)
            .redirectInput(java.lang.ProcessBuilder.Redirect.INHERIT)
            .redirectOutput(java.lang.ProcessBuilder.Redirect.INHERIT)
            .redirectError(java.lang.ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()
        if (result != 0) error("Commande échouée : git add Essentials")
    }
}
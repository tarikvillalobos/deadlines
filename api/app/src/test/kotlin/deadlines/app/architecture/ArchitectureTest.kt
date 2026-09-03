package deadlines.app.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.core.spec.style.StringSpec

private const val DOMAINS_PACKAGE = "deadlines.domains."

class ArchitectureTest :
    StringSpec({
        val scope = Konsist.scopeFromProject()

        "domain and application layers stay free of frameworks" {
            scope.files
                .withPackage("..domain..", "..application..")
                .assertFalse { it.importsAnyOf("io.ktor", "org.jetbrains.exposed", "org.koin") }
        }

        "platform never depends on domains" {
            scope.files
                .withPackage("deadlines.platform..")
                .assertFalse { it.importsAnyOf("deadlines.domains") }
        }

        "domain contexts never depend on each other" {
            scope.files
                .withPackage("deadlines.domains..")
                .assertTrue { file ->
                    val ownContext = DOMAINS_PACKAGE + file.domainContext + "."
                    file.imports.none { it.name.startsWith(DOMAINS_PACKAGE) && !it.name.startsWith(ownContext) }
                }
        }

        "core and contracts depend on nothing else in the project" {
            scope.files
                .withPackage("deadlines.core..", "deadlines.contracts..")
                .assertFalse { it.importsAnyOf("deadlines.platform", "deadlines.domains", "deadlines.app") }
        }
    })

private fun KoFileDeclaration.importsAnyOf(vararg prefixes: String) =
    imports.any { import -> prefixes.any { import.name.startsWith(it) } }

private val KoFileDeclaration.domainContext: String?
    get() = packagee?.name?.removePrefix(DOMAINS_PACKAGE)?.substringBefore('.')

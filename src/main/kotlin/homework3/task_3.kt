package homework3

import com.charleskorn.kaml.Yaml
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.FileSpec
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Class for serialization representing function
 */
@Serializable
data class Function(
    val name: String,
)

/**
 * Class for serialization representing config to generate test file
 *
 * @param packageName name of the package in which test file will be generated
 * @param className name of the class, in generated file corresponding class
 * will be named [className]Test
 * @param functions list of functions of the class
 * @constructor removes doubles [functions] if any and displays warning
 */
@Serializable
@SerialName("test")
data class YamlConfig(
    @SerialName("package name")
    val packageName: String,
    @SerialName("class name")
    val className: String,
    @SerialName("functions")
    val functions: MutableList<Function>?
) {
    var setOfFuncs: Set<Function> = emptySet()
        get() = field
    init {
        if (functions != null) {
            setOfFuncs = functions.toSet()
            if (setOfFuncs.size != functions.size) {
                println("Warning: yaml config contains identical functions. Only one function is generated.")
            }
            functions.clear()
        }
    }
}

/**
 * Generate file with test
 *
 * @param configPath path to yaml config
 * @param locationToSave where to generated file, if file
 * already exists, it will be overwritten each time func is
 * invoked
 *
 * Example:
 * ----------------------#yaml config
 * package name: homework1
 * class name: PerformedCommandStorage
 * functions:
 * - name: "forwardApply"
 * - name: "backwardApply"
 * - name: "dly"
 * -----------------------//Corresponding generated file
 * package yamlTest
 *
 * import kotlin.Unit
 * import org.junit.jupiter.api.Test
 *
 * public class YamlTestTest {
 * @Test
 * public fun foo(): Unit {
 * }
 *
 * @Test
 * public fun foobar(): Unit {
 * }
 * }
 *
 * -------------------------
 */
fun generateTestFile(configPath: String, locationToSave: String) {
    val configFile = File(configPath)
    if (configFile.exists()) {
        val configString = configFile.readText()
        val config = Yaml.default.decodeFromString(YamlConfig.serializer(), configString)
        val name = ClassName(config.packageName, config.className + "Test")
        val type = TypeSpec.classBuilder(name).addModifiers(KModifier.INTERNAL)
        if (config.functions != null) {
            for (f in config.setOfFuncs) {
                type.addFunction(FunSpec.builder(f.name).addAnnotation(Test::class).build())
            }
        }
        val file = FileSpec.builder(config.packageName, config.className)
            .addType(type.build()).build()
        File(locationToSave).apply {
                file.writeTo(this)
        }
    } else {
        println("Could not open file: $configFile")
    }
}

fun main() {
    generateTestFile("src/main/resources/homework3/yamlConfigs/exampleConfig.yaml",
    "src/main/resources/homework3/GeneratedTests")
}

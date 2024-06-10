import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logger
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.submit
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkQueue
import org.gradle.workers.WorkerExecutor
import org.slf4j.LoggerFactory
import java.io.File
import java.io.OutputStream
import javax.inject.Inject
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


@CacheableTask
abstract class GenerateJaxbClasses : DefaultTask() {

    init {
        description = "Generates JAXB classes from XSD"
        group = "build"
    }

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val schemaFiles: ListProperty<File>

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val bindingFile: RegularFileProperty

    @get:Optional
    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val xsdOutputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        xsdOutputDir.get().asFile.mkdirs()

        val args = mutableListOf(
            "-d", xsdOutputDir.get().asFile.absolutePath,
            "-extension",
            "-no-header"
        )

        packageName.orNull?.let {
            args.addAll(listOf("-p", it))
        }

        bindingFile.orNull?.let {
            args.addAll(listOf("-b", it.asFile.absolutePath))
        }

        args.addAll(schemaFiles.get().map { it.absolutePath })

        project.javaexec {
            // Log standard output to the info level and standard error to the quiet level
            standardOutput = LoggingOutputStream(logger::info)
            errorOutput = LoggingOutputStream(logger::quiet)

            classpath = project.configurations["compileClasspath"]
            mainClass.set( "com.sun.tools.xjc.XJCFacade")
            args(args)
            logger.info("Generating JAXB classes in ${xsdOutputDir.get().asFile.absolutePath}")
            logger.debug("XJC arguments: {}", args)
            logger.debug("XJC classpath: ${classpath.asPath}")
            logger.debug("XJC main class: ${mainClass.get()}")
            logger.debug("XJC working directory: {}", project.projectDir)
        }
    }
}

abstract class TransformXmlTask : DefaultTask() {

    init {
        description = "Transforms XML files using an XSLT stylesheet"
        group = "build"
    }

    @get:InputDirectory
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:InputFile
    abstract val stylesheet: RegularFileProperty

    @TaskAction
    fun transform() {
        outputDir.get().asFile.mkdirs()

        inputDir.get().asFile.listFiles { file -> file.extension == "xsd" }?.forEach { file ->
            val outputFile = File(outputDir.get().asFile, file.name.replace(".xsd", ".html"))
            val factory = TransformerFactory.newInstance()
            val transformer = factory.newTransformer(StreamSource(stylesheet.get().asFile))
            transformer.transform(StreamSource(file), StreamResult(outputFile))
        }
    }
}

interface WdslWorkParameters : WorkParameters {
        fun getClasspath() : ConfigurableFileCollection
        fun getArgs() : ListProperty<String>
}

abstract class GenerateWsdlSources @Inject constructor(private val execOperations: ExecOperations) : WorkAction<WdslWorkParameters> {

    override fun execute() {
        execOperations.javaexec {
            classpath = parameters.getClasspath()
            standardOutput = OutputStream.nullOutputStream()
            errorOutput = OutputStream.nullOutputStream()
            mainClass.set("org.apache.cxf.tools.wsdlto.WSDLToJava")
            classpath(parameters.getClasspath().asPath)
            args(parameters.getArgs().get())
        }

    }
}

abstract class GenerateWsdlSourcesTask : DefaultTask() {

    init {
        description = "Generates Java sources from WSDL files"
        group = "build"
    }

    @get:Input
    abstract val wsdlFiles: ListProperty<String>

    @get:OutputDirectory
    abstract val generatedSourcesOutputDir: DirectoryProperty

    @get:Inject
    abstract val workerExecutor : WorkerExecutor

    @TaskAction
    fun generate() {
        val workQueue: WorkQueue = workerExecutor.noIsolation()

        wsdlFiles.get().forEach { wsdl ->
            workQueue.submit(GenerateWsdlSources::class) {
                getClasspath().setFrom(project.configurations["wsdlDeps"])
                getArgs().set(listOf(
                    "-quiet",
                    "-d", generatedSourcesOutputDir.get().asFile.absolutePath,
                    "-wsdlLocation", "classpath:wsdl/${File(wsdl).name}",
                    project.layout.projectDirectory.file(wsdl).asFile.absolutePath
                ))
            }
        }
    }
}

// A custom OutputStream that logs the output to the method passed in the constructor
class LoggingOutputStream(private val log: (String) -> Unit) : OutputStream() {
    private val buffer = StringBuilder()

    override fun write(b: Int) {
        if (b == '\n'.code) {
            log(buffer.toString())
            buffer.setLength(0)
        } else {
            buffer.append(b.toChar())
        }
    }
}
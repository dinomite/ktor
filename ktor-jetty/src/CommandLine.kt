package ktor.application.jetty

import ktor.application.*
import java.util.*
import java.net.*
import java.io.File
import javax.naming.InitialContext
import org.apache.log4j.*

fun server(appConfig : ApplicationConfig) {
    val jettyRunner = JettyApplicationHost(appConfig)
    jettyRunner.start()
}

fun main(args: Array<String>) {
    val map = HashMap<String, String>()
    for (arg in args) {
        val data = arg.split('=')
        if (data.size == 2) {
            map[data[0]] = data[1]
        }
    }

    val jar = map["-jar"]?.let { File(it).toURI().toURL() }
    val classPath = if (jar == null) array<URL>() else array<URL>(jar)

    val config = ContextConfig(InitialContext())
    config.set("ktor.environment", map["-env"] ?: "development")
    //config.loadJsonResourceConfig(classPath)

    val logPath = config.tryGet("ktor.log.properties")
    if (logPath != null) {
        PropertyConfigurator.configureAndWatch(logPath, 5000)
    }
    else {
        BasicConfigurator.configure()
        LogManager.getRootLogger()?.setLevel(Level.INFO)
    }

    val log = SL4JApplicationLog("<Application>")
    val appConfig = ApplicationConfig(config, log, jar)

    println(config.toString())
    server(appConfig)
}